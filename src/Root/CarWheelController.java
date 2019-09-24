package Root;

public class CarWheelController implements ControllerInterface {
    MenuItem[] menuItems;
    String location = "/carwheel/";
    String name = "CarWheelController";
    MenuItem controller;
    CarWheelService service;

    public CarWheelController(CarWheelService service){
        this.service = service;
        menuItems = new MenuItem[4];
        menuItems[0]= new MenuItem("Add job",location + "add");
        menuItems[1]= new MenuItem("Clear jobs",location + "clear");
        menuItems[2]= new MenuItem("Stop belt",location + "stop");
        menuItems[3]= new MenuItem("Tilbage","/");
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
                    case "add":
                        return new Page(menuItems, null, get_add());
                    case "clear":
                        return new Page(menuItems, null, get_clear());
                    case "stop":
                        return new Page(menuItems, null, get_stop());
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
                case "post_clear":
                    return post_clear(form);
                case "post_stop":
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


    private Form get_add(){
        return null;
    }

    private String[] post_add(Form form){
        /*
        String[] result = new String[1];
        result[0] = "Der er sket en fejl brugerne er ikke oprettet.";
        Question[] q = form.getQuestions();
        if(q.length == 1){
            service.create(q[0].getAnswer());
            result[0] = "Brugeren er oprettet";
        }

        return result;
        */
        return null;
    }

    private Form get_clear(){
        return null;
    }

    private String[] post_clear(Form form){
        /*
        String[] result = new String[1];
        result[0] = "Der er sket en fejl brugerne er ikke oprettet.";
        Question[] q = form.getQuestions();
        if(q.length == 1){
            service.create(q[0].getAnswer());
            result[0] = "Brugeren er oprettet";
        }

        return result;
        */
        return null;
    }

    private Form get_stop(){
        return null;
    }

    private String[] post_stop(Form form){
        /*
        String[] result = new String[1];
        result[0] = "Der er sket en fejl brugerne er ikke oprettet.";
        Question[] q = form.getQuestions();
        if(q.length == 1){
            service.create(q[0].getAnswer());
            result[0] = "Brugeren er oprettet";
        }

        return result;
        */
        return null;
    }


}
