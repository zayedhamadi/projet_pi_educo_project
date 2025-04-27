package pi_project.Zayed.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pi_project.Zayed.Entity.Cesser;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Service.CesserImpl;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.Constant;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class listInactifUserController {

    private final CesserImpl cesserService = new CesserImpl();
    private final UserImpl userService = new UserImpl();
    private final Constant constant = new Constant();
    private final int itemsPerPage = 10;
    @FXML
    private TableView<Cesser> cessationTable;
    @FXML
    private TableColumn<Cesser, String> nomCol;
    @FXML
    private TableColumn<Cesser, String> prenomCol;
    @FXML
    private TableColumn<Cesser, String> dateCessationCol;
    @FXML
    private TableColumn<Cesser, String> motifCol;
    @FXML
    private TableColumn<Cesser, Void> actionCol;
    @FXML
    private TextField searchField;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;
    @FXML
    private Label pageInfoLabel;
    private ObservableList<Cesser> originalData;
    private FilteredList<Cesser> filteredData;
    private int currentPage = 1;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadCesserData();
        setupPagination();
    }

    private void setupTableColumns() {
        nomCol.setCellValueFactory(cellData -> {
            User user = userService.getSpeceficUser(cellData.getValue().getIdUserId());
            return new javafx.beans.property.SimpleStringProperty(user != null ? user.getNom() : "N/A");
        });

        prenomCol.setCellValueFactory(cellData -> {
            User user = userService.getSpeceficUser(cellData.getValue().getIdUserId());
            return new javafx.beans.property.SimpleStringProperty(user != null ? user.getPrenom() : "N/A");
        });

        dateCessationCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateMotif().format(formatter));
        });

        motifCol.setCellValueFactory(new PropertyValueFactory<>("motif"));

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button reactivateBtn = new Button("Réactiver");

            {
                reactivateBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                reactivateBtn.setOnAction(event -> {
                    Cesser cesser = getTableView().getItems().get(getIndex());
                    handleReactivateAccount(cesser);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(reactivateBtn);
                }
            }
        });
    }

    private void loadCesserData() {
        try {
            List<Cesser> cesserList = cesserService.getAllCesser();
            originalData = FXCollections.observableArrayList(cesserList);
            filteredData = new FilteredList<>(originalData, p -> true);

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(cesser -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();
                    User user = userService.getSpeceficUser(cesser.getIdUserId());
                    String nom = user != null ? user.getNom().toLowerCase() : "";
                    String prenom = user != null ? user.getPrenom().toLowerCase() : "";
                    String motif = cesser.getMotif().toLowerCase();

                    return nom.contains(lowerCaseFilter) ||
                            prenom.contains(lowerCaseFilter) ||
                            motif.contains(lowerCaseFilter);
                });
                currentPage = 1;
                updatePagination();
            });
            updatePagination();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les données de cessation", e.getMessage());
        }
    }

    private void setupPagination() {
        updatePagination();
    }

    private void updatePagination() {
        if (filteredData == null) return;

        int totalItems = filteredData.size();
        totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        if (totalPages == 0) {
            totalPages = 1;
        }

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        pageInfoLabel.setText("Page " + currentPage + "/" + totalPages);

        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);

        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredData.size());

        ObservableList<Cesser> currentPageData = FXCollections.observableArrayList(
                filteredData.subList(fromIndex, toIndex)
        );

        cessationTable.setItems(currentPageData);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
        }
    }

    @FXML
    private void handleSearch() {
        System.out.println("handle search");
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        currentPage = 1;
        updatePagination();
    }

    private void handleReactivateAccount(Cesser cesser) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de réactivation");
        confirmation.setHeaderText("Réactiver le compte utilisateur");
        confirmation.setContentText("Êtes-vous sûr de vouloir réactiver ce compte ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                User reactivatedUser = cesserService.ActiverUserCesser(cesser.getIdUserId());
                cesserService.SupprimerCessation(cesser.getIdUserId());
                loadCesserData();
                Constant.showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Compte réactivé", "Le compte a été réactivé avec succès.");
            } catch (Exception e) {
                Constant.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Échec de la réactivation", e.getMessage());
            }
        }
    }
}