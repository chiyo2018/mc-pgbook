package net.futurecoders.sample9;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
public final class Sample9 extends JavaPlugin {
    //ランダムな値を生成するためのRandomクラス
    private final Random random = new Random();
    //処理済みのアイテムのUUIDを保存するSet
    private final Set<UUID> processedItems = new HashSet<>();
    //降らせるアイテム
    private static final Material FALLING_ITEM = Material.CREEPER_HEAD;

    @Override
    public void onEnable() {
        // アイテム生成タスク（100tickごと）
        new BukkitRunnable() {
            @Override
            public void run() {
                //各ワールドについて実行する。
                for (World world : getServer().getWorlds()) {
                    //各プレイヤーについて実行する。
                    for (Player player : world.getPlayers()) {
                        //プレイヤーの現在地から周囲10ブロックのランダムな座標を取得
                        Location base = player.getLocation();
                        World w = player.getWorld();
                        double x = base.getX() + random.nextInt(21) - 10;
                        double z = base.getZ() + random.nextInt(21) - 10;
                        double y = Math.min(base.getY() + 40, w.getMaxHeight() - 5);




                        //クリーパーヘッドを先程の座標にドロップさせる
                        Location spawnLoc = new Location(w, x, y, z);
                        ItemStack creeperHead = new ItemStack(FALLING_ITEM);
                        Item itemEntity = w.dropItem(spawnLoc, creeperHead);
                        //下向きの速度を設定
                        itemEntity.setVelocity(new Vector(0, -0.3, 0));
                    }
                }
            }
        }.runTaskTimer(this, 20, 100);

        // 地面着地チェックタスク（100tickごと）
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    //各エンティティについて実行する。
                    for (Entity entity : world.getEntities()) {
                        //アイテム以外のエンティティは処理しない
                        if (!(entity instanceof Item)) continue;
                        //エンティティをItem型に変換する
                        Item item = (Item) entity;
                        //クリーパーヘッド以外のアイテムは処理しない
                        if (item.getItemStack().getType() != FALLING_ITEM) continue;
                        //地面に着地していて、まだ処理していないアイテムだけ処理する
                        if (item.isOnGround() && !processedItems.contains(item.getUniqueId())) {
                            onItemLanded(item);
                            processedItems.add(item.getUniqueId());
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20, 20);
    }

    private void onItemLanded(Item item) {
        //アイテムの現在地を取得
        Location loc = item.getLocation();
        World w = loc.getWorld();

        //ワールドが取得できなかった場合は処理しない
        if (w == null) return;

        // 音とパーティクルを出す
        w.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.0f);
        w.spawnParticle(Particle.EXPLOSION, loc, 30, 1, 1, 1, 0.2);
    }

}
