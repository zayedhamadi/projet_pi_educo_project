package pi_project.Zayed.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.UserImpl;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Random;
import java.util.Set;

public class Constant {
    public  final String pathImage = "C:/Users/21690/Desktop/projet_pi/symfony_project-/educo_platform/public/uploads/";
    public  final String defaultPathImage = "file:src/main/resources/Zayed/images/default-user.png";
    public  final int nombreDePage= 10;
    private static final Gson gson = new Gson();
    private static final String passwordChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
    private static final int passwordLength = 10;

    public static void showAlert(Alert.AlertType alertType, String message, String headerText, String title) {
        UserImpl.showAlert(alertType, message, headerText, title);
    }

    public static void handleException(Exception e, String message) {
        System.err.println(message + ": " + e.getMessage());
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur", "Erreur lors de la connexion" + ": " + e.getMessage());
        throw new RuntimeException(e);
    }

    public static Set<Role> extractRolesFromJson(String json) {
        Type setType = new TypeToken<Set<Role>>() {
        }.getType();
        return gson.fromJson(json, setType);
    }

    public static String generateRandomPassword() {
        Random rand = new Random();
        StringBuilder password = new StringBuilder(passwordLength);

        for (int i = 0; i < passwordLength; i++) {
            password.append(passwordChars.charAt(rand.nextInt(passwordChars.length())));
        }
        return password.toString();
    }
    public Image loadImageFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        try {
            // Chemin complet pour Symfony
            String fullPath = "C:\\Users\\21690\\Desktop\\projet_pi\\symfony_project-\\educo_platform\\public\\" + path;
            File file = new File(fullPath);

            if (file.exists()) {
                return new Image(file.toURI().toString());
            } else {
                // Image par défaut si aucune image n'est trouvée
                return new Image(getClass().getResourceAsStream("/images/default-profile.png"));
            }
        } catch (Exception e) {
            System.out.println("Erreur de chargement de l'image: " + e.getMessage());
            return new Image(getClass().getResourceAsStream("/images/default-profile.png"));
        }
    }

    public Image getDefaultImage() {
        return new Image(this.defaultPathImage);
    }



}
