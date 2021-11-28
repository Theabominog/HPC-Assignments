import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
public class Counter{
  private Node head;
  public Counter() {
    head=new Node(0);
  }
  /* Add an element.*/
  public void inc() {
    head.lock();
    head.count=head.count+1;
    head.unlock();
    }
  public void display()
  {
	System.out.println("\t"+head.count+"\n");
  }
 /* Node*/
  private class Node {
    int count;
    Lock lock;
    Node(int item) {
     this.count = item;
    this.lock = new HBOLock();
    // this.lock = new QLockTimeOut();
    }
    void lock() {lock.lock();}
    void unlock() {lock.unlock();}
  }
}
