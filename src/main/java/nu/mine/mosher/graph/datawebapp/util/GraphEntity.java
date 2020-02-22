package nu.mine.mosher.graph.datawebapp.util;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.io.Serializable;
import java.time.*;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class GraphEntity implements Serializable {
    @ReadOnly @Id @GeneratedValue public Long id;
    @ReadOnly @Version public Long version;
    @ReadOnly @Convert(UuidStringConverter.class) @Index(unique=true) public UUID uuid;
    @ReadOnly @Index() public ZonedDateTime utcCreated;
    @ReadOnly @Index() public ZonedDateTime utcModified;
    @ReadOnly transient public ZonedDateTime utcLoaded = ZonedDateTime.now(ZoneOffset.UTC);
}
