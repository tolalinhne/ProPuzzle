package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionStore {
    private static final String DATA_FILE = "questions.json";
    private static List<Question> questionList = new ArrayList<>();

    static {
        loadFromFile();
    }

    private static void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Question[] arr = gson.fromJson(reader, Question[].class);
            if (arr != null) {
                for (Question q : arr) {
                    questionList.add(q);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(questionList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addQuestion(Question question) {
        questionList.add(question);
        saveToFile();
    }

    public static List<Question> getAllQuestions() {
        return questionList;
    }

    public static Question findById(String questionId) {
        for (Question q : questionList) {
            if (q.getQuestionId().equalsIgnoreCase(questionId)) {
                return q;
            }
        }
        return null;
    }

    public static boolean removeQuestion(String questionId) {
        for (int i = 0; i < questionList.size(); i++) {
            if (questionList.get(i).getQuestionId().equalsIgnoreCase(questionId)) {
                questionList.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public static void updateQuestion(Question updatedQ) {
        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            if (q.getQuestionId().equalsIgnoreCase(updatedQ.getQuestionId())) {
                questionList.set(i, updatedQ);
                break;
            }
        }
        saveToFile();
    }

    public static Question getQuestionById(String id) {
        List<Question> allQuestions = getAllQuestions();
        return allQuestions.stream()
                .filter(q -> q.getQuestionId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

}
