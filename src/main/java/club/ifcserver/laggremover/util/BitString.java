package club.ifcserver.laggremover.util;

import java.nio.charset.Charset;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/util/BitString.class */
public enum BitString {
    BLOCK_FULL(226, 150, 136),
    BLOCK_FULL_LIGHT(226, 150, 145),
    SQUARE(226, 150, 160),
    BLOCK_HALF(226, 150, 140);
    
    private String comp;

    BitString(int... data) {
        byte[] da = new byte[data.length];
        for (int i = 0; i < da.length; i++) {
            da[i] = (byte) data[i];
        }
        this.comp = new String(da, Charset.forName("UTF-8"));
    }

    public String getComp() {
        return this.comp;
    }
}
