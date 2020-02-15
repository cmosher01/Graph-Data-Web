package nu.mine.mosher.graph.sample.model;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@NodeEntity(label=Persona.TYPE)
public class Persona implements Serializable {
    public static final String TYPE = "Persona";
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue Long id;
    @Version Long version;
    @Convert(UuidStringConverter.class) @Index(unique=true) @Id UUID uuid;
    @Index() ZonedDateTime utcCreated;
    @Index() ZonedDateTime utcModified;

    @Property String description;

    @Relationship(type=Role.TYPE) TreeSet<Role> hadRolesIn = new TreeSet<>();

    @Override
    public String toString() {
        return this.description;
    }
}
