package de.malkusch.ha.shared.infrastructure;

import de.malkusch.ha.automation.model.TrashCollection;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.util.Locale.GERMANY;
import static tools.jackson.databind.cfg.EnumFeature.WRITE_ENUMS_USING_INDEX;

@Service
public class TrashCollectionFormatter {

    private final ObjectMapper mapper;
    private final ObjectWriter writer;

    public TrashCollectionFormatter(ObjectMapper mapper) {
        this.mapper = mapper;
        this.writer = mapper.writer(WRITE_ENUMS_USING_INDEX);
    }

    public TrashCollection parseJson(String json) {
        try {
            return mapper.readValue(json, TrashCollection.class);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Can't parse TrashCollection: " + json, e);
        }
    }

    public String json(TrashCollection trashCollection) {
        try {
            return writer.writeValueAsString(trashCollection);
        } catch (JacksonException e) {
            throw new IllegalStateException("Can't format TrashCollection: " + trashCollection, e);
        }
    }

    public static String trashCollection(TrashCollection trashCollection) {
        return String.format("%s:\t%s", //
                date(trashCollection), //
                trashCans(trashCollection));
    }

    public static String trashCans(TrashCollection trashCollection) {
        var cans = trashCollection.trashCans().stream().sorted().toArray();
        return Arrays.toString(cans);
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("E d.M.uu", GERMANY);

    public static String date(TrashCollection trashCollection) {
        return DATE_FORMAT.format(trashCollection.date());
    }
}
