package Root;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BeltPoolMKN implements Threadhandler, FIFOObserver {
    private BeltMKN[] belts;
    private boolean running = true;
    private ExecutorService poolmanager;
    private boolean debug = false;

    private volatile FIFO<WheelInterface> resourcequeue = null;

    public BeltPoolMKN(FIFO<WheelInterface> resourcequeue, int size){
        this.resourcequeue = resourcequeue;
        this.resourcequeue.attach(this);
        belts = new BeltMKN[size];
        for(int i = 0; i < size; i++){
            belts[i] = new BeltMKN("Belt " + (1+i), this);
        }
        poolmanager = Executors.newFixedThreadPool(4);

    }

    @Override
    public BeltInterface[] getBelts() {
        return belts;
    }

    @Override
    public void stopBelt(int BeltNumber) {
        belts[BeltNumber].forceStop();
    }

    @Override
    public void startBelt(int BeltNumber) {
        belts[BeltNumber].setWaiting(this);
    }

    @Override
    public void stopAll() {
        for(int i = 0; i < belts.length; i++){
            stopBelt(i);
        }
        running = false;
        synchronized (this){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        WheelInterface nextitem;
        while(running) {
            nextitem = resourcequeue.peek();
            if (nextitem != null) {
                while (nextitem != null) {
                    for (BeltMKN belt : belts) {
                        if(debug) System.out.println("foreach " + belt.getName() + " state is " + belt.getState());
                        if (belt.getState() == BeltState.WAITING) {
                            if(debug) System.out.println("Assigning job to " + belt.getName() + " with state " + belt.getState());
                            belt.setWheel(nextitem);
                            poolmanager.execute(belt);
                            nextitem = null;
                            break;
                        } else {
                            if(debug) System.out.println(belt.getName() + " state is " + belt.getState());
                        }
                    }
                    if (nextitem != null) {
                        synchronized (this) {
                            try {
                                if(debug) System.out.println("No belts available, waiting");
                                wait();
                                if(debug) System.out.println("Notified by belt");
                            } catch (InterruptedException e) {
                                if(debug) System.out.println("InterruptedException");
                                e.printStackTrace();
                            }
                        }
                    }
                }
                resourcequeue.pop();
            } else {
                synchronized (this) {
                    try {
                        if(debug) System.out.println("Waiting for items in the queue");
                        wait();
                        if(debug) System.out.println("Notified by queue");
                    } catch (InterruptedException e) {
                        if(debug) System.out.println("No items in queue");
                        e.printStackTrace();
                    }
                }
            }
        }
        if(debug) System.out.println("Shutting down the thread pool");
        poolmanager.shutdown();
    }

    @Override
    public void FIFONotEmpty() {
        synchronized (this) {
            notify();
        }
    }
}
