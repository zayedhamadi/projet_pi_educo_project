package pi_project.louay.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pi_project.louay.Entity.reclamation;
import pi_project.louay.Enum.Statut;
import pi_project.louay.Service.reclamationImp;

import java.io.IOException;

public class reclamationController {

    @FXML
    private TableView<reclamation> tableView;

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

    @FXML
    private Pagination pagination;

    @FXML
    private ComboBox<Statut> statutFilterComboBox; // Ajout ComboBox pour filtrage

    private final reclamationImp service = new reclamationImp();
    private ObservableList<reclamation> allReclamations = FXCollections.observableArrayList();
    private ObservableList<reclamation> filteredReclamations = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 15;

    @FXML
    public void initialize() {

        colTitre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitre()));
        colDescription.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        colStatut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStatut()));

        loadReclamations();

        // Setup du ComboBox de filtrage
        statutFilterComboBox.setItems(FXCollections.observableArrayList(Statut.values()));
        statutFilterComboBox.getItems().add(0, null); // Option "Tous"
        statutFilterComboBox.setPromptText("Tous les statuts");

        // Action sur changement de sélection
        statutFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter(newVal);
        });

        pagination.setPageFactory(this::createPage);

        supprimerBtn.setOnAction(e -> {
            reclamation selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.supprimer(selected);
                allReclamations.remove(selected);
                applyFilter(statutFilterComboBox.getValue());
            }
        });

        modifierBtn.setOnAction(e -> {
            reclamation selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/louay/modifierreclamation.fxml"));
                    Parent view = loader.load();

                    StackPane contentPane = (StackPane) tableView.getScene().lookup("#contentPane");
                    contentPane.getChildren().setAll(view);

                    modifierRController controller = loader.getController();
                    controller.setReclamation(selected);

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

    private void loadReclamations() {
        allReclamations = FXCollections.observableArrayList(service.getAll());
        filteredReclamations = FXCollections.observableArrayList(allReclamations);
        refreshPagination();
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredReclamations.size());
        ObservableList<reclamation> pageData = FXCollections.observableArrayList(
                filteredReclamations.subList(fromIndex, toIndex)
        );
        tableView.setItems(pageData);
        return new VBox(tableView);
    }

    private void refreshPagination() {
        int pageCount = (int) Math.ceil((double) filteredReclamations.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private void applyFilter(Statut selectedStatut) {
        if (selectedStatut == null) {
            filteredReclamations = FXCollections.observableArrayList(allReclamations);
        } else {
            filteredReclamations = FXCollections.observableArrayList();
            for (reclamation r : allReclamations) {
                if (r.getStatut() == selectedStatut) {
                    filteredReclamations.add(r);
                }
            }
        }
        refreshPagination();
    }

    public void refreshTable() {
        loadReclamations();
    }
}
