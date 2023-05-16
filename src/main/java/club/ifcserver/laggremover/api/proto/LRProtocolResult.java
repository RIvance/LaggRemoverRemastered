package club.ifcserver.laggremover.api.proto;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/proto/LRProtocolResult.class */
public abstract class LRProtocolResult {
    private LRProtocol sup;

    public abstract Object[] getData();

    public LRProtocolResult(LRProtocol sup) {
        this.sup = sup;
    }

    public LRProtocol getSuper() {
        return this.sup;
    }
}
