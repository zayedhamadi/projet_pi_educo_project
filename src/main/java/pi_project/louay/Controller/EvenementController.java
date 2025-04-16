package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EvenementController implements Initializable {

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
    private TableColumn<evenement, Void> actionsColumn; // Ajout de la colonne d'actions

    @FXML
    private Button ajouterBtn;

    private final evenementImp evenementService = new evenementImp();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurer les colonnes du tableau
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        inscriptionColumn.setCellValueFactory(new PropertyValueFactory<>("inscriptionRequise"));
        nombrePlacesColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Ajouter les actions (modifier et supprimer) dans la table
        ajouterBoutonsActions();

        // Charger les événements au démarrage
        afficherEvenements();

        // Gérer le clic sur le bouton "Créer un événement"
        ajouterBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/ajouterevenement.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Créer un événement");
                stage.setScene(new Scene(root));
                stage.showAndWait(); // Attendre la fermeture du formulaire

                // Rafraîchir la table après la fermeture du formulaire d'ajout
                afficherEvenements();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Afficher les événements
    private void afficherEvenements() {
        List<evenement> allEvenements = evenementService.getAll(); // Récupérer tous les événements
        ObservableList<evenement> evenementData = FXCollections.observableArrayList(allEvenements);
        eventTable.setItems(evenementData);
    }

    private void ajouterBoutonsActions() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button modifierBtn = new Button("Modifier");
            private final Button supprimerBtn = new Button("Supprimer");
            private final Button consulterBtn = new Button("Consulter");

            {
                modifierBtn.setOnAction(e -> {
                    evenement evt = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireModification(evt);
                });

                supprimerBtn.setOnAction(e -> {
                    evenement evt = getTableView().getItems().get(getIndex());
                    evenementService.supprimer(evt);
                    afficherEvenements();
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
                    HBox buttons = new HBox(5, modifierBtn, supprimerBtn);
                    if (evt.isInscriptionRequise()) {
                        buttons.getChildren().add(consulterBtn);
                    }
                    setGraphic(buttons);
                }
            }
        });
    }


    // Ouvrir le formulaire de modification
    private void ouvrirFormulaireModification(evenement evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/modifierevenement.fxml"));
            Parent root = loader.load();

            modifierevenement controller = loader.getController(); // Pas EvenementController ici !
            controller.setEvenement(evt);


            Stage stage = new Stage();
            stage.setTitle("Modifier Événement");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre la fermeture
            afficherEvenements(); // Rafraîchir après modification

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void ouvrirListeInscriptions(evenement evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/consulter_insc_event.fxml"));
            Parent root = loader.load();

            ConsulterInscriptionController controller = loader.getController();
            controller.setEvenement(evt); // transmettre l'événement au controller

            Stage stage = new Stage();
            stage.setTitle("Inscriptions pour " + evt.getTitre());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Pré-remplir les champs lors de la modification
    public void setEvenementAModifier(evenement evt) {
        // Exemple : titreField.setText(evt.getTitre()); etc.
        // Compléter la logique ici selon tes besoins
    }
}
