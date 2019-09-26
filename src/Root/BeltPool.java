package Root;

public class BeltPool implements Runnable {

    private Belt[] belts;
    private Thread[] beltThreads;
    private Belt[] waitingBelts;

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
//                System.out.println(belt.getName() + " finished wheel");
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
            b.forceStop();
        }
    }

    @Override
    public void run() {
        while (true) {
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




