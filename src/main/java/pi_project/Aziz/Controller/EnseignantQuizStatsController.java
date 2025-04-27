package pi_project.Aziz.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import pi_project.Aziz.Entity.Note;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Service.NoteService;
import pi_project.Aziz.Service.QuizService;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnseignantQuizStatsController {
    @FXML private VBox statsContainer;
    @FXML private TableView<QuizStats> statsTable;
    @FXML private TableColumn<QuizStats, String> quizNameColumn;
    @FXML private TableColumn<QuizStats, String> studentNameColumn;
    @FXML private TableColumn<QuizStats, Double> scoreColumn;
    @FXML private BarChart<String, Number> scoreChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final NoteService noteService = new NoteService();
    private final QuizService quizService = new QuizService();
    private final eleveservice eleveService = new eleveservice();

    @FXML
    public void initialize() {
        setupTable();
        loadQuizStatistics();
    }

    private void setupTable() {
        quizNameColumn.setCellValueFactory(new PropertyValueFactory<>("quizName"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        scoreColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty((double)cellData.getValue().getScore()).asObject());

        scoreColumn.setCellFactory(column -> new TableCell<QuizStats, Double>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                setText(empty ? null : String.format("%.1f%%", score));
            }
        });
    }

    private void loadQuizStatistics() {
        try {
            ObservableList<QuizStats> statsData = FXCollections.observableArrayList();
            Map<String, Double> chartData = new HashMap<>();

            List<Quiz> quizzes = quizService.recuperer();

            for (Quiz quiz : quizzes) {
                List<Note> notes = noteService.getNotesByQuiz(quiz.getId());
                double quizTotal = 0;
                int count = 0;

                for (Note note : notes) {
                    eleve student = eleveService.getOne(note.getEleve().getId());
                    String studentName = student.getNom() + " " + student.getPrenom();
                    float score = note.getScore();

                    statsData.add(new QuizStats(quiz.getNom(), studentName, score));
                    quizTotal += score;
                    count++;
                }

                if (count > 0) {
                    chartData.put(quiz.getNom(), quizTotal/count);
                }
            }

            statsTable.setItems(statsData);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Average Scores");
            chartData.forEach((quizName, avgScore) ->
                    series.getData().add(new XYChart.Data<>(quizName, avgScore))
            );
            scoreChart.getData().add(series);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load quiz statistics");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class QuizStats {
        private final String quizName;
        private final String studentName;
        private final double score;

        public QuizStats(String quizName, String studentName, float score) {
            this.quizName = quizName;
            this.studentName = studentName;
            this.score = (double)score;
        }

        public String getQuizName() { return quizName; }
        public String getStudentName() { return studentName; }
        public double getScore() { return score; }
    }
}
