package nu.mine.mosher.graph.sample.model;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.io.Serializable;
import java.util.UUID;

@RelationshipEntity(type=Role.TYPE)
public class Role implements Serializable, Comparable<Role> {
    public static final String TYPE = "Role";
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue Long id;
    @Version Long version;
    @Convert(UuidStringConverter.class) @Index(unique=true) @Id UUID uuid;

    @Property String description;

    @StartNode Persona persona;
    @EndNode Event event;

    @Override
    public String toString() {
        return this.description;
    }

    @Override
    public int compareTo(Role that) {
        return this.description.compareTo(that.description);
    }
}
