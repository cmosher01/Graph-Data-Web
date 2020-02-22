package nu.mine.mosher.graph.sample.imdb;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;

@RelationshipEntity(type=HadRoleIn.TYPE)
public class HadRoleIn extends GraphEntity implements Serializable, Comparable<HadRoleIn> {
    public static final String TYPE = "HAD_ROLE_IN";

    @Property public String category;
    @Property public String job;

    @StartNode public Person person;
    @EndNode public Movie movie;

    @Override
    public String toString() {
        return String.format("(%s)--[%s]->(%s)", this.person, this.category, this.movie);
    }

    /*
    Warning: if compareTo returns 0, the two relationships are
    considered to be the same, by OGM.
     */
    @Override
    public int compareTo(HadRoleIn that) {
        return this.id.compareTo(that.id);
    }
}
