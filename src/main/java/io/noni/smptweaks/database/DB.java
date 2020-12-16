package io.noni.smptweaks.database;

import com.zaxxer.hikari.HikariDataSource;
import io.noni.smptweaks.SMPTweaks;
import org.bukkit.configuration.file.FileConfiguration;

public class DB {
    private static HikariDataSource hikari;
    private static FileConfiguration config = SMPTweaks.getPlugin().getConfig();

    static  {
        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(10);

        if(config.getBoolean("mysql.enabled")) {
            String host = config.getString("mysql.host");
            String database = config.getString("mysql.database");
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            hikari.setJdbcUrl("jdbc:mysql://" + host + "/" + database);
            hikari.setUsername(username);
            hikari.setPassword(password);
        }
    }

    private DB() {}

    public static HikariDataSource getHikari() {
        return hikari;
    }
}
