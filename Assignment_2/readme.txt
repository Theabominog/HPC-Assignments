1.) Concurrent Stack - Run the command "javac ConcurrentStack.java" and then "java ConcurrentStack", the operations are in the java file where you can modify them to add or remove operations.
2.) Concurrent Queue - Run the command "javac ConcurrentQueue.java" and then "java ConcurrentQueue", the operations are in the java file where you can modify them to add or remove operations.
3.) Queue Lock with timeouts - Run the following commands - javac Counter.java
						          - javac Counter_Test.java
						          - javac QLockTimeOut.java
						          - java Counter_Test {num_threads} {time}
						          - uncomment this.lock = new QLockTimeOut
4.) Hierarchical Lock - Run the following commands - javac Counter.java
						   - javac Counter_Test.java
						   - javac HBOLock.java
						   - java Counter_Test {num_threads} {time}
						   - uncomment this.lock = new HBOLock
5.) Lockfree Skiplist - Run the command "javac LockFreeSkipList.java" and "java LockFreeSkipList"


Concurrent Stack - This is an implementation of a lock free stack, that is also unbounded, it consists of a linked list where the top field points to the first node, we have methods trypush
and trypop that try to gain the lock to add the newly made node to the top of the stack or remove it respectively, if they use the exponential backoff mechanism to reduce contention.

Concurrent Queue - This is an implementation of an unbounded queue, i.e it can hold an unlimited number of items, we use two locks to ensure that enqueue amd dequeue operations do no not 
contend with each other. however this may suffer from locks.

Queue Lock with Timeouts - this implementation of a queue lock allows the thread to specify a timeout, the time it is willing to wait to acquire a lock, as it only requires a limited number 
of operations it is wait free.

Hierarchical Lock - This is an implementation of a hierarchical backoff lock, which allows for exploiting spatial locality which leads ot faster transfer of locks amonng local threads.

LockFree SkipList - This implementation represents a space with a bottom level list, if the reference in the bottom level list is unmarked, the key is present, higher lists are
considered as lock free list and they contain references to the bottom level for efficiency. Find avoids unnecessary reads by checking the references, and avoiding unnecessary 
reads
