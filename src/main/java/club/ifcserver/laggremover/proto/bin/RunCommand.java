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

public class RunCommand implements LRProtocol {
    public static Counter counter;
    private static final String help = new HelpFormatter().set(HelpFormatter.HelpFormatterType.DESCRIPTION, "§eRuns a command that could either be from LaggRemover or another plugin.").set(HelpFormatter.HelpFormatterType.CATEGORIES, "§eUNKNOWN").set(HelpFormatter.HelpFormatterType.ARGUMENTS, HelpFormatter.generateArgs(new RunCommand().getProtocolParser())).set(HelpFormatter.HelpFormatterType.RETURNS, "§e{0: <none>}").make();

    @Override
    public void init() {
        counter = Protocol.getCounter(this);
    }

    @Override
    public String id() {
        return "run_c";
    }

    @Override
    public String help() {
        return help;
    }

    @Override
    public ProtocolCategory[] category() {
        return new ProtocolCategory[]{ProtocolCategory.UNKNOWN};
    }

    @Override
    public LRProtocolResult run(Object[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String) args[0]);
        return new LRProtocolResult(this) {
            @Override
            public Object[] getData() {
                return new Object[0];
            }
        };
    }

    @Override
    public ProtoParse getProtocolParser() {
        return new ProtoParse() {
            @Override
            public HashMap<String, ProtoParseData> getKeysToClass() {
                HashMap<String, ProtoParseData> k = new HashMap<>();
                k.put("Command", new ProtoParseData(ProtoParseKeywords.STRING, 0));
                return k;
            }
        };
    }
}
