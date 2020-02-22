package nu.mine.mosher.graph.sample.imdb;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.TreeSet;

@NodeEntity(label=Movie.TYPE)
public class Movie extends GraphEntity implements Serializable {
    public static final String TYPE = "Movie";

    @Property public String titleType;
    @Property public String primaryTitle;
    @Property public int startYear;
    @Property public int runtimeMinutes;

    @Relationship(type=HadRoleIn.TYPE, direction=Relationship.INCOMING) public TreeSet<HadRoleIn> castAndCrew = new TreeSet<>();

    @Override
    public String toString() {
        return this.primaryTitle;
    }
}
