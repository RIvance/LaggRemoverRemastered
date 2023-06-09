package club.ifcserver.laggremover.main;

import club.ifcserver.laggremover.api.Module;
import club.ifcserver.laggremover.api.proto.DelayedLRProtocolResult;
import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.api.proto.LRProtocolResult;
import club.ifcserver.laggremover.api.proto.Protocol;
import club.ifcserver.laggremover.inf.Help;
import club.ifcserver.laggremover.util.DoubleVar;
import club.ifcserver.laggremover.util.LRConfig;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.ZipFile;

public class LaggRemover extends JavaPlugin implements Listener {
    public static final String CONFIG_VERSION = "0.1.7";
    public static final long MEMORY_MBYTE_SIZE = 1024;
    public static LaggRemover instance;
    public static String prefix = "§6§lLaggRemover §7§l>>§r ";
    public static File modDir;
    private static HashMap<Module, String[]> loaded;

    public void onEnable() {

        instance = this;
        BukkitScheduler scheduler = Bukkit.getScheduler();

        Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);

        scheduler.runTaskTimer(this, __ -> new TickPerSecond().run(), 100L, 1L);

        Help.init();
        Protocol.init();
        LRConfig.init();
        loaded = new HashMap<>();
        prefix = Objects.requireNonNull(getConfig().getString("prefix")).replaceAll("&", "§");

        if (LRConfig.autoChunk) {
            scheduler.runTaskTimer(this, scheduledTask -> {
                for (World world : LaggRemover.this.getServer().getWorlds()) {
                    if (world.getPlayers().size() == 0) {
                        for (Chunk chunk : world.getLoadedChunks()) {
                            if (!world.unloadChunkRequest(chunk.getX(), chunk.getZ())) {
                                getLogger().info("Failed to unload chuck (" + chunk.getX() + ", " + chunk.getZ() + ")");
                            }
                        }
                    }
                }
            }, 200L, 200L);
        }

        if (LRConfig.autoLagRemoval) {
            autoLagRemovalLoop();
        }
        modDir = new File(getDataFolder(), "Modules");
        if (!modDir.exists()) {
            modDir.mkdirs();
        }
        for (File f : Objects.requireNonNull(modDir.listFiles())) {
            if (!f.isDirectory() && f.getName().endsWith(".jar")) {
                try (ZipFile zipFile = new ZipFile(f)) {
                    URL[] classes = { f.toURI().toURL() };
                    URLClassLoader loader = new URLClassLoader(classes, LaggRemover.class.getClassLoader());
                    YamlConfiguration c = new YamlConfiguration();
                    c.load(new InputStreamReader(zipFile.getInputStream(zipFile.getEntry("module.yml"))));
                    String name = c.getString("name");
                    String version = c.getString("version");
                    String author = c.getString("author");
                    getLogger().info("Loading module \"" + name + "-" + version + "\" created by \"" + author + "\"...");
                    Class<?> plugin = Class.forName(c.getString("main"), true, loader);
                    Module module = (Module) plugin.getDeclaredConstructor().newInstance();
                    loaded.put(module, new String[]{name, version, author});
                    module.onEnable();
                } catch (IOException | InvalidConfigurationException | ReflectiveOperationException exception) {
                    getLogger().info("LaggRemover located an invalid module named \"" + f.getName() + "\"");
                }
            }
        }
        getLogger().info("Loaded " + loaded.size() + " module(s)");
        if (LRConfig.isAIActive) {
            getLogger().info("The LaggRemover AI is now active!");
        }
        getLogger().info("LaggRemover has been enabled!");
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(instance);
        for (Module module : loaded.keySet()) {
            module.onDisable();
        }
        instance = null;
        getLogger().info("LaggRemover has been disabled!");
    }

    public static String[] getModulesList() {
        StringBuilder sb = new StringBuilder();
        for (String[] s : loaded.values()) {
            sb.append(s[0]);
            sb.append(", ");
        }
        String sbs = sb.toString();
        if (!sbs.equals("")) {
            sbs = sbs.substring(0, sbs.length() - 2);
        }
        return new String[]{ sbs, Integer.toString(loaded.size()) };
    }

    public static String[] getProtocolList() {
        StringBuilder sb = new StringBuilder();
        Collection<LRProtocol> protocols = Protocol.getProtocols();
        for (LRProtocol p : protocols) {
            sb.append(p.id());
            sb.append(", ");
        }
        String sbs = sb.toString();
        if (!sbs.equals("")) {
            sbs = sbs.substring(0, sbs.length() - 2);
        }
        return new String[]{sbs, Integer.toString(protocols.size())};
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        Entity ent = e.getEntity();
        if (!ent.hasMetadata("NPC") && LRConfig.thinMobs && ent.getLocation().getChunk().getEntities().length > LRConfig.thinAt) {
            e.setCancelled(true);
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;
        if (cmd.getName().equalsIgnoreCase("lr")) {
            if (args.length == 0) {
                Help.send(player, 1);
                return true;
            } else if (!LRCommand.onCommand(player, args)) {
                for (Module m : loaded.keySet()) {
                    if (m.onCommand(sender, label, args)) {
                        return true;
                    }
                }
                Help.sendMsg(player, "§cCommand not found! Use /lr help for a list of commands.", true);
                return true;
            } else {
                return true;
            }
        }
        return true;
    }

    public static void broadcast(String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
        }
    }

    public static String[] getData(Module m) {
        return loaded.get(m);
    }

    public void autoLagRemovalLoop() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            for (LRProtocol p : LRConfig.periodic_protocols.keySet()) {
                DoubleVar<Object[], Boolean> dat = LRConfig.periodic_protocols.get(p);
                if (dat.getVar2()) {
                    Protocol.rund(p, dat.getVar1(), new DelayedLRProtocolResult() { // from class: drew6017.lr.main.LaggRemover.3.1
                        @Override
                        public void receive(LRProtocolResult result) {
                        }
                    });
                } else {
                    p.run(dat.getVar1());
                }
            }
            LaggRemover.this.autoLagRemovalLoop();
        }, 1200L * LRConfig.autoLagRemovalTime);
    }
}
