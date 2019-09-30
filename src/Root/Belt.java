package Root;

public class Belt implements Runnable {

    private String name;
    private int runs = 0;
    private boolean emergency = false;
    private int elapsedTime = 0;
    private boolean running = true;

    private BeltState state = BeltState.WAITING;
    private WheelInterface wheel;
    private BeltPool owner;

    public Belt (String name, WheelInterface wheel, BeltPool owner){
        this.name = name;
        this.wheel = wheel;
        this.owner = owner;
    }

    @Override
    public void run() {
        while (running) {
            if(!emergency) {
                if (wheel != null) {
                    try {
                        //Preparation
                        routine(2000, BeltState.PREPARING);
                        //Run
                        elapsedTime = 0;
                        routine(wheel.getProductionTime(), BeltState.RUNNING);
                        runs++;
                        //Cleaning Process
                        if (runs >= 10) {
                            routine(6000, BeltState.CLEANING);
                            runs = 0;
                        }
                        wheel = null;
                        owner.signalDone(this);
                    } catch (InterruptedException e) {
//                    System.out.println("An error has occured at " + name);
                        state = BeltState.INTERRUPTED;
                    }
                }
                try {
                    routine(100, BeltState.WAITING);
                } catch (Exception e) {
//                System.out.println("An error has occured at " + name);
                    state = BeltState.INTERRUPTED;
                }
            } else {
                state = BeltState.INTERRUPTED;
            }
        }
    }


    void forceStop(){
        emergency = true;
        state = BeltState.INTERRUPTED;
    }

    void setWaiting(BeltPool bp){
        emergency = false;
        if(bp == owner) state = BeltState.WAITING;
    }

    void terminateBelt(){
        running = false;
        forceStop();
    }


    public String getName(){
        return name;
    }

    public BeltState getState(){
        return state;
    }

    public WheelInterface getWheel(){
        return wheel;
    }

    public void setWheel(WheelInterface newWheel){
        wheel = newWheel;
    }

    public int getRemainingTime(){
        if(state == BeltState.RUNNING){
            return wheel.getProductionTime() - elapsedTime;
        }
        return 0;
    }


    private void routine (int millis, BeltState routineState) throws InterruptedException {
        state = routineState;
        while (millis >= 100){
            if(emergency){
                state = BeltState.INTERRUPTED;
                throw new InterruptedException("Belt was forcefully stopped");
            }
            Thread.sleep(100);
            millis -= 100;
            elapsedTime += 100;
        }
        Thread.sleep(millis);
        state = BeltState.WAITING;
    }
}

enum BeltState {
    WAITING, PREPARING, RUNNING, CLEANING, INTERRUPTED
}
