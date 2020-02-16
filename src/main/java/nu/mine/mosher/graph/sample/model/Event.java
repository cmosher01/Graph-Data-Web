package nu.mine.mosher.graph.sample.model;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.TreeSet;

@NodeEntity(label=Event.TYPE)
public class Event extends GraphEntity implements Serializable {
    public static final String TYPE = "Event";

    @Property public String description;

    @Relationship(type=Role.TYPE, direction=Relationship.INCOMING) public TreeSet<Role> players = new TreeSet<>();
    @Relationship(type=At.TYPE) public At place; // many (events) to one (place)

    @Override
    public String toString() {
        return this.description;
    }
}
