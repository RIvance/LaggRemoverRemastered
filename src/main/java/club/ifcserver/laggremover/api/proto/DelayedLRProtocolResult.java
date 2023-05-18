package club.ifcserver.laggremover.api.proto;

public abstract class DelayedLRProtocolResult {
    public abstract void receive(LRProtocolResult lRProtocolResult);
}
