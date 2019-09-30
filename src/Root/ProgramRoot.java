package Root;

public class ProgramRoot {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        //Mulige hjul der kan produceres
        WheelInterface[] wheels = new WheelInterface[5];
        wheels[0] = new defaultWheel("Normal wheel", 10000);
        wheels[1] = new defaultWheel("High wheel", 17000);
        wheels[2] = new defaultWheel("Winter wheel", 14000);
        wheels[3] = new defaultWheel("Mother fucker big wheel", 200000);
        wheels[4] = new defaultWheel("Fast wheel", 100);
        //Vores kø hvor de forskellige hjul opbevares indtil de bliver produceret
        FIFO<WheelInterface> resourcequeue = new FIFO();
        //Opretter en BeltPool til vores service
        Threadhandler pool;
        //pool = new BeltPoolMKN(resourcequeue, 4);
        pool = new BeltPool(resourcequeue, 4);
        Thread threadpool = new Thread(pool);
        threadpool.start();

        //Vores Controller til vores cmd interface
        ControllerInterface[] controllers = new ControllerInterface[1];
        controllers[0] = new CarWheelController(new CarWheelService(resourcequeue, wheels, pool));

        //Gives status over køen og hvad der bliver produceret pt. Implementeret som en simpel webserver
        WebStatus webstatus = new WebStatus(resourcequeue, wheels, pool, false);
        webstatus.start();

        //Command line interface til at styre produktion
        new CMDGUI(controllers);

        pool.stopAll();

        //When CMDGUI ends we terminate our WebStatus thread.
        webstatus.close();

        //We let the Java/OS handle any Threads we have forgotten to close.
        System.exit(0);
    }
}
