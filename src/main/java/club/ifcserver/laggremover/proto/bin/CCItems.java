package club.ifcserver.laggremover.proto.bin;

import club.ifcserver.laggremover.api.aparser.ProtoParse;
import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.api.proto.LRProtocolResult;
import club.ifcserver.laggremover.api.proto.Protocol;
import club.ifcserver.laggremover.api.proto.ProtocolCategory;
import club.ifcserver.laggremover.api.proto.help.HelpFormatter;
import club.ifcserver.laggremover.util.Counter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class CCItems implements LRProtocol {
    public static Counter counter;
    private static final String help = new HelpFormatter().set(HelpFormatter.HelpFormatterType.DESCRIPTION, "§eRemoves items from all worlds, selected worlds, or selected chunks.").set(HelpFormatter.HelpFormatterType.CATEGORIES, "§eCPU, RAM, and NETWORK").set(HelpFormatter.HelpFormatterType.ARGUMENTS, HelpFormatter.generateArgs(new CCItems().getProtocolParser())).set(HelpFormatter.HelpFormatterType.RETURNS, "§e{0: <(int)CCed>}").make();

    @Override
    public void init() {
        counter = Protocol.getCounter(this);
    }

    @Override
    public String id() {
        return "cc_items";
    }

    @Override
    public String help() {
        return help;
    }

    @Override
    public ProtocolCategory[] category() {
        return new ProtocolCategory[]{ProtocolCategory.CPU, ProtocolCategory.RAM, ProtocolCategory.NETWORK};
    }

    @Override
    public LRProtocolResult run(Object[] args) {
        LRProtocolResult result;
        boolean count = (Boolean) args[0];
        if (args.length == 1) {
            final int i = clearItems(count);
            result = new LRProtocolResult(this) {
                @Override
                public Object[] getData() {
                    return new Object[]{Integer.valueOf(i)};
                }
            };
        } else if (args.length == 2) {
            final int i3 = args[1] instanceof World ? clearItems(((World) args[1]).getEntities(), count) : clearItems(Arrays.asList(((Chunk) args[1]).getEntities()), count);
            result = new LRProtocolResult(this) {
                @Override
                public Object[] getData() {
                    return new Object[]{i3};
                }
            };
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public ProtoParse getProtocolParser() {
        return new ProtoParse() {
            @Override
            public HashMap<String, ProtoParseData> getKeysToClass() {
                HashMap<String, ProtoParseData> k = new HashMap<>();
                k.put("Count", new ProtoParseData(ProtoParseKeywords.BOOLEAN, 0));
                k.put("World", new ProtoParseData(ProtoParseKeywords.WORLD, 1));
                k.put("Chunk", new ProtoParseData(ProtoParseKeywords.CHUNK, 1));
                return k;
            }
        };
    }

    private int clearItems(List<Entity> ents, boolean count) {
        int i = 0;
        for (Entity e : ents) {
            if (e.getType().equals(EntityType.DROPPED_ITEM)) {
                if (!count) {
                    e.remove();
                }
                i++;
            }
        }
        return i;
    }

    private int clearItems(boolean count) {
        int i = 0;
        for (World w : Bukkit.getWorlds()) {
            i += clearItems(w.getEntities(), count);
        }
        return i;
    }
}
