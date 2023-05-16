package club.ifcserver.laggremover.api.proto;

import club.ifcserver.laggremover.main.LaggRemover;
import club.ifcserver.laggremover.proto.bin.CCEntities;
import club.ifcserver.laggremover.proto.bin.CCItems;
import club.ifcserver.laggremover.proto.bin.LRGC;
import club.ifcserver.laggremover.proto.bin.RunCommand;
import club.ifcserver.laggremover.util.Counter;
import club.ifcserver.laggremover.util.LRConfig;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/proto/Protocol.class */
public class Protocol {
    private static HashMap<String, LRProtocol> protocols;

    public static int init() {
        protocols = new HashMap<>();
        register(new CCEntities(), new CCItems(), new LRGC(), new RunCommand());
        return protocols.size();
    }

    public static void register(LRProtocol... pros) {
        for (LRProtocol p : pros) {
            register(p);
        }
    }

    public static void register(LRProtocol p) {
        p.init();
        protocols.put(p.id(), p);
    }

    public static Collection<LRProtocol> getProtocols() {
        return protocols.values();
    }

    public static boolean hasProtocol(String id) {
        return protocols.containsKey(id);
    }

    public static LRProtocol getProtocol(String name) {
        return protocols.get(name);
    }

    public static LRProtocolResult run(String p, Object[] args) {
        return protocols.get(p).run(args);
    }

    public static LRProtocolResult run(LRProtocol p, Object[] args) {
        return p.run(args);
    }

    public static Counter getCounter(LRProtocol p) {
        return getCounter(p.id());
    }

    public static Counter getCounter(String p) {
        String var = null;
        HashMap<Long, Counter.CountAction> actions = new HashMap<>();
        List<String> args = LaggRemover.lr.getConfig().getStringList("protocol_warnings." + p + ".stages");
        for (int i = 0; i < args.size(); i++) {
            final String[] a = args.get(i).replaceAll("&", "§").replaceAll("%PREFIX%", LaggRemover.prefix).split(":");
            if (a[0].equalsIgnoreCase("f")) {
                var = a[1];
            } else {
                actions.put(Long.valueOf(Long.parseLong(a[0])), new Counter.CountAction(Long.parseLong(a[0])) { // from class: drew6017.lr.api.proto.Protocol.1
                    @Override // drew6017.lr.util.Counter.CountAction
                    public void onTrigger() {
                        LaggRemover.broadcast(a[1]);
                    }
                });
            }
        }
        final String var2 = var;
        Counter counter = new Counter(LaggRemover.lr.getConfig().getLong("protocol_warnings." + p + ".time")) { // from class: drew6017.lr.api.proto.Protocol.2
            @Override // drew6017.lr.util.Counter
            public void onFinish() {
                if (var2 != null) {
                    LaggRemover.broadcast(var2);
                }
            }
        };
        counter.setActions(actions);
        return counter;
    }

    public static void rund(LRProtocol p, Object[] args, DelayedLRProtocolResult res) {
        Counter c = LRConfig.counters.get(p);
        if (c.start()) {
            delayLoop(c, res, p, args);
        }
    }

    public static void rund(String p, Object[] args, DelayedLRProtocolResult res) {
        rund(getProtocol(p), args, res);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void delayLoop(final Counter c, final DelayedLRProtocolResult res, final LRProtocol p, final Object[] args) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(LaggRemover.lr, new Runnable() { // from class: drew6017.lr.api.proto.Protocol.3
            @Override // java.lang.Runnable
            public void run() {
                if (Counter.this.isActive()) {
                    Protocol.delayLoop(Counter.this, res, p, args);
                } else {
                    res.receive(Protocol.run(p, args));
                }
            }
        }, 1L);
    }
}
