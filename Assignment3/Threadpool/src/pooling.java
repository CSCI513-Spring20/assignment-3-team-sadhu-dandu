import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
public class pooling {
    final int Size;
    public  boolean isShutdown = false;
    private final LinkedBlockingQueue<Task> list  = new LinkedBlockingQueue<Task>();
    private final ArrayList<Task> allTasks = new ArrayList<Task>();
    private final PerformanceTask[] work;
    //Constructor
    public pooling(int Size){
        this.Size = Size;
        work = new PerformanceTask[this.Size];
        int i =0;
        while ( i < Size) {
            work[i] = new PerformanceTask("Thread " + i);
            work[i].start();
            i++;
        }
    }
   //method  to synchronize  threads and executes threads
    public void taskexecution(Task task) {
        synchronized (list) {
            list.add(task);
            list.notify();
        }
    }
    //Method to terminate threads
    public void shutdown() {
        this.isShutdown = true;
    }
    
    // class extending the thread
    private class PerformanceTask extends Thread {
        public PerformanceTask(String name)
        {
            super(name);
        }
        @Override
        public void run() {
            Task t;

            while (true) {
                synchronized (list) {
                    if (isShutdown && list.isEmpty()) {
                        break;
                    }
                    while (list.isEmpty()) {
                        try {
                            list.wait();
                        } catch (InterruptedException e) {
                            System.out.println( e.getMessage());
                        }
                    }
                    t = (Task) list.poll();
                }
                try {
                    t.run();
                    t.setIsCompleted();
                } catch (RuntimeException e) {
                    System.out.println( e.getMessage());
                }
            }
        }
    }


}
