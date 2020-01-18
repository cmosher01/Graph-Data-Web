package nu.mine.mosher.model;

import java.io.Serializable;

public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    public Long id;
    public String description;
    public Persona persona;
    public Event event;

    public String getDisplay() {
        return "-"+this.description+"-";
    }

//    @Override
//    public String toString() {
//        return "Role{id='" + id + "'" +
//            ",description='" + description.replace("'","''") + "" +
//            '}';
//    }
}
