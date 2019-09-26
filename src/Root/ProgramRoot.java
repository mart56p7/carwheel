package Root;

public class ProgramRoot {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        //Mulige hjul der kan produceres
        WheelInterface[] wheels = new WheelInterface[4];
        wheels[0] = new wheelNormal("Normal wheel", 10000);
        wheels[1] = new wheelHigh("High wheel", 17000);
        wheels[2] = new wheelWinter("Winter wheel", 14000);
        wheels[3] = new wheelWinter("Mother fucker big wheel", 200000);
        //Vores kø hvor de forskellige hjul opbevares indtil de bliver produceret
        FIFO<WheelInterface> resourcequeue = new FIFO();
        //Opretter en Beltpool til vores service
        Threadhandler pool = new BeltPool(resourcequeue, 4);
        //Vores Controller til vores cmd interface
        ControllerInterface[] controllers = new ControllerInterface[1];
        controllers[0] = new CarWheelController(new CarWheelService(resourcequeue, wheels, pool));

        //Gives status over køen og hvad der bliver produceret pt. Implementeret som en simpel webserver
        WebStatus webstatus = new WebStatus(resourcequeue, wheels, false);
        webstatus.start();

        //Command line interface til at styre produktion
        new CMDGUI(controllers);

        //When CMDGUI ends we terminate our WebStatus thread.
        webstatus.close();
    }
}
