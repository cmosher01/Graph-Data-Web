package nu.mine.mosher.graph.sample.model;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;

@RelationshipEntity(type=Role.TYPE)
public class Role extends GraphEntity implements Serializable, Comparable<Role> {
    public static final String TYPE = "Role";

    @Property public String description;

    @StartNode public Persona persona;
    @EndNode public Event event;

    @Override
    public String toString() {
        return this.description;
    }

    @Override
    public int compareTo(Role that) {
        return this.description.compareTo(that.description);
    }
}
