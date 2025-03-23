package Model;

import Utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExamStore {
    private static final String DATA_FILE = "exams.json";
    private static List<Exam> examList = new ArrayList<>();

    static {
        loadFromFile();
    }

    private static void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            Exam[] arr = gson.fromJson(reader, Exam[].class);
            if (arr != null) {
                examList.clear();
                for (Exam e : arr) {
                    examList.add(e);
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
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            gson.toJson(examList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void addExam(Exam exam) {
        examList.add(exam);
        saveToFile();
    }

    public static List<Exam> getAllExams() {
        return examList;
    }

    public static List<Exam> getExamsByTeacher(String teacherUsername) {
        return examList.stream()
                .filter(e -> e.getTeacherUsername() != null &&
                        e.getTeacherUsername().equalsIgnoreCase(teacherUsername))
                .collect(Collectors.toList());
    }

    public static boolean removeExam(String examId) {
        for (int i = 0; i < examList.size(); i++) {
            if (examList.get(i).getExamId().equalsIgnoreCase(examId)) {
                examList.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public static void updateExam(Exam updatedExam) {
        for (int i = 0; i < examList.size(); i++) {
            if (examList.get(i).getExamId().equalsIgnoreCase(updatedExam.getExamId())) {
                examList.set(i, updatedExam);
                break;
            }
        }
        saveToFile();
    }
}
