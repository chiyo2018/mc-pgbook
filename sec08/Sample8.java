package net.futurecoders.sample8;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public final class Sample8 extends JavaPlugin {
    private long startTick;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        World world = Bukkit.getWorlds().get(0);
        if (!getConfig().contains("serverStartTick")) {
            startTick = world.getFullTime();
            getConfig().set("serverStartTick", startTick);
            saveConfig();
        } else {
            startTick = getConfig().getLong("serverStartTick");
        }
        getLogger().info("仮想カレンダー開始: Tick = " + startTick);
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("date")) {
            World world = Bukkit.getWorlds().get(0);
            long currentTick = world.getFullTime();
            long elapsedTicks = currentTick - startTick;
            long daysPassed = elapsedTicks / 24000;
            long year = (daysPassed / 360) + 1;
            long month = ((daysPassed % 360) / 30) + 1;
            long day = (daysPassed % 30) + 1;
            sender.sendMessage("§6Minecraft暦: " + year + "年 " + month + "月 " + day + "日");
            return true;
        }
        return false;
    }
}
