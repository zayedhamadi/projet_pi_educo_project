package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Service.reclamationImp;
import pi_project.louay.Enum.Statut;
import javafx.scene.layout.StackPane;


import java.io.IOException;

public class reclamationController {

    @FXML
    private TableView<reclamation> tableView;

    @FXML
    private TableColumn<reclamation, Integer> colId;

    @FXML
    private TableColumn<reclamation, String> colTitre;

    @FXML
    private TableColumn<reclamation, String> colDescription;

    @FXML
    private TableColumn<reclamation, Statut> colStatut;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    private final reclamationImp service = new reclamationImp();
    private ObservableList<reclamation> data;

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colTitre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitre()));
        colDescription.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        colStatut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStatut()));

        // Chargement des données
        data = FXCollections.observableArrayList(service.getAll());
        tableView.setItems(data);

        // Bouton Supprimer
        supprimerBtn.setOnAction(e -> {
            reclamation selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.supprimer(selected);
                data.remove(selected);
            }
        });

        // Bouton Modifier
        modifierBtn.setOnAction(e -> {
            reclamation selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/modifierreclamation.fxml"));
                    Parent view = loader.load();

                    // Remplacer seulement le contenu central (ex: dans un StackPane avec fx:id="contentPane")
                    StackPane contentPane = (StackPane) tableView.getScene().lookup("#contentPane");
                    contentPane.getChildren().setAll(view);

                    // Passer la réclamation au contrôleur de modification
                    modifierRController controller = loader.getController();
                    controller.setReclamation(selected);
                    //controller.setReclamationController(this); // si tu veux pouvoir faire un refresh après

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la vue de modification.", ButtonType.OK);
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une réclamation à modifier.", ButtonType.OK);
                alert.showAndWait();
            }
        });

    }
    public void refreshTable() {
        data.setAll(service.getAll());
    }

}
