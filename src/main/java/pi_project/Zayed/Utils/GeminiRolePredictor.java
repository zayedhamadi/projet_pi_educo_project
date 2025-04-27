package pi_project.Zayed.Utils;

import pi_project.Zayed.Entity.User;

import java.net.http.*;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GeminiRolePredictor {
    private static final String API_KEY = "AIzaSyCBUT_JXZl85uPmwmm4K8wlcIkv1yhIuxM";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    public static String predictRole(User user) throws Exception {
        String prompt = generatePrompt(user);

        String requestBody = """
        {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }]
        }
        """.formatted(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return extractRoleFromResponse(response.body());
    }

    private static String generatePrompt(User user) {
        return String.format("""
            Voici les informations d'un utilisateur :
            - Nom : %s
            - Email : %s
            - Genre : %s
            - État du compte : %s
            - Description : %s
            
            Selon ces informations, quel serait le rôle le plus probable de cet utilisateur (Admin, Enseignant, Parent, Client...) ?
            Réponds seulement par le rôle.
        """, user.getNom(), user.getEmail(), user.getGenre(), user.getEtat_compte(), user.getDescription());
    }

    private static String extractRoleFromResponse(String responseBody) {
        // 🔍 À personnaliser selon le retour exact de Gemini
        int index = responseBody.indexOf("text");
        if (index != -1) {
            int start = responseBody.indexOf(":", index) + 2;
            int end = responseBody.indexOf("\"", start);
            return responseBody.substring(start, end).trim();
        }
        return "Inconnu";
    }
}
