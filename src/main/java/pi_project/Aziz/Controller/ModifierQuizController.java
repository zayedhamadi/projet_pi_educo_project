package pi_project.Aziz.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Service.QuizService;
import pi_project.db.DataSource;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModifierQuizController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> classeCombo;
    @FXML private ComboBox<String> matiereCombo;
    @FXML private ComboBox<String> coursCombo;
    @FXML private DatePicker datePicker;

    private Quiz quizToEdit;
    private QuizService quizService = new QuizService();
    private Connection conn;

    public ModifierQuizController() {
        this.conn = DataSource.getInstance().getConn(); // DB connection
    }

    @FXML
    private void initialize() {
        populateComboBoxes();
    }

    private void populateComboBoxes() {
        classeCombo.getItems().addAll(getClassesFromDatabase());
        matiereCombo.getItems().addAll(getMatieresFromDatabase());
        coursCombo.getItems().addAll(getCoursFromDatabase());
    }

    private List<String> getClassesFromDatabase() {
        List<String> classes = new ArrayList<>();
        String sql = "SELECT nom_classe FROM classe";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                classes.add(rs.getString("nom_classe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private List<String> getMatieresFromDatabase() {
        List<String> matieres = new ArrayList<>();
        String sql = "SELECT nom FROM matiere";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                matieres.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }

    private List<String> getCoursFromDatabase() {
        List<String> cours = new ArrayList<>();
        String sql = "SELECT name FROM cours";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                cours.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cours;
    }

    public void setQuiz(Quiz quiz) {
        this.quizToEdit = quiz;

        titreField.setText(quiz.getNom());
        descriptionField.setText(quiz.getDescription());
        datePicker.setValue(new java.sql.Date(quiz.getDateAjout().getTime()).toLocalDate());

        // Get and set names from IDs
        classeCombo.setValue(getClasseNameById(quiz.getClasseId()));
        matiereCombo.setValue(getMatiereNameById(quiz.getMatiereId()));
        coursCombo.setValue(getCoursNameById(quiz.getCoursId()));
    }

    private String getClasseNameById(int id) {
        String sql = "SELECT nom_classe FROM classe WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nom_classe");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMatiereNameById(int id) {
        String sql = "SELECT nom FROM matiere WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nom");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCoursNameById(int id) {
        String sql = "SELECT name FROM cours WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getClasseId(String nom) {
        String sql = "SELECT id FROM classe WHERE nom_classe = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getMatiereId(String nom) {
        String sql = "SELECT id FROM matiere WHERE nom = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getCoursId(String nom) {
        String sql = "SELECT id FROM cours WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return; // Don't proceed if validation fails
        }

        try {
            quizToEdit.setNom(titreField.getText());
            quizToEdit.setDescription(descriptionField.getText());
            quizToEdit.setDateAjout(java.sql.Date.valueOf(datePicker.getValue()));

            quizToEdit.setClasseId(getClasseId(classeCombo.getValue()));
            quizToEdit.setMatiereId(getMatiereId(matiereCombo.getValue()));
            quizToEdit.setCoursId(getCoursId(coursCombo.getValue()));

            quizService.modifier(quizToEdit);

            showInfo("Succès", "Le quiz a été modifié avec succès.");
            retourListeQuiz();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur", "Échec de la mise à jour du quiz: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        // Validate quiz title
        if (titreField.getText().isEmpty()) {
            showError("Erreur de validation", "Le titre du quiz ne peut pas être vide");
            titreField.requestFocus();
            return false;
        }

        if (titreField.getText().length() < 3) {
            showError("Erreur de validation", "Le titre doit contenir au moins 3 caractères");
            titreField.requestFocus();
            return false;
        }

        if (!titreField.getText().matches("^[a-zA-Z0-9\\s]+$")) {
            showError("Erreur de validation", "Le titre ne peut contenir que des lettres, chiffres et espaces");
            titreField.requestFocus();
            return false;
        }

        // Validate description
        if (descriptionField.getText().isEmpty()) {
            showError("Erreur de validation", "La description ne peut pas être vide");
            descriptionField.requestFocus();
            return false;
        }

        if (descriptionField.getText().length() < 10) {
            showError("Erreur de validation", "La description doit contenir au moins 10 caractères");
            descriptionField.requestFocus();
            return false;
        }

        if (descriptionField.getText().length() > 500) {
            showError("Erreur de validation", "La description ne peut pas dépasser 500 caractères");
            descriptionField.requestFocus();
            return false;
        }

        // Validate date
        if (datePicker.getValue() == null) {
            showError("Erreur de validation", "Veuillez sélectionner une date");
            datePicker.requestFocus();
            return false;
        }

        // Validate class selection
        if (classeCombo.getValue() == null) {
            showError("Erreur de validation", "Veuillez sélectionner une classe");
            classeCombo.requestFocus();
            return false;
        }

        // Validate subject selection
        if (matiereCombo.getValue() == null) {
            showError("Erreur de validation", "Veuillez sélectionner une matière");
            matiereCombo.requestFocus();
            return false;
        }

        // Validate course selection
        if (coursCombo.getValue() == null) {
            showError("Erreur de validation", "Veuillez sélectionner un cours");
            coursCombo.requestFocus();
            return false;
        }

        return true;
    }

  

    @FXML
    private void handleCancel() {
        retourListeQuiz();
    }

    private void retourListeQuiz() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close(); // Just close the current window
    }

    private void showError(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
