package pi_project.Aziz.Controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import pi_project.Aziz.Entity.Question;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Utils.PdfResultGenerator;
import pi_project.Fedi.entites.eleve;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class QuizResultsController {

    @FXML private Label quizTitleLabel;
    @FXML private Label scoreLabel;
    @FXML private Label percentageLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label feedbackLabel;
    @FXML private VBox detailedResultsContainer; // Add this to your FXML


    private int correctAnswers;
    private int totalQuestions;
    private Quiz quiz;
    private eleve student;

    public void setResults(int correctAnswers, int totalQuestions, Quiz quiz, eleve student,
                           List<String> studentAnswers, List<Question> questions) {
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.quiz = quiz;
        this.student = student;

        updateUIWithResults();
        displayDetailedResults(studentAnswers, questions);
        schedulePdfPopup();
    }

    private void displayDetailedResults(List<String> studentAnswers, List<Question> questions) {
        detailedResultsContainer.getChildren().clear();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String studentAnswer = studentAnswers.get(i);
            String correctAnswer = question.getReponse();
            boolean isCorrect = studentAnswer.equalsIgnoreCase(correctAnswer);

            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-padding: 15; -fx-background-color: " +
                    (isCorrect ? "#e8f5e9" : "#ffebee") + "; " +
                    "-fx-border-color: #bdbdbd; -fx-border-radius: 5;");

            // Question text
            Label questionLabel = new Label((i+1) + ". " + question.getTexte());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            questionLabel.setWrapText(true);

            // Student's answer
            Label studentAnswerLabel = new Label("Your answer: " + studentAnswer);
            studentAnswerLabel.setTextFill(isCorrect ? Color.GREEN : Color.RED);

            // Correct answer (only show if wrong)
            if (!isCorrect) {
                Label correctAnswerLabel = new Label("Correct answer: " + correctAnswer);
                correctAnswerLabel.setTextFill(Color.GREEN);
                questionBox.getChildren().add(correctAnswerLabel);
            }

            // Add all options
            for (String option : question.getOptions()) {
                Label optionLabel = new Label("• " + option);
                if (option.equals(correctAnswer)) {
                    optionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2ecc71;");
                } else if (option.equals(studentAnswer) && !isCorrect) {
                    optionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
                }
                questionBox.getChildren().add(optionLabel);
            }

            questionBox.getChildren().addAll(questionLabel, studentAnswerLabel);
            detailedResultsContainer.getChildren().add(questionBox);
        }
    }
    private void updateUIWithResults() {
        float percentage = calculatePercentage();

        Platform.runLater(() -> {
            quizTitleLabel.setText(quiz.getNom());
            studentNameLabel.setText(student.getPrenom() + " " + student.getNom());
            scoreLabel.setText(correctAnswers + "/" + totalQuestions);
            percentageLabel.setText(String.format("%.1f%%", percentage));
            feedbackLabel.setText(getFeedbackMessage(percentage));
        });
    }

    private void schedulePdfPopup() {
        Platform.runLater(() -> {
            if (isWindowShowing()) {
                PauseTransition delay = new PauseTransition(Duration.millis(100));
                delay.setOnFinished(event -> Platform.runLater(this::showPdfDownloadOption));
                delay.play();
            }
        });
    }

    private void showPdfDownloadOption() {
        if (!isWindowShowing()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Download Results");
        alert.setHeaderText("Would you like to download your results as PDF?");
        alert.setContentText("You can save a copy of your quiz results for future reference.");
        alert.initOwner(quizTitleLabel.getScene().getWindow());

        ButtonType downloadButton = new ButtonType("Download PDF", ButtonBar.ButtonData.OK_DONE);
        ButtonType laterButton = new ButtonType("Not Now", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(downloadButton, laterButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == downloadButton) {
                Platform.runLater(this::handleDownloadPdf);
            }
        });
    }

    private void handleDownloadPdf() {
        try {
            String pdfPath = PdfResultGenerator.generateQuizResultPdf(
                    student, quiz, correctAnswers, totalQuestions);

            if (pdfPath == null) {
                showAlert("Error", "Failed to generate PDF results");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Quiz Results");
            fileChooser.setInitialFileName("quiz_result_" + student.getId() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File destination = fileChooser.showSaveDialog(quizTitleLabel.getScene().getWindow());
            if (destination != null) {
                Files.copy(
                        new File(pdfPath).toPath(),
                        destination.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                showAlert("Success", "Results saved successfully to:\n" + destination.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to save PDF: " + e.getMessage());
        }
    }

    private boolean isWindowShowing() {
        return quizTitleLabel != null
                && quizTitleLabel.getScene() != null
                && quizTitleLabel.getScene().getWindow() != null
                && quizTitleLabel.getScene().getWindow().isShowing();
    }

    private float calculatePercentage() {
        return totalQuestions > 0 ? ((float) correctAnswers / totalQuestions) * 100 : 0;
    }

    private String getFeedbackMessage(float percentage) {
        if (percentage >= 80) {
            return "Excellent work! You've mastered this topic.";
        } else if (percentage >= 60) {
            return "Good job! You passed the quiz.";
        } else {
            return "Keep practicing! Review the material and try again.";
        }
    }

    private void showAlert(String title, String message) {
        if (!isWindowShowing()) {
            return;
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(quizTitleLabel.getScene().getWindow());
            alert.showAndWait();
        });
    }
}