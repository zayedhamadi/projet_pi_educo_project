package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class QuizHistoryCardController {
    @FXML
    private HBox cardContainer;
    @FXML
    private Label quizNameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label scoreLabel;
    @FXML private ProgressBar scoreProgress; // Add this line


    public void setQuizData(String quizName, double score) {
        quizNameLabel.setText(quizName);
        scoreLabel.setText(String.format("%.1f%%", score));
        scoreProgress.setProgress(score / 100);

        // Dynamic color scheme
        if (score >= 80) {
            setCardStyle("#e8f5e9", "#2ecc71", "#27ae60");
        } else if (score >= 50) {
            setCardStyle("#fff8e1", "#f39c12", "#e67e22");
        } else {
            setCardStyle("#ffebee", "#e74c3c", "#c0392b");
        }
    }

    private void setCardStyle(String bgColor, String textColor, String progressColor) {
        cardContainer.setStyle("-fx-background-color: " + bgColor + ";");
        quizNameLabel.setStyle("-fx-text-fill: " + textColor + ";");
        scoreLabel.setStyle("-fx-text-fill: " + textColor + ";");
        scoreProgress.setStyle("-fx-accent: " + progressColor + ";");
    }
}