package club.ifcserver.laggremover.api.proto;

import club.ifcserver.laggremover.api.aparser.ProtoParse;

public interface LRProtocol {
    void init();

    String id();

    String help();

    ProtocolCategory[] category();

    LRProtocolResult run(Object[] objects);

    ProtoParse getProtocolParser();
}
