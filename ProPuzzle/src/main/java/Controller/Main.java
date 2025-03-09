package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Khởi động ứng dụng...");
        stage.setTitle("ProPuzzle");

        // Kiểm tra file logo
        Image icon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/Image/logo.jpg")));
        stage.getIcons().add(icon);

        // In ra URL của FXML
        URL fxmlUrl = Main.class.getResource("/Controller/LoginView.fxml");
        System.out.println("FXML URL: " + fxmlUrl);
        if (fxmlUrl == null) {
            throw new RuntimeException("Không tìm thấy LoginView.fxml!");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        System.out.println("Cửa sổ hiển thị!");
    }

}
