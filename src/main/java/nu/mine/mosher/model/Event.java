package nu.mine.mosher.model;

import java.io.Serializable;
import java.util.*;

/*
 * Entity classes must satisfy these requirements:
 * 1. is serializable
 * 2. has Long id property
 * 3. has "display" property to show it in a list
 * 4. has no properties that are collections (other than collections of references-to-entities)
 */

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    public Long id;
    public String description;
    public Set<Role> players = new HashSet<>();

    public String getDisplay() {
        return "["+this.description+"]";
    }
//    public void addRole(final Role role) {
//        this.players.add(Objects.requireNonNull(role));
//    }
//
//    @Override
//    public String toString() {
//        return "Event{id='" + id + "'" +
//            ",description='" + description.replace("'","''") + "" +
//            '}';
//    }
}
