package Root;


public class CarWheelService {
    volatile FIFO<WheelInterface> resourcequeue = null;
    WheelInterface[] wheels = null;

    private BeltPool pool;
    private Thread poolThread;

    public CarWheelService(FIFO<WheelInterface> resourcequeue, WheelInterface[] wheels){
        this.resourcequeue = resourcequeue;
        this.wheels = wheels;

        pool = new BeltPool(resourcequeue, 4);
        poolThread = new Thread(pool);
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

    public void stop(){

    }

    BeltPool getPool(){
        return pool;
    }

    public WheelInterface[] getWheels(){
        return wheels;
    }

    public FIFO<WheelInterface> getQueue(){
        return resourcequeue;
    }
}




