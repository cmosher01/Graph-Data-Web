package nu.mine.mosher.app.sample;

import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.*;

public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue Long id;

    String description;

    @Relationship(type=Role.TYPE) Set<Role> hadRolesIn = new HashSet<>();

    @Override
    public String toString() {
        return this.description;
    }
}
