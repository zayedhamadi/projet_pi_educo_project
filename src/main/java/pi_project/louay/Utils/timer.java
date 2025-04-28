package pi_project.louay.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class timer {
    private static final String FILE_PATH = "timers.json";
    private static final ObjectMapper mapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    public static Map<Integer, LocalDateTime> readTimers() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new HashMap<>();
            }
            return mapper.readValue(file, new TypeReference<Map<Integer, LocalDateTime>>() {});
        } catch (IOException e) {
            System.err.println("Error reading timers file: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void writeTimers(Map<Integer, LocalDateTime> timers) {
        Objects.requireNonNull(timers, "Timers map cannot be null");
        try {
            mapper.writeValue(new File(FILE_PATH), timers);
        } catch (IOException e) {
            System.err.println("Error writing timers file: " + e.getMessage());
        }
    }

    public static void addTimer(int eventId, int durationSeconds) {
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        Map<Integer, LocalDateTime> timers = new HashMap<>(readTimers());
        timers.put(eventId, LocalDateTime.now().plusSeconds(durationSeconds));
        writeTimers(timers);
    }

    public static LocalDateTime getExpiration(int eventId) {
        return readTimers().get(eventId);
    }

    public static Map<Integer, LocalDateTime> getAllTimers() {
        return Collections.unmodifiableMap(readTimers());
    }

    public static boolean removeTimer(int eventId) {
        Map<Integer, LocalDateTime> timers = new HashMap<>(readTimers());
        boolean removed = timers.remove(eventId) != null;
        if (removed) {
            writeTimers(timers);
        }
        return removed;
    }
}