package Root;

import java.io.Serializable;

/**
 * A menu item is a name and a location. The location can be given to a controller to get menuitems, text or a Form (embedded in a page object).
 * */
public class MenuItem implements Serializable {
    private String name;
    private String location;

    public MenuItem(String name,String location){
        setName(name);
        setLocation(location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
