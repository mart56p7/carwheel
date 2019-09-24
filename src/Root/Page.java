package Root;

import java.io.Serializable;

/**
 * The page object is used between the View and the controllers. The page object containts Text that is shown as the first thing when the view shows the page
 * The next thing the view shows is the Form, when the form is filled out, the controller the Page came from is called with the form and any text result is shown to the user
 * The last thing that is shown is the MenuItems that are part of the Page
 * */
public class Page implements Serializable {
    String[] text;
    Form form;
    MenuItem[] menuitems;

    public Page(MenuItem[] menuitems){
        this(menuitems, null);
    }

    public Page(MenuItem[] menuitems, String[] text){
        this(menuitems, text, null);
    }

    public Page(MenuItem[] menuitems, String[] text, Form form){
        this.menuitems = menuitems;
        this.text = text;
        this.form = form;
    }
    public MenuItem[] getMenuItems(){
        return menuitems;
    }

    public String[] getText(){
        return text;
    }

    public Form getForm(){
        return form;
    }


}
