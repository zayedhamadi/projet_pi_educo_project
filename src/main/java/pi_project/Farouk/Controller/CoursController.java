package pi_project.Farouk.Controller;

import pi_project.Farouk.Models.Cours;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Farouk.Services.CoursService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CoursController {

    @FXML private TextField nameField;
    @FXML private TextField matiereIdField;
    @FXML private TextField classIdField;
    @FXML private TextField chapterNumberField;
    @FXML private Label statusLabel;

    private File selectedPdfFile;

    private final CoursService coursService = new CoursService();

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        selectedPdfFile = fileChooser.showOpenDialog(new Stage());

        if (selectedPdfFile != null) {
            statusLabel.setText("Selected: " + selectedPdfFile.getName());
        } else {
            statusLabel.setText("No file selected.");
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validate inputs
            if (nameField.getText().isEmpty() ||
                    matiereIdField.getText().isEmpty() ||
                    classIdField.getText().isEmpty() ||
                    chapterNumberField.getText().isEmpty() ||
                    selectedPdfFile == null) {

                statusLabel.setText("All fields and file selection are required!");
                return;
            }

            // Copy the PDF to a desired folder (e.g., "uploads")
            String uploadsDir = "/Farouk/uploads/";
            File uploadsFolder = new File(uploadsDir);
            if (!uploadsFolder.exists()) uploadsFolder.mkdirs();

            File destinationFile = new File(uploadsFolder, selectedPdfFile.getName());
            Files.copy(selectedPdfFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Create Cours object with all fields
            Cours cours = new Cours(
                    nameField.getText(),
                    Integer.parseInt(matiereIdField.getText()),
                    Integer.parseInt(classIdField.getText()),
                    Integer.parseInt(chapterNumberField.getText()),
                    selectedPdfFile.getName() // Only the filename, not the path
            );

            // Save to database
            coursService.ajouter(cours);

            // Close the window
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            statusLabel.setText("Matiere ID, Class ID, and Chapter Number must be numbers!");
        } catch (IOException e) {
            statusLabel.setText("Failed to upload PDF file.");
            e.printStackTrace();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }


}
