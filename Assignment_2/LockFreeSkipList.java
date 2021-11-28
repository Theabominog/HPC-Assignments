import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;
import java.math.*;

public final class LockFreeSkipList<T> {
    static final int MAX_LEVEL = 5;
    final Node<T> head = new Node<T>(Integer.MIN_VALUE);
    final Node<T> tail = new Node<T>(Integer.MAX_VALUE);
    public LockFreeSkipList() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<LockFreeSkipList.Node<T>>(tail, false);
        }
    }
    public static final class Node<T> {
        final T value; final int key;
        final AtomicMarkableReference<Node<T>>[] next;
        private int topLevel;
        public Node(int key) {
            value = null; 
            this.key = key;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null,false);
            }
        topLevel = MAX_LEVEL;
        }
        public Node(T x, int height) {
            value = x;
            key = x.hashCode();
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null,false);
            }
            topLevel = height;
        }
    }

    boolean find(T x, Node<T>[] preds, Node<T>[] succs) {
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = {false};
        boolean snip;
        Node<T> pred = null, curr = null, succ = null;
        retry:
        while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ, false, false);
                        if (!snip) continue retry;
                        curr = pred.next[level].getReference();
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.key < key){
                        pred = curr; curr = succ;
                    } else {
                        break;
                    }
                }
                preds[level] = pred;
                succs[level] = curr;
            }
            return (curr.key == key);
        }
    }

    public static int randomLevel() {
        int lvl = (int)(Math.log(1.-Math.random())/Math.log(1.-0.5));
        return Math.min(lvl, MAX_LEVEL);
    }

    boolean add(T x) {
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            boolean found = find(x, preds, succs);
            if (found) {
                return false;
            } else {
                Node<T> newNode = (Node<T>)new Node(x, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];
                newNode.next[bottomLevel].set(succ, false);
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    continue;
                }
                for (int level = bottomLevel+1; level <= topLevel; level++) {
                    while (true) {
                        pred = preds[level];
                        succ = succs[level];
                        if (pred.next[level].compareAndSet(succ, newNode, false, false))
                            break;
                        find(x, preds, succs);
                    }
                }
                return true;
            }
        }
    }

    boolean remove(T x) {
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T> succ;
        while (true) {
            boolean found = find(x, preds, succs);
            if (!found) {
                return false;
            } else {
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel+1; level--) {
                    boolean[] marked = {false};
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].attemptMark(succ, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = {false};
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find(x, preds, succs);
                        return true;
                    }
                    else if (marked[0]) return false;
                }
            }
        }
    }

    boolean contains(T x) {
        int bottomLevel = 0;
        int v = x.hashCode();
        boolean[] marked = {false};
        Node<T> pred = head, curr = null, succ = null;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
            curr = pred.next[level].getReference();
            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {
                    curr = pred.next[level].getReference();
                    succ = curr.next[level].get(marked);
                }
                if (curr.key < v){
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
        return (curr.key == v);
    }

    public static void main(String[] args) {
        LockFreeSkipList<Integer> L = (LockFreeSkipList<Integer>)new LockFreeSkipList();
        int max_ops = 10000;
        for (int i = 0; i < max_ops; i++) {
            // if(i % 10000 == 0) {System.out.println(i);}
            L.add(i);
        }

        for (int i = 0; i < 1000; i++) {
            int check = (int)(Math.random() * max_ops);
            int elem = (int)(Math.random() * max_ops);
            int add_elem = (int)(Math.random() * 1000) + max_ops;
            if (check % 3 == 0) {
                System.out.println("Adding node " + add_elem);
                try {
                    boolean a = L.add(add_elem);
                    if (a) {
                        System.out.println("Added node " + add_elem);
                    } else {
                        System.out.println("Node " + add_elem + " already present");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }

            } else if (check % 3 == 1) {
                System.out.println("Checking if node: " + elem + " is present");
                try {
                    boolean a = L.contains(elem);
                    if (a) {
                        System.out.println("Contains node " + elem);
                    } else {
                        System.out.println("Doesn't contain node " + elem);
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            } else if (check % 3 == 2) {
                System.out.println("Removing node: " + elem);
                try {
                    boolean a = L.remove(elem);
                    if (a) {
                        System.out.println("Removed node " + elem);
                    } else {
                        System.out.println("Node " + elem + " doesnt exist");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            } 
            System.out.println(" ");
        }
    }
};
