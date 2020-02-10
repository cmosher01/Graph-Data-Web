package nu.mine.mosher.app.sample;

import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.*;

/*
 * Entity classes must satisfy these requirements:
 * 1. is serializable
 * 2. has Long id property
 * 4. has no properties that are collections (other than collections of references-to-entities)
 */

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue Long id;

    String description;

    @Relationship(type=Role.TYPE, direction=Relationship.INCOMING) Set<Role> players = new HashSet<>();

    @Override
    public String toString() {
        return this.description;
    }
}
