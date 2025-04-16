package pi_project.Farouk.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.MatiereService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MatiereController {
    @FXML private TextField enseignantIdField;
    @FXML private TextField nomField;
    @FXML private TextField coeffField;

    private final MatiereService matiereService = new MatiereService();
    private Matiere currentMatiere;

    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInput()) {
            try {
                if (currentMatiere == null) {
                    // Add new matiere
                    Matiere matiere = new Matiere(
                            Integer.parseInt(enseignantIdField.getText()),
                            nomField.getText(),
                            Double.parseDouble(coeffField.getText())
                    );
                    matiereService.ajouter(matiere);
                    showAlert("Success", "Matiere added successfully");
                } else {
                    // Update existing matiere
                    currentMatiere.setId_ensg(Integer.parseInt(enseignantIdField.getText()));
                    currentMatiere.setNom(nomField.getText());
                    currentMatiere.setCoefficient(Double.parseDouble(coeffField.getText()));
                    matiereService.modifier(currentMatiere);
                    showAlert("Success", "Matiere updated successfully");
                }
                handleClear(event);
                goToListMatiere();
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Please enter valid numbers");
            } catch (SQLException e) {
                showAlert("Database Error", "Error saving matiere: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        enseignantIdField.clear();
        nomField.clear();
        coeffField.clear();
        currentMatiere = null;
    }

    @FXML
    private void goToListMatiere() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Farouk/MatiereList.fxml"));
            Stage stage = (Stage) enseignantIdField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load list view");
        }
    }

    public void setMatiereData(Matiere matiere) {
        this.currentMatiere = matiere;
        enseignantIdField.setText(String.valueOf(matiere.getId_ensg()));
        nomField.setText(matiere.getNom());
        coeffField.setText(String.valueOf(matiere.getCoefficient()));
    }

    private boolean validateInput() {
        if (enseignantIdField.getText().isEmpty() ||
                nomField.getText().isEmpty() ||
                coeffField.getText().isEmpty()) {
            showAlert("Input Error", "All fields are required");
            return false;
        }

        try {
            Integer.parseInt(enseignantIdField.getText());
            Double.parseDouble(coeffField.getText());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
