package Model;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AttemptStore {
    private static final String FILE_NAME = "attempts.json";
    private static List<Attempt> attemptList = new ArrayList<>();

    static {
        loadFromFile();
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            Attempt[] arr = gson.fromJson(reader, Attempt[].class);
            if (arr != null) {
                attemptList.addAll(Arrays.asList(arr));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();
            gson.toJson(attemptList, writer);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void addAttempt(Attempt attempt) {
        attemptList.add(attempt);
        saveToFile();
    }

    public static List<Attempt> getAllAttempts() {
        return attemptList;
    }

    public static List<Attempt> getAttemptsByUserAndPractice(String userId, String practiceId) {
        return attemptList.stream()
                .filter(a -> a.getUserId().equalsIgnoreCase(userId) &&
                        a.getPracticeId().equalsIgnoreCase(practiceId))
                .collect(Collectors.toList());
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), FORMATTER);
        }
    }
}
