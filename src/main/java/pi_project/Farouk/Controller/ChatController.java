package pi_project.Farouk.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.input.MouseEvent;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ChatController {
    @FXML
    private TextFlow chatFlow;
    @FXML
    private TextField inputField;
    @FXML
    private TextArea chatArea;

    private final HttpClient client = HttpClient.newHttpClient();
    private final String OLLAMA_URL = "http://localhost:11434/api/generate";

    @FXML
    private void initialize() {
        chatArea.setEditable(false);
        chatArea.appendText("AI: Hello! How can I help you today?\n\n");
    }

    @FXML
    private void handleSend() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty()) {
            chatArea.appendText("You: " + userMessage + "\n");
            inputField.clear();

            // Disable input while processing
            inputField.setDisable(true);

            // Call Ollama async
            generateResponse(userMessage).thenAccept(response -> {
                javafx.application.Platform.runLater(() -> {
                    chatArea.appendText("AI: " + response + "\n\n");
                    inputField.setDisable(false);
                    inputField.requestFocus();
                });
            });
        }
    }

    @FXML
    private void handleUploadPdf(MouseEvent event) {
        // Open file dialog to select a PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            System.out.println("File selected: " + file.getAbsolutePath());  // Add this to check the selected file
            String pdfText = extractTextFromPdf(file);

            if (pdfText.isEmpty()) {
                chatArea.appendText("AI: No text extracted from the PDF.\n");
            } else {
                // Call Ollama API with extracted PDF text, but do NOT display it right away
                generateResponse(pdfText).thenAccept(response -> {
                    javafx.application.Platform.runLater(() -> {
                        chatArea.appendText("You uploaded a PDF.\n");  // Optional, to let the user know the file was uploaded
                        chatArea.appendText("AI: " + response + "\n\n");
                        inputField.requestFocus();
                    });
                });
            }
        } else {
            System.out.println("No file selected.");
        }
    }



    private String extractTextFromPdf(File file) {
        StringBuilder text = new StringBuilder();
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text.append(stripper.getText(document));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text from PDF.";
        }
        if (text.toString().isEmpty()) {
            return "The PDF file is empty or unreadable.";
        }
        // If PDF text is too long, you might want to trim it.
        if (text.length() > 2000) {
            text.setLength(2000); // Limiting the text length to 2000 characters
        }
        return text.toString();
    }




    private CompletableFuture<String> generateResponse(String prompt) {
        String requestBody = String.format("""
    {
        "model": "llama2",
        "prompt": "%s",
        "stream": false
    }
    """, escapeJson(prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("API Response Status: " + response.statusCode());  // Log status code
                    System.out.println("API Response Body: " + response.body());  // Log response body

                    if (response.statusCode() == 200) {
                        return parseOllamaResponse(response.body());
                    } else {
                        return "Sorry, I encountered an error processing your request.";
                    }
                });
    }

    private String parseOllamaResponse(String jsonResponse) {
        try {
            // Check for a valid response format from Ollama API
            int start = jsonResponse.indexOf("\"response\":\"") + 12;
            int end = jsonResponse.indexOf("\"", start);
            if (start != -1 && end != -1) {
                return jsonResponse.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            } else {
                return "Error parsing the API response.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing the response from the AI.";
        }
    }

    private String escapeJson(String input) {
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
