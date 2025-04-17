package pi_project.louay.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

public class modifierRController {

    @FXML
    private Label labelTitre;

    @FXML
    private Label labelDescription;

    @FXML
    private ComboBox<Statut> statutComboBox;

    @FXML
    private Button btnModifier;

    private final reclamationImp reclamationService = new reclamationImp();

    private reclamation reclamationAmodifier;

    private reclamationController reclamationView; // Pour rafraîchir la vue principale

    public void setReclamation(reclamation r) {
        this.reclamationAmodifier = r;

        if (r != null) {
            labelTitre.setText(r.getTitre());
            labelDescription.setText(r.getDescription());
            statutComboBox.setValue(r.getStatut());  // Afficher le statut actuel
        }
    }

    public void setReclamationController(reclamationController controller) {
        this.reclamationView = controller;
    }

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec les valeurs de l'énumération Statut
        statutComboBox.getItems().setAll(Statut.values());
    }

    // Méthode pour modifier le statut de la réclamation
    @FXML
    private void modifierReclamation(ActionEvent event) {
        Statut nouveauStatut = statutComboBox.getValue();

        // Vérifier si un statut a été sélectionné
        if (nouveauStatut == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez sélectionner un statut.");
            return;
        }

        // Si une modification est effectuée (comparaison avec l'ancien statut)
        if (reclamationAmodifier != null && nouveauStatut != reclamationAmodifier.getStatut()) {
            reclamationAmodifier.setStatut(nouveauStatut);
            reclamationService.modifier(reclamationAmodifier);

            // Rafraîchir la table dans le contrôleur principal
            if (reclamationView != null) {
                reclamationView.refreshTable(); // Rafraîchir la table des réclamations
            }

            // Afficher un message de succès et fermer la fenêtre
            showAlert(Alert.AlertType.INFORMATION, "Statut modifié avec succès !");

            // Revenir à la page des réclamations
            revenirAReclamations();
        } else {
            // Si aucune modification n'est détectée
            showAlert(Alert.AlertType.WARNING, "Aucune modification détectée.");
        }
    }

    // Méthode pour revenir à la page des réclamations
    private void revenirAReclamations() {
        try {
            // Charger la page des réclamations (reclamation.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/reclamation.fxml"));
            Parent reclamationViewNode = loader.load();

            // Récupérer le contrôleur de la vue des réclamations
            reclamationController controller = loader.getController();
            controller.refreshTable(); // Rafraîchir la table des réclamations

            // Chercher le StackPane contentPane (le conteneur central de ton layout)
            StackPane contentPane = (StackPane) btnModifier.getScene().lookup("#contentPane");

            // Remplacer le contenu central du StackPane avec la vue des réclamations
            contentPane.getChildren().setAll(reclamationViewNode);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour à la page des réclamations.");
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
