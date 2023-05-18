package club.ifcserver.laggremover.main;

import club.ifcserver.laggremover.api.proto.DelayedLRProtocolResult;
import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.api.proto.LRProtocolResult;
import club.ifcserver.laggremover.api.proto.Protocol;
import club.ifcserver.laggremover.inf.Help;
import club.ifcserver.laggremover.proto.bin.CCEntities;
import club.ifcserver.laggremover.util.DoubleVar;
import club.ifcserver.laggremover.util.LRConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.WorldInitEvent;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/Events.class */
public class Events implements Listener {
    private static final List<UUID> useLocationLagRemoval = new ArrayList<>();
    private static final List<UUID> chatDelay = new ArrayList<>();
    private static boolean canSLDRun = true;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldLoad(WorldInitEvent e) {
        if (LRConfig.noSpawnChunks) {
            e.getWorld().setKeepSpawnInMemory(false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (!p.hasPermission("lr.nochatdelay") && LRConfig.chatDelay > 0) {
            if (chatDelay.contains(uuid)) {
                e.setCancelled(true);
                Help.sendMsg(p, "§cPlease slow down your chat.", true);
                return;
            }
            chatDelayCooldown(uuid);
        }
        if (LRConfig.doRelativeAction && !useLocationLagRemoval.contains(uuid) && e.getMessage().toLowerCase().contains("lag")) {
            if (canSLDRun && LRConfig.isAIActive) {
                smartLagDetection();
            }
            final List<Entity> ents = p.getNearbyEntities(LRConfig.localLagRadius, LRConfig.localLagRadius, LRConfig.localLagRadius);
            if (ents.size() < LRConfig.localLagTriggered) {
                return;
            }
            cooldown(uuid);
            int entsLeng = (int) (ents.size() * LRConfig.localThinPercent);
            int toRemove = ents.size() - entsLeng;
            for (int i = 0; i < toRemove && !ents.isEmpty(); i++) {
                ents.remove(0);
            }
            p.sendMessage("§eEntities around you are being removed because we detected you were lagging.");
            if (LRConfig.doOnlyItemsForRelative) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(LaggRemover.lr, new Runnable() { // from class: drew6017.lr.main.Events.1
                    @Override // java.lang.Runnable
                    public void run() {
                        for (Entity en : ents) {
                            if (en.getType().equals(EntityType.DROPPED_ITEM)) {
                                en.remove();
                            }
                        }
                    }
                }, 1L);
            } else if (LRConfig.dontDoFriendlyMobsForRelative) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(LaggRemover.lr, new Runnable() { // from class: drew6017.lr.main.Events.2
                    @Override // java.lang.Runnable
                    public void run() {
                        CCEntities.clearEntities(ents, false, CCEntities.hostile);
                        for (Entity en : ents) {
                            if (en.getType().equals(EntityType.DROPPED_ITEM)) {
                                en.remove();
                            }
                        }
                    }
                }, 1L);
            } else {
                // from class: drew6017.lr.main.Events.3
                // java.lang.Runnable
                Bukkit.getScheduler().scheduleSyncDelayedTask(LaggRemover.lr, () -> {
                    CCEntities.clearEntities(ents, false, CCEntities.hostile);
                    CCEntities.clearEntities(ents, false, CCEntities.peaceful);
                    for (Entity en : ents) {
                        if (en.getType().equals(EntityType.DROPPED_ITEM)) {
                            en.remove();
                        }
                    }
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (!LRConfig.thinMobs || e.getLocation().getChunk().getEntities().length <= LRConfig.thinAt) {
            return;
        }
        e.setCancelled(true);
    }

    private void cooldown(final UUID u) {
        useLocationLagRemoval.add(u);
        // from class: drew6017.lr.main.Events.4
        Bukkit.getScheduler().scheduleSyncDelayedTask(
            LaggRemover.lr,
            () -> Events.useLocationLagRemoval.remove(u),
            20L * LRConfig.localLagRemovalCooldown
        );
    }

    private void smartAIcooldown() {
        canSLDRun = false;
        // from class: drew6017.lr.main.Events.5
        Bukkit.getScheduler().scheduleSyncDelayedTask(LaggRemover.lr, () -> {
            boolean unused = Events.canSLDRun = true;
        }, 1200 * LRConfig.smartaicooldown);
    }

    private void chatDelayCooldown(final UUID u) {
        chatDelay.add(u);
        // from class: drew6017.lr.main.Events.6
        Bukkit.getScheduler().scheduleSyncDelayedTask(
            LaggRemover.lr,
            () -> Events.chatDelay.remove(u),
            LRConfig.chatDelay
        );
    }

    private void smartLagDetection() {
        smartAIcooldown();
        Runtime r = Runtime.getRuntime();
        long ram_used = ((r.totalMemory() - r.freeMemory()) / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
        long ram_total = (r.maxMemory() / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
        if (ram_total - ram_used < LRConfig.ramConstant) {
            for (LRProtocol p : LRConfig.ram_protocols.keySet()) {
                DoubleVar<Object[], Boolean> dat = LRConfig.ram_protocols.get(p);
                if (dat.getVar2()) {
                    Protocol.rund(p, dat.getVar1(), new DelayedLRProtocolResult() { // from class: drew6017.lr.main.Events.7
                        @Override // drew6017.lr.api.proto.DelayedLRProtocolResult
                        public void receive(LRProtocolResult result) {
                        }
                    });
                } else {
                    p.run(dat.getVar1());
                }
            }
        } else if (TPS.getTPS() < LRConfig.lagConstant) {
            for (LRProtocol p2 : LRConfig.tps_protocols.keySet()) {
                DoubleVar<Object[], Boolean> dat2 = LRConfig.tps_protocols.get(p2);
                if (dat2.getVar2()) {
                    Protocol.rund(p2, dat2.getVar1(), new DelayedLRProtocolResult() { // from class: drew6017.lr.main.Events.8
                        @Override // drew6017.lr.api.proto.DelayedLRProtocolResult
                        public void receive(LRProtocolResult result) {
                        }
                    });
                } else {
                    p2.run(dat2.getVar1());
                }
            }
        }
    }
}
