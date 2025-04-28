package pi_project.louay.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import pi_project.Zayed.Utils.session;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;
import pi_project.Zayed.Entity.User;
import pi_project.louay.Utils.captcha;

import java.time.LocalDate;

public class ajouterRController {

    @FXML
    private TextField TitreField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField captchaField;

    @FXML
    private ImageView captchaImage;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final reclamationImp reclamationService = new reclamationImp();

    private String captchaText;

    @FXML
    private void initialize() {
        saveButton.setOnAction(this::ajouterReclamation);
        cancelButton.setOnAction(e -> annuler());

        genererCaptcha();
    }


    private void genererCaptcha() {
        int width = 150;
        int height = 50;


        captchaText = captcha.genererTexteAleatoire(5);


        Image captchaImageGenerated = captcha.genererCaptchaImage(captchaText, width, height);


        captchaImage.setImage(captchaImageGenerated);
    }


    private void ajouterReclamation(ActionEvent event) {
        String titre = TitreField.getText().trim();
        String description = descriptionField.getText().trim();
        String captchaInput = captchaField.getText().trim();

        if (titre.isEmpty() || description.isEmpty() || captchaInput.isEmpty()) {
            afficherAlerte("Champs requis", "Veuillez remplir tous les champs y compris le CAPTCHA.");
            return;
        }

        if (!captchaInput.equals(captchaText)) {
            afficherAlerte("Erreur CAPTCHA", "Le code CAPTCHA est incorrect.");
            captchaField.clear();
            genererCaptcha(); // Régénère un nouveau CAPTCHA
            return;
        }

        if (titre.length() < 5 || titre.length() > 50) {
            afficherAlerte("Titre invalide", "Le titre doit contenir entre 5 et 50 caractères.");
            return;
        }

        if (description.length() < 10) {
            afficherAlerte("Description invalide", "La description doit contenir au moins 10 caractères.");
            return;
        }


        Integer userId = session.getUserSession();
        User userConnecte = reclamationService.getUserService().getSpeceficUser(userId);


        if (userConnecte == null) {
            afficherAlerte("Erreur utilisateur", "Utilisateur non trouvé.");
            return;
        }


        reclamation nouvelleReclamation = new reclamation();
        nouvelleReclamation.setTitre(titre);
        nouvelleReclamation.setDescription(description);
        nouvelleReclamation.setDateDeCreation(LocalDate.now());
        nouvelleReclamation.setStatut(Statut.EN_ATTENTE);
        nouvelleReclamation.setUser(userConnecte); // Utilisation de l'objet User


        reclamationService.ajouter(nouvelleReclamation);

        afficherAlerte("Succès", "Réclamation ajoutée avec succès !");
        viderChamps();
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    private void annuler() {
        viderChamps();
        genererCaptcha();
    }

    private void viderChamps() {
        TitreField.clear();
        descriptionField.clear();
        captchaField.clear();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
