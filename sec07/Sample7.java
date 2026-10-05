package net.futurecoders.sample7;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class Sample7 extends JavaPlugin {

    private ArmorStand targetStand;
    private BukkitTask moveTask;

    // 移動設定
    private final double startX = 0.5; // マスの中心に合わせるため +0.5
    private final double startY = 100.0;
    private final double startZ = 0.5;
    private final double maxDistance = 10.0; // 往復する距離（10マス）
    private final double speed = 0.1;       // 1ティックあたりの移動距離

    @Override
    public void onEnable() {
        // サーバーが起動完了した段階でワールドを取得して的を召喚
        Bukkit.getScheduler().runTaskLater(this, this::spawnMovingTarget, 20L);
    }

    @Override
    public void onDisable() {
        // プラグイン停止時にタスクを停止し、生成した防具立てを削除
        if (moveTask != null && !moveTask.isCancelled()) {
            moveTask.cancel();
        }
        if (targetStand != null && targetStand.isValid()) {
            targetStand.remove();
        }
    }

    private void spawnMovingTarget() {
        // メインワールドを取得（環境に合わせて必要に応じて変更してください）
        World world = Bukkit.getWorlds().get(0);
        Location startLoc = new Location(world, startX, startY, startZ);

        // 的のいるチャンクをロードしたまま保持する
        startLoc.getChunk().addPluginChunkTicket(this);

        // 既存の同座標にある古い防具立ての削除・防具立ての生成
        targetStand = (ArmorStand) world.spawnEntity(startLoc, EntityType.ARMOR_STAND);

        // 防具立て（的）の設定
        targetStand.setCustomName("§c動く的");//名前を付ける
        targetStand.setCustomNameVisible(true);
        targetStand.setGravity(false);        // 浮遊させるために重力を無効化
        targetStand.setArms(true);           // 腕を表示
        targetStand.setCanPickupItems(false); // アイテム拾いを防止

        // 移動アニメーションタスクの開始 (1ティック毎に実行 = 20FPS)
        moveTask = new BukkitRunnable() {
            private double currentOffset = 0.0;
            private boolean movingForward = true;


            @Override
            public void run() {
                if (targetStand == null || !targetStand.isValid()) {
                    cancel();
                    return;
                }

                // 移動方向に応じてオフセットを更新
                if (movingForward) {
                    currentOffset += speed;
                    if (currentOffset >= maxDistance) {
                        currentOffset = maxDistance;
                        movingForward = false; // 端に達したら折り返す
                    }
                } else {
                    currentOffset -= speed;
                    if (currentOffset <= 0.0) {
                        currentOffset = 0.0;
                        movingForward = true;  // 原点に戻ったら折り返す
                    }
                }
                
                // 新しい位置をセット
                Location newLoc = new Location(world, startX, startY, startZ + currentOffset);
                targetStand.teleport(newLoc);
            }
        }.runTaskTimer(this, 0L, 1L);
    }
}
