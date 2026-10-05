package net.futurecoders.sample6;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sample6 extends JavaPlugin{

    @Override
    public void onEnable() {
        // コマンド処理役として自分自身(this)を登録
        getCommand("custompotion").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        Player player = ((Player) sender).getPlayer();


        // /custompotion get の判定
        if (args.length > 0 && args[0].equalsIgnoreCase("get")) {
            // アイテム（ポーション）の作成
            ItemStack potion = new ItemStack(Material.POTION);
            ItemMeta meta = potion.getItemMeta();

            if (meta != null) {
                // 表示名の設定 (1.21+ 推奨の Component)
                meta.displayName(Component.text("カスタムポーション"));
                potion.setItemMeta(meta);
            }

            // プレイヤーに付与
            player.getInventory().addItem(potion);
            player.sendMessage("§aカスタムポーションを入手しました！");
            return true;
        }

        // 使い方を表示
        player.sendMessage("§c使用方法: /custompotion get");
        return true;
    }
}
