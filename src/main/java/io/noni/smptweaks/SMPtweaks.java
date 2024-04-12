package io.noni.smptweaks;

import io.noni.smptweaks.commands.*;
import io.noni.smptweaks.database.DatabaseManager;
import io.noni.smptweaks.events.*;
import io.noni.smptweaks.models.ConfigCache;
import io.noni.smptweaks.tasks.*;
import io.noni.smptweaks.utils.LoggingUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public final class SMPtweaks extends JavaPlugin {
    private static SMPtweaks plugin;
    private static DatabaseManager databaseManager;
    private static FileConfiguration config;
    private static ConfigCache configCache;
    private static Map<String, String> translations;
    private static Map<UUID, UUID> playerTrackers = new HashMap<>();
    private static List<UUID> coordinateDisplays = new ArrayList<>();

    /**
     * Plugin startup logic
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        // Variable for checking startup duration
        long startingTime = System.currentTimeMillis();

        // Static reference to plugin
        plugin = this;

        // Check if optional Paper classes are available
        boolean isPaperServer = false;
        try {
            Class.forName("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent");
            Class.forName("com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent");
            isPaperServer = true;
            LoggingUtils.info("Paper-API detected! SMPtweaks will use Paper events");
        } catch (ClassNotFoundException e) {
            LoggingUtils.info("This server doesn't seem to run Paper or a Paper-fork, falling back to using Spigot events");
        }

        // Copy default config files
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Static reference to config
        config = getConfig();
        configCache = new ConfigCache();

        // Static reference to Hikari
        databaseManager = new DatabaseManager();

        // Load translations
        var languageCode = config.getString("language");
        translations = TranslationUtils.loadTranslations(languageCode);

        //
        // Register Event Listeners
        //
        Stream.of(
            config.getBoolean("disable_night_skip")
                    ? new TimeSkip() : null,

            config.getBoolean("disable_night_skip")
                    ? new PlayerBedEnter() : null,

            config.getBoolean("disable_night_skip")
                    ? new PlayerBedLeave() : null,

            config.getBoolean("remove_xp_on_death.enabled") ||
            config.getBoolean("remove_inventory_on_death.enabled") ||
            config.getBoolean("remove_equipment_on_death.enabled") ||
            config.getBoolean("decrease_item_durability_on_death.enabled")
                    ? new PlayerDeath() : null,

            config.getInt("respawn_health") != 20 ||
            config.getInt("respawn_food_level") != 20
                    ? new PlayerRespawn() : null,

            config.getDouble("xp_multiplier") != 1
                    ? new PlayerExpChange() : null,

            config.getDouble("mending_repair_amount_multiplier") != 1
                    ? new PlayerItemMend() : null,

            config.getBoolean("server_levels.enabled")
                    ? new PlayerExpPickup() : null,

            config.getBoolean("buff_vegetarian_food")
                    ? new PlayerItemConsume() : null,

            config.getBoolean("disable_too_expensive_repairs")
                    ? new AnvilInventoryClickEvent() : null,

            config.getBoolean("server_levels.enabled") ||
            config.getBoolean("rewards.enabled")
                    ? new PlayerJoin() : null,

            config.getBoolean("spawn_rates.enabled")
                    ? (isPaperServer ? new PaperPreCreatureSpawn() : new CreatureSpawn()) : null,

            config.getBoolean("shulkers_spawn_naturally")
                    ? (isPaperServer ? new PaperShulkerSpawn() : new ShulkerSpawn()) : null,

            config.getBoolean("spawn_rates.enabled") &&
            configCache.getEntitySpawnRates().containsKey(EntityType.PHANTOM)
                    ? (isPaperServer ? new PaperPhantomPreSpawn() : null) : null,

            config.getBoolean("custom_drops.enabled")
                    ? new EntityDeath() : null,

            config.getBoolean("better_tipped_arrows")
                    ? new EntityDamageByEntity() : null,

            config.getBoolean("better_tipped_arrows")
                    ? new ProjectileLaunch() : null,

            config.getBoolean("server_levels.enabled") ||
            config.getBoolean("rewards.enabled")
                    ? new PlayerLeave() : null,

            config.getBoolean("enable_commands.track")
                    ? new TrackedPlayerLeave() : null
        ).forEach(this::registerEvent);

        //
        // Register Recipes
        //
        if(config.getBoolean("custom_recipes.enabled")) {
            configCache.getShapedRecipes().forEach(this::registerRecipe);
            configCache.getShapelessRecipes().forEach(this::registerRecipe);
        }

        //
        // Register PlaceholderExpansions
        //
        if(config.getBoolean("server_levels.enabled") && config.getBoolean("papi_placeholders.enabled")) {
            registerPlaceholders();
        }

        //
        // Register Commands
        //
        if(config.getBoolean("enable_commands.whereis")) {
            getCommand("whereis").setExecutor(new WhereisCommand());
        }
        if(config.getBoolean("rewards.enabled")) {
            getCommand("collect").setExecutor(new CollectCommand());
        }
        if(config.getBoolean("enable_commands.track")) {
            getCommand("track").setExecutor(new TrackCommand());
        }
        if(config.getBoolean("enable_commands.coords")) {
            getCommand("coords").setExecutor(new CoordsCommand());
        }
        if(config.getBoolean("enable_commands.level") && config.getBoolean("server_levels.enabled")) {
            getCommand("level").setExecutor(new LevelCommand());
            getCommand("level").setTabCompleter(new LevelTab());
        }

        //
        // Schedule tasks
        //
        if(config.getInt("day_duration_modifier") != 0 || config.getInt("night_duration_modifier") != 0) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeModifierTask(
                    config.getInt("day_duration_modifier"),
                    config.getInt("night_duration_modifier")
            ), 0L, 2L);
        }
        if(config.getBoolean("clear_weather_at_dawn")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new WeatherClearerTask(), 0L, 200L);
        }
        if(config.getBoolean("enable_commands.track")) {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new TrackerUpdateTask(), 0L, 10L);
        }
        if(config.getBoolean("enable_commands.coords")) {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new CoordsDisplayTask(), 0L, 10L);
        }

        //
        // Include bStats
        //
        new Metrics(this, 11736);

        //
        // Done :)
        //
        LoggingUtils.info("Up and running! Startup took " + (System.currentTimeMillis() - startingTime) + "ms");
    }

    /**
     * Register events
     * @param listener The Listener to register
     */
    private void registerEvent(@Nullable Listener listener) {
        if(listener != null) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Register recipes
     * @param recipe The Recipe to add to Bukkit
     */
    private void registerRecipe(@Nullable Recipe recipe) {
        if(recipe != null) {
            Bukkit.addRecipe(recipe);
        }
    }

    /**
     * Register placeholders
     */
    private void registerPlaceholders() {
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            LoggingUtils.warn("Unable to find PlaceholderAPI. Is it installed?");
            return;
        }

        if(!(new io.noni.smptweaks.placeholders.LevelExpansion().register())) {
            LoggingUtils.warn("Unable to register PlaceholderAPI expansion");
        }
    }

    /**
     * Plugin shutdown logic
     */
    @Override
    public void onDisable() {
        // Make sure Daylight cycle is turned back on
        if(config.getInt("day_duration_modifier") != 0) {
            Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }

        // Store all player data
        if(
            config.getBoolean("server_levels.enabled") ||
            config.getBoolean("rewards.enabled")
        ) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                new PlayerMetaStorerTask(player).run();
            }
        }
    }

    /**
     * Get reference to this plugin
     * @return Plugin
     */
    public static SMPtweaks getPlugin() {
        return plugin;
    }

    /**
     * Get reference to DB
     * @return DatabaseManager
     */
    public static DatabaseManager getDB() {
        return databaseManager;
    }

    /**
     * Get reference to config
     * @return FileConfiguration
     */
    public static FileConfiguration getCfg() {
        return config;
    }

    /**
     * Get reference to cached config
     * @return ConfigCache
     */
    public static ConfigCache getConfigCache() {
        return configCache;
    }

    /**
     * Get translations map
     * @return Translations
     */
    public static Map<String, String> getTranslations() {
        return translations;
    }

    /**
     * Get playerTrackers map
     * @return playerTrackers
     */
    public static Map<UUID, UUID> getPlayerTrackers() {
        return playerTrackers;
    }

    /**
     * Get coordinateDisplays list
     * @return coordinateDisplays
     */
    public static List<UUID> getCoordinateDisplays() { return coordinateDisplays; }
}
