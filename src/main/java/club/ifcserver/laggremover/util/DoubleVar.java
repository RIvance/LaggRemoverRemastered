package club.ifcserver.laggremover.util;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/util/DoubleVar.class */
public class DoubleVar<T1, T2> {
    private final T1 t1;
    private final T2 t2;

    public DoubleVar(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getVar1() {
        return this.t1;
    }

    public T2 getVar2() {
        return this.t2;
    }
}
