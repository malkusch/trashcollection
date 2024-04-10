package de.malkusch.ha.shared.infrastructure;

import static java.util.Locale.GERMANY;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.malkusch.ha.automation.model.TrashCollection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrashCollectionFormatter {

    private final ObjectMapper mapper;

    public TrashCollection parseJson(String json) {
        try {
            return mapper.readValue(json, TrashCollection.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can't parse TrashCollection: " + json, e);
        }
    }

    public String json(TrashCollection trashCollection) {
        try {
            return mapper.writeValueAsString(trashCollection);
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
