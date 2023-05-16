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

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/proto/bin/LRGC.class */
public class LRGC implements LRProtocol {
    public static Counter counter;
    private static String help = new HelpFormatter().set(HelpFormatter.HelpFormatterType.DESCRIPTION, "§eReduces RAM usage by removing unneeded items stored in RAM.").set(HelpFormatter.HelpFormatterType.CATEGORIES, "§eRAM").set(HelpFormatter.HelpFormatterType.ARGUMENTS, HelpFormatter.generateArgs(new LRGC().getPP())).set(HelpFormatter.HelpFormatterType.RETURNS, "§e{0: <(long)ramMB>}").make();

    @Override // drew6017.lr.api.proto.LRProtocol
    public void init() {
        counter = Protocol.getCounter(this);
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public String id() {
        return "lr_gc";
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public String help() {
        return help;
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public ProtocolCategory[] category() {
        return new ProtocolCategory[]{ProtocolCategory.RAM};
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public LRProtocolResult run(Object[] args) {
        Runtime r = Runtime.getRuntime();
        long Lused = ((r.totalMemory() - r.freeMemory()) / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
        System.gc();
        final long used = Lused - (((r.totalMemory() - r.freeMemory()) / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE);
        return new LRProtocolResult(this) { // from class: drew6017.lr.proto.bin.LRGC.1
            @Override // drew6017.lr.api.proto.LRProtocolResult
            public Object[] getData() {
                return new Object[]{Long.valueOf(used)};
            }
        };
    }

    @Override // drew6017.lr.api.proto.LRProtocol
    public ProtoParse getPP() {
        return new ProtoParse() { // from class: drew6017.lr.proto.bin.LRGC.2
            @Override // drew6017.lr.api.aparser.ProtoParse
            public HashMap<String, ProtoParseData> getKeysToClass() {
                return new HashMap<>();
            }
        };
    }
}
