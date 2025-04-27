package pi_project.Farouk.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Cours;
import pi_project.Farouk.Services.CoursService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.util.Properties;

public class ParentController {
    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private VBox coursesContainer;
    @FXML
    private ScrollPane scrollPane;

    private String uploadPath;

    @FXML

    private final CoursService coursService = new CoursService();

    @FXML
    public void initialize() {
        try {
            loadConfig();
            // Populate class dropdown
            List<String> classNames = coursService.getAllClassNames();
            classComboBox.setItems(FXCollections.observableArrayList(classNames));

            // Set up listener for class selection changes
            classComboBox.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> loadCoursesForClass(newVal));

            // Configure scroll pane
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load class names: " + e.getMessage());
        }
    }

    private void loadConfig() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            uploadPath = props.getProperty("upload.path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCoursesForClass(String className) {
        if (className == null || className.isEmpty()) {
            return;
        }

        try {
            // Clear previous content
            coursesContainer.getChildren().clear();

            // Get courses grouped by subject
            Map<String, List<Cours>> coursesBySubject = coursService.getCoursesByMatiereAndClass(className);

            if (coursesBySubject.isEmpty()) {
                Label noCoursesLabel = new Label("No courses available for " + className);
                noCoursesLabel.getStyleClass().add("no-courses-label");
                coursesContainer.getChildren().add(noCoursesLabel);
                return;
            }

            // Create UI elements for each subject and its courses
            for (Map.Entry<String, List<Cours>> entry : coursesBySubject.entrySet()) {
                String subjectName = entry.getKey();
                List<Cours> courses = entry.getValue();

                // Create subject header
                Label subjectLabel = new Label(subjectName);
                subjectLabel.getStyleClass().add("subject-header");

                // Create container for this subject's courses
                VBox subjectCoursesBox = new VBox(5);
                subjectCoursesBox.getStyleClass().add("subject-courses");

                // Add each course
                for (Cours course : courses) {
                    HBox courseBox = createCourseBox(course);
                    subjectCoursesBox.getChildren().add(courseBox);
                }

                // Add to main container
                coursesContainer.getChildren().addAll(subjectLabel, subjectCoursesBox);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load courses: " + e.getMessage());
        }
    }

//    private HBox createCourseBox(Cours course) {
//        HBox box = new HBox(20);
//        box.getStyleClass().add("course-box");
//
//        // Chapter and name
//        Label infoLabel = new Label(
//                String.format("%d. %s", course.getChapterNumber(), course.getName())
//        );
//        infoLabel.getStyleClass().add("course-info");
//
//        // PDF button
//        Button pdfButton = new Button("View PDF");
//        pdfButton.getStyleClass().add("pdf-button");
//        pdfButton.setOnAction(e -> openPdf(course.getPdfFilename()));
//
//        box.getChildren().addAll(infoLabel, pdfButton);
//        return box;
//    }
    private HBox createCourseBox(Cours course) {
        HBox box = new HBox(20);
        box.getStyleClass().add("course-item");
        box.setAlignment(Pos.CENTER_LEFT);

        Label infoLabel = new Label(
                String.format("%d. %s", course.getChapterNumber(), course.getName())
        );
        infoLabel.getStyleClass().add("course-header");

        Button pdfButton = new Button("View PDF");
        pdfButton.getStyleClass().add("course-button");
        pdfButton.setOnAction(e -> openPdf(course.getPdfFilename()));

        box.getChildren().addAll(infoLabel, pdfButton);
        return box;
    }


    private void openPdf(String pdfFilename) {
        if (uploadPath == null || uploadPath.isEmpty()) {
            showAlert("Configuration Error", "Upload path not set.");
            return;
        }

        String pdfFullPath = "file:///" + uploadPath + "/" + pdfFilename;
        pdfFullPath = pdfFullPath.replace("\\", "/"); // Windows path fix

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(pdfFullPath));
            } else {
                showAlert("Error", "Desktop is not supported. Cannot open PDF.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to open PDF: " + e.getMessage());
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleChatButtonClick() {
        try {
            // Load the chat page (chat.fxml)
            Parent root = FXMLLoader.load(getClass().getResource("/Farouk/chat.fxml"));
            // Create a new Stage (window)
            Stage stage = new Stage();
            stage.setTitle("Chat");
            stage.setScene(new Scene(root, 800, 600)); // You can adjust the size here
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to open chat page: " + e.getMessage());
        }
    }

}