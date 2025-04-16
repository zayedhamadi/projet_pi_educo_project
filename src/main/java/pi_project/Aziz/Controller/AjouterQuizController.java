package pi_project.Aziz.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Quiz;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import pi_project.Aziz.Service.QuizService;
import pi_project.db.DataSource;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AjouterQuizController {

    @FXML
    private TextField nomTextField; // For quiz name

    @FXML
    private DatePicker dateAjoutDatePicker;

    @FXML
    private TextField descriptionTextField;// For quiz creation date

    @FXML
    private ComboBox<String> classeComboBox; // For selecting class

    @FXML
    private ComboBox<String> matiereComboBox; // For selecting subject

    @FXML
    private ComboBox<String> coursComboBox; // For selecting course

    private QuizService quizService;
    private Connection conn;

    public AjouterQuizController() {
        quizService = new QuizService(); // Initialize the service
        this.conn = DataSource.getInstance().getConn();  // Assuming you have a connection utility class
    }

    // Method to initialize the dropdowns and populate them with data from the database
    public void initialize() {
        populateComboBoxes();
    }

    private void populateComboBoxes() {
        // Populate class comboBox with classes from DB
        classeComboBox.getItems().addAll(getClassesFromDatabase());

        // Populate subject comboBox with subjects from DB
        matiereComboBox.getItems().addAll(getMatieresFromDatabase());

        // Populate course comboBox with courses from DB
        coursComboBox.getItems().addAll(getCoursFromDatabase());
    }

    private List<String> getClassesFromDatabase() {
        List<String> classes = new ArrayList<>();
        String sql = "SELECT nom_classe FROM classe";  // Assuming the table name is "classe" and it has a "name" column.
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                classes.add(rs.getString("nom_classe")); // Add class name to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private List<String> getMatieresFromDatabase() {
        List<String> matieres = new ArrayList<>();
        String sql = "SELECT nom FROM matiere";  // Assuming the table name is "matiere" and it has a "name" column.
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                matieres.add(rs.getString("nom")); // Add subject name to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }

    private List<String> getCoursFromDatabase() {
        List<String> cours = new ArrayList<>();
        String sql = "SELECT name FROM cours";  // Assuming the table name is "cours" and it has a "name" column.
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                cours.add(rs.getString("name")); // Add course name to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cours;
    }

    // Method to handle the "Add Quiz" button click
    @FXML
    private void onAjouterQuizButtonClick() {
        if (!validateForm()) {
            return; // Don't proceed if validation fails
        }

        // Rest of your code remains the same...
        int classeId = getClasseId(classeComboBox.getValue());
        int matiereId = getMatiereId(matiereComboBox.getValue());
        int coursId = getCoursId(coursComboBox.getValue());

        Quiz newQuiz = new Quiz();
        newQuiz.setNom(nomTextField.getText());
        newQuiz.setDescription(descriptionTextField.getText());
        newQuiz.setDateAjout(java.sql.Date.valueOf(dateAjoutDatePicker.getValue()));
        newQuiz.setClasseId(classeId);
        newQuiz.setMatiereId(matiereId);
        newQuiz.setCoursId(coursId);

        try {
            quizService.ajouter(newQuiz);
            showAlert("Success", "Quiz added successfully!", AlertType.INFORMATION);
            retourListeQuiz();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the quiz: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private boolean validateForm() {
        // Validate quiz name
        if (nomTextField.getText().isEmpty()) {
            showAlert("Validation Error", "Quiz name cannot be empty", AlertType.ERROR);
            nomTextField.requestFocus();
            return false;
        }

        if (nomTextField.getText().length() < 3) {
            showAlert("Validation Error", "Quiz name must be at least 3 characters", AlertType.ERROR);
            nomTextField.requestFocus();
            return false;
        }

        if (!nomTextField.getText().matches("^[a-zA-Z0-9\\s]+$")) {
            showAlert("Validation Error", "Quiz name can only contain letters, numbers and spaces", AlertType.ERROR);
            nomTextField.requestFocus();
            return false;
        }

        // Validate description
        if (descriptionTextField.getText().isEmpty()) {
            showAlert("Validation Error", "Description cannot be empty", AlertType.ERROR);
            descriptionTextField.requestFocus();
            return false;
        }

        if (descriptionTextField.getText().length() < 10) {
            showAlert("Validation Error", "Description must be at least 10 characters", AlertType.ERROR);
            descriptionTextField.requestFocus();
            return false;
        }

        if (descriptionTextField.getText().length() > 500) {
            showAlert("Validation Error", "Description cannot exceed 500 characters", AlertType.ERROR);
            descriptionTextField.requestFocus();
            return false;
        }

        // Validate date
        if (dateAjoutDatePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a date", AlertType.ERROR);
            dateAjoutDatePicker.requestFocus();
            return false;
        }

        // Validate class selection
        if (classeComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a class", AlertType.ERROR);
            classeComboBox.requestFocus();
            return false;
        }

        // Validate subject selection
        if (matiereComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a subject", AlertType.ERROR);
            matiereComboBox.requestFocus();
            return false;
        }

        // Validate course selection
        if (coursComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a course", AlertType.ERROR);
            coursComboBox.requestFocus();
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void retourListeQuiz() {
        Stage stage = (Stage) nomTextField.getScene().getWindow();
        stage.close(); // Just close the current window
    }
    // Helper method to show alert messages
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Methods to map names to IDs from the database

    private int getClasseId(String classeName) {
        String sql = "SELECT id FROM classe WHERE nom_classe = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classeName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");  // Return the class ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if no class is found
    }

    private int getMatiereId(String matiereName) {
        String sql = "SELECT id FROM matiere WHERE nom = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matiereName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");  // Return the subject ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if no subject is found
    }

    private int getCoursId(String coursName) {
        String sql = "SELECT id FROM cours WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coursName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");  // Return the course ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if no course is found
    }
}
