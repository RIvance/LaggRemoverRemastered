package club.ifcserver.laggremover.api.aparser;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public abstract class ProtoParse {

    public static abstract class KeywordParser {
        public abstract Object parse(String str);
    }

    public abstract HashMap<String, ProtoParseData> getKeysToClass();

    public static class ProtoParseData {
        private final ProtoParseKeywords clazz;
        private final int index;

        public ProtoParseData(ProtoParseKeywords clazz, int index) {
            this.clazz = clazz;
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }

        public ProtoParseKeywords getClazz() {
            return this.clazz;
        }
    }

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/aparser/ProtoParse$ProtoParseKeywords.class */
    public enum ProtoParseKeywords {
        BOOLEAN("Boolean", new KeywordParser() {
            @Override
            public Object parse(String data) {
                return Boolean.parseBoolean(data);
            }
        }),
        INTEGER("Integer", new KeywordParser() {
            @Override
            public Object parse(String data) {
                return Integer.parseInt(data);
            }
        }),
        STRING("String", new KeywordParser() {
            @Override
            public Object parse(String data) {
                return data;
            }
        }),
        WORLD("World", new KeywordParser() {
            @Override
            public Object parse(String data) {
                return Bukkit.getWorld(data);
            }
        }),
        CHUNK("Chunk", new KeywordParser() {
            @Override
            public Object parse(String data) {
                String[] pos = data.split(",");
                World world = Bukkit.getWorld(pos[0]);
                if (world == null) {
                    return null;
                }
                return world.getChunkAt(Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
            }
        }),
        ENTITY_TYPE_ARRAY("EntityType[]", new KeywordParser() {
            @Override
            public Object parse(String data) {
                String[] s = data.split(",");
                EntityType[] entityTypes = new EntityType[s.length];
                for (int i = 0; i < entityTypes.length; i++) {
                    entityTypes[i] = EntityType.valueOf(s[i]);
                }
                return entityTypes;
            }
        });

        private final KeywordParser parser;
        private final String name;

        ProtoParseKeywords(String name, KeywordParser parser) {
            this.parser = parser;
            this.name = name;
        }

        public KeywordParser getParser() {
            return this.parser;
        }

        public String getProperName() {
            return this.name;
        }
    }
}
