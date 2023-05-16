package club.ifcserver.laggremover.api;

import club.ifcserver.laggremover.inf.Help;
import club.ifcserver.laggremover.main.LaggRemover;
import java.io.File;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/Module.class */
public class Module {
    public static void registerHelp(String com, String help) {
        Help.addCommandH("§e " + com + ":§7 " + help);
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public final File getDataFolder() {
        return new File(LaggRemover.modDir, LaggRemover.getData(this)[0]);
    }

    public final Logger getLogger() {
        return LaggRemover.lr.getLogger();
    }

    public boolean onCommand(CommandSender sender, String label, String[] args) {
        return false;
    }
}
