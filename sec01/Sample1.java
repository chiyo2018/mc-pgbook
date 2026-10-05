package net.futurecoders.sample1;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sample1 extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        //イベント処理を行うクラスを自分自身に設定
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        //チャット欄に「こんにちは」と表示
        event.setJoinMessage("こんにちは");
    }
}
