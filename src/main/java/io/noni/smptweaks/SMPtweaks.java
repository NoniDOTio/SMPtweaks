package io.noni.smptweaks;

import io.noni.smptweaks.commands.CollectCommand;
import io.noni.smptweaks.commands.LevelCommand;
import io.noni.smptweaks.commands.LevelTab;
import io.noni.smptweaks.commands.WhereisCommand;
import io.noni.smptweaks.database.DatabaseManager;
import io.noni.smptweaks.events.*;
import io.noni.smptweaks.models.ConfigCache;
import io.noni.smptweaks.tasks.PlayerMetaStorerTask;
import io.noni.smptweaks.tasks.TimeModifierTask;
import io.noni.smptweaks.tasks.WeatherClearerTask;
import io.noni.smptweaks.utils.LoggingUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Stream;

public final class SMPtweaks extends JavaPlugin {
    private static SMPtweaks plugin;
    private static DatabaseManager databaseManager;
    private static FileConfiguration config;
    private static ConfigCache configCache;
    private static Map<String, String> translations;

    /**
     * Plugin startup logic
     */
    @Override
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
            LoggingUtils.info("Paper events will be used in order to improve performance");
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

            config.getBoolean("server_levels.enabled")
                    ? new PlayerJoin() : null,

            config.getBoolean("spawn_rates.enabled") ||
            config.getBoolean("shulkers_spawn_naturally")
                    ? (isPaperServer ? new PaperPreCreatureSpawn() : new CreatureSpawn()) : null,

            config.getBoolean("spawn_rates.enabled") &&
            configCache.getEntitySpawnRates().containsKey(EntityType.PHANTOM)
                    ? (isPaperServer ? new PaperPhantomPreSpawn() : null) : null,

            config.getBoolean("custom_drops.enabled")
                    ? new EntityDeath() : null,

            config.getBoolean("better_tipped_arrows")
                    ? new EntityDamageByEntity() : null,

            config.getBoolean("better_tipped_arrows")
                    ? new ProjectileLaunch() : null,

            config.getBoolean("server_levels.enabled")
                    ? new PlayerLeave() : null
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
     * @param listener
     */
    private void registerEvent(@Nullable Listener listener) {
        if(listener != null) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Register recipes
     * @param recipe
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
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if(new io.noni.smptweaks.placeholders.LevelExpansion().register()) {
                LoggingUtils.info("Registered PlaceholderAPI expansion");
            } else {
                LoggingUtils.warn("Unable to register PlaceholderAPI expansion");
            }
        }
    }

    /**
     * Plugin shutdown logic
     */
    @Override
    public void onDisable() {
        LoggingUtils.info("Disabling SMPtweaks...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            new PlayerMetaStorerTask(player).run();
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
     * Get hashmap of translations
     * @return Translations
     */
    public static Map<String, String> getTranslations() {
        return translations;
    }
}
