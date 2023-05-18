package club.ifcserver.laggremover.inf;

import club.ifcserver.laggremover.api.proto.LRProtocolResult;
import club.ifcserver.laggremover.main.LaggRemover;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/inf/Help.class */
public class Help {
    private static final List<String> commandsHelp = new ArrayList<>();

    public static void init() {
        commandsHelp.add("§e /lr help(h) <num>:§7 Lists all available commands.");
        commandsHelp.add("§e /lr master(m) <world:none>:§7 Displays a lot of information about the world and server you are in.");
        commandsHelp.add("§e /lr tps:§7 Displays the servers TPS.");
        commandsHelp.add("§e /lr gc:§7 Frees up RAM on your server by removing unnecessary objects stored by the system.");
        commandsHelp.add("§e /lr ram:§7 Lists data about current RAM usage.");
        commandsHelp.add("§e /lr protocol(pr) <options>:§7 Allows for the manual viewing/running/etc of protocols.");
        commandsHelp.add("§e /lr clear(c):§7 Removes various entities/items.");
        commandsHelp.add("§e /lr count(ct):§7 Counts various entities/items.");
        commandsHelp.add("§e /lr world(w) <world>:§7 Displays stats about the world requested.");
        commandsHelp.add("§e /lr unload(u) <world>:§7 Unloads all chunks in the world specified.");
        commandsHelp.add("§e /lr modules(mo):§7 Lists all modules that are currently loaded by LaggRemover.");
        commandsHelp.add("§e /lr info(i):§7 Displays info about LaggRemover. Author, copyright, etc.");
        commandsHelp.add("§e /lr ping(p) <player:none>:§7 Shows a players ping.");
    }

    public static void send(Player p, int pageNum) {
        List<List<String>> pages = getPages();
        int maxPages = pages.size();
        if (pageNum > maxPages) {
            sendMsg(p, "§cHelp page #" + pageNum + " does not exist.", true);
            return;
        }
        sendMsg(p, "§3---------========== Help Page (§b" + pageNum + "§3/§b" + Integer.toString(maxPages) + "§3) ==========---------", false);
        for (String s : pages.get(pageNum - 1)) {
            sendMsg(p, s, false);
        }
    }

    public static void sendMsg(Player p, String msg, boolean pre) {
        if (p == null) {
            LaggRemover.instance.getLogger().info(ChatColor.stripColor(msg));
            return;
        }
        if (pre) {
            msg = LaggRemover.prefix + msg;
        }
        p.sendMessage(msg);
    }

    public static void addCommandH(String cmd) {
        commandsHelp.add(cmd);
    }

    private static List<List<String>> getPages() {
        List<List<String>> h = new ArrayList<>();
        List<String> c = new ArrayList<>();
        for (String s : commandsHelp) {
            if (c.size() == 6) {
                h.add(c);
                c = new ArrayList<>();
            }
            c.add(s);
        }
        if (!c.isEmpty()) {
            h.add(c);
        }
        return h;
    }

    public static void sendProtocolResultInfo(Player p, LRProtocolResult r) {
        Object[] data;
        StringBuilder s = new StringBuilder();
        s.append("{");
        int i = 0;
        for (Object o : r.getData()) {
            s.append(i).append(": ").append(o).append(", ");
            i++;
        }
        String fin = s.toString();
        sendMsg(p, "§eProtocol: " + r.getSuper().id() + " | §7" + (s.substring(0, fin.length() - 2) + "}"), true);
    }
}
