package nu.mine.mosher.app.sample.model;

import org.neo4j.ogm.annotation.Relationship;

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
    private Long id;
    private String description;
    @Relationship(type = "HAD_ROLE_IN", direction = Relationship.INCOMING)
    private Set<Role> players = new HashSet<>();

    @Override
    public String toString() {
        return "["+this.description+"]";
    }
}
