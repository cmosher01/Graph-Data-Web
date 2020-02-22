package nu.mine.mosher.graph.sample.imdb;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.TreeSet;

@NodeEntity(label=Person.TYPE)
public class Person extends GraphEntity implements Serializable {
    public static final String TYPE = "Person";

    @Property public String primaryName;
    @Property public int birthYear;
    @Property public int deathYear;

    @Relationship(type=HadRoleIn.TYPE) public TreeSet<HadRoleIn> roles = new TreeSet<>();

    @Override
    public String toString() {
        return this.primaryName;
    }
}
