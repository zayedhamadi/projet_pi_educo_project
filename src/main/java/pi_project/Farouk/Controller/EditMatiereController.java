package pi_project.Farouk.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.MatiereService;
import pi_project.Farouk.Services.EnseignantService;
import pi_project.Saif.Controller.MainLayoutController;
import pi_project.Zayed.Entity.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EditMatiereController {

    @FXML private ComboBox<User> enseignantCombo;
    @FXML private TextField nomField;
    @FXML private TextField coeffField;

    private final MatiereService matiereService = new MatiereService();
    private final EnseignantService enseignantService = new EnseignantService();
    private Matiere currentMatiere;

    @FXML
    public void initialize() {
        loadEnseignants();
    }

    private void loadEnseignants() {
        try {
            List<User> enseignants = enseignantService.getAllEnseignants();
            ObservableList<User> observableList = FXCollections.observableArrayList(enseignants);
            enseignantCombo.setItems(observableList);

            // Configure how teachers are displayed in the ComboBox
            enseignantCombo.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });

            // Configure button cell to display selected teacher
            enseignantCombo.setButtonCell(new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText("Select Teacher");
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Error", "Failed to load teachers: " + e.getMessage());
        }
    }

    public void setMatiereData(Matiere matiere) {
        this.currentMatiere = matiere;
        nomField.setText(matiere.getNom());
        coeffField.setText(String.valueOf(matiere.getCoefficient()));

        // Select the corresponding teacher in the ComboBox
        enseignantCombo.getItems().stream()
                .filter(user -> user.getId() == matiere.getId_ensg())
                .findFirst()
                .ifPresent(teacher -> enseignantCombo.getSelectionModel().select(teacher));
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        try {
            User selectedTeacher = enseignantCombo.getValue();
            currentMatiere.setId_ensg(selectedTeacher.getId());
            currentMatiere.setNom(nomField.getText());
            currentMatiere.setCoefficient(Double.parseDouble(coeffField.getText()));

            if (matiereService.modifier(currentMatiere)) {
                showSuccessAlert("Success", "Subject updated successfully");
                goToListMatiere();
            } else {
                showAlert("Error", "Failed to update subject");
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid coefficient");
        } catch (SQLException e) {
            showAlert("Database Error", "Error updating subject: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (enseignantCombo.getValue() == null) {
            showAlert("Input Error", "Please select a teacher");
            return false;
        }
        if (nomField.getText().isEmpty()) {
            showAlert("Input Error", "Subject name is required");
            return false;
        }
        if (coeffField.getText().isEmpty()) {
            showAlert("Input Error", "Coefficient is required");
            return false;
        }

        try {
            Double.parseDouble(coeffField.getText());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid coefficient");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        goToListMatiere();
    }

    private void goToListMatiere() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
            Parent root = loader.load();

            // Get the controller and show Matiere view
            MainLayoutController mainController = loader.getController();
            mainController.showMatiereView();

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load list view");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}