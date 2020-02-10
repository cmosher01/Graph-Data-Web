package nu.mine.mosher.app.sample;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.io.Serializable;
import java.util.*;

@NodeEntity(label=Persona.TYPE)
public class Persona implements Serializable {
    public static final String TYPE = "Persona";
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue Long id;
    @Version Long version;
    @Convert(UuidStringConverter.class) @Index(unique=true) @Id UUID uuid;

    @Property String description;

    @Relationship(type=Role.TYPE) TreeSet<Role> hadRolesIn = new TreeSet<>();

//    Persona() {
//    }
//
//    Persona(final String description) {
//        this.description = description;
//        this.uuid = UUID.randomUUID();
//    }

    @Override
    public String toString() {
        return this.description;
    }

//    @Override
//    public String toString() {
//        return String.format("%s{id=%d,version=%d,uuid=%s,description=%s}", TYPE, id, version, uuid, description);
//    }
}
