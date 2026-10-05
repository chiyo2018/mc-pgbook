package net.futurecoders.sample5;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Sample5 extends JavaPlugin implements Listener {

    // プレイヤーごとの「直前に捨てたアイテム」を保存
    private final Map<UUID, ItemStack[]> trashItems = new HashMap<>();

    @Override
    public void onEnable() {
        // コマンドを登録
        getCommand("trash").setExecutor(this);

        // イベントを登録
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // プレイヤー以外は実行できない
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行できます。");
            return true;
        }

        Player player = (Player) sender;

        // /trash restore
        if (args.length > 0 && args[0].equalsIgnoreCase("restore")) {
            restoreItems(player);
            return true;
        }

        // /trash
        openTrash(player);

        return true;
    }

    /**
     * ゴミ箱を開く
     */
    private void openTrash(Player player) {

        Inventory trash = Bukkit.createInventory(
                null,
                54,
                ChatColor.DARK_GRAY + "ゴミ箱"
        );

        player.openInventory(trash);
    }

    /**
     * ゴミ箱を閉じたとき
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        // プレイヤー以外なら無視
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        // ゴミ箱以外なら無視
        if (!event.getView().getTitle().equals(ChatColor.DARK_GRAY + "ゴミ箱")) {
            return;
        }

        Player player = (Player) event.getPlayer();

        Inventory inventory = event.getInventory();

        // ゴミ箱の中身を保存
        ItemStack[] items = inventory.getContents();

        // 空のスロットなども含めてコピー
        ItemStack[] savedItems = new ItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() != Material.AIR) {
                savedItems[i] = items[i].clone();
            }
        }

        // 直前のゴミ箱の中身として保存
        trashItems.put(player.getUniqueId(), savedItems);

        // ゴミ箱の中身を削除
        inventory.clear();

        player.sendMessage(ChatColor.GRAY + "ゴミ箱の中身を削除しました。");
        player.sendMessage(ChatColor.YELLOW + "/trash restore " + ChatColor.GRAY + "で直前に捨てたアイテムを復元できます。");
    }

    /**
     * アイテムを復元
     */
    private void restoreItems(Player player) {

        UUID uuid = player.getUniqueId();

        // 保存されたアイテムがあるか確認
        if (!trashItems.containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "復元できるアイテムがありません。");
            return;
        }

        ItemStack[] items = trashItems.get(uuid);

        boolean restored = false;

        // アイテムをプレイヤーのインベントリに戻す
        for (ItemStack item : items) {

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());

            // インベントリに入りきらなかった場合
            for (ItemStack remaining : leftover.values()) {
                player.getWorld().dropItemNaturally(
                        player.getLocation(),
                        remaining
                );
            }

            restored = true;
        }

        if (restored) {
            player.sendMessage(ChatColor.GREEN + "直前に捨てたアイテムを復元しました。");

            // 一度復元したら再度復元できないようにする
            trashItems.remove(uuid);
        } else {
            player.sendMessage(ChatColor.RED + "復元できるアイテムがありません。");
        }
    }
}