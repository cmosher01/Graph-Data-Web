package nu.mine.mosher.graph.sample.model;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.io.Serializable;
import java.util.*;

/*
 * Entity classes must satisfy these requirements:
 *
 * * is Serializable
 * * Long id
 * * Long version
 * * UUID uuid
 * * has no properties that are collections (other than collections of references-to-entities)
 */

@NodeEntity(label=Event.TYPE)
public class Event implements Serializable {
    public static final String TYPE = "Event";
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue Long id;
    @Version Long version;
    @Convert(UuidStringConverter.class) @Index(unique=true) @Id UUID uuid;

    @Property String description;

    @Relationship(type=Role.TYPE, direction=Relationship.INCOMING) TreeSet<Role> players = new TreeSet<>();

    @Override
    public String toString() {
        return this.description;
    }
}
