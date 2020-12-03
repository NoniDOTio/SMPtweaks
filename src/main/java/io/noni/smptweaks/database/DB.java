package io.noni.smptweaks.database;

import com.zaxxer.hikari.HikariDataSource;
import io.noni.smptweaks.SMPTweaks;
import org.bukkit.configuration.file.FileConfiguration;

public class DB {
    HikariDataSource hikari;

    public DB() {
        this.initializeHikari();
    }

    public void initializeHikari() {
        FileConfiguration config = SMPTweaks.getPlugin().getConfig();
        HikariDataSource hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(10);

        if(config.getBoolean("mysql.enabled") == true) {
            String host = config.getString("mysql.host");
            String database = config.getString("mysql.database");
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            hikari.setJdbcUrl("jdbc:mysql://" + host + "/" + database);
            hikari.setUsername(username);
            hikari.setPassword(password);
        }
    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}
