package club.ifcserver.laggremover.api.aparser;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/aparser/ProtoParse.class */
public abstract class ProtoParse {

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/aparser/ProtoParse$KeywordParser.class */
    public static abstract class KeywordParser {
        public abstract Object parse(String str);
    }

    public abstract HashMap<String, ProtoParseData> getKeysToClass();

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/aparser/ProtoParse$ProtoParseData.class */
    public static class ProtoParseData {
        private ProtoParseKeywords clazz;
        private int index;

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
        BOOLEAN("Boolean", new KeywordParser() { // from class: drew6017.lr.api.aparser.ProtoParse.ProtoParseKeywords.1
            @Override // drew6017.lr.api.aparser.ProtoParse.KeywordParser
            public Object parse(String data) {
                return Boolean.valueOf(Boolean.parseBoolean(data));
            }
        }),
        INTEGER("Integer", new KeywordParser() { // from class: drew6017.lr.api.aparser.ProtoParse.ProtoParseKeywords.2
            @Override // drew6017.lr.api.aparser.ProtoParse.KeywordParser
            public Object parse(String data) {
                return Integer.valueOf(Integer.parseInt(data));
            }
        }),
        STRING("String", new KeywordParser() { // from class: drew6017.lr.api.aparser.ProtoParse.ProtoParseKeywords.3
            @Override // drew6017.lr.api.aparser.ProtoParse.KeywordParser
            public Object parse(String data) {
                return data;
            }
        }),
        WORLD("World", new KeywordParser() { // from class: drew6017.lr.api.aparser.ProtoParse.ProtoParseKeywords.4
            @Override // drew6017.lr.api.aparser.ProtoParse.KeywordParser
            public Object parse(String data) {
                return Bukkit.getWorld(data);
            }
        }),
        CHUNK("Chunk", new KeywordParser() { // from class: drew6017.lr.api.aparser.ProtoParse.ProtoParseKeywords.5
            @Override // drew6017.lr.api.aparser.ProtoParse.KeywordParser
            public Object parse(String data) {
                String[] s = data.split(",");
                World w = Bukkit.getWorld(s[0]);
                if (w == null) {
                    return null;
                }
                return w.getChunkAt(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
            }
        }),
        ENTITY_TYPE_ARRAY("EntityType[]", new KeywordParser() { // from class: drew6017.lr.api.aparser.ProtoParse.ProtoParseKeywords.6
            @Override // drew6017.lr.api.aparser.ProtoParse.KeywordParser
            public Object parse(String data) {
                String[] s = data.split(",");
                EntityType[] ents = new EntityType[s.length];
                for (int i = 0; i < ents.length; i++) {
                    ents[i] = EntityType.valueOf(s[i]);
                }
                return ents;
            }
        });
        
        private KeywordParser p;
        private String s;

        ProtoParseKeywords(String s, KeywordParser p) {
            this.p = p;
            this.s = s;
        }

        public KeywordParser getParser() {
            return this.p;
        }

        public String getProperName() {
            return this.s;
        }
    }
}
