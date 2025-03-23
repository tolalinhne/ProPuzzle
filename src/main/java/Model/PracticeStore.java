package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PracticeStore {
    private static final String DATA_FILE = "practices.json";
    private static List<Practice> practiceList = new ArrayList<>();

    static {
        loadFromFile();
    }

    private static void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Practice[] practices = gson.fromJson(reader, Practice[].class);
            if (practices != null) {
                for (Practice p : practices) {
                    practiceList.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(practiceList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addPractice(Practice practice) {
        practiceList.add(practice);
        saveToFile();
    }

    public static List<Practice> getAllPractices() {
        return practiceList;
    }

    public static boolean removePractice(String practiceId) {
        for (int i = 0; i < practiceList.size(); i++) {
            if (practiceList.get(i).getPracticeId().equalsIgnoreCase(practiceId)) {
                practiceList.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public static List<Practice> getPracticesByTeacher(String teacherUsername) {
        return practiceList.stream()
                .filter(p -> p.getTeacherUsername() != null &&
                        p.getTeacherUsername().equalsIgnoreCase(teacherUsername))
                .collect(Collectors.toList());
    }

    public static void updatePractice(Practice updatedPractice) {
        for (int i = 0; i < practiceList.size(); i++) {
            Practice p = practiceList.get(i);
            if (p.getPracticeId().equalsIgnoreCase(updatedPractice.getPracticeId())) {
                practiceList.set(i, updatedPractice);
                break;
            }
        }
        saveToFile();
    }

    private List<Practice> getRecentPractices(int limit) {
        List<Practice> all = PracticeStore.getAllPractices();
        return all.stream()
                .sorted(Comparator.comparing(Practice::getPracticeId).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

}
