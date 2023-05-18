package club.ifcserver.laggremover.main;

import club.ifcserver.laggremover.util.BitString;
import club.ifcserver.laggremover.util.DrewMath;
import org.bukkit.scheduler.BukkitRunnable;

public class TickPerSecond extends BukkitRunnable {
    public static int TICK_COUNT = 0;
    public static long[] TICKS = new long[600];

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticks) {
        if (TICK_COUNT < ticks) {
            return 20.0d;
        }
        int target = ((TICK_COUNT - 1) - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];
        return ticks / (elapsed / 1000.0d);
    }

    @Override
    public void run() {
        TICKS[TICK_COUNT % TICKS.length] = System.currentTimeMillis();
        TICK_COUNT++;
    }

    public static String format() {
        double tps = DrewMath.round(getTPS(), 2);
        int tps_whole = (int) DrewMath.round(getTPS(), 0);
        String bf = BitString.BLOCK_FULL.getComp();
        String bl = BitString.BLOCK_FULL_LIGHT.getComp();
        StringBuilder sb = new StringBuilder();
        sb.append("§b").append(tps).append("\n§e<");
        if (tps_whole > 15) {
            sb.append("§a");
        } else if (tps_whole > 5) {
            sb.append("§e");
        } else {
            sb.append("§c");
        }
        boolean hasTricked = false;
        for (int i = 1; i < 21; i++) {
            if (i > tps_whole) {
                if (!hasTricked) {
                    sb.append("§7");
                    hasTricked = true;
                }
                sb.append(bl);
            } else {
                sb.append(bf);
            }
        }
        sb.append("§e>");
        return sb.toString();
    }
}
