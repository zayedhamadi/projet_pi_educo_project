package pi_project.Farouk.Controller;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class ChatControllerPdf {

    @FXML
    private TextField questionField;

    @FXML
    private TextArea responseArea;

    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    public void onAsk() {
        String question = questionField.getText().trim();
        if (question.isEmpty()) {
            responseArea.setText("Please enter a question.");
            return;
        }

        String json = String.format("{\"query\":\"%s\"}", question.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/ask"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    javafx.application.Platform.runLater(() -> responseArea.setText(response));
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> responseArea.setText("Error: " + ex.getMessage()));
                    return null;
                });
    }

}
