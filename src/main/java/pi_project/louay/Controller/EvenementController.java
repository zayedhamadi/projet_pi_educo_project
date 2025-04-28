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
import pi_project.louay.Entity.evenement;
import pi_project.louay.Enum.EventType;
import pi_project.louay.Service.evenementImp;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilterCombo;  // ComboBox pour le filtrage par type d'événement

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

        // Initialisation du filtre par type d'événement
        typeFilterCombo.getItems().add("Tous types");  // Ajoute "Tous types" en premier
        for (EventType eventType : EventType.values()) {
            typeFilterCombo.getItems().add(eventType.getLabel());  // Ajoute les autres types
        }
        typeFilterCombo.setValue("Tous types");  // Par défaut, afficher "Tous types"

        // Gestion du bouton "Ajouter"
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

        // Gestion de la recherche par titre
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());

        // Gestion du filtre par type d'événement
        typeFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
    }

    private void applyFilter() {
        pagination.setCurrentPageIndex(0); // Revenir à la première page
        pagination.setPageFactory(this::createPage); // Mettre à jour la page avec les filtres appliqués
    }

    public void loadEvents() {
        allEvents = FXCollections.observableArrayList(evenementService.getAll());
        int pageCount = (int) Math.ceil((double) allEvents.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
    }

    private VBox createPage(int pageIndex) {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = typeFilterCombo.getValue();  // Obtenir le type d'événement sélectionné

        List<evenement> filteredEvents = allEvents.stream()
                .filter(event -> event.getTitre().toLowerCase().contains(searchText))
                .filter(event -> selectedType.equals("Tous types") || event.getType().getLabel().equals(selectedType))  // Filtrer par type
                .collect(Collectors.toList());

        // Calculer les indices pour la pagination
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredEvents.size());

        ObservableList<evenement> pageData = FXCollections.observableArrayList(
                filteredEvents.subList(fromIndex, toIndex)
        );

        eventTable.setItems(pageData);
        ajouterBoutonsActions();

        return new VBox(eventTable);  // Retourner la vue avec la table mise à jour
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
