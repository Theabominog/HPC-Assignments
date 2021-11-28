import java.util.concurrent.locks.ReentrantLock;
import javax.naming.spi.DirStateFactory.Result;
import java.util.EmptyStackException;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

public class ConcurrentStack<T> {
    AtomicReference<Node> top = new AtomicReference<Node>(null);
    Backoff backoff = new Backoff(15, 25);
    
    public class Node {
        public T value;
        public Node next;
        public Node(T value) {
            this.value = value;
            next = null;
        }
    }

    protected boolean tryPush(Node node){
        Node temp = top.get();
        node.next = temp;
        return(top.compareAndSet(temp, node));
    }

    protected Node tryPop() throws EmptyStackException {
        Node oldTop = top.get();
        if (oldTop == null) {
            throw new EmptyStackException();
        }
        Node newTop = oldTop.next;
        if (top.compareAndSet(oldTop, newTop)) {
            return oldTop;
        }
        else{
            return null;
        }
    }
    
    public void push(T value) {
        Node newnode = new Node(value);
        while (true) {
            if (tryPush(newnode)) {
                System.out.println("Pushed " + value + " to stack");
                return;
            } else {
                try{
                    backoff.backoff();
                } catch (InterruptedException E){
                    System.out.println(E);
                }
            }
        }
    }
    
    public T pop() throws EmptyStackException {
        while (true) {
            Node finVal = tryPop();
            if (finVal != null) {
                return finVal.value;
            }
            else {
                try{
                    backoff.backoff();
                } catch (InterruptedException E){
                    System.out.println("The stack is empty");
                }
            }
        }
    }

    public static void main(String[] args) {
        ConcurrentStack<Integer> L = new ConcurrentStack();
        L.push(7);
        L.push(15);
        L.push(25);
        System.out.println(L.pop());
        System.out.println(L.pop());
        System.out.println(L.pop());
        System.out.println("Testing pop operation on empty stack");
        System.out.println(L.pop());
    }
}