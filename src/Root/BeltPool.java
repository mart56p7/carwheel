package Root;

public class BeltPool implements Runnable, Threadhandler{

    private Belt[] belts;
    private Thread[] beltThreads;
    private Belt[] waitingBelts;
    private boolean running = true;

    private volatile FIFO<WheelInterface> resourcequeue = null;

    public BeltPool(FIFO<WheelInterface> resourcequeue, int size){
        this.resourcequeue = resourcequeue;
        this.belts = new Belt[size];
        this.beltThreads = new Thread[size];
        this.waitingBelts = new Belt[size];
        for (int i = 0; i < size; i++) {
            belts[i] = new Belt("Belt " + i, null, this);
            waitingBelts[i] = belts[i];
            beltThreads[i] = new Thread(belts[i]);
            beltThreads[i].start();
        }
    }

    synchronized boolean signalDone(Belt belt){
        for(int i = 0; i < waitingBelts.length; i++){
            if(waitingBelts[i] == null){
                waitingBelts[i] = belt;
                return true;
            }
        }
        return false;
    }

    public Belt[] getBelts(){
        return belts;
    }

    public void stopBelt(int BeltNumber){
        belts[BeltNumber].forceStop();
        for(int i = 0; i < waitingBelts.length; i++){
            if(waitingBelts[i] == belts[BeltNumber]){
                waitingBelts[i] = null;
            }
        }
    }

    public void startBelt(int BeltNumber){
        belts[BeltNumber].setWaiting(this);
        signalDone(belts[BeltNumber]);
    }

    public void stopAll(){
        for (Belt b : belts){
            b.terminateBelt();
        }
    }

    @Override
    public void run() {
        while (running) {
            while (resourcequeue.peek() != null) {
                for (int i = 0; i < waitingBelts.length; i++) {
                    if (waitingBelts[i] != null) {
                        waitingBelts[i].setWheel(resourcequeue.pop());
                        waitingBelts[i] = null;
                    }
                }
            }
        }
    }
}




