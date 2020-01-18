package nu.mine.mosher.model;

import java.io.Serializable;
import java.util.*;

public class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    public Long id;
    public String description;
    public Set<Role> hadRolesIn = new HashSet<>();

    public String getDisplay() {
        return "<"+this.description+">";
    }

//    public void addRole(final Role role) {
//        this.hadRolesIn.add(Objects.requireNonNull(role));
//    }
//
//    @Override
//    public String toString() {
//        return "Persona{id='" + id + "'" +
//            ",description='" + description.replace("'","''") + "" +
//            '}';
//    }
}
