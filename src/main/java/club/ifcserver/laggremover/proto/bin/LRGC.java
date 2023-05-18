package club.ifcserver.laggremover.proto.bin;

import club.ifcserver.laggremover.api.aparser.ProtoParse;
import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.api.proto.LRProtocolResult;
import club.ifcserver.laggremover.api.proto.Protocol;
import club.ifcserver.laggremover.api.proto.ProtocolCategory;
import club.ifcserver.laggremover.api.proto.help.HelpFormatter;
import club.ifcserver.laggremover.main.LaggRemover;
import club.ifcserver.laggremover.util.Counter;
import java.util.HashMap;

public class LRGC implements LRProtocol {
    public static Counter counter;
    private static String help = new HelpFormatter().set(HelpFormatter.HelpFormatterType.DESCRIPTION, "§eReduces RAM usage by removing unneeded items stored in RAM.").set(HelpFormatter.HelpFormatterType.CATEGORIES, "§eRAM").set(HelpFormatter.HelpFormatterType.ARGUMENTS, HelpFormatter.generateArgs(new LRGC().getProtocolParser())).set(HelpFormatter.HelpFormatterType.RETURNS, "§e{0: <(long)ramMB>}").make();

    @Override
    public void init() {
        counter = Protocol.getCounter(this);
    }

    @Override
    public String id() {
        return "lr_gc";
    }

    @Override
    public String help() {
        return help;
    }

    @Override
    public ProtocolCategory[] category() {
        return new ProtocolCategory[]{ProtocolCategory.RAM};
    }

    @Override
    public LRProtocolResult run(Object[] args) {
        Runtime r = Runtime.getRuntime();
        long Lused = ((r.totalMemory() - r.freeMemory()) / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
        System.gc();
        final long used = Lused - (((r.totalMemory() - r.freeMemory()) / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE);
        return new LRProtocolResult(this) {
            @Override
            public Object[] getData() {
                return new Object[]{Long.valueOf(used)};
            }
        };
    }

    @Override
    public ProtoParse getProtocolParser() {
        return new ProtoParse() {
            @Override
            public HashMap<String, ProtoParseData> getKeysToClass() {
                return new HashMap<>();
            }
        };
    }
}
