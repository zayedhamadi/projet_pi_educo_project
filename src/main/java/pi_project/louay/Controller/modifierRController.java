package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;
import javafx.scene.control.Label;

public class modifierRController {

    @FXML
    private ComboBox<Statut> statutComboBox;

    @FXML
    private Button btnModifier;

    @FXML
    private Label labelTitre;

    @FXML
    private Label labelDescription;


    private final reclamationImp reclamationService = new reclamationImp();

    private reclamation reclamationAmodifier;

    public void setReclamation(reclamation r) {
        this.reclamationAmodifier = r;
        statutComboBox.setValue(r.getStatut()); // Afficher le statut actuel
        labelTitre.setText(r.getTitre());
        labelDescription.setText(r.getDescription());

    }

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec les valeurs de l'énumération Statut
        statutComboBox.setItems(FXCollections.observableArrayList(Statut.values()));

        btnModifier.setOnAction(this::modifierStatut);
    }

    private void modifierStatut(ActionEvent event) {
        if (reclamationAmodifier != null) {
            Statut nouveauStatut = statutComboBox.getValue();
            if (nouveauStatut != null && nouveauStatut != reclamationAmodifier.getStatut()) {
                reclamationAmodifier.setStatut(nouveauStatut);
                reclamationService.modifier(reclamationAmodifier);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Statut modifié avec succès !");
                alert.showAndWait();
                ((Button) event.getSource()).getScene().getWindow().hide();
            }
        }
    }
}

