package club.ifcserver.laggremover.api.proto;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/proto/LRProtocolResult.class */
public abstract class LRProtocolResult {
    private final LRProtocol protocol;

    public abstract Object[] getData();

    public LRProtocolResult(LRProtocol protocol) {
        this.protocol = protocol;
    }

    public LRProtocol getSuper() {
        return this.protocol;
    }
}
