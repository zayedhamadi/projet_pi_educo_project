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
        // Initialiser les événements des boutons
        saveButton.setOnAction(this::ajouterReclamation);
        cancelButton.setOnAction(e -> annuler());

        // Exemple provisoire d'utilisateur connecté (à remplacer dynamiquement)
        userConnecte.setId(2); // Remplacer par l'ID réel de l'utilisateur connecté
    }

    private void ajouterReclamation(ActionEvent event) {
        // Récupérer les valeurs des champs
        String titre = TitreField.getText().trim();
        String description = descriptionField.getText().trim();

        // Validation des champs
        if (titre.isEmpty() || description.isEmpty()) {
            afficherAlerte("Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        // Validation de la longueur du titre
        if (titre.length() < 5 || titre.length() > 50) {
            afficherAlerte("Titre invalide", "Le titre doit contenir entre 5 et 50 caractères.");
            return;
        }

        // Validation de la longueur de la description
        if (description.length() < 10) {
            afficherAlerte("Description invalide", "La description doit contenir au moins 10 caractères.");
            return;
        }

        // Créer une nouvelle réclamation
        reclamation nouvelleReclamation = new reclamation();
        nouvelleReclamation.setTitre(titre);
        nouvelleReclamation.setDescription(description);
        nouvelleReclamation.setDateDeCreation(LocalDate.now());
        nouvelleReclamation.setStatut(Statut.EN_ATTENTE); // Par défaut
        nouvelleReclamation.setUser(userConnecte);

        // Ajouter la réclamation via le service
        reclamationService.ajouter(nouvelleReclamation);

        // Afficher une alerte de succès
        afficherAlerte("Succès", "Réclamation ajoutée avec succès !");

        // Réinitialiser les champs du formulaire
        viderChamps();

        // Fermer la fenêtre après l'ajout
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    private void annuler() {
        // Réinitialiser les champs sans enregistrer
        viderChamps();
    }

    private void viderChamps() {
        // Effacer les champs de saisie
        TitreField.clear();
        descriptionField.clear();
    }

    private void afficherAlerte(String titre, String message) {
        // Afficher une alerte avec un titre et un message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
