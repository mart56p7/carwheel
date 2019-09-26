package Root;

public interface Threadhandler {

    Belt[] getBelts();
    void stopBelt(int BeltNumber);
    void startBelt(int BeltNumber);
    void stopAll();

}
