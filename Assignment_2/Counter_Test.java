/* Testing Concurrent Linked Lists */
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
public class Counter_Test{
	private static int THREADS;
	private static int TIME;
	Counter instance;
	long []opCount;
	long totalOps;
	Thread []th;
	long start;
	public Counter_Test(int num_threads, int time )
	{
		instance=new Counter();
		THREADS=num_threads;
		TIME=time;
		th=new Thread[num_threads];
		opCount=new long[num_threads];
		totalOps=0;
	}
	public void testParallel()throws Exception{
		for(int i=0;i<THREADS;i++)
		{
			th[i]=new AllMethods();
		}
		start=System.currentTimeMillis();
		 for(int i=0;i<THREADS;i++)
                {
                        th[i].start();
                }
		 for(int i=0;i<THREADS;i++)
                {
                        th[i].join();
                }
	}
	
	class AllMethods extends Thread{
		int pri_range=Integer.MAX_VALUE-10;
		//int int_range=RANGE;//Integer.MAX_VALUE/2-10;	
		public void run()
		{	
			int j=ThreadID.get();
			long count=0;
			long end=System.currentTimeMillis();
			for(int i=0;(end-start)<=TIME;i=i+1)
			{
				
				instance.inc();
				count=count+1;
			       if(count%100==0){ end=System.currentTimeMillis();}
			}
			opCount[j]=count;
		}
	}
	public long totalOperations()
	{
		for(int i=0;i<THREADS;i++)
		{
			totalOps=totalOps+opCount[i];
		}
		return totalOps;
	}
	void display()
	{
		instance.display();
	}
	public static void main(String[] args){
		Runtime runtime=Runtime.getRuntime();
		//runtime.gc();
		int num_threads=Integer.parseInt(args[0]);
		int time=Integer.parseInt(args[1]);
		Counter_Test ob=new Counter_Test(num_threads,time);
		try{ ob.testParallel(); }catch(Exception e){ System.out.println(e); }
		long total_Operations=ob.totalOperations();
		double throughput=(total_Operations/(1000000.0*time))*1000;// Millions of Operations per second
		System.out.print(" c_name:"+ob.instance.getClass().getName());
		System.out.print(":num_threads:"+num_threads+":totalOps:"+total_Operations+" :throughput:"+throughput+"\n");	
		//System.out.println("Time: "+(end-start));
		ob.display();
		//System.out.println("");	
	}
}	
