package pi_project.Aziz.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pi_project.Aziz.Service.GeminiService;
import pi_project.Aziz.Service.QuizService;
import pi_project.Aziz.Entity.Quiz;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SmartQuizRecommenderController {
    @FXML private TextArea chatArea;
    @FXML private TextField userInputField;
    @FXML private Button sendButton;

    private final GeminiService geminiService;
    private final QuizService quizService;
    private List<Quiz> availableQuizzes;

    public SmartQuizRecommenderController() {
        this.geminiService = new GeminiService("AIzaSyAxrw3a_a6eyzcLZRMZHNoPOQptA6twJ-k");
        this.quizService = new QuizService();
    }

    public void initialize() {
        try {
            availableQuizzes = quizService.recuperer();
            showWelcomeMessage();
        } catch (Exception e) {
            chatArea.appendText("Error loading quizzes: " + e.getMessage() + "\n");
        }
    }

    private void showWelcomeMessage() {
        String subjects = availableQuizzes.stream()
                .map(Quiz::getMatiereName)
                .distinct()
                .collect(Collectors.joining(", "));

        chatArea.appendText("Welcome to Quiz Recommender!\n");
        chatArea.appendText("Available subjects: " + subjects + "\n\n");
        chatArea.appendText("What would you like to practice today?\n");
    }

    @FXML
    private void handleSend() {
        String userMessage = userInputField.getText().trim();
        if (userMessage.isEmpty()) return;

        appendToChat("You", userMessage);
        userInputField.clear();
        disableInput(true);

        new Thread(() -> {
            try {
                String response;
                if (userMessage.equalsIgnoreCase("list")) {
                    response = listAllQuizzes();
                } else {
                    response = getPersonalizedRecommendation(userMessage);
                }

                Platform.runLater(() -> {
                    appendToChat("Recommender", response);
                    disableInput(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    appendToChat("System", "Error: " + e.getMessage());
                    disableInput(false);
                });
            }
        }).start();
    }

    private String getPersonalizedRecommendation(String userInput) throws IOException {
        String prompt = "Based on this request: '" + userInput + "'\n" +
                "Recommend quizzes from these options:\n" +
                availableQuizzes.stream()
                        .map(q -> "- " + q.getNom() + " (" + q.getMatiereName() + ")")
                        .collect(Collectors.joining("\n")) +
                "\n\nFormat response with:\n" +
                "1. Recommended quiz name\n" +
                "2. Subject\n" +
                "3. Brief description\n" +
                "4. Why it matches the request";

        return geminiService.getResponse(prompt);
    }

    private String listAllQuizzes() {
        return "Available Quizzes:\n" +
                availableQuizzes.stream()
                        .map(q -> String.format(
                                "- %s (Subject: %s, Class: %s)\n  %s",
                                q.getNom(),
                                q.getMatiereName(),
                                q.getClasseName(),
                                q.getDescription()))
                        .collect(Collectors.joining("\n\n"));
    }

    private void appendToChat(String sender, String message) {
        Platform.runLater(() ->
                chatArea.appendText(sender + ": " + message + "\n\n"));
    }

    private void disableInput(boolean disabled) {
        Platform.runLater(() -> {
            userInputField.setDisable(disabled);
            sendButton.setDisable(disabled);
            if (!disabled) userInputField.requestFocus();
        });
    }
}