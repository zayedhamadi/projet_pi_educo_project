package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Fedi.entites.eleve;

import java.io.IOException;

public class QuizCardController {
    @FXML private Label quizTitle;
    @FXML private Label quizDescription;
    // Other UI elements...

    private Quiz quiz;
    private eleve currentStudent;  // Changed from dummyStudent to currentStudent

    public void setQuizData(Quiz quiz) {
        this.quiz = quiz;
        updateUI();
    }

    public void setCurrentStudent(eleve student) {  // Renamed from setDummyStudent
        this.currentStudent = student;
    }

    private void updateUI() {
        if (quiz != null) {
            quizTitle.setText(quiz.getNom());
            quizDescription.setText(quiz.getDescription());
            // Set other fields...
        }
    }

    @FXML
    private void handleStartQuiz() {
        if (currentStudent == null) {
            showAlert("Error", "No student information available");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/QuizQuestionView.fxml"));
            Parent root = loader.load();

            QuizQuestionsController controller = loader.getController();
            controller.setQuiz(quiz);
            controller.setEleve(currentStudent);  // Pass the actual student

            Stage stage = (Stage) quizTitle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.sizeToScene();
        } catch (IOException e) {
            showAlert("Error", "Failed to load quiz questions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}