package de.malkusch.ha.shared.infrastructure;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.malkusch.ha.automation.model.TrashCollection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrashCollectionFormatter {

    private final ObjectMapper mapper;

    public TrashCollection parse(String trashCollection) {
        try {
            return mapper.readValue(trashCollection, TrashCollection.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can't parse TrashCollection: " + trashCollection, e);
        }
    }

    public String format(TrashCollection trashCollection) {
        try {
            return mapper.writeValueAsString(trashCollection);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Can't format TrashCollection: " + trashCollection, e);
        }
    }
}
