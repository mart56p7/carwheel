package Root;

public interface BeltInterface {
    String getName();
    WheelInterface getWheel();
    BeltState getState();
    void setWheel(WheelInterface newWheel);
    int getRemainingTime();
    void forceStop();
    void setWaiting(Threadhandler bp);
    void terminateBelt();
}
