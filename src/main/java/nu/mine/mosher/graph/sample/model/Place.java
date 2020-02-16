package nu.mine.mosher.graph.sample.model;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.*;

@NodeEntity(label=Place.TYPE)
public class Place extends GraphEntity implements Serializable {
    public static final String TYPE = "Place";

    @Property public String description;
    @Property public boolean defunct;

    @Relationship(type=At.TYPE, direction=Relationship.INCOMING) public TreeSet<At> events = new TreeSet<>();

    @Override
    public String toString() {
        return this.description;
    }
}
