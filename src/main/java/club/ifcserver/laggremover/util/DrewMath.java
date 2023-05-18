package club.ifcserver.laggremover.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.bukkit.Bukkit;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/util/DrewMath.class */
public class DrewMath {
    public static double round(double value, int places) {
        if (places < 0) {
            return 0.0d;
        }
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static long getSize(File folder) {
        long j;
        long size;
        long i = 0;
        for (File f : Objects.requireNonNull(folder.listFiles())) {
            if (f.isFile()) {
                j = i;
                size = f.length();
            } else {
                j = i;
                size = getSize(f);
            }
            i = j + size;
        }
        return i;
    }

    public static File getSpigotRoot() {
        File f = new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        return new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - f.getName().length()) + File.separator);
    }

    public static int intFrom(String s) {
        char[] charArray;
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
                sb.append(c);
            }
        }
        return Integer.parseInt(sb.toString());
    }

    public static <T> T cast(Class<T> cls, Object o) {
        return cls.cast(o);
    }

    public static boolean between(long var, long low, long high) {
        return var >= low && var <= high;
    }

    public static String getTagForTime(long time) {
        return between(time, 13000L, 24000L) ? "night" : between(time, 12000L, 12999L) ? "dusk" : between(time, 0L, 999L) ? "dawn" : "day";
    }
}
