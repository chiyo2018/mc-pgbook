package net.futurecoders.sample2;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sample2 extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // このクラスのイベントを処理するように指定
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer(); // ログインしたプレイヤー
        PlayerInventory inventory = player.getInventory(); // プレイヤーのインベントリ
        ItemStack itemStack = new ItemStack(Material.DIAMOND, 64); // 山積みのダイヤモンド


        if (inventory.contains(Material.DIAMOND, 64)) {
            inventory.addItem(itemStack); // プレイヤーインベントリに山積みのダイヤモンドを加える
            player.sendMessage(ChatColor.GOLD + "よく来たな！もっとダイヤモンドをくれてやろう、このとんでもない成金め！！");
        }
    }
}
