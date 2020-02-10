package nu.mine.mosher.app.sample;

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

//    Role() {
//    }
//
//    Role(String description, Persona persona, Event event) {
//        this.description = description;
//        this.uuid = UUID.randomUUID();
//        this.persona = persona;
//        this.event = event;
//    }

//    @Override
//    public String toString() {
//        return String.format("%s{id=%d,version=%d,uuid=%s,description=%s}", TYPE, id, version, uuid, description);
//    }

    @Override
    public String toString() {
        return this.description;
    }

    @Override
    public int compareTo(Role that) {
        return this.description.compareTo(that.description);
    }
}
