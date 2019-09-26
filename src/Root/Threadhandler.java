package Root;

public interface Threadhandler extends Runnable {

    BeltInterface[] getBelts();
    void stopBelt(int BeltNumber);
    void startBelt(int BeltNumber);
    void stopAll();

}
