package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import pi_project.Aziz.Service.*;
import pi_project.Aziz.Entity.*;
import java.io.File;
import java.util.List;

public class PdfQuizController {
    @FXML private Button uploadButton;
    @FXML private TextArea resultArea;
    private QuizGeneratorService quizService;

    // Initialize the service in the constructor
    public PdfQuizController() {
        // Replace "YOUR_API_KEY_HERE" with your actual Gemini API key
        String apiKey = "AIzaSyAxrw3a_a6eyzcLZRMZHNoPOQptA6twJ-k";
        GeminiService geminiService = new GeminiService(apiKey);
        this.quizService = new QuizGeneratorService(geminiService);
    }

    // Keep all other methods exactly the same
    @FXML
    private void handleUpload() {
        if (quizService == null) {
            resultArea.setText("Error: Service not initialized");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            uploadButton.setDisable(true);
            resultArea.setText("Traitement en cours...");

            new Thread(() -> {
                try {
                    QuizGeneratorService.QuizData result =
                            quizService.generateQuizFromPdf(file.getAbsolutePath());

                    javafx.application.Platform.runLater(() -> {
                        displayQuiz(result.quiz, result.questions);
                        uploadButton.setDisable(false);
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        resultArea.setText("Erreur: " + e.getMessage());
                        uploadButton.setDisable(false);
                    });
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void displayQuiz(Quiz quiz, List<Question> questions) {
        StringBuilder sb = new StringBuilder();
        sb.append("QUIZ: ").append(quiz.getNom()).append("\n\n");

        for (Question q : questions) {
            sb.append("Q: ").append(q.getTexte()).append("\n");
            for (String option : q.getOptions()) {
                sb.append("  ").append(option).append("\n");
            }
            sb.append("→ Réponse: ").append(q.getReponse()).append("\n\n");
        }

        resultArea.setText(sb.toString());
    }
}