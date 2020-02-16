package nu.mine.mosher.graph.datawebapp.util;

import org.apache.wicket.util.convert.converter.ZonedDateTimeConverter;

import java.time.format.*;

public class IsoDateConverter extends ZonedDateTimeConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    protected DateTimeFormatter getDateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }
}
