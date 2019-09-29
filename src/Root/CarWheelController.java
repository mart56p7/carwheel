package Root;

public class CarWheelController implements ControllerInterface {
    MenuItem[] menuItems;
    String location = "/carwheel/";
    String name = "CarWheelController";
    MenuItem controller;
    CarWheelService service;

    public CarWheelController(CarWheelService service){
        this.service = service;
        menuItems = new MenuItem[5];
        menuItems[0]= new MenuItem("Display job queue",location + "display");
        menuItems[1]= new MenuItem("Add job",location + "add");
        menuItems[2]= new MenuItem("Clear jobs",location + "clear");
        menuItems[3]= new MenuItem("Start/Stop belt",location + "startstop");
        menuItems[4]= new MenuItem("Tilbage","/");
        controller = new MenuItem(name,location);
    }



    /**
     * If the controller manages the location given, return a page else return null
     * */
    @Override
    public Page getPage(String _location) {
        if(_location.equals(location)){
            return new Page(menuItems);
        }
        else{
            if(_location.length() > location.length() && _location.startsWith(location)) {
                String subpath = _location.substring(location.length());
                switch (subpath) {
                    case "display":
                        return get_display();
                    case "add":
                        return get_add();
                    case "clear":
                        return get_clear();
                    case "startstop":
                        return get_stop();
                    default:
                        break;
                }
            }
        }
        return null;
    }


    /**
     * Post a form to the controller, the controller responds with the output text from the request
     * */
    @Override
    public String[] postForm(Form form) {
        String _location = form.getPostTarget();
        if(_location.length() > location.length() && _location.startsWith(location)) {
            String subpath = _location.substring(location.length());
            switch (subpath) {
                case "post_add":
                    return post_add(form);
                case "post_startstop":
                    return post_stop(form);
                default:
                    break;
            }
        }

        return null;
    }

    /**
     * Returns menu items for the controller
     * */
    @Override
    public MenuItem getController() {
        return controller;
    }


    /**
     * Return the name of the controller
     * */
    @Override
    public String getName() {
        return name;
    }


    private Page get_display(){
        FIFO<WheelInterface> jq = service.getQueue();
        String[] rstr = new String[jq.size()+3];
        rstr[0] = "\n";
        if(jq.size() == 0){
            rstr[1] = "The job queue is empty";
        }
        else{
            rstr[1] = "Dispalying jobs in order they will be produced";
        }
        for(int i = 0; i < jq.size(); i++){
            rstr[i+2] = i + ": " + jq.get(i).getName();
        }
        rstr[rstr.length - 1] = "\n";
        return new Page(menuItems, rstr, null);
    }

    private Page get_add(){
        WheelInterface[] wheels = service.getWheels();
        String[] wheelsinfo = new String[wheels.length];
        for(int i = 0; i < wheels.length; i++){
            wheelsinfo[i] = i + ": " + wheels[i].getName();
        }
        Question[] q = new Question[2];
        q[0] = new Question("Select wheel to produce");
        q[1] = new Question("Enter amount");
        return new Page(menuItems, wheelsinfo, new Form(q, location + "post_add"));
    }

    private String[] post_add(Form form){
        String[] rstr = new String[1];
        rstr[0] = "An error occured";
        Question[] q = form.getQuestions();
        if(q.length == 2){
            try{
                WheelInterface[] wheels = service.getWheels();
                //The wheel number
                WheelInterface wheel = wheels[Integer.parseInt(q[0].getAnswer())];
                //The amount of wheels to produce
                int numwheels = Integer.parseInt(q[1].getAnswer());
                service.add(wheel, numwheels);
                rstr[0] = "Added " + numwheels + " of type " + wheel.getName() + " to production queue.";
            }catch(Exception e){
                rstr[0] = "An error occured";
            }

        }
        return rstr;
    }

    private Page get_clear(){
        FIFO<WheelInterface> jq = service.getQueue();
        String[] rstr = new String[2];
        rstr[0] = "\n";
        rstr[1] = "Removed " + jq.size() + " elements from job queue.";
        service.clear();
        return new Page(menuItems, rstr, null);
    }



    private Page get_stop(){
        BeltInterface[] belts = service.getBelts();
        String[] beltinfo = new String[belts.length];
        for(int i = 0; i < belts.length; i++){
            beltinfo[i] = i + ": " + belts[i].getName() + ", current state: " + belts[i].getState();
        }
        Question[] q = new Question[1];
        q[0] = new Question("Select belt to stop, or to wait for orders if INTERRUPTED");
        return new Page(menuItems, beltinfo, new Form(q, location + "post_startstop"));
    }

    private String[] post_stop(Form form){
        String[] rstr = new String[1];
        rstr[0] = "An error occured";
        Question[] q = form.getQuestions();
        if(q.length == 1){
            try{
                int beltnumber = Integer.parseInt(q[0].getAnswer());
                BeltInterface belt = service.getBelts()[beltnumber];
                if(belt.getState() != BeltState.INTERRUPTED){
                    service.stopBelt(beltnumber);
                    rstr[0] = "Forcing " + belt.getName() + " to stop.";
                } else {
                    service.startBelt(beltnumber);
                    rstr[0] = "Setting " + belt.getName() + " to wait for orders.";
                }
            }catch(Exception e){
                rstr[0] = "An error occured";
            }
        }
        return rstr;
    }


}
