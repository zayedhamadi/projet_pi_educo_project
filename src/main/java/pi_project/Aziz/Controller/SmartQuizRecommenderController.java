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
    private boolean isBotTyping = false;

    public SmartQuizRecommenderController() {
        this.geminiService = new GeminiService("AIzaSyAxrw3a_a6eyzcLZRMZHNoPOQptA6twJ-k");
        this.quizService = new QuizService();
    }

    public void initialize() {
        try {
            availableQuizzes = quizService.recuperer();
            showWelcomeMessage();
        } catch (Exception e) {
            appendToChat("System", "Error loading quizzes: " + e.getMessage());
        }
    }

    private void showWelcomeMessage() {
        String subjects = availableQuizzes.stream()
                .map(Quiz::getMatiereName)
                .distinct()
                .collect(Collectors.joining(", "));

        appendToChat("System", "Welcome to Quiz Recommender!");
        appendToChat("System", "Available subjects: " + subjects);
        appendToChat("System", "What would you like to practice today?");
        appendToChat("System", "Type 'list' to see all available quizzes.");
    }

    @FXML
    private void handleSend() {
        String userMessage = userInputField.getText().trim();
        if (userMessage.isEmpty() || isBotTyping) return;

        appendToChat("You", userMessage);
        userInputField.clear();
        disableInput(true);

        new Thread(() -> {
            try {
                String response;
                if (userMessage.equalsIgnoreCase("list")) {
                    response = listAllQuizzes();
                    appendToChat("Recommender", response);
                } else {
                    response = getPersonalizedRecommendation(userMessage);
                    appendToChat("Recommender", response);
                }
            } catch (Exception e) {
                appendToChat("System", "Error: " + e.getMessage());
            } finally {
                disableInput(false);
            }
        }).start();
    }

    private void appendToChat(String sender, String message) {
        Platform.runLater(() -> {
            if (sender.equals("Recommender")) {
                isBotTyping = true;
                simulateTypingEffect(sender, message);
            } else {
                chatArea.appendText(sender + ": " + message + "\n\n");
                chatArea.end();
            }
        });
    }

    private void simulateTypingEffect(String sender, String message) {
        new Thread(() -> {
            try {
                String currentText = chatArea.getText();

                // Show typing indicator
                Platform.runLater(() -> {
                    chatArea.appendText(sender + ": ...\n");
                    chatArea.end();
                });

                Thread.sleep(500);

                Platform.runLater(() -> {
                    chatArea.setText(currentText);
                    chatArea.appendText(sender + ": ");
                    chatArea.end();
                });

                // Type out message
                StringBuilder typedMessage = new StringBuilder();
                for (char c : message.toCharArray()) {
                    Thread.sleep(20);

                    final String charToAdd = String.valueOf(c);
                    Platform.runLater(() -> {
                        typedMessage.append(charToAdd);
                        chatArea.setText(currentText + sender + ": " + typedMessage.toString());
                        chatArea.end();
                    });
                }

                Platform.runLater(() -> {
                    chatArea.appendText("\n\n");
                    chatArea.end();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isBotTyping = false;
                disableInput(false);
            }
        }).start();
    }

    private void disableInput(boolean disabled) {
        Platform.runLater(() -> {
            userInputField.setDisable(disabled);
            sendButton.setDisable(disabled);
            if (!disabled) {
                userInputField.requestFocus();
            }
        });
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
}