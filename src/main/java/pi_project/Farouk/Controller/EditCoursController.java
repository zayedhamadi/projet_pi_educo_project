package pi_project.Farouk.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import pi_project.Farouk.Models.Cours;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.CoursService;
import pi_project.Farouk.Services.MatiereService;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import pi_project.Aziz.Controller.EnseignantlyoutController;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EditCoursController {

    @FXML private TextField nameField;
    @FXML private ComboBox<Matiere> matiereComboBox;
    @FXML private ComboBox<classe> classComboBox;
    @FXML private Label pdfLabel;

    private String selectedPdfPath;
    private Cours cours;
    private final CoursService coursService = new CoursService();
    private final MatiereService matiereService = new MatiereService();
    private final classeservice classeService = new classeservice();

    public void setCours(Cours cours) throws SQLException {
        this.cours = cours;
        nameField.setText(cours.getName());
        selectedPdfPath = cours.getPdfFilename();
        pdfLabel.setText(selectedPdfPath != null ? new File(selectedPdfPath).getName() : "No file selected");

        initializeComboBoxes();
    }

    private void initializeComboBoxes() throws SQLException {
        // Load Matieres
        List<Matiere> matieres = matiereService.recupererTous();
        matiereComboBox.setItems(FXCollections.observableArrayList(matieres));
        matiereComboBox.setConverter(new StringConverter<Matiere>() {
            @Override public String toString(Matiere matiere) { return matiere.getNom(); }
            @Override public Matiere fromString(String string) { return null; }
        });

        // Load Classes
        List<classe> classes = classeService.getAll();
        classComboBox.setItems(FXCollections.observableArrayList(classes));
        classComboBox.setConverter(new StringConverter<classe>() {
            @Override public String toString(classe classe) { return classe.getNomclasse(); }
            @Override public classe fromString(String string) { return null; }
        });

        // Set current selections
        matiereComboBox.getSelectionModel().select(getMatiereById(cours.getIdMatiere()));
        classComboBox.getSelectionModel().select(getClasseById(cours.getClasse()));
    }

    private Matiere getMatiereById(int id) {
        return matiereComboBox.getItems().stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private classe getClasseById(int id) {
        return classComboBox.getItems().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @FXML
    private void onBrowseClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(pdfLabel.getScene().getWindow());
        if (file != null) {
            selectedPdfPath = file.getAbsolutePath();
            pdfLabel.setText(file.getName());
        }
    }

    @FXML
    private void onCancel() {
        navigateToCoursList();
    }

    @FXML
    private void onSave() {
        try {
            if (nameField.getText().isEmpty()) {
                showAlert("Error", "Course name is required!");
                return;
            }
            if (matiereComboBox.getValue() == null) {
                showAlert("Error", "Please select a matiere!");
                return;
            }
            if (classComboBox.getValue() == null) {
                showAlert("Error", "Please select a class!");
                return;
            }

            cours.setName(nameField.getText());
            cours.setIdMatiere(matiereComboBox.getValue().getId());
            cours.setClasse(classComboBox.getValue().getId());
            cours.setPdfFilename(selectedPdfPath);

            boolean updated = coursService.modifier(cours);
            if (updated) {
                navigateToCoursList();
            } else {
                showAlert("Error", "Failed to update course");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void navigateToCoursList() {
        try {
            // Load the main layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/enseignanlayout.fxml"));
            Parent root = loader.load();

            // Get controller and show cours list
            EnseignantlyoutController mainController = loader.getController();
            mainController.showCoursView();
            // Get the current stage and replace the scene
            Stage stage = (Stage) pdfLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not load course list");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}