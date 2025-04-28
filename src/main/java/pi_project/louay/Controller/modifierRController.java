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

    private reclamationController reclamationView;

    public void setReclamation(reclamation r) {
        this.reclamationAmodifier = r;

        if (r != null) {
            labelTitre.setText(r.getTitre());
            labelDescription.setText(r.getDescription());
            statutComboBox.setValue(r.getStatut());
        }
    }

    public void setReclamationController(reclamationController controller) {
        this.reclamationView = controller;
    }

    @FXML
    public void initialize() {

        statutComboBox.getItems().setAll(Statut.values());
    }


    @FXML
    private void modifierReclamation(ActionEvent event) {
        Statut nouveauStatut = statutComboBox.getValue();


        if (nouveauStatut == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez sélectionner un statut.");
            return;
        }


        if (reclamationAmodifier != null && nouveauStatut != reclamationAmodifier.getStatut()) {
            reclamationAmodifier.setStatut(nouveauStatut);
            reclamationService.modifier(reclamationAmodifier);


            if (reclamationView != null) {
                reclamationView.refreshTable();
            }


            showAlert(Alert.AlertType.INFORMATION, "Statut modifié avec succès !");


            revenirAReclamations();
        } else {

            showAlert(Alert.AlertType.WARNING, "Aucune modification détectée.");
        }
    }


    private void revenirAReclamations() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/reclamation.fxml"));
            Parent reclamationViewNode = loader.load();


            reclamationController controller = loader.getController();
            controller.refreshTable();


            StackPane contentPane = (StackPane) btnModifier.getScene().lookup("#contentPane");


            contentPane.getChildren().setAll(reclamationViewNode);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour à la page des réclamations.");
        }
    }


    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
