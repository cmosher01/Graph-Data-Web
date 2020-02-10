package nu.mine.mosher.app.sample;

import org.neo4j.ogm.annotation.*;

import java.io.Serializable;

@RelationshipEntity(type=Role.TYPE)
public class Role implements Serializable {
    public static final String TYPE = "HAD_ROLE_IN";

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue Long id;

    String description;

    @StartNode Persona persona;
    @EndNode Event event;

    @Override
    public String toString() {
        return this.description;
    }
}
