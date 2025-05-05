package pi_project.Aziz.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import pi_project.Aziz.Entity.Note;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Entity.Question;
import pi_project.Aziz.Service.NoteService;
import pi_project.Aziz.Service.QuestionService;
import pi_project.Fedi.entites.eleve;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class QuizQuestionsController {
    private static final Logger logger = Logger.getLogger(QuizQuestionsController.class.getName());
    private static final int DEFAULT_QUIZ_TIME_SECONDS = 1800; // 30 minutes

    // FXML Components
    @FXML private VBox questionsContainer;
    @FXML private Label timerLabel;
    @FXML private Button submitButton;
    @FXML private Label quizTitle;
    @FXML private ScrollPane scrollPane;

    // Quiz State
    private Quiz quiz;
    private eleve currentStudent;
    private int correctAnswers = 0;
    private int totalQuestions = 0;
    private int timeRemainingSeconds = DEFAULT_QUIZ_TIME_SECONDS;
    private Timeline timer;

    // Services
    private final QuestionService questionService = new QuestionService();
    private final NoteService noteService = new NoteService();

    public void setQuiz(Quiz quiz) {
        Objects.requireNonNull(quiz, "Quiz cannot be null");
        this.quiz = quiz;
        initializeQuiz();
    }

    public void setEleve(eleve student) {
        Objects.requireNonNull(student, "Student cannot be null");
        this.currentStudent = student;
        logger.info(String.format("Student set: %s %s (ID: %d)",
                student.getNom(), student.getPrenom(), student.getId()));
    }

    private void initializeQuiz() {
        try {
            quizTitle.setText(quiz.getNom());
            timeRemainingSeconds = quiz.getTimeLimit() > 0
                    ? quiz.getTimeLimit() * 60
                    : DEFAULT_QUIZ_TIME_SECONDS;

            startTimer();
            loadQuestions();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Quiz initialization failed", e);
            showInfoAndExit("Initialization Error", "Failed to start the quiz");
        }
    }

    private void startTimer() {
        updateTimerDisplay();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemainingSeconds--;
            updateTimerDisplay();

            if (timeRemainingSeconds <= 0) {
                timer.stop();
                handleTimeExpired();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerDisplay() {
        int minutes = timeRemainingSeconds / 60;
        int seconds = timeRemainingSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        if (timeRemainingSeconds <= 30) {
            timerLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else if (timeRemainingSeconds <= 300) {
            timerLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        } else {
            timerLabel.setStyle("-fx-text-fill: #2c3e50;");
        }
    }

    private void handleTimeExpired() {
        submitButton.setDisable(true);
        timerLabel.setText("TIME'S UP!");
        showAlert("Time Expired", "Your time has run out! Submitting automatically...");
        handleSubmit();
    }

    private void loadQuestions() throws SQLException {
        questionsContainer.getChildren().clear();
        List<Question> questions = questionService.getQuestionsForQuiz(quiz.getId());

        if (questions.isEmpty()) {
            showInfoAndExit("No Questions", "This quiz has no questions.");
            return; // Exit without throwing an exception
        }

        totalQuestions = questions.size();
        questions.forEach(this::loadQuestionItem);
    }
    private void loadQuestionItem(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/QuestionItem.fxml"));
            VBox questionItem = loader.load();

            QuestionItemController controller = loader.getController();
            controller.setQuestionData(question);
            questionItem.getProperties().put("controller", controller);

            questionsContainer.getChildren().add(questionItem);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load question: " + question.getId(), e);
        }
    }

    @FXML
    private void handleSubmit() {
        try {
            stopTimer();
            validateSubmissionState();
            calculateResults();
            saveResults();
            showResults();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Quiz submission failed", e);
            showInfoAndExit("Submission Error", e.getMessage());
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void validateSubmissionState() {
        if (quiz == null || currentStudent == null) {
            throw new IllegalStateException("Missing quiz or student information");
        }
    }

    private void calculateResults() {
        correctAnswers = (int) questionsContainer.getChildren().stream()
                .map(node -> node.getProperties().get("controller"))
                .filter(QuestionItemController.class::isInstance)
                .map(QuestionItemController.class::cast)
                .filter(QuestionItemController::isCorrect)
                .count();
    }

    private void saveResults() throws SQLException {
        float scorePercentage = calculatePercentage();
        Note note = new Note(0, scorePercentage, currentStudent, quiz);
        noteService.saveNote(note);
        logger.info(String.format("Saved results - Score: %.1f%%", scorePercentage));
    }

    private float calculatePercentage() {
        return totalQuestions > 0 ? ((float) correctAnswers / totalQuestions) * 100 : 0;
    }

    private void showResults() {
        try {
            // Collect student answers
            List<String> studentAnswers = questionsContainer.getChildren().stream()
                    .map(node -> (QuestionItemController) node.getProperties().get("controller"))
                    .map(QuestionItemController::getSelectedAnswer)
                    .collect(Collectors.toList());

            // Get all questions
            List<Question> questions = questionService.getQuestionsForQuiz(quiz.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/QuizResults.fxml"));
            Parent root = loader.load();

            QuizResultsController controller = loader.getController();
            controller.setResults(
                    correctAnswers,
                    totalQuestions,
                    quiz,
                    currentStudent,
                    studentAnswers,
                    questions
            );

            Stage stage = (Stage) questionsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.sizeToScene();
        } catch (IOException | SQLException e) {
            logger.log(Level.SEVERE, "Failed to show results", e);
            showAlert("Error", "Could not display quiz results");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAndExit(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        // After the alert, close the quiz window
        Stage stage = (Stage) questionsContainer.getScene().getWindow();
        stage.close();
    }
}