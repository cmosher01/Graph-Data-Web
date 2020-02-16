package nu.mine.mosher.graph.sample.model;

import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.neo4j.ogm.annotation.*;

import java.io.Serializable;
import java.util.TreeSet;

@NodeEntity(label=Persona.TYPE)
public class Persona extends GraphEntity implements Serializable {
    public static final String TYPE = "Persona";

    @Property public String description;

    @Relationship(type=Role.TYPE) public TreeSet<Role> hadRolesIn = new TreeSet<>();

    @Override
    public String toString() {
        return this.description;
    }
}
