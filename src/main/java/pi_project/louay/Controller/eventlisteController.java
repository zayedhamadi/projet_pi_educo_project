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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;

import pi_project.Fedi.services.eleveservice;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;
import pi_project.Fedi.entites.eleve;
//import pi_project.Fedi.services.eleveservice;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class eventlisteController implements Initializable {



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

    private final evenementImp evenementService = new evenementImp();
    private final eleveservice eleveService = new eleveservice();

    private int idParent=2; // ID du parent connecté

    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration des colonnes
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        inscriptionColumn.setCellValueFactory(new PropertyValueFactory<>("inscriptionRequise"));
        nombrePlacesColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Ajouter le bouton "Réserver" si nécessaire
        ajouterBoutonReserver();

        // Charger les événements
        afficherEvenements();
    }

    private void afficherEvenements() {
        List<evenement> allEvenements = evenementService.getAll();
        ObservableList<evenement> evenementData = FXCollections.observableArrayList(allEvenements);
        eventTable.setItems(evenementData);
    }

    private void ajouterBoutonReserver() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button reserverBtn = new Button("Réserver");

            {
                reserverBtn.setOnAction(e -> {
                    evenement evt = getTableView().getItems().get(getIndex());
                    reserverEvenement(evt);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    evenement evt = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);

                    if (evt.isInscriptionRequise()) {
                        buttons.getChildren().add(reserverBtn);
                    }

                    setGraphic(buttons);
                }
            }
        });
    }

    private void reserverEvenement(evenement evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/reserverEvenement.fxml"));
            Parent root = loader.load();

            ReservationController controller = loader.getController();

            // 🔥 Récupérer les enfants du parent connecté
            List<eleve> enfants = eleveService.getEnfantsParParent(idParent);

            // 👇 Appel correct avec les deux paramètres attendus
            controller.setEvenement(evt, enfants);

            Stage stage = new Stage();
            stage.setTitle("Réserver : " + evt.getTitre());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
