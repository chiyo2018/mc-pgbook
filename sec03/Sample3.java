package net.futurecoders.sample3;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sample3 extends JavaPlugin implements Listener{

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // 右クリックされたエンティティが馬でなければ処理を終了

        if (!(event.getRightClicked() instanceof Horse)) {
            return;
        }
        Player player = event.getPlayer();
        Horse horse = (Horse) event.getRightClicked();

        // メッセージをプレイヤーに送信
        player.sendMessage(ChatColor.GOLD + "--- 馬の能力 ---");
        player.sendMessage(ChatColor.RED + "❤ 体力: " + ChatColor.WHITE + horse.getAttribute(Attribute.MAX_HEALTH).getValue() );
        player.sendMessage(ChatColor.AQUA + "→ 速度: " + ChatColor.WHITE + horse.getAttribute(Attribute.MOVEMENT_SPEED) .getValue());
        player.sendMessage(ChatColor.GOLD + "----------------");
    }
}
