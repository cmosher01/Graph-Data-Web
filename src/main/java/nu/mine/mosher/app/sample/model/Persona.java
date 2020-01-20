package nu.mine.mosher.app.sample.model;

import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.*;

public class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String description;
    @Relationship(type = "HAD_ROLE_IN")
    private Set<Role> hadRolesIn = new HashSet<>();

    @Override
    public String toString() {
        return "persona "+this.description;
    }
}
