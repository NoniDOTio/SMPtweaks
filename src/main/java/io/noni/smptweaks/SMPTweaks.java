package io.noni.smptweaks;

import io.noni.smptweaks.commands.LevelCommand;
import io.noni.smptweaks.commands.WhereisCommand;
import io.noni.smptweaks.database.DatabaseManager;
import io.noni.smptweaks.events.*;
import io.noni.smptweaks.placeholders.LevelExpansion;
import io.noni.smptweaks.recipes.RecipeManager;
import io.noni.smptweaks.tasks.PlayerMetaStorerTask;
import io.noni.smptweaks.tasks.TimeModifierTask;
import io.noni.smptweaks.tasks.WeatherClearerTask;
import io.noni.smptweaks.utils.LoggingUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.stream.Stream;

public final class SMPTweaks extends JavaPlugin {

    private static SMPTweaks plugin;
    private static DatabaseManager databaseManager;
    private static FileConfiguration config;

    /**
     * Plugin startup logic
     */
    @Override
    public void onEnable() {
        // Variable for checking startup duration
        long startingTime = System.currentTimeMillis();

        // Static reference to plugin
        plugin = this;

        // Static reference to config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        config = getConfig();

        // Static reference to Hikari
        databaseManager = new DatabaseManager();

        // Register Event Listeners
        Stream.of(
            config.getBoolean("disable_night_skip")
                    ? new TimeSkip() : null,

            config.getBoolean("disable_night_skip")
                    ? new PlayerBedEnter() : null,

            config.getBoolean("disable_night_skip")
                    ? new PlayerBedLeave() : null,

            config.getBoolean("drop_xp_on_death.enabled") ||
            config.getBoolean("remove_level_on_death.enabled") ||
            config.getBoolean("drop_inventory_on_death.enabled") ||
            config.getBoolean("drop_equipment_on_death.enabled")
                    ? new PlayerDeath() : null,

            config.getBoolean("server_levels.enabled") ||
            config.getDouble("xp_multiplier") != 1
                    ? new PlayerExpChange() : null,

            config.getBoolean("server_levels.enabled")
                    ? new PlayerJoin() : null,

            config.getBoolean("server_levels.enabled")
                    ? new PlayerLeave() : null,

            config.getBoolean("server_levels.enabled") ||
            config.getDouble("xp_multiplier") != 1 ||
            config.getDouble("mending_repair_amount_multiplier") != 1
                    ? new PlayerItemMend() : null
        ).forEach(this::registerEvent);

        // Register Recipes
        Stream.of(
            config.getBoolean("craftable_elytra")
                    ? RecipeManager.elytra() : null
        ).forEach(this::registerRecipe);

        // Register PlaceholderExpansions
        Stream.of(
            config.getBoolean("server_levels.enabled")
                    ? new LevelExpansion() : null
        ).forEach(this::registerPlaceholder);

        // Register Commands
        if(config.getBoolean("enable_commands.whereis")) {
            getCommand("whereis").setExecutor(new WhereisCommand());
        }
        if(config.getBoolean("enable_commands.level") && config.getBoolean("server_levels.enabled")) {
            getCommand("level").setExecutor(new LevelCommand());
        }

        // Schedule tasks
        if(config.getInt("shorten_nights_by") != 0 || config.getInt("extend_days_by") != 0) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeModifierTask(), 0L, 2L);
        }
        if(config.getBoolean("clear_weather_at_dawn")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new WeatherClearerTask(), 0L, 100L);
        }

        // Done :)
        LoggingUtils.info("Up and running! Startup took " + (System.currentTimeMillis() - startingTime) + "ms");
    }

    /**
     * Register events
     * @param listener
     */
    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Register recipes
     * @param recipe
     */
    private void registerRecipe(Recipe recipe) {
        Bukkit.addRecipe(recipe);
    }

    /**
     * Register placeholders
     * @param expansion
     */
    private void registerPlaceholder(PlaceholderExpansion expansion) {
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            expansion.register();
        }
    }

    /**
     * Plugin shutdown logic
     */
    @Override
    public void onDisable() {
        LoggingUtils.info("Disabling SMPTweaks...");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            BukkitTask playerMetaStorerTask = new PlayerMetaStorerTask(player).runTask(this);
        }
    }

    /**
     * Grab reference to plugin
     * @return Plugin
     */
    public static SMPTweaks getPlugin() {
        return plugin;
    }

    /**
     * Grab reference to DB
     * @return DatabaseManager
     */
    public static DatabaseManager getDB() {
        return databaseManager;
    }

    /**
     * Grab reference to config
     * @return FileConfiguration
     */
    public static FileConfiguration getCfg() {
        return config;
    }
}
