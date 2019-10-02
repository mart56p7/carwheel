package Root;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Semap implements Threadhandler,FIFOObserver {

    private BeltMKN[] belts;
    private final Semaphore sem;
    private ExecutorService executor;
    private volatile FIFO<WheelInterface> resourcequeue = null;
    private boolean running = true;

    public Semap(FIFO<WheelInterface> resourcequeue, int size) {
        this.resourcequeue = resourcequeue;
        this.resourcequeue.attach(this);
        belts = new BeltMKN[size];
        sem = new Semaphore(size,true);
        for (int i =0;i<size;i++){
            belts[i] = new BeltMKN("Belt " + (1+i), this);
        }
    }

    @Override
    public void FIFONotEmpty() {
        synchronized (this){
            notify();
        }
    }

    @Override
    public BeltInterface[] getBelts() {
        return belts;
    }

    @Override
    public void stopBelt(int BeltNumber) {
        belts[BeltNumber].forceStop();
        if(belts[BeltNumber].getState()==BeltState.INTERRUPTED){
            sem.tryAcquire(1);
        }
    }

    @Override
    public void startBelt(int BeltNumber) {
        belts[BeltNumber].setWaiting(this);
        if(belts[BeltNumber].getState()==BeltState.WAITING){
            sem.release(1);
        }
    }

    @Override
    public void stopAll() {
        for(int i =0;i<belts.length;i++){
            stopBelt(i);
        }
        running = false;
        synchronized (this){
            Thread.currentThread().interrupt();
        }
    }
    private void sync(){
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        WheelInterface nextitem;
        while (running) {
            nextitem = resourcequeue.peek();
            if (nextitem != null) {
                while (nextitem != null){
                    if (sem.availablePermits() != 0) {
                        for(int i = 0;i<belts.length;i++){
                            executor = Executors.newSingleThreadExecutor();
                            if (belts[i].getState()==BeltState.WAITING){
                                try {
                                sem.acquire(1);
                                belts[i].setWheel(nextitem);
                                executor.execute(belts[i]);
                                nextitem = null;
                                break;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {
                                    sem.release(1);
                                }
                            }
                        }
                        if(nextitem != null){
                            sync();
                        }
                    }

                }
                resourcequeue.pop();
            }
            else{
                sync();
            }
        }
        executor.shutdown();
    }
}
