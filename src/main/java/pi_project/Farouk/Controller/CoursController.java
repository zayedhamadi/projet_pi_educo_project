package pi_project.Farouk.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.converter.StringConverter;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import pi_project.Farouk.Models.Cours;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;  // Fadi's class service

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.CoursService;
import pi_project.Farouk.Services.MatiereService;
import javafx.scene.control.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import pi_project.Aziz.Controller.EnseignantlyoutController;

public class CoursController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<Matiere> matiereComboBox;
    @FXML private ComboBox<classe> classComboBox;  // For Fadi's classes
    @FXML private TextField classIdField;
    @FXML private TextField chapterNumberField;
    @FXML private Label statusLabel;

    private File selectedPdfFile;

    private final CoursService coursService = new CoursService();
    private final MatiereService matiereService = new MatiereService();
    private final classeservice classeService = new classeservice();  // Fadi's service
    private ObservableList<Matiere> matieresList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing CoursController..."); // Debug
        loadMatieres();  // Your existing method
        loadClasses();
    }

    private void loadMatieres() {
        try {


            List<Matiere> matieres = matiereService.recupererTous();
            ObservableList<Matiere> matieresList = FXCollections.observableArrayList(matieres);
            matiereComboBox.setItems(matieresList);

            // Set how to display matiere in the combo box
            matiereComboBox.setCellFactory(param -> new ListCell<Matiere>() {
                @Override
                protected void updateItem(Matiere matiere, boolean empty) {
                    super.updateItem(matiere, empty);
                    if (empty || matiere == null) {
                        setText(null);
                    } else {
                        setText(matiere.getNom());
                    }
                }
            });

            matiereComboBox.setButtonCell(new ListCell<Matiere>() {
                @Override
                protected void updateItem(Matiere matiere, boolean empty) {
                    super.updateItem(matiere, empty);
                    if (empty || matiere == null) {
                        setText(null);
                    } else {
                        setText(matiere.getNom());
                    }
                }
            });
        } catch (Exception e) {
            statusLabel.setText("Error loading matieres");
            e.printStackTrace();
        }
    }

    private void loadClasses() {
        try {
            List<classe> classes = classeService.getAll();  // Using Fadi's service
            ObservableList<classe> classesList = FXCollections.observableArrayList(classes);
            classComboBox.setItems(classesList);

            // Configure display
            classComboBox.setCellFactory(param -> new ListCell<classe>() {
                @Override
                protected void updateItem(classe classe, boolean empty) {
                    super.updateItem(classe, empty);
                    setText(empty || classe == null ? null : classe.getNomclasse());
                }
            });

            classComboBox.setButtonCell(new ListCell<classe>() {
                @Override
                protected void updateItem(classe classe, boolean empty) {
                    super.updateItem(classe, empty);
                    setText(empty || classe == null ? "Select a Class" : classe.getNomclasse());
                }
            });

        } catch (Exception e) {
            statusLabel.setText("Error loading classes");
            e.printStackTrace();
        }
    }

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
            // Validate Course Name
            String courseName = nameField.getText().trim();
            if (courseName.isEmpty()) {
                statusLabel.setText("Course name is required!");
                nameField.setStyle("-fx-border-color: red;");

                return;
            }
            if (courseName.matches(".*\\d.*")) {
                statusLabel.setText("Course name must not be a number!");
                nameField.setStyle("-fx-border-color: red;");
                return;
            }
            if (!Character.isUpperCase(courseName.charAt(0))) {
                statusLabel.setText("Course name must start with a capital letter!");
                nameField.setStyle("-fx-border-color: red;");

                return;
            }

            // Validate Chapter Number
            String chapterText = chapterNumberField.getText().trim();
            if (chapterText.isEmpty()) {
                statusLabel.setText("Chapter number is required!");
                chapterNumberField.setStyle("-fx-border-color: red;");

                return;
            }
            int chapterNumber;
            try {
                chapterNumber = Integer.parseInt(chapterText);

            } catch (NumberFormatException e) {
                statusLabel.setText("Chapter number must be a valid integer!");
                chapterNumberField.setStyle("-fx-border-color: red;");
                return;
            }

//            // Validate inputs
//            if (nameField.getText().isEmpty()) {
//                statusLabel.setText("Course name is required!");
//                return;
//            }
//            if (matiereComboBox.getValue() == null) {
//                statusLabel.setText("Please select a matiere!");
//                return;
//            }
//            if (classComboBox.getValue() == null) {  // Changed from classIdField
//                statusLabel.setText("Please select a class!");
//                return;
//            }
//            if (chapterNumberField.getText().isEmpty()) {
//                statusLabel.setText("Chapter number is required!");
//                return;
//            }
//            if (selectedPdfFile == null) {
//                statusLabel.setText("Please select a PDF file!");
//                return;
//            }
//            int chapterNumber;
//            try {
//                chapterNumber = Integer.parseInt(chapterNumberField.getText());
//            } catch (NumberFormatException e) {
//                statusLabel.setText("Chapter number must be a valid integer!");
//                return;
//            }


            // Load upload path from config.properties
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("config.properties")) {
                props.load(in);
            } catch (IOException e) {
                statusLabel.setText("Failed to read config file: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            String uploadPath = props.getProperty("upload.path"); // Example: C:/xampp/htdocs/uploads

            File uploadsFolder = new File(uploadPath);
            // Create the folder if it doesn't exist
            if (!uploadsFolder.exists()) {
                boolean created = uploadsFolder.mkdirs();
                if (!created) {
                    statusLabel.setText("Failed to create upload directory!");
                    return;
                }
            }


            // Copy the PDF file
            File destinationFile = new File(uploadsFolder, selectedPdfFile.getName());
            try {
                Files.copy(
                        selectedPdfFile.toPath(),
                        destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println("File saved to: " + destinationFile.getAbsolutePath()); // Debug log
            } catch (IOException e) {
                statusLabel.setText("Failed to save file: " + e.getMessage());
                e.printStackTrace();
                return;
            }


            // Create Cours object with all fields
            Cours cours = new Cours(
                    nameField.getText(),
                    matiereComboBox.getValue().getId(), // Get ID from selected matiere
                    classComboBox.getValue().getId(),     // Fadi's class ID
                    chapterNumber,  // Using the parsed integer
                    selectedPdfFile.getName() // Only the filename, not the path
            );

            try {
                coursService.ajouter(cours);
                navigateToCoursList();
            } catch (Exception e) {
                statusLabel.setText("Failed to save course: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            statusLabel.setText("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToCoursList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/enseignanlayout.fxml"));
            Parent root = loader.load();

            EnseignantlyoutController controller = loader.getController();
            controller.showCoursView();

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading course list");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleCancel() {
        navigateToCoursList();    }


}
