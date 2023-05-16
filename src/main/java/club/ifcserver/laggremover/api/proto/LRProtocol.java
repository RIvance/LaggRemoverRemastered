package club.ifcserver.laggremover.api.proto;

import club.ifcserver.laggremover.api.aparser.ProtoParse;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/proto/LRProtocol.class */
public interface LRProtocol {
    void init();

    String id();

    String help();

    ProtocolCategory[] category();

    LRProtocolResult run(Object[] objArr);

    ProtoParse getPP();
}
