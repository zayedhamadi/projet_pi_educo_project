package pi_project.louay.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;
import pi_project.Zayed.Entity.User;

import java.time.LocalDate;

public class ajouterRController {

    @FXML
    private TextField TitreField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final reclamationImp reclamationService = new reclamationImp();

    // ⚠️ À remplacer avec l'utilisateur connecté (actuel)
    private final User userConnecte = new User();

    @FXML
    private void initialize() {
        // Ici, tu peux initialiser les événements des boutons
        saveButton.setOnAction(this::ajouterReclamation);
        cancelButton.setOnAction(e -> annuler());

        // exemple provisoire d'user connecté (à remplacer dynamiquement)
        userConnecte.setId(2); // tu mets ici l'ID réel de l'utilisateur connecté
    }

    private void ajouterReclamation(ActionEvent event) {
        String titre = TitreField.getText().trim();
        String description = descriptionField.getText().trim();

        if (titre.isEmpty() || description.isEmpty()) {
            afficherAlerte("Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        reclamation nouvelleReclamation = new reclamation();
        nouvelleReclamation.setTitre(titre);
        nouvelleReclamation.setDescription(description);
        nouvelleReclamation.setDateDeCreation(LocalDate.now());
        nouvelleReclamation.setStatut(Statut.EN_ATTENTE); // Par défaut
        nouvelleReclamation.setUser(userConnecte);

        reclamationService.ajouter(nouvelleReclamation);
        afficherAlerte("Succès", "Réclamation ajoutée avec succès !");
        viderChamps();
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    private void annuler() {
        viderChamps();
    }

    private void viderChamps() {
        TitreField.clear();
        descriptionField.clear();
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
}
