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
    private TableColumn<inscriptionevenement, Void> actionsColumn; // Colonne pour les actions

    @FXML
    private Label titreLabel;

    @FXML
    private Button retourButton;  // Bouton retour

    private final inscevenementImp inscriptionService = new inscevenementImp();

    private ObservableList<inscriptionevenement> filteredInscriptions = FXCollections.observableArrayList();

    public void setEvenement(evenement evt) {
        titreLabel.setText("Inscriptions pour : " + evt.getTitre());

        nomEnfantColumn.setCellValueFactory(data -> {
            eleve enfant = data.getValue().getEnfant_id();
            return new javafx.beans.property.SimpleStringProperty(enfant != null ? enfant.getNom() : "N/A");
        });
        dateInscriptionColumn.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));

        // Ajouter les boutons d'actions pour chaque inscription
        ajouterBoutonsActions();

        // Charger les inscriptions de cet événement
        List<inscriptionevenement> allInscriptions = inscriptionService.getAll();
        filteredInscriptions.clear();
        for (inscriptionevenement ins : allInscriptions) {
            if (ins.getEvenement().getId() == evt.getId()) {
                filteredInscriptions.add(ins);
            }
        }
        inscriptionTable.setItems(filteredInscriptions); // Ceci est crucial pour l'affichage
    }

    private void ajouterBoutonsActions() {
        actionsColumn.setCellFactory(col -> new TableCell<inscriptionevenement, Void>() {
            private final Button supprimerBtn = new Button("Supprimer");

            {
                supprimerBtn.setOnAction(e -> {
                    inscriptionevenement inscription = getTableView().getItems().get(getIndex());
                    inscriptionService.supprimer(inscription); // Supprimer l'inscription
                    filteredInscriptions.remove(inscription); // Retirer l'inscription de la liste observable
                    inscriptionTable.setItems(filteredInscriptions); // Rafraîchir la table
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, supprimerBtn); // Ajout du bouton "Supprimer"
                    setGraphic(buttons);
                }
            }
        });
    }

    @FXML
    private void initialize() {
        // Gestionnaire d'événements pour le bouton retour
        retourButton.setOnAction(event -> retournerVersEvenements());
    }

    private void retournerVersEvenements() {
        try {
            // Charger la page des événements depuis le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/evenement.fxml")); // Remplace par le bon chemin de ton fichier FXML
            Parent view = loader.load();

            // Récupérer le StackPane du contrôleur Evenement
            StackPane contentPane = (StackPane) retourButton.getScene().lookup("#contentPane");

            // Remplacer le contenu central avec la vue des événements
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
}
