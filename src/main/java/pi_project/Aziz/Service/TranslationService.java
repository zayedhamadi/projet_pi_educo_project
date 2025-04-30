package pi_project.Aziz.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class TranslationService {
    private static final String LIBRE_TRANSLATE_API = "https://libretranslate.de/translate";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public String translate(String text, String sourceLang, String targetLang) throws IOException, InterruptedException {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String requestBody = String.format(
                "{\"q\":\"%s\",\"source\":\"%s\",\"target\":\"%s\"}",
                text.replace("\"", "\\\""),
                sourceLang,
                targetLang
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LIBRE_TRANSLATE_API))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getString("translatedText");
        } else {
            throw new IOException("Translation failed: " + response.body());
        }
    }
}