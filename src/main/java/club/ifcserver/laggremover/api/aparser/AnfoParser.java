package club.ifcserver.laggremover.api.aparser;

import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.util.DoubleVar;
import java.util.Collections;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/aparser/AnfoParser.class */
public class AnfoParser {
    public static DoubleVar<Object[], Boolean> parse(LRProtocol pro, String data_raw) throws AnfoParseException, ParseException {
        JSONObject data = readJSON(data_raw);
        HashMap<String, ProtoParse.ProtoParseData> k = pro.getPP().getKeysToClass();
        HashMap<Integer, Object> a = new HashMap<>();
        boolean isDelay = false;
        for (Object o : data.keySet()) {
            String key = (String) o;
            if (key.equals("Delay")) {
                isDelay = Boolean.parseBoolean((String) data.get(key));
            } else if (!k.containsKey(key)) {
                throw new AnfoParseException("The arguments for \"" + pro.id() + "\" contained an unregistered key.");
            } else {
                ProtoParse.ProtoParseData d = k.get(key);
                String data_in = (String) data.get(key);
                a.put(Integer.valueOf(d.getIndex()), data_in.equalsIgnoreCase("null") ? null : d.getClazz().getParser().parse(data_in));
            }
        }
        Object[] oa = new Object[((Integer) Collections.max(a.keySet())).intValue() + 1];
        for (int i = 0; i < oa.length; i++) {
            if (a.containsKey(Integer.valueOf(i))) {
                oa[i] = a.get(Integer.valueOf(i));
            } else {
                oa[i] = null;
            }
        }
        return new DoubleVar<>(oa, Boolean.valueOf(isDelay));
    }

    private static JSONObject readJSON(String data) throws ParseException {
        JSONParser parse = new JSONParser();
        return (JSONObject) parse.parse(data);
    }

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/aparser/AnfoParser$AnfoParseException.class */
    public static class AnfoParseException extends Exception {
        private String msg;

        AnfoParseException(String msg) {
            this.msg = msg;
        }

        @Override // java.lang.Throwable
        public String toString() {
            return this.msg;
        }
    }
}
