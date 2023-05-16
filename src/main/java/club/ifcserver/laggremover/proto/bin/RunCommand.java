package club.ifcserver.laggremover.proto.bin;

import club.ifcserver.laggremover.api.aparser.ProtoParse;
import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.api.proto.LRProtocolResult;
import club.ifcserver.laggremover.api.proto.Protocol;
import club.ifcserver.laggremover.api.proto.ProtocolCategory;
import club.ifcserver.laggremover.api.proto.help.HelpFormatter;
import club.ifcserver.laggremover.util.Counter;
import java.util.HashMap;
import org.bukkit.Bukkit;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/proto/bin/RunCommand.class */
public class RunCommand implements LRProtocol {
    public static Counter counter;
    private static String help = new HelpFormatter().set(HelpFormatter.HelpFormatterType.DESCRIPTION, "§eRuns a command that could either be from LaggRemover or another plugin.").set(HelpFormatter.HelpFormatterType.CATEGORIES, "§eUNKNOWN").set(HelpFormatter.HelpFormatterType.ARGUMENTS, HelpFormatter.generateArgs(new RunCommand().getPP())).set(HelpFormatter.HelpFormatterType.RETURNS, "§e{0: <none>}").make();

    @Override // drew6017.lr.api.proto.LRProtocol
    public void init() {
        counter = Protocol.getCounter(this);
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public String id() {
        return "run_c";
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public String help() {
        return help;
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public ProtocolCategory[] category() {
        return new ProtocolCategory[]{ProtocolCategory.UNKNOWN};
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public LRProtocolResult run(Object[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String) args[0]);
        return new LRProtocolResult(this) { // from class: drew6017.lr.proto.bin.RunCommand.1
            @Override // drew6017.lr.api.proto.LRProtocolResult
            public Object[] getData() {
                return new Object[0];
            }
        };
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public ProtoParse getPP() {
        return new ProtoParse() { // from class: drew6017.lr.proto.bin.RunCommand.2
            @Override // drew6017.lr.api.aparser.ProtoParse
            public HashMap<String, ProtoParseData> getKeysToClass() {
                HashMap<String, ProtoParseData> k = new HashMap<>();
                k.put("Command", new ProtoParseData(ProtoParseKeywords.STRING, 0));
                return k;
            }
        };
    }
}
