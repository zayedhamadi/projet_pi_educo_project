package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

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
    @FXML
    private Pagination pagination;

    private final evenementImp evenementService = new evenementImp();
    private static final int ROWS_PER_PAGE = 10;
    private ObservableList<evenement> allEvents = FXCollections.observableArrayList();

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
        inscriptionColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Oui" : "Non");
                }
            }
        });



        ajouterBoutonsActions();


        loadEvents();


        pagination.setPageFactory(this::createPage);


        ajouterBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/ajouterevenement.fxml"));
                Parent view = loader.load();
                StackPane contentPane = (StackPane) eventTable.getScene().lookup("#contentPane");
                contentPane.getChildren().setAll(view);
                ajouterevenement controller = loader.getController();
                controller.setEvenementController(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadEvents() {
        allEvents = FXCollections.observableArrayList(evenementService.getAll());
        int pageCount = (int) Math.ceil((double) allEvents.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, allEvents.size());

        ObservableList<evenement> pageData = FXCollections.observableArrayList(
                allEvents.subList(fromIndex, toIndex)
        );
        eventTable.setItems(pageData);

        return new VBox(eventTable);
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
                    refreshTable();
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

    public void refreshTable() {
        int currentPage = pagination.getCurrentPageIndex();
        loadEvents();
        pagination.setPageFactory(this::createPage);
        pagination.setCurrentPageIndex(Math.min(currentPage, pagination.getPageCount() - 1));
    }

    private void ouvrirFormulaireModification(evenement evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/modifierevenement.fxml"));
            Parent view = loader.load();
            StackPane contentPane = (StackPane) eventTable.getScene().lookup("#contentPane");
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


            StackPane contentPane = (StackPane) eventTable.getScene().lookup("#contentPane");


            contentPane.getChildren().setAll(view);

            ConsulterInscriptionController controller = loader.getController();
            controller.setEvenement(evt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
