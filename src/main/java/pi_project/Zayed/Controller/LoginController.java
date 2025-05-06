package pi_project.Zayed.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.securiteLogin;
import pi_project.Zayed.Utils.Constant;

import java.util.Objects;

public class LoginController {

    @FXML
    private StackPane s;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private ImageView eyeImage;

    private boolean isPasswordVisible = false;
    private TextField visiblePasswordField;

    @FXML
    private void initialize() {
        if (s != null) {
            eyeImage.setOnMouseClicked(event -> togglePasswordVisibility());

            visiblePasswordField = new TextField();
            visiblePasswordField.setManaged(false);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.textProperty().bindBidirectional(password.textProperty());
            visiblePasswordField.promptTextProperty().bind(password.promptTextProperty());
            visiblePasswordField.setStyle("-fx-background-color: transparent; -fx-border-color: #0596ff; -fx-border-width: 0px 0px 2px 0px;");
            visiblePasswordField.setLayoutX(password.getLayoutX());
            visiblePasswordField.setLayoutY(password.getLayoutY());
            visiblePasswordField.setPrefSize(password.getPrefWidth(), password.getPrefHeight());
            s.getChildren().add(visiblePasswordField);
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setVisible(true);
            password.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            isPasswordVisible = false;
            StackPane.setAlignment(eyeImage, Pos.CENTER_RIGHT);
        } else {
            password.setVisible(false);
            password.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            isPasswordVisible = true;
            StackPane.setAlignment(eyeImage, Pos.CENTER_LEFT);
        }
    }

    @FXML
    public void GoingToVerificationCode() {
        String userEmail = email.getText().trim();
        String userPassword = password.getText().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Constant.showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs", "Échec");
            return;
        }

        if (!userEmail.matches(".+@.+\\..+")) {
            Constant.showAlert(Alert.AlertType.WARNING, "Email invalide", "Format d'email incorrect", "Échec");
            return;
        }

        try {
            User user = new User(userEmail, userPassword);
            AuthenticationImpl authService = new AuthenticationImpl();
            boolean isAuthenticated = authService.login(user);

            if (isAuthenticated) {
                securiteLogin securite = new securiteLogin();

                // Vérifier si une session valide existe
                if (securite.isSessionValid(userEmail)) {
                    // Rediriger directement si session valide
                    Role userRole = authService.getUserRole(userEmail);
                    redirectBasedOnRole(userRole);
                    return;
                }

                // Sinon, envoyer un code de vérification
                securite.generateAndSendVerificationCode(userEmail);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/VerificationCode.fxml"));
                Parent root = loader.load();
                VerificationController verificationController = loader.getController();
                verificationController.setUserEmail(userEmail);
                Stage currentStage = (Stage) s.getScene().getWindow();
                Scene scene = new Scene(root);
                currentStage.setScene(scene);
                currentStage.setTitle("Vérification du code");
                currentStage.show();
            } else {
                Constant.showAlert(Alert.AlertType.ERROR, "Échec", "Identifiants incorrects", "Échec");
            }
        } catch (Exception e) {
            Constant.handleException(e, "Une erreur est survenue pendant la connexion");
        }
    }

    private void redirectBasedOnRole(Role userRole) {
        try {
            String fxmlPath = "";
            String title = "";

            switch (userRole) {
                case Admin:
                    fxmlPath = "/Saif/MainLayout.fxml";
                    title = "Profil Admin";
                    break;
                case Enseignant:
                    fxmlPath = "/Aziz/enseignanlayout.fxml";
                    title = "Profil Enseignant";
                    break;
                case Parent:
                    fxmlPath = "/Saif/NavbarParent.fxml";
                    title = "Profil Parent";
                    break;
                default:
                    throw new IllegalStateException("Rôle non reconnu: " + userRole);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage currentStage = (Stage) s.getScene().getWindow();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle(title);
            currentStage.show();

        } catch (Exception e) {
            Constant.handleException(e, "Erreur lors de la redirection");
        }
    }

    @FXML
    public void goingToForgetPwPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/ChoixForgetPw.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) s.getScene().getWindow();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Mot de passe oublié");
            currentStage.show();
        } catch (Exception e) {
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Problème lors de l'accès à la page de réinitialisation", e.getMessage());
        }
    }
}