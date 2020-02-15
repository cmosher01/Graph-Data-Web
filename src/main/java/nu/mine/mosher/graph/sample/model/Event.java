package nu.mine.mosher.graph.sample.model;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.*;

/*
 * Entity classes must satisfy these requirements:
 *
 * * is Serializable
 * * Long id
 * * Long version
 * * UUID uuid
 * * has no properties that are collections (other than collections of references-to-entities)
 */

@NodeEntity(label=Event.TYPE)
public class Event extends GraphEntity implements Serializable {
    public static final String TYPE = "Event";
    private static final long serialVersionUID = 1L;

    @Property public String description;

    @Relationship(type=Role.TYPE, direction=Relationship.INCOMING) public TreeSet<Role> players = new TreeSet<>();

    @Override
    public String toString() {
        return this.description;
    }
}
