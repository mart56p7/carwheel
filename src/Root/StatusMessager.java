package Root;

public class StatusMessager implements Runnable {
    FIFO<WheelInterface> resourcequeue = null;

    public StatusMessager(FIFO<WheelInterface> resourcequeue){
        System.out.println("aaa");
        this.resourcequeue = resourcequeue;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            System.out.println("Sending data");
        }catch(Exception e){}

    }
}
