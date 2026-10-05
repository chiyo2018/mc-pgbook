package net.futurecoders.sample4;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sample4 extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("mv").setExecutor(this);

        // newWorldが存在するか確認
        World world = Bukkit.getWorld("newWorld");

        // newWorldが存在しなければ生成
        if (world == null) {
            WorldCreator creator = new WorldCreator("newWorld");
            world = creator.createWorld();

            getLogger().info("ワールド[newWorld]を生成しました。");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // getWorld()やteleport()に必要なのでPlayerにキャストしましょう。
        Player player = (Player) sender;

        // 引数がある場合はワールドを取得
        World world = Bukkit.getWorld(args[0]);

        // ワールドのスポーン地点を取得
        Location location = world.getSpawnLocation();

        // ワールド移動させる
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

        return true;
    }
}