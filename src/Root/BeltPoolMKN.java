package Root;

public class BeltPoolMKN implements Threadhandler{
    private BeltMKN[] belts;
    private Thread[] beltThreads;
    private Belt[] waitingBelts;
    private boolean running = true;

    private volatile FIFO<WheelInterface> resourcequeue = null;

    public BeltPoolMKN(FIFO<WheelInterface> resourcequeue, int size){
        this.resourcequeue = resourcequeue;
        belts = new BeltMKN[size];

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

    }

    @Override
    public void stopAll() {

    }

    @Override
    public void run() {

    }
}
