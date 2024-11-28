package de.malkusch.ha.shared.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.malkusch.ha.automation.model.TrashCollection;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_INDEX;
import static java.util.Locale.GERMANY;

@Service
public class TrashCollectionFormatter {

    private final ObjectWriter writer;
    private final ObjectMapper mapper;

    public TrashCollectionFormatter(ObjectMapper mapper) {
        this.mapper = mapper;
        this.writer = mapper.writer().with(WRITE_ENUMS_USING_INDEX);
    }

    public TrashCollection parseJson(String json) {
        try {
            return mapper.readValue(json, TrashCollection.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can't parse TrashCollection: " + json, e);
        }
    }

    public String json(TrashCollection trashCollection) {
        try {
            return writer.writeValueAsString(trashCollection);
        } catch (JsonProcessingException e) {
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
