package de.malkusch.ha.automation.infrastructure.calendar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.malkusch.ha.automation.model.TrashCollection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class InMemoryCalendarCache {

    private final Path path;
    private final ObjectMapper mapper;

    public InMemoryCalendarCache(String path) throws IOException {
        this(Paths.get(path));
    }

    public InMemoryCalendarCache(Path path) throws IOException {
        this(path, new ObjectMapper().findAndRegisterModules());
    }

    public InMemoryCalendarCache(Path path, ObjectMapper mapper) throws IOException {
        this.path = path;
        this.mapper = mapper;
    }

    private static record TrashCollections(Collection<TrashCollection> collections) {

    }

    public Collection<TrashCollection> load() throws IOException {
        try (var stream = new FileInputStream(path.toFile())) {
            var collections = mapper.readValue(stream, TrashCollections.class).collections;
            log.debug("Calender loaded from file {}", path);
            return collections;
        }
    }

    public void store(Collection<TrashCollection> collections) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        try (var stream = new FileOutputStream(path.toFile())) {
            mapper.writeValue(stream, new TrashCollections(collections));
        }
        log.debug("Calender stored in file {}", path);
    }
}