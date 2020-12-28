package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMetaLoaderTask extends BukkitRunnable {
    private Player player;

    public PlayerMetaLoaderTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try(Connection con = SMPTweaks.getPlugin().getDBConnection()) {
            PreparedStatement select = con.prepareStatement("" +
                "SELECT `level`, `total_xp`, `xp_display_mode`, `last_special_item_drop` " +
                "FROM `smptweaks_player` " +
                "WHERE `uuid` = ? " +
                "LIMIT 1"
            );
            select.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = select.executeQuery();
            resultSet.first();

            PlayerMeta playerMeta = new PlayerMeta(
                player,
                resultSet.getInt("level"),
                resultSet.getInt("total_xp"),
                resultSet.getInt("xp_display_mode"),
                1
            );
            playerMeta.pushToPDC();

            LoggingUtils.info("Loaded PlayerMeta for " + player.getName());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
