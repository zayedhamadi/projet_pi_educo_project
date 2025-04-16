package pi_project.Farouk.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.MatiereService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListMatiereController {

    @FXML private TableView<Matiere> matiereTable;
//    @FXML private TableColumn<Matiere, Integer> idCol;
    @FXML private TableColumn<Matiere, Integer> enseignantIdCol;
    @FXML private TableColumn<Matiere, String> nomCol;
    @FXML private TableColumn<Matiere, Double> coeffCol;
    @FXML private TableColumn<Matiere, Void> actionsCol;
    @FXML private TextField searchField;

    private final MatiereService matiereService = new MatiereService();
    private final ObservableList<Matiere> matiereList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
//        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        enseignantIdCol.setCellValueFactory(new PropertyValueFactory<>("id_ensg"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        coeffCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));

        // Add action buttons column
        addActionButtons();

        refreshTable();
    }
    private void addActionButtons() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                // Style buttons
                editButton.getStyleClass().add("action-edit");
                deleteButton.getStyleClass().add("action-delete");


                // Set button actions
                editButton.setOnAction(event -> {
                    Matiere matiere = getTableView().getItems().get(getIndex());
                    goToEditMatiere(matiere);
                });

                deleteButton.setOnAction(event -> {
                    Matiere matiere = getTableView().getItems().get(getIndex());
                    handleDeleteMatiere(matiere);
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }
    private void refreshTable() {
        try {
            List<Matiere> matieres = matiereService.recupererTous();
            matiereList.setAll(matieres);
            matiereTable.setItems(matiereList);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading data: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        try {
            List<Matiere> allMatieres = matiereService.recupererTous();
            matiereList.clear();
            for (Matiere m : allMatieres) {
                if (m.getNom().toLowerCase().contains(keyword) ||
                        String.valueOf(m.getId_ensg()).contains(keyword) ||
                        String.valueOf(m.getCoefficient()).contains(keyword)) {
                    matiereList.add(m);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error searching matieres: " + e.getMessage());
        }
    }

    @FXML
    private void goToAddMatiere() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Farouk/AjouterMatiere.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) matiereTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load add matiere view");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleDeleteMatiere(Matiere matiere) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Matiere");
        confirmation.setContentText("Are you sure you want to delete " + matiere.getNom() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (matiereService.supprimer(matiere)) {
                        matiereList.remove(matiere);
                        showSuccessAlert("Success", "Matiere deleted successfully");
                    } else {
                        showAlert("Error", "Failed to delete matiere");
                    }
                } catch (SQLException e) {
                    showAlert("Database Error", "Error deleting matiere: " + e.getMessage());
                }
            }
        });
    }
    private void goToEditMatiere(Matiere matiere) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Farouk/EditMatiere.fxml"));
            Parent root = loader.load();

            // Pass the matiere to edit to the controller
            EditMatiereController controller = loader.getController();
            controller.setMatiereData(matiere);

            Stage stage = (Stage) matiereTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load edit matiere view");
        }
    }
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}