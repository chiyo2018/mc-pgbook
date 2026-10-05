package net.futurecoders.sample10;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


// CommandExecutor と Listener の両方を実装
public final class Sample10 extends JavaPlugin implements Listener, CommandExecutor {


    private final List<Location> respawnSpots = new ArrayList<>();
    private final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GREEN + "RRS" + ChatColor.GRAY + "] ";


    @Override
    public void onEnable() {
        // 設定ファイルを保存し、読み込む
        this.saveDefaultConfig();
        this.loadRespawnSpots();


        // イベントとコマンドを登録
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("respawnspot").setExecutor(this);


        getLogger().info("[RandomRespawn] プラグインが有効化されました。設定済み地点数: " + respawnSpots.size());
    }


    // --- 設定ファイルの管理 (load/save) ---


    public void loadRespawnSpots() {
        respawnSpots.clear();


        ConfigurationSection spots = getConfig().getConfigurationSection("spots");
        if (spots == null) return;


        for (String key : spots.getKeys(false)) {
            ConfigurationSection spot = spots.getConfigurationSection(key);
            try {
                // Location オブジェクトを復元
                Location loc = new Location(
                        getServer().getWorld(spot.getString("world")),
                        spot.getDouble("x"),
                        spot.getDouble("y"),
                        spot.getDouble("z"),
                        (float) spot.getDouble("yaw", 0.0),
                        (float) spot.getDouble("pitch", 0.0)
                );
                respawnSpots.add(loc);
            } catch (Exception e) {
                getLogger().warning("地点ID '" + key + "' の読み込みに失敗しました。ワールド名やデータ形式を確認してください。");
            }
        }
    }


    public void saveRespawnSpots() {
        // セクションを再作成
        // 既存のセクションは丸ごと置き換えられる
        ConfigurationSection spots = getConfig().createSection("spots");


        for (int i = 0; i < respawnSpots.size(); i++) {
            Location loc = respawnSpots.get(i);
            String key = String.valueOf(i + 1); // 1 から始まる ID として保存


            spots.set(key + ".world", loc.getWorld().getName());
            spots.set(key + ".x", loc.getX());
            spots.set(key + ".y", loc.getY());
            spots.set(key + ".z", loc.getZ());
            spots.set(key + ".yaw", loc.getYaw());
            spots.set(key + ".pitch", loc.getPitch());
        }


        this.saveConfig();
    }




    // --- イベント処理 (リスポーン) ---


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (respawnSpots.isEmpty()) return;


        Player player = event.getPlayer();


        Bukkit.getScheduler().runTask(this, () -> {
            Location loc = respawnSpots.get(new Random().nextInt(respawnSpots.size()));
            player.teleport(loc);
            event.setRespawnLocation(loc);
        });
    }




    // --- コマンド処理 (/respawnspot) ---


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 権限チェック (plugin.yml で定義)
        if (!sender.hasPermission("sample10.admin")) {
            sender.sendMessage(ChatColor.RED + "あなたにはこのコマンドを実行する権限がありません。");
            return true;
        }


        // サブコマンドは必須
        if (args.length == 0) {
            return false;
        }


        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "add":
                return addSpot(sender);
            default:
                return false;
        }
    }


    private boolean addSpot(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行可能です。");
            return true;
        }




        Player player = (Player) sender;
        Location loc = player.getLocation();
        respawnSpots.add(loc);
        saveRespawnSpots(); // 保存と再読み込み




        int id = respawnSpots.size();
        sender.sendMessage(PREFIX + ChatColor.GREEN + "地点ID " + id + " を追加しました。");
        return true;
    }
}
