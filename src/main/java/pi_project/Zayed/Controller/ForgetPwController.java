package pi_project.Zayed.Controller;



import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Utils.Constant;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ForgetPwController {


    private static final Logger LOGGER = Logger.getLogger(ForgetPwController.class.getName());

    @FXML
    AnchorPane s;

    @FXML
    private TextField email;

    @FXML
    public void goingTologinPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/login.fxml"));
            Parent root = loader.load();


            Stage currentStage = (Stage) s.getScene().getWindow();


            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle(" Se connecter");
            currentStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de router a la page Se connecter ", "Erreur d aller a la page Se connecter");
        }
    }

    @FXML
    public void forgetPassword() {
        try {
            String mail = email.getText().trim();
            if (mail.isEmpty()) {
                showWarning("Champs requis", "Veuillez remplir le champ email.");
                return;
            }
            if (!mail.matches(".+@.+\\..+")) {
                showWarning("Email invalide", "Format d'email incorrect.");
                return;
            }

            AuthenticationImpl authentication = new AuthenticationImpl();
            authentication.forgetPassword(mail);


            LOGGER.info("Mot de passe oublié - email envoyé à : " + mail);
            Platform.runLater(() -> Constant.showAlert(Alert.AlertType.INFORMATION, "Succès", "Veuillez consulter votre mail pour votre nouveau mot de passe", "Email envoyé."));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du mot de passe", e);
            Constant.showAlert(Alert.AlertType.ERROR, "Échec", "Une erreur est survenue. Veuillez réessayer.", "Erreur");
        }
    }

    private void showWarning(String title, String message) {
        Platform.runLater(() -> Constant.showAlert(Alert.AlertType.WARNING, title, message, "Échec"));
    }

}