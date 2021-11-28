import java.util.concurrent.locks.ReentrantLock;
import javax.naming.spi.DirStateFactory.Result;
import java.util.EmptyStackException;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

public class ConcurrentQueue<T>{
    ReentrantLock addLock, remLock;
    volatile Node head, tail;

    public ConcurrentQueue() {
        head = new Node(null);
        tail = head;
        addLock = new ReentrantLock();
        remLock = new ReentrantLock();
    }

    protected class Node{
        public T value;
        public volatile Node next;
        public Node(T x){
            value = x;
            next = null;
        }
    }

    public void enqueue(T x) {
        addLock.lock();
        try {
            Node newnode = new Node(x);
            tail.next = newnode;
            tail = newnode;
        } finally {
            addLock.unlock();
            System.out.println("Added " + x + " to queue");
        }
    }

    public T dequeue() throws EmptyStackException {
        T result;
        remLock.lock();
        try{
            if(head.next == null){
                throw new EmptyStackException();
            }
            result = head.next.value;
            head = head.next;
        } finally{
            remLock.unlock();
        }
        return result;
    }

    public static void main(String[] args) {
        ConcurrentQueue<Integer> L = new ConcurrentQueue();
        L.enqueue(15);
        L.enqueue(25);
        System.out.println(L.dequeue());
        System.out.println(L.dequeue());
        try{System.out.println(L.dequeue());}
        catch(EmptyStackException E){
            System.out.println("Queue is empty");
            return;
        }
    }
}
