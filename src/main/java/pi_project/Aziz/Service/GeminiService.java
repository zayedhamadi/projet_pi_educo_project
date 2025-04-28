package pi_project.Aziz.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeminiService {
    // Update to match your quickstart guide exactly
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final String apiKey;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getResponse(String prompt) throws IOException {
        String urlWithKey = API_URL + "?key=" + apiKey;
        URL url = new URL(urlWithKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Update to match the exact JSON format from your quickstart
            String jsonBody = String.format("""
                {
                    "contents": [{
                        "parts": [{"text": "%s"}]
                    }]
                }
                """, escapeJson(prompt));

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            if (status != 200) {
                // Enhanced error handling
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    String errorResponse = br.lines().collect(Collectors.joining());
                    throw new IOException("API Error " + status + ": " + errorResponse);
                }
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String response = br.lines().collect(Collectors.joining());
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                return json.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
            }
        } finally {
            connection.disconnect();
        }
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}