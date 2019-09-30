package Root;


public class CarWheelService {
    volatile FIFO<WheelInterface> resourcequeue = null;
    WheelInterface[] wheels = null;

    private Threadhandler pool;
    private Thread poolThread;

    public CarWheelService(FIFO<WheelInterface> resourcequeue, WheelInterface[] wheels, Threadhandler pool){
        this.resourcequeue = resourcequeue;
        this.wheels = wheels;

        this.pool = pool;
        poolThread = new Thread((Runnable)pool);
        poolThread.start();
    }

    public void add(WheelInterface wheel, int numwheels){
        synchronized (resourcequeue){
            for(int i = 0; i < numwheels; i++){
                resourcequeue.push(wheel);
            }
        }
    }

    public String[] clear(){
        String[] rstr = null;
        synchronized (resourcequeue){
            rstr = new String[resourcequeue.size()];
            int i = 0;
            while(resourcequeue.size() > 0) {
                WheelInterface w = resourcequeue.pop();
                rstr[i] = i + ": Removed " + w.getName();
                i++;
            }
        }
        return rstr;
    }

    public Belt[] getBelts (){
        return pool.getBelts();
    }

    public void startBelt(int Beltnumber){
        pool.startBelt(Beltnumber);
    }

    public void stopBelt(int Beltnumber){
        pool.stopBelt(Beltnumber);
    }

    public WheelInterface[] getWheels(){
        return wheels;
    }

    public FIFO<WheelInterface> getQueue(){
        return resourcequeue;
    }
}




