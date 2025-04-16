package pi_project.Farouk.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.MatiereService;

import java.io.IOException;
import java.sql.SQLException;

public class EditMatiereController {

    @FXML private TextField enseignantIdField;
    @FXML private TextField nomField;
    @FXML private TextField coeffField;

    private final MatiereService matiereService = new MatiereService();
    private Matiere currentMatiere;

    public void setMatiereData(Matiere matiere) {
        this.currentMatiere = matiere;
        enseignantIdField.setText(String.valueOf(matiere.getId_ensg()));
        nomField.setText(matiere.getNom());
        coeffField.setText(String.valueOf(matiere.getCoefficient()));
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (validateInput()) {
            try {
                currentMatiere.setId_ensg(Integer.parseInt(enseignantIdField.getText()));
                currentMatiere.setNom(nomField.getText());
                currentMatiere.setCoefficient(Double.parseDouble(coeffField.getText()));

                if (matiereService.modifier(currentMatiere)) {
                    showSuccessAlert("Success", "Matiere updated successfully");
                    goToListMatiere();
                } else {
                    showAlert("Error", "Failed to update matiere");
                }
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Please enter valid numbers");
            } catch (SQLException e) {
                showAlert("Database Error", "Error updating matiere: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        goToListMatiere();
    }

    private void goToListMatiere() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Farouk/MatiereList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load list view");
        }
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