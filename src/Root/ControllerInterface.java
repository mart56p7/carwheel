package Root;

import java.io.Serializable;

public interface ControllerInterface extends Serializable {
    /**
     * Returns a page if the controller has access to the location, else returns null
     * */
    Page getPage(String location);

    /**
     * Returns a String array or null. postForm should only be called on the controller that has given the page the Form is from.
     * */
    String[] postForm(Form form);

    /**
     * Returns the menu items the controller is owning.
     * */
    MenuItem getController();

    /**
     * Returns the name of the controller
     * */
    String getName();
}
