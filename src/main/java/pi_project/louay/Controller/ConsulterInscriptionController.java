package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Entity.inscriptionevenement;
import pi_project.louay.Service.inscevenementImp;
import pi_project.Fedi.entites.eleve;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class ConsulterInscriptionController {

    @FXML
    private TableView<inscriptionevenement> inscriptionTable;
    @FXML
    private TableColumn<inscriptionevenement, String> nomEnfantColumn;
    @FXML
    private TableColumn<inscriptionevenement, LocalDate> dateInscriptionColumn;
    @FXML
    private TableColumn<inscriptionevenement, Void> actionsColumn;

    @FXML
    private Label titreLabel;

    @FXML
    private Button retourButton;

    @FXML
    private void onButtonHover() {
        retourButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;");
    }

    @FXML
    private void onButtonExit() {
        retourButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;");
    }


    @FXML
    private Label messageLabel;

    @FXML
    private Label placesRestantesLabel; // ✅ Ajout du label pour places restantes

    private final inscevenementImp inscriptionService = new inscevenementImp();

    private ObservableList<inscriptionevenement> filteredInscriptions = FXCollections.observableArrayList();

    private evenement currentEvenement; // 🔥 Garde une référence à l'événement actuel

    public void setEvenement(evenement evt) {
        currentEvenement = evt; // 🔥 stocker pour pouvoir l'utiliser après
        titreLabel.setText("Inscriptions pour : " + evt.getTitre());

        // Remplir les colonnes du tableau
        nomEnfantColumn.setCellValueFactory(data -> {
            eleve enfant = data.getValue().getEnfant_id();
            return new javafx.beans.property.SimpleStringProperty(enfant != null ? enfant.getNom() : "N/A");
        });
        dateInscriptionColumn.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));

        ajouterBoutonsActions();

        // Charger toutes les inscriptions pour cet événement
        List<inscriptionevenement> allInscriptions = inscriptionService.getAll();
        filteredInscriptions.clear();
        for (inscriptionevenement ins : allInscriptions) {
            if (ins.getEvenement().getId() == evt.getId()) {
                filteredInscriptions.add(ins);
            }
        }
        inscriptionTable.setItems(filteredInscriptions);

        // ✅ Calcul et affichage du nombre de places restantes
        updatePlacesRestantes();
    }

    private void ajouterBoutonsActions() {
        actionsColumn.setCellFactory(col -> new TableCell<inscriptionevenement, Void>() {
            private final Button supprimerBtn = new Button("Supprimer");

            {
                supprimerBtn.setOnAction(e -> {
                    inscriptionevenement inscription = getTableView().getItems().get(getIndex());
                    inscriptionService.supprimer(inscription);
                    filteredInscriptions.remove(inscription);
                    inscriptionTable.setItems(filteredInscriptions);

                    // ✅ Mettre à jour le label après suppression
                    updatePlacesRestantes();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, supprimerBtn);
                    setGraphic(buttons);
                }
            }
        });
    }

    @FXML
    private void initialize() {
        retourButton.setOnAction(event -> retournerVersEvenements());
    }

    private void retournerVersEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/evenement.fxml"));
            Parent view = loader.load();

            StackPane contentPane = (StackPane) retourButton.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(view);

            EvenementController controller = loader.getController();
            controller.refreshTable();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers la page des événements.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Ta logique propre pour calculer les places restantes depuis la BDD
    private void updatePlacesRestantes() {
        if (currentEvenement != null) {
            int nombreInscriptions = inscriptionService.getNombreInscriptions(currentEvenement.getId());
            int placesRestantes = currentEvenement.getNombrePlaces() - nombreInscriptions;

            placesRestantesLabel.setText("Nombre de places restantes : " + placesRestantes);

            if (placesRestantes <= 3) {
                placesRestantesLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                placesRestantesLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }
        }
    }
}
