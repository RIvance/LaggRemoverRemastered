package club.ifcserver.laggremover.api.vcon.v;

import club.ifcserver.laggremover.api.vcon.VersionAdapter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/vcon/v/v1_8_R3.class */
public class v1_8_R3 implements VersionAdapter {
    @Override // drew6017.lr.api.vcon.VersionAdapter
    public int getPing(Player p) {
        return ((CraftPlayer) p).getHandle().ping;
    }
}
