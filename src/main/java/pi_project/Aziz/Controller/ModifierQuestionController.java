package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Question;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Service.QuestionService;
import pi_project.db.DataSource;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModifierQuestionController {

    @FXML private TextArea contenuField;
    @FXML private VBox optionsContainer; // Added to match FXML
    @FXML private TextField reponseField;
    @FXML private ComboBox<String> quizCombo;

    private Question questionToEdit;
    private QuestionService questionService = new QuestionService();
    private Connection conn;
    private List<TextField> optionFields = new ArrayList<>(); // For dynamic options

    public ModifierQuestionController() {
        this.conn = DataSource.getInstance().getConn();
    }

    @FXML
    private void initialize() {
        populateQuizCombo();
    }

    @FXML
    private void addOptionField() {
        TextField optionField = new TextField();
        optionField.setPromptText("Option " + (optionFields.size() + 1));
        optionsContainer.getChildren().add(optionField);
        optionFields.add(optionField);
    }

    private void populateQuizCombo() {
        quizCombo.getItems().clear();
        quizCombo.getItems().addAll(getQuizTitlesFromDatabase());
    }

    private List<String> getQuizTitlesFromDatabase() {
        List<String> quizTitles = new ArrayList<>();
        String sql = "SELECT titre FROM quiz"; // Changed from 'nom' to 'titre' to match your FXML
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                quizTitles.add(rs.getString("titre"));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load quizzes");
            e.printStackTrace();
        }
        return quizTitles;
    }

    public void setQuestion(Question question) {
        this.questionToEdit = question;
        contenuField.setText(question.getTexte());
        reponseField.setText(question.getReponse());

        // Load options into dynamic fields
        if (question.getOptions() != null) {
            for (String option : question.getOptions()) {
                TextField optionField = new TextField(option);
                optionsContainer.getChildren().add(optionField);
                optionFields.add(optionField);
            }
        }

        quizCombo.setValue(getQuizTitleById(question.getQuiz().getId()));
    }

    private String getQuizTitleById(int id) {
        String sql = "SELECT titre FROM quiz WHERE id = ?"; // Changed from 'nom' to 'titre'
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("titre");
        } catch (SQLException e) {
            showError("Database Error", "Failed to load quiz title");
        }
        return null;
    }

    private int getQuizIdByTitle(String title) {
        String sql = "SELECT id FROM quiz WHERE titre = ?"; // Changed from 'nom' to 'titre'
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            showError("Database Error", "Failed to find quiz ID");
        }
        return -1;
    }

    @FXML
    private void handleSave() {
        try {
            // Validate inputs
            if (contenuField.getText().isEmpty() || reponseField.getText().isEmpty()) {
                showError("Validation Error", "Question text and answer cannot be empty");
                return;
            }

            // Update question object
            questionToEdit.setTexte(contenuField.getText());
            questionToEdit.setReponse(reponseField.getText());

            // Get options from dynamic fields
            List<String> options = new ArrayList<>();
            for (TextField field : optionFields) {
                if (!field.getText().isEmpty()) {
                    options.add(field.getText());
                }
            }
            questionToEdit.setOptions(options);

            // Set quiz (assuming your Question entity has a Quiz object)
            int quizId = getQuizIdByTitle(quizCombo.getValue());
            if (quizId == -1) {
                showError("Error", "Invalid quiz selected");
                return;
            }
            Quiz quiz = new Quiz();
            quiz.setId(quizId);
            questionToEdit.setQuiz(quiz);

            // Call service to update
            questionService.modifier(questionToEdit);

            showInfo("Success", "Question updated successfully");
            retourListeQuestions();

        } catch (SQLException | IOException e) {
            showError("Error", "Failed to update question: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            retourListeQuestions();
        } catch (IOException e) {
            showError("Error", "Failed to return to list: " + e.getMessage());
        }
    }

    private void retourListeQuestions() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/afficherQuestion.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) contenuField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}