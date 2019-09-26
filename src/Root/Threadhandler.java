package Root;

public interface Threadhandler extends Runnable {

    Belt[] getBelts();
    void stopBelt(int BeltNumber);
    void startBelt(int BeltNumber);
    void stopAll();

}
