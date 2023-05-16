package club.ifcserver.laggremover.util;

import club.ifcserver.laggremover.api.aparser.AnfoParser;
import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.api.proto.Protocol;
import club.ifcserver.laggremover.main.LaggRemover;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.parser.ParseException;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/util/LRConfig.class */
public class LRConfig {
    public static double lagConstant;
    public static long ramConstant;
    public static long smartaicooldown;
    public static boolean thinMobs;
    public static int thinAt;
    public static boolean isAIActive;
    public static boolean autoChunk;
    public static boolean noSpawnChunks;
    public static boolean autoLagRemoval;
    public static boolean doRelativeAction;
    public static boolean doOnlyItemsForRelative;
    public static boolean dontDoFriendlyMobsForRelative;
    public static int autoLagRemovalTime;
    public static int localLagRemovalCooldown;
    public static int chatDelay;
    public static int localLagRadius;
    public static int localLagTriggered;
    public static float localThinPercent;
    public static HashMap<LRProtocol, Counter> counters;
    public static HashMap<LRProtocol, DoubleVar<Object[], Boolean>> periodic_protocols;
    public static HashMap<LRProtocol, DoubleVar<Object[], Boolean>> ram_protocols;
    public static HashMap<LRProtocol, DoubleVar<Object[], Boolean>> tps_protocols;

    public static void reload() {
        DoubleVar<Object[], Boolean> dat;
        DoubleVar<Object[], Boolean> dat2;
        DoubleVar<Object[], Boolean> dat3;
        FileConfiguration f = new YamlConfiguration();
        try {
            f.load(new File(LaggRemover.lr.getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            LaggRemover.lr.getLogger().severe("Error loading configuration:");
            e.printStackTrace();
            f = LaggRemover.lr.getConfig();
        }
        lagConstant = f.getDouble("TPS");
        ramConstant = f.getLong("RAM");
        autoChunk = f.getBoolean("autoChunk");
        thinMobs = f.getBoolean("thinMobs");
        thinAt = f.getInt("thinAt");
        autoLagRemovalTime = f.getInt("auto-lag-removal.every");
        noSpawnChunks = f.getBoolean("noSpawnChunks");
        autoLagRemoval = f.getBoolean("auto-lag-removal.run");
        isAIActive = f.getBoolean("smartlagai");
        List<String> noSaveWorlds = f.getStringList("nosaveworlds");
        counters = new HashMap<>();
        periodic_protocols = new HashMap<>();
        ram_protocols = new HashMap<>();
        tps_protocols = new HashMap<>();
        localLagRemovalCooldown = f.getInt("localLagRemovalCooldown");
        chatDelay = f.getInt("chatDelay");
        doRelativeAction = f.getBoolean("doRelativeAction");
        doOnlyItemsForRelative = f.getBoolean("doOnlyItemsForRelative");
        dontDoFriendlyMobsForRelative = f.getBoolean("dontDoFriendlyMobsForRelative");
        localLagRadius = f.getInt("localLagRadius");
        localLagTriggered = f.getInt("localLagTriggered");
        localThinPercent = f.getInt("localThinPercent") / 100.0f;
        smartaicooldown = f.getLong("smartaicooldown");
        for (LRProtocol p : Protocol.getProtocols()) {
            if (f.contains("protocol_warnings." + p.id())) {
                counters.put(p, Protocol.getCounter(p));
            }
            String lpk = "lag_protocols.periodically." + p.id();
            String lpk_ram = "lag_protocols.low_ram." + p.id();
            String lpk_tps = "lag_protocols.low_tps." + p.id();
            if (f.contains(lpk) && (dat3 = loadP(p, lpk, f)) != null) {
                periodic_protocols.put(p, dat3);
            }
            if (f.contains(lpk_ram) && (dat2 = loadP(p, lpk_ram, f)) != null) {
                ram_protocols.put(p, dat2);
            }
            if (f.contains(lpk_tps) && (dat = loadP(p, lpk_tps, f)) != null) {
                tps_protocols.put(p, dat);
            }
        }
        if (!noSaveWorlds.contains("DISABLED")) {
            for (World w : Bukkit.getWorlds()) {
                if (noSaveWorlds.contains(w.getName())) {
                    w.setAutoSave(false);
                    LaggRemover.lr.getLogger().info("World \"" + w.getName() + "\" will not automatically save.");
                }
            }
        }
    }

    private static DoubleVar<Object[], Boolean> loadP(LRProtocol p, String lpk, FileConfiguration f) {
        try {
            DoubleVar<Object[], Boolean> dat = AnfoParser.parse(p, f.getString(lpk));
            return dat;
        } catch (AnfoParser.AnfoParseException | ParseException e) {
            LaggRemover.lr.getLogger().info("Error parsing protocol info for \"" + lpk + "\": " + e.toString());
            return null;
        }
    }

    public static void init() {
        File config = new File(LaggRemover.lr.getDataFolder(), "config.yml");
        if (!config.exists()) {
            LaggRemover.lr.saveDefaultConfig();
        }
        if (DrewMath.intFrom(LaggRemover.lr.getConfig().getString("version")) < 16) {
            LaggRemover.lr.getLogger().info("The saved version is not compatible with this version of LaggRemover and could not be updated by the automatic configuration updater. LaggRemover will back up the current configuration and generate a new one for you. Please manually copy over any old settings.");
            try {
                FileWriter w = new FileWriter(new File(LaggRemover.lr.getDataFolder(), "config(backup-" + new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(Calendar.getInstance().getTime()) + ").yml"));
                w.write(new String(Files.readAllBytes(config.toPath())));
                w.flush();
                w.close();
            } catch (IOException e) {
                LaggRemover.lr.getLogger().info("An error occurred when backing up the old configuration (" + e.getMessage() + ").");
            }
            if (config.delete()) {
                LaggRemover.lr.saveDefaultConfig();
            } else {
                LaggRemover.lr.getLogger().info("Could not delete old configuration. Please delete it manually and restart your server to prevent imminent errors.");
            }
        }
        check(LaggRemover.config_version);
        reload();
    }

    private static void check(String version) {
        try {
            if (!LaggRemover.lr.getConfig().getString("version").equals(version)) {
                updateConfig(version);
            }
        } catch (Exception e) {
            updateConfig(version);
        }
    }

    private static void updateConfig(String config_version) {
        HashMap<String, Object> newConfig = getConfigVals();
        FileConfiguration c = LaggRemover.lr.getConfig();
        for (String var : c.getKeys(false)) {
            newConfig.remove(var);
        }
        if (newConfig.size() != 0) {
            for (String key : newConfig.keySet()) {
                c.set(key, newConfig.get(key));
            }
            try {
                c.set("version", config_version);
                c.save(new File(LaggRemover.lr.getDataFolder(), "config.yml"));
            } catch (IOException e) {
            }
        }
        LaggRemover.lr.getLogger().info("Your configuration file was updated to v" + config_version);
    }

    private static HashMap<String, Object> getConfigVals() {
        HashMap<String, Object> var = new HashMap<>();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringFromInputStream(LaggRemover.class.getResourceAsStream("/config.yml")));
        } catch (InvalidConfigurationException e) {
        }
        for (String key : config.getKeys(false)) {
            var.put(key, config.get(key));
        }
        return var;
    }

    private static String stringFromInputStream(InputStream in) {
        return new Scanner(in).useDelimiter("\\A").next();
    }
}
