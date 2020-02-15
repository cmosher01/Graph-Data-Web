package nu.mine.mosher.graph.sample.model;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;

@RelationshipEntity(type=At.TYPE)
public class At extends GraphEntity implements Serializable, Comparable<At> {
    public static final String TYPE = "WAS_AT";

    @Property public String notes;

    @StartNode public Event event;
    @EndNode public Place place;

    @Override
    public String toString() {
        return this.notes;
    }

    @Override
    public int compareTo(At that) {
        return this.notes.compareTo(that.notes);
    }
}
