package pi_project.Farouk.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Cours;
import pi_project.Farouk.Services.CoursService;

import java.io.File;
import java.sql.SQLException;

public class EditCoursController {
    @FXML private TextField nameField;
    @FXML private TextField matiereIdField;
    @FXML private TextField classeField;
    @FXML private Label pdfLabel;

    private final CoursService coursService = new CoursService();
    private Cours currentCours;
    private String selectedPdfPath;

    public void setCoursData(Cours cours) {
        this.currentCours = cours;
        nameField.setText(cours.getName());
        matiereIdField.setText(String.valueOf(cours.getIdMatiere()));
        classeField.setText(String.valueOf(cours.getClasse()));
        pdfLabel.setText(cours.getPdfFilename() != null ? cours.getPdfFilename() : "No file selected");
        this.selectedPdfPath = cours.getPdfFilename();
    }

    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showOpenDialog(pdfLabel.getScene().getWindow());
        if (file != null) {
            this.selectedPdfPath = file.getAbsolutePath();
            pdfLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleSave() {
        try {
            currentCours.setName(nameField.getText());
            currentCours.setIdMatiere(Integer.parseInt(matiereIdField.getText()));
            currentCours.setClasse(Integer.parseInt(classeField.getText()));
            currentCours.setPdfFilename(selectedPdfPath);

            boolean updated = coursService.modifier(currentCours);
            if (updated) {
                showAlert("Success", "Course updated successfully", Alert.AlertType.INFORMATION);
                closeWindow();
            } else {
                showAlert("Error", "Failed to update course", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for ID and class", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Database Error", "Error updating course: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}