package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectStore {

    private static final String DATA_FILE = "subjects.json";
    private static List<Subject> subjectList = new ArrayList<>();

    static {
        loadFromFile();
    }

    private static void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Subject[] subjects = gson.fromJson(reader, Subject[].class);
            if (subjects != null) {
                for (Subject s : subjects) {
                    subjectList.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(subjectList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addSubject(Subject subject) {
        subjectList.add(subject);
        saveToFile();
    }

    public static List<Subject> getAllSubjects() {
        return subjectList;
    }

    public static Subject findByName(String name) {
        for (Subject s : subjectList) {
            if (s.getSubjectName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    public static boolean removeSubject(String name) {
        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getSubjectName().equalsIgnoreCase(name)) {
                subjectList.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public static void updateSubject(Subject updatedSubject) {
        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getSubjectName().equalsIgnoreCase(updatedSubject.getSubjectName())) {
                subjectList.set(i, updatedSubject);
                break;
            }
        }
        saveToFile();
    }
}
