import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;


public class HBOLock implements Lock {
    private static final int MIN_DELAY = 5;
    private static final int MAX_DELAY = 20;
    private static final int UnMarked = -1;
    
    AtomicInteger state;
    public HBOLock() {
        state = new AtomicInteger(UnMarked);
    }
    public void lock() {
        int cluster = ThreadID.get() / 2;
        Backoff lBackoff = new Backoff(MIN_DELAY, MAX_DELAY);
        Backoff rBackoff = new Backoff(MIN_DELAY, MAX_DELAY);
        while (true) {
            if (state.compareAndSet(UnMarked, cluster)) {
                return;
            }
            int lockState = state.get();
            if (lockState == cluster) {
                try {
                    lBackoff.backoff();
                } catch(Exception e) {}
            } else {
                try {
                    rBackoff.backoff();
                } catch(Exception e) {}
            }
        }
    }
    public void unlock() {
        state.set(UnMarked);
    }

    public class ThreadID {
        private static volatile int nextID = 0;
        private static ThreadLocalID threadID = new ThreadLocalID();
        public static int get() {
          return threadID.get();
        }
        public static void reset() {
          nextID = 0;
        }
        private static class ThreadLocalID extends ThreadLocal<Integer> {
          protected synchronized Integer initialValue() {
            return nextID++;
          }
        }
      }

    public Condition newCondition() {
        throw new java.lang.UnsupportedOperationException();
    }
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new java.lang.UnsupportedOperationException();
    }
    public boolean tryLock() {
        throw new java.lang.UnsupportedOperationException();
    }
    public void lockInterruptibly() throws InterruptedException {
        throw new java.lang.UnsupportedOperationException();
    }
}