package io.noni.smptweaks.database;

import com.zaxxer.hikari.HikariDataSource;
import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
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
        } else {
            File dbFile = this.getSQLite();
            if(dbFile != null) {
                this.hikariDataSource.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            }
        }

        if(!canConnect()) {
            LoggingUtils.error("Unable to connect to database.");
            return;
        }

        if(!isSetUpCorrectly()) {
            LoggingUtils.warn("The database is not set up correctly.");
            setUp();
        }
    }

    /**
     * Get HikariDataSource
     */
    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    /**
     * Create SQLite file
     */
    private File getSQLite() {
        File databaseFile = new File(SMPTweaks.getPlugin().getDataFolder(), "smptweaks.db");
        if(!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                LoggingUtils.error("Could not create SQLite database file.");
                e.printStackTrace();
                return null;
            }
        }
        return databaseFile;
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
            if(resultSet.next()) {
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
     * Check if player is already in DB
     */
    private boolean playerInDB(Player player) {
        try (Connection con = this.hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement("" +
                    "SELECT `name` " +
                    "FROM `smptweaks_player` " +
                    "WHERE `uuid` = ? " +
                    "LIMIT 1"
            );
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.isBeforeFirst();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Store PlayerMeta in DB
     * @param player
     */
    public void savePlayerMeta(Player player) {
        PlayerMeta playerMeta = new PlayerMeta(player);

        try(Connection con = this.hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (!playerInDB(player)) {
                preparedStatement = con.prepareStatement("" +
                        "INSERT INTO `smptweaks_player` (`name`, `level`, `total_xp`, `xp_display_mode`, `uuid`) " +
                        "VALUES(?, ?, ?, ?, ?)"
                );
            } else {
                preparedStatement = con.prepareStatement("" +
                        "UPDATE `smptweaks_player` " +
                        "SET " +
                        "`name` = ?, " +
                        "`level` = ?, " +
                        "`total_xp` = ?, " +
                        "`xp_display_mode` = ? " +
                        "WHERE `uuid` = ?"

                );
            }
            preparedStatement.setString(1, playerMeta.getPlayer().getName());
            preparedStatement.setInt(2, playerMeta.getLevel());
            preparedStatement.setInt(3, playerMeta.getTotalXp());
            preparedStatement.setInt(4, playerMeta.getXpDisplayMode());
            preparedStatement.setString(5, playerMeta.getPlayer().getUniqueId().toString());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
