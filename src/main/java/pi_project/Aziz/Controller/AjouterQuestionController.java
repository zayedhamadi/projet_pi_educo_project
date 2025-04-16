package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AjouterQuestionController {

    @FXML private TextArea questionText;
    @FXML private VBox optionsContainer;
    @FXML private TextField correctAnswerField;
    @FXML private ComboBox<String> quizCombo;

    private QuestionService questionService = new QuestionService();
    private Connection conn = DataSource.getInstance().getConn();
    private List<TextField> optionFields = new ArrayList<>();
    private Quiz currentQuiz;
    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;

        // Display only the quiz name in ComboBox
        if (quizCombo != null && currentQuiz != null) {
            // Clear existing items (optional)
            quizCombo.getItems().clear();

            // Add just the quiz name
            quizCombo.getItems().add(currentQuiz.getNom());

            // Preselect it
            quizCombo.getSelectionModel().select(currentQuiz.getNom());
        }

    }
    @FXML
    public void initialize() {
        populateQuizComboBox();
        addOptionField(); // Add one option by default
    }

    private void populateQuizComboBox() {
        quizCombo.getItems().addAll(getQuizNamesFromDatabase());
    }

    private List<String> getQuizNamesFromDatabase() {
        List<String> quizNames = new ArrayList<>();
        String sql = "SELECT titre FROM quiz";  // 🛠️ changed here

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                quizNames.add(rs.getString("titre"));  // 🛠️ and here
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database Error", "Failed to load quiz names");
        }

        return quizNames;
    }
    @FXML
    private void addOptionField() {
        TextField optionField = new TextField();
        optionField.setPromptText("Option " + (optionFields.size() + 1));
        optionField.getStyleClass().add("form-field");
        optionFields.add(optionField);
        optionsContainer.getChildren().add(optionField);
    }

    @FXML
    private void handleSave() {
        try {
            if (validateForm()) {
                Question question = createQuestion();
                questionService.ajouter(question);
                showSuccessPopup();
                closeWindow(); // ✅ Just close, don’t reload
            }
        } catch (SQLException e) {
            showAlert("Error", "Save Failed", e.getMessage());
        }
    }
    private void retourListeQuiz() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/afficherquestion.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) questionText.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    private boolean validateForm() {
        String question = questionText.getText().trim();
        String answer = correctAnswerField.getText().trim();
        String selectedQuiz = quizCombo.getValue();

        if (question.isEmpty()) {
            showAlert("Error", "Missing Field", "Please enter question text");
            questionText.requestFocus();
            return false;
        }

        if (question.length() > 500) {
            showAlert("Error", "Invalid Length", "Question text cannot exceed 500 characters");
            questionText.requestFocus();
            return false;
        }

        if (containsInvalidChars(question)) {
            showAlert("Error", "Invalid Characters",
                    "Question contains invalid characters: <>{}[]()&^%$#@!~`");
            questionText.requestFocus();
            return false;
        }
        // 🆕 Validate options
        List<String> options = new ArrayList<>();
        for (TextField field : optionFields) {
            String option = field.getText().trim();
            if (!option.isEmpty()) {
                options.add(option);
            }
        }

        // 🛑 Not enough options
        if (options.size() < 2) {
            showAlert("Error", "Invalid Options", "Please enter at least two options");
            return false;
        }

        // 🛑 Duplicate check
        if (hasDuplicates(options)) {
            showAlert("Error", "Duplicate Options", "Options must be unique");
            return false;
        }

        if (answer.isEmpty()) {
            showAlert("Error", "Missing Field", "Please enter correct answer");
            correctAnswerField.requestFocus();
            return false;
        }

        if (answer.length() > 200) {
            showAlert("Error", "Invalid Length", "Correct answer cannot exceed 200 characters");
            correctAnswerField.requestFocus();
            return false;
        }


        // 🛑 Answer not in options
        if (!options.stream().anyMatch(opt -> opt.equalsIgnoreCase(answer))) {
            showAlert("Error", "Invalid Answer", "Correct answer must match one of the options");
            correctAnswerField.requestFocus();
            return false;
        }

        if (selectedQuiz == null || selectedQuiz.isEmpty()) {
            showAlert("Error", "Missing Selection", "Please select a quiz");
            quizCombo.requestFocus();
            return false;
        }

        return true;
    }

    private boolean hasDuplicates(List<String> list) {
        return list.stream().map(String::toLowerCase).distinct().count() < list.size();
    }

    // Updated to check more special characters
    private boolean containsInvalidChars(String text) {
        Pattern pattern = Pattern.compile("[<>{}()\\[\\]&^%$#@!~`]");
        return pattern.matcher(text).find();
    }

    // More robust option checking
    private boolean isAnswerInOptions(String answer) {
        for (Node node : optionsContainer.getChildren()) {
            if (node instanceof TextField) {
                String option = ((TextField) node).getText().trim();
                if (option.equalsIgnoreCase(answer)) {
                    return true;
                }
            }
        }
        return false;
    }
    private Question createQuestion() throws SQLException {
        Question question = new Question();
        question.setTexte(questionText.getText());
        question.setReponse(correctAnswerField.getText());

        // ✅ Fetch full Quiz object from DB by title
        String selectedQuizTitle = quizCombo.getValue();
        Quiz quiz = questionService.getQuizByTitle(selectedQuizTitle);

        if (quiz == null) {
            throw new SQLException("Quiz not found with title: " + selectedQuizTitle);
        }

        question.setQuiz(quiz); // ✅ Set the full object (needed for getId())
        question.setQuizName(quiz.getNom()); // Optional: store title too

        List<String> options = new ArrayList<>();
        for (TextField field : optionFields) {
            if (!field.getText().isEmpty()) {
                options.add(field.getText());
            }
        }
        question.setOptions(options);

        return question;
    }
    private void showSuccessPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Question Added");
        alert.setContentText("Question successfully added to quiz: " + quizCombo.getValue());
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        quizCombo.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


}

