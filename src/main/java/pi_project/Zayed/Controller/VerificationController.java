package pi_project.Zayed.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import pi_project.Zayed.Entity.LoginHistory;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.LoginHistoryImp;
import pi_project.Zayed.Service.securiteLogin;
import pi_project.Zayed.Utils.Constant;

import java.time.LocalDateTime;

public class VerificationController {

    LoginHistoryImp loginHistoryService = new LoginHistoryImp();
    @FXML
    private StackPane s;
    @FXML
    private TextField code;
    @FXML
    private Hyperlink resendLink;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Text timerText;
    @Setter
    private String userEmail;
    private Timeline timer;
    private int countdown = 60;

    @FXML
    public void initialize() {
        startCountdown();
    }

    @FXML
    public void resendCode() {
        progressIndicator.setVisible(true);
        resendLink.setDisable(true);

        new Thread(() -> {
            try {
                securiteLogin securiteLogin = new securiteLogin();
                securiteLogin.generateAndSendVerificationCode(userEmail);

                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    Constant.showAlert(Alert.AlertType.INFORMATION,
                            "Code renvoyé",
                            "Un nouveau code a été envoyé à votre adresse email",
                            "Succès");
                    resetCountdown();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    resendLink.setDisable(false);
                    Constant.handleException(e, "Erreur lors de l'envoi du code");
                });
            }
        }).start();
    }

    private void startCountdown() {
        resendLink.setDisable(true);
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            countdown--;
            timerText.setText("Vous pourrez renvoyer le code dans " + countdown + "s");

            if (countdown <= 0) {
                timer.stop();
                resendLink.setDisable(false);
                timerText.setText("");
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void resetCountdown() {
        countdown = 60;
        startCountdown();
    }

    @FXML
    public void login() {
        String verificationCode = code.getText().trim();

        if (verificationCode.isEmpty()) {
            Constant.showAlert(Alert.AlertType.WARNING,
                    "Champ requis",
                    "Veuillez saisir le code de vérification",
                    "Échec");
            return;
        }

        try {
            securiteLogin securiteLogin = new securiteLogin();
            boolean isCodeValid = securiteLogin.verifyCode(userEmail, verificationCode);

            if (!isCodeValid) {
                AuthenticationImpl authService = new AuthenticationImpl();
                User user = authService.getUserByEmail(userEmail);

                if (user != null && user.getEtat_compte() == EtatCompte.inactive) {
                    Constant.showAlert(Alert.AlertType.ERROR,
                            "Compte désactivé",
                            "Votre compte a été désactivé après 3 tentatives infructueuses. Contactez l'administrateur.",
                            "Échec");
                    return;
                }

                int remainingAttempts = 3 - securiteLogin.getFailedAttemptsCount(userEmail);
                Constant.showAlert(Alert.AlertType.ERROR,
                        "Code invalide",
                        "Code incorrect. Il vous reste " + remainingAttempts + " tentative(s) avant désactivation.",
                        "Échec");
                return;
            }

            // Suite du processus de connexion
            AuthenticationImpl authService = new AuthenticationImpl();
            User user = authService.getUserByEmail(userEmail);
            Role userRole = authService.getUserRole(userEmail);

            if (user == null || userRole == null) {
                Constant.showAlert(Alert.AlertType.ERROR,
                        "Erreur",
                        "Utilisateur non trouvé",
                        "Échec");
                return;
            }

            LoginHistory loginHistory = new LoginHistory(
                    userEmail,
                    LocalDateTime.now(),
                    user.getId()
            );
            this.loginHistoryService.addLoginHistory(loginHistory);
            pi_project.Zayed.Utils.session.setUserSession(user.getId());

            redirectBasedOnRole(userRole);

        } catch (Exception e) {
            Constant.handleException(e, "Une erreur est survenue lors de la vérification");
        }
    }

    private void redirectBasedOnRole(Role userRole) {
        try {
            String fxmlPath = "";
            String title = "";
            String welcomeMessage = "";

            switch (userRole) {
                case Admin:
                    fxmlPath = "/Saif/MainLayout.fxml";
                    title = "Profil Admin";
                    welcomeMessage = "Vous avez accès en tant qu'Admin";
                    break;
                case Enseignant:
                    fxmlPath = "/Aziz/enseignanlayout.fxml";
                    title = "Profil Enseignant";
                    welcomeMessage = "Vous avez accès en tant qu'Enseignant";
                    break;
                case Parent:
                    fxmlPath = "/Saif/NavbarParent.fxml";
                    title = "Profil Parent";
                    welcomeMessage = "Vous avez accès en tant que Parent";
                    break;
                default:
                    throw new IllegalStateException("Rôle non reconnu: " + userRole);
            }

            Constant.showAlert(Alert.AlertType.INFORMATION,
                    "Connexion réussie",
                    "Bienvenue " + userRole,
                    welcomeMessage);

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
    public void retourauthentification() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) s.getScene().getWindow();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Authentification");
            currentStage.show();
        } catch (Exception e) {
            Constant.showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Problème lors du retour à l'authentification",
                    e.getMessage());
        }
    }
}