package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Question;

import java.io.IOException;

public class DetailsQuestionController {

    @FXML private Label questionLabel;
    @FXML private VBox optionsContainer;
    @FXML private Label reponseLabel;
    @FXML private Label quizLabel;

    private Question currentQuestion;

    public void setQuestion(Question question) {
        this.currentQuestion = question;
        updateUI();
    }

    private void updateUI() {
        if (currentQuestion != null) {
            questionLabel.setText(currentQuestion.getTexte());
            reponseLabel.setText(currentQuestion.getReponse());
            quizLabel.setText(currentQuestion.getQuiz().getNom());

            // Clear existing options
            optionsContainer.getChildren().clear();

            // Add each option as a labeled item
            if (currentQuestion.getOptions() != null) {
                for (String option : currentQuestion.getOptions()) {
                    Label optionLabel = new Label(option);
                    optionLabel.getStyleClass().add("option-label");
                    optionsContainer.getChildren().add(optionLabel);
                }
            }
        }
    }

    @FXML
    private void handleRetour() {
        Stage stage = (Stage) questionLabel.getScene().getWindow();
        stage.close(); // ✅ Close the window
    }


}