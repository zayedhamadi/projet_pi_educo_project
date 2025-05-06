package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Fedi.entites.eleve;

import java.io.IOException;

public class QuizCardController {
    @FXML private Pane quizCard;
    @FXML private VBox quizContainer;
    @FXML private Label quizTitle;
    @FXML private Label quizDescription;
    @FXML private Label quizSubject;
    @FXML private Label quizClass;
    @FXML private Button startButton;

    private Quiz quiz;
    private eleve currentStudent;

    @FXML
    public void initialize() {
        // Setup hover effects
        setupHoverEffects();
    }

    public void setQuizData(Quiz quiz) {
        this.quiz = quiz;
        updateUI();
    }

    public void setCurrentStudent(eleve student) {
        this.currentStudent = student;
    }

    private void updateUI() {
        if (quiz != null) {
            quizTitle.setText(quiz.getNom());
            quizDescription.setText(quiz.getDescription());
            quizSubject.setText(quiz.getMatiereName());
            quizClass.setText(quiz.getClasseName());
        }
    }

    private void setupHoverEffects() {
        // Card hover effect
        quizContainer.setOnMouseEntered(e -> {
            quizContainer.getStyleClass().add("quiz-container-hover");
        });

        quizContainer.setOnMouseExited(e -> {
            quizContainer.getStyleClass().remove("quiz-container-hover");
        });

        // Button hover effect
        startButton.setOnMouseEntered(e -> {
            startButton.getStyleClass().add("start-button-hover");
        });

        startButton.setOnMouseExited(e -> {
            startButton.getStyleClass().remove("start-button-hover");
        });
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
            controller.setEleve(currentStudent);

            Stage quizStage = new Stage();
            quizStage.setScene(new Scene(root));
            quizStage.setTitle(quiz.getNom());
            quizStage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load quiz questions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}