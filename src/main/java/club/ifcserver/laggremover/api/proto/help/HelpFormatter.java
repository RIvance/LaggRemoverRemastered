package club.ifcserver.laggremover.api.proto.help;

import club.ifcserver.laggremover.api.aparser.ProtoParse;
import com.avaje.ebean.validation.NotNull;
import club.ifcserver.laggremover.util.DoubleVar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/proto/help/HelpFormatter.class */
public class HelpFormatter {
    private HashMap<HelpFormatterType, String> parts = new HashMap<>();

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/proto/help/HelpFormatter$HelpFormatterType.class */
    public enum HelpFormatterType {
        DESCRIPTION,
        CATEGORIES,
        ARGUMENTS,
        RETURNS
    }

    public HelpFormatter set(HelpFormatterType type, String var) {
        if (!type.equals(HelpFormatterType.RETURNS)) {
            var = var + "\n";
        }
        this.parts.put(type, var);
        return this;
    }

    @NotNull
    public String mk() throws HelpFormatException {
        if (this.parts.size() != 4) {
            throw new HelpFormatException("You must set all help fields in order to compile help information.");
        }
        return "§aDescription: " + this.parts.get(HelpFormatterType.DESCRIPTION) + "§aCategory(s): " + this.parts.get(HelpFormatterType.CATEGORIES) + "§aArgument(s): " + this.parts.get(HelpFormatterType.ARGUMENTS) + "§aReturn(s): " + this.parts.get(HelpFormatterType.RETURNS);
    }

    public String make() {
        try {
            return mk();
        } catch (HelpFormatException e) {
            return null;
        }
    }

    public String getInfo(HelpFormatterType type) {
        return this.parts.get(type);
    }

    @NotNull
    public static String generateArgs(ProtoParse p) {
        HashMap<String, ProtoParse.ProtoParseData> var = p.getKeysToClass();
        StringBuilder sb = new StringBuilder();
        HashMap<Integer, List<DoubleVar<String, ProtoParse.ProtoParseData>>> var1 = new HashMap<>();
        for (String s : var.keySet()) {
            ProtoParse.ProtoParseData da = var.get(s);
            List<DoubleVar<String, ProtoParse.ProtoParseData>> l = var1.containsKey(Integer.valueOf(da.getIndex())) ? var1.get(Integer.valueOf(da.getIndex())) : new ArrayList<>();
            l.add(new DoubleVar<>(s, da));
            var1.put(Integer.valueOf(da.getIndex()), l);
        }
        sb.append("§e{");
        int v1length = var1.size();
        for (Integer num : var1.keySet()) {
            int i = num.intValue();
            sb.append(i).append(": ");
            List<DoubleVar<String, ProtoParse.ProtoParseData>> list = var1.get(Integer.valueOf(i));
            int size = list.size();
            for (int ii = 0; ii < size; ii++) {
                DoubleVar<String, ProtoParse.ProtoParseData> vag = list.get(ii);
                sb.append(vag.getVar2().getClazz().getProperName()).append("(").append(vag.getVar1()).append(")");
                if (ii + 1 < size) {
                    sb.append(" | ");
                }
            }
            if (i + 1 != v1length) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/proto/help/HelpFormatter$HelpFormatException.class */
    public class HelpFormatException extends Exception {
        private String msg;

        HelpFormatException(String msg) {
            this.msg = msg;
        }

        @Override // java.lang.Throwable
        public String toString() {
            return this.msg;
        }
    }
}
