import java.util.concurrent.locks.ReentrantLock;
import javax.naming.spi.DirStateFactory.Result;
import javax.swing.table.AbstractTableModel;

import java.util.EmptyStackException;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.TimeUnit;

public class QLockTimeOut implements Lock{
    static QNode AVAILABLE = new QNode();
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;
    public QLockTimeOut() {
        tail = new AtomicReference<QNode>(null);
        myNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
            return new QNode();
            }
        };
    }
    static class QNode {
        public QNode pred = null;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException{
        boolean temp = false;
        return temp;
    }

    public void lockInterruptibly(){
        System.out.println("not required");
    }

    public boolean tryLock(){
        long startTime = System.currentTimeMillis();
        long patience = 100;
        QNode qnode = new QNode();
        myNode.set(qnode);
        qnode.pred = null;
        QNode myPred = tail.getAndSet(qnode);
        if (myPred == null || myPred.pred == AVAILABLE) {
            return true;
        }
        while (System.currentTimeMillis() - startTime < patience) {
            QNode predPred = myPred.pred;
            if (predPred == AVAILABLE) {
                return true;
            } else if (predPred != null) {
                myPred = predPred;
            }
        }
        if (!tail.compareAndSet(qnode, myPred)){
            qnode.pred = myPred;}
        return false;
    }
    
    public void lock() {
        boolean temp = false;
        temp = tryLock();
        // System.out.println(temp);
    }

    public void unlock() {
        QNode qnode = myNode.get();
        if (!tail.compareAndSet(qnode, null))
            qnode.pred = AVAILABLE;
    }

    public Condition newCondition(){
        Condition temp = null;
        return temp;
    }

    // public static void main(String[] args) {
    //     QLockTimeOut L = new QLockTimeOut();
        
    //     System.out.println(L.tryLock());
    //     System.out.println(L.tryLock());
    //     L.unlock();
    //     System.out.println(L.tryLock());
    // }
}