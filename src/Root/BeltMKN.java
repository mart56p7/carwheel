package Root;

public class BeltMKN implements BeltInterface, Runnable{
    private WheelInterface wheel = null;
    private String name;
    private volatile BeltState state = BeltState.WAITING;
    private long timestart;
    private long jobtime = 0;
    private Threadhandler owner;
    private boolean debug = false;
    private int runs = 0;
    private Thread runner = null;

    public BeltMKN(String name, Threadhandler owner){
        this.name = name;
        this.owner = owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WheelInterface getWheel() {
        return wheel;
    }

    @Override
    public synchronized BeltState getState() {
        return state;
    }

    @Override
    public void setWheel(WheelInterface newWheel) {
        //Setting the state before the thread that does the work on the belt is very important!
        this.state = BeltState.PREPARING;
        this.wheel = newWheel;
    }

    @Override
    public int getRemainingTime() {
        return (int)(jobtime - (System.currentTimeMillis() - timestart));
    }

    @Override
    public void forceStop() {
        terminateBelt();
    }

    @Override
    public void setWaiting(Threadhandler bp) {
        state = BeltState.WAITING;
        synchronized (owner) {
            owner.notify();
        }
    }

    @Override
    public void terminateBelt() {
        state = BeltState.INTERRUPTED;
        if(runner != null) {
            runner.interrupt();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            // The thread is saved, so we can interrupt it
            runner = Thread.currentThread();
            try {
                if(debug) System.out.println("Working " + Thread.currentThread().getName() + " " + name);
                //Prep running
                state = BeltState.PREPARING;
                timestart = System.currentTimeMillis();
                jobtime = 2000;
                wait(jobtime);
                //Production
                if (wheel.getProductionTime() > 0) {
                    state = BeltState.RUNNING;
                    jobtime = wheel.getProductionTime();
                    timestart = System.currentTimeMillis();
                    wait(jobtime);
                }
                //Cleanup if needed
                if (runs >= 10) {
                    state = BeltState.CLEANING;
                    jobtime = 6000;
                    timestart = System.currentTimeMillis();
                    wait(jobtime);
                    runs = 0;
                }
                runs++;
                state = BeltState.WAITING;

                synchronized (owner) {
                    owner.notify();
                }
            }
            catch(InterruptedException ie){
                if(debug)  System.out.println("Belt " + name + " is interrupted " + ie.getMessage());
            }
            catch(Exception e){
                if(debug)  System.out.println(e.getMessage());
            }
            finally {
                if(debug)  System.out.println("Done");
                runner = null;

            }
        }
    }
}
