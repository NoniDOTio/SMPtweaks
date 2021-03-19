package io.noni.smptweaks.database;

import com.zaxxer.hikari.HikariDataSource;
import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private final HikariDataSource hikariDataSource;
    private static final FileConfiguration config = SMPTweaks.getPlugin().getConfig();

    public DatabaseManager() {
        this.hikariDataSource = new HikariDataSource();
        this.hikariDataSource.setMaximumPoolSize(10);

        if(config.getBoolean("mysql.enabled")) {
            String host = config.getString("mysql.host");
            String database = config.getString("mysql.database");
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + host + "/" + database);
            this.hikariDataSource.setUsername(username);
            this.hikariDataSource.setPassword(password);
        }
    }

    /**
     * Get HikariDataSource
     */
    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    /**
     * Check if connection to DB is possible
     * @return
     */
    public boolean canConnect() {
        try(Connection con = this.hikariDataSource.getConnection()) {
            return true;
        } catch (SQLException throwables) {
            LoggingUtils.error("Unable to connect to database.");
            throwables.printStackTrace();
            return false;
        }
    }

    /**
     * Check if table exists
     * @return
     */
    public boolean isSetUpCorrectly() {
        try(Connection con = this.hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement("" +
                    "SELECT 1 " +
                    "FROM `smptweaks_player` " +
                    "LIMIT 1"
            );
            return preparedStatement.execute();
        } catch (SQLException throwables) {
            LoggingUtils.error("The database is not set up correctly.");
            return false;
        }
    }

    /**
     * Create table
     */
    public void setUp() {
        try(Connection con = this.hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement("" +
                    "CREATE TABLE IF NOT EXISTS `smptweaks_player` (" +
                    "`uuid` VARCHAR(255) UNIQUE NULL PRIMARY KEY," +
                    "`name` VARCHAR(255) NOT NULL," +
                    "`level` SMALLINT DEFAULT 1 NOT NULL," +
                    "`total_xp` INTEGER DEFAULT 0 NOT NULL," +
                    "`xp_display_mode` TINYINT DEFAULT 0 NOT NULL," +
                    "`last_reward_claimed` DATETIME NULL," +
                    "`last_special_item_drop` DATETIME NULL" +
                    ")"
            );
            preparedStatement.execute();
        } catch (SQLException throwables) {
            LoggingUtils.error("Could not set up database.");
            throwables.printStackTrace();
        }
    }

    /**
     * Get PlayerMeta from DB
     * @param player
     * @return playerMeta
     */
    public PlayerMeta getPlayerMeta(Player player) {
        try (Connection con = this.hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement("" +
                    "SELECT `level`, `total_xp`, `xp_display_mode`, `last_special_item_drop` " +
                    "FROM `smptweaks_player` " +
                    "WHERE `uuid` = ? " +
                    "LIMIT 1"
            );
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.first()) {
                return new PlayerMeta(
                        player,
                        resultSet.getInt("level"),
                        resultSet.getInt("total_xp"),
                        resultSet.getInt("xp_display_mode"),
                        true
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ChatUtils.negative(player, "Deine Metadaten konnten nicht geladen werden. Dein Fortschritt wird eventuell nicht gespeichert. Bitte trenne die Verbindung mit dem Server und versuche es erneut.");
        return null;
    }

    /**
     * Store PlayerMeta in DB
     * @param playerMeta
     */
    public void savePlayerMeta(PlayerMeta playerMeta) {
        try(Connection con = this.hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement("" +
                    "INSERT INTO `smptweaks_player` (`name`, `uuid`, `level`, `total_xp`, `xp_display_mode`)" +
                    "VALUES(?, ?, ?, ?, ?)" +
                    "ON DUPLICATE KEY UPDATE" +
                    "`name` = VALUES(`name`)," +
                    "`level` = VALUES(`level`)," +
                    "`total_xp` = VALUES(`total_xp`)," +
                    "`xp_display_mode` = VALUES(`xp_display_mode`)"
            );
            preparedStatement.setString(1, playerMeta.getPlayer().getName());
            preparedStatement.setString(2, playerMeta.getPlayer().getUniqueId().toString());
            preparedStatement.setInt(3, playerMeta.getLevel());
            preparedStatement.setInt(4, playerMeta.getTotalXp());
            preparedStatement.setInt(5, playerMeta.getXpDisplayMode());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
