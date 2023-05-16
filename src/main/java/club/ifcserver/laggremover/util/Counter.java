package club.ifcserver.laggremover.util;

import club.ifcserver.laggremover.main.LaggRemover;
import java.util.HashMap;
import org.bukkit.Bukkit;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/util/Counter.class */
public abstract class Counter {
    private long on;
    private long secondsDelay;
    private HashMap<Long, CountAction> actions = new HashMap<>();
    private boolean started = false;

    public abstract void onFinish();

    static /* synthetic */ long access$110(Counter x0) {
        long j = x0.on;
        x0.on = j - 1;
        return j;
    }

    public Counter(long secondsDelay) {
        this.secondsDelay = secondsDelay;
        this.on = secondsDelay;
    }

    public boolean start() {
        if (!this.started) {
            this.started = true;
            one();
        }
        return this.started;
    }

    public void reset() {
        this.on = this.secondsDelay;
        this.started = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void one() {
        if (this.actions.containsKey(Long.valueOf(this.on))) {
            this.actions.get(Long.valueOf(this.on)).onTrigger();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(LaggRemover.lr, new Runnable() { // from class: drew6017.lr.util.Counter.1
            @Override // java.lang.Runnable
            public void run() {
                if (Counter.this.started) {
                    Counter.access$110(Counter.this);
                    if (Counter.this.on > 0) {
                        Counter.this.one();
                        return;
                    }
                    Counter.this.reset();
                    Counter.this.onFinish();
                }
            }
        }, 20L);
    }

    public Counter addAction(CountAction a) {
        this.actions.put(Long.valueOf(a.getTrigger()), a);
        return this;
    }

    public Counter setActions(HashMap<Long, CountAction> actions) {
        this.actions = actions;
        return this;
    }

    public boolean isActive() {
        return this.started;
    }

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/util/Counter$CountAction.class */
    public static abstract class CountAction {
        private long trigger;

        public abstract void onTrigger();

        public CountAction(long trigger) {
            this.trigger = trigger;
        }

        public long getTrigger() {
            return this.trigger;
        }
    }
}
