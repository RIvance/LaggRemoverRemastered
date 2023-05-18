package club.ifcserver.laggremover.api.proto.help;

import club.ifcserver.laggremover.api.aparser.ProtoParse;
import club.ifcserver.laggremover.util.DoubleVar;
import org.jetbrains.annotations.NotNull;

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
    public String make0() throws HelpFormatException {
        if (this.parts.size() != 4) {
            throw new HelpFormatException("You must set all help fields in order to compile help information.");
        }
        return "§aDescription: " + this.parts.get(HelpFormatterType.DESCRIPTION) + "§aCategory(s): " + this.parts.get(HelpFormatterType.CATEGORIES) + "§aArgument(s): " + this.parts.get(HelpFormatterType.ARGUMENTS) + "§aReturn(s): " + this.parts.get(HelpFormatterType.RETURNS);
    }

    public String make() {
        try {
            return make0();
        } catch (HelpFormatException e) {
            return null;
        }
    }

    public String getInfo(HelpFormatterType type) {
        return this.parts.get(type);
    }

    @NotNull
    public static String generateArgs(ProtoParse protoParse) {
        HashMap<String, ProtoParse.ProtoParseData> var = protoParse.getKeysToClass();
        StringBuilder sb = new StringBuilder();
        HashMap<Integer, List<DoubleVar<String, ProtoParse.ProtoParseData>>> var1 = new HashMap<>();
        for (String s : var.keySet()) {
            ProtoParse.ProtoParseData protoParseData = var.get(s);
            List<DoubleVar<String, ProtoParse.ProtoParseData>> list = var1.containsKey(Integer.valueOf(protoParseData.getIndex())) ? var1.get(Integer.valueOf(protoParseData.getIndex())) : new ArrayList<>();
            list.add(new DoubleVar<>(s, protoParseData));
            var1.put(protoParseData.getIndex(), list);
        }
        sb.append("§e{");
        int v1length = var1.size();
        for (Integer num : var1.keySet()) {
            int i = num;
            sb.append(i).append(": ");
            List<DoubleVar<String, ProtoParse.ProtoParseData>> list = var1.get(i);
            int size = list.size();
            for (int j = 0; j < size; j++) {
                DoubleVar<String, ProtoParse.ProtoParseData> vag = list.get(j);
                sb.append(vag.getVar2().getClazz().getProperName()).append("(").append(vag.getVar1()).append(")");
                if (j + 1 < size) {
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

    public static class HelpFormatException extends Exception {
        private final String message;

        HelpFormatException(String message) {
            this.message = message;
        }

        @Override // java.lang.Throwable
        public String toString() {
            return this.message;
        }
    }
}
