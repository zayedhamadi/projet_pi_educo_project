package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;

import java.io.IOException;

public class EvenementController {

    @FXML
    private TableView<evenement> eventTable;

    @FXML
    private TableColumn<evenement, String> titreColumn;
    @FXML
    private TableColumn<evenement, String> descriptionColumn;
    @FXML
    private TableColumn<evenement, String> dateDebutColumn;
    @FXML
    private TableColumn<evenement, String> dateFinColumn;
    @FXML
    private TableColumn<evenement, String> lieuColumn;
    @FXML
    private TableColumn<evenement, Boolean> inscriptionColumn;
    @FXML
    private TableColumn<evenement, Integer> nombrePlacesColumn;
    @FXML
    private TableColumn<evenement, EventType> typeColumn;
    @FXML
    private TableColumn<evenement, Void> actionsColumn;

    @FXML
    private Button ajouterBtn;

    @FXML
    private StackPane contentPane;

    private final evenementImp evenementService = new evenementImp();

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        inscriptionColumn.setCellValueFactory(new PropertyValueFactory<>("inscriptionRequise"));
        nombrePlacesColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Ajouter boutons d'action
        ajouterBoutonsActions();

        // Charger la table
        afficherEvenements();

        // Action bouton ajouter
        ajouterBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/ajouterevenement.fxml"));
                Parent view = loader.load();

                // Récupérer la vue principale avec le StackPane (contentPane)
                StackPane contentPane = (StackPane) eventTable.getScene().lookup("#contentPane");

                // Remplacer le contenu central
                contentPane.getChildren().setAll(view);

                ajouterevenement controller = loader.getController();
                controller.setEvenementController(this); // Passer le controller actuel à l'autre contrôleur

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Méthode pour afficher les événements dans la table
    private void afficherEvenements() {
        ObservableList<evenement> evenementData = FXCollections.observableArrayList(evenementService.getAll());
        eventTable.setItems(evenementData);
    }

    // Méthode pour rafraîchir la table après modification, ajout, ou suppression
    public void refreshTable() {
        afficherEvenements(); // Recharge les événements dans la table
    }

    private void ajouterBoutonsActions() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifierBtn = new Button("Modifier");
            private final Button supprimerBtn = new Button("Supprimer");
            private final Button consulterBtn = new Button("Consulter");

            {
                modifierBtn.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
                supprimerBtn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white;");
                consulterBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

                modifierBtn.setOnAction(e -> {
                    evenement evt = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireModification(evt);
                });

                supprimerBtn.setOnAction(e -> {
                    evenement evt = getTableView().getItems().get(getIndex());
                    evenementService.supprimer(evt);
                    afficherEvenements(); // Rafraîchir la table après la suppression
                });

                consulterBtn.setOnAction(e -> {
                    evenement evt = getTableView().getItems().get(getIndex());
                    ouvrirListeInscriptions(evt);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    evenement evt = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(8, modifierBtn, supprimerBtn);
                    if (evt.isInscriptionRequise()) {
                        buttons.getChildren().add(consulterBtn);
                    }
                    setGraphic(buttons);
                }
            }
        });
    }

    private void ouvrirFormulaireModification(evenement evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/modifierevenement.fxml"));
            Parent view = loader.load();

            // Récupérer la zone de contenu du layout principal
            StackPane contentPane = (StackPane) eventTable.getScene().lookup("#contentPane");

            // Remplacer le contenu central (sans toucher à la sidebar)
            contentPane.getChildren().setAll(view);

            modifierevenement controller = loader.getController();
            controller.setEvenement(evt);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirListeInscriptions(evenement evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/consulter_insc_event.fxml"));
            Parent view = loader.load();

            // Récupérer la zone de contenu du layout principal
            StackPane contentPane = (StackPane) eventTable.getScene().lookup("#contentPane");

            // Remplacer le contenu central (sans toucher à la sidebar)
            contentPane.getChildren().setAll(view);

            ConsulterInscriptionController controller = loader.getController();
            controller.setEvenement(evt);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
