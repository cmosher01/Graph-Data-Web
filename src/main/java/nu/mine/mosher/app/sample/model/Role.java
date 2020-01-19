package nu.mine.mosher.app.sample.model;

import org.neo4j.ogm.annotation.*;

import java.io.Serializable;

@RelationshipEntity(type = "HAD_ROLE_IN")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String description;
    @StartNode
    private Persona persona;
    @EndNode
    private Event event;

    @Override
    public String toString() {
        return "-"+this.description+"-";
    }
}
