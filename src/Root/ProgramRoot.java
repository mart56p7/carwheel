package Root;

public class ProgramRoot {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        WheelInterface[] wheels = new WheelInterface[3];
        wheels[0] = new wheelNormal("Normal wheel", 10);
        wheels[1] = new wheelHigh("High wheel", 12);
        wheels[2] = new wheelWinter("Winter wheel", 14);
        FIFO<WheelInterface> resourcequeue = new FIFO();
        ControllerInterface[] controllers = new ControllerInterface[1];
        controllers[0] = new CarWheelController(new CarWheelService(resourcequeue, wheels));



        //Gives status to outside console
        WebStatus webstatus = new WebStatus(resourcequeue, wheels, false);
        webstatus.start();

        new CMDGUI(controllers);
        //When CMDGUI ends we terminate our WebStatus thread.
        System.out.println("Interrupting");
        webstatus.close();
    }
}
