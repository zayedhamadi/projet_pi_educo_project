package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Note;
import pi_project.Aziz.Service.NoteService;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.session;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class QuizHistoryController {
    @FXML private VBox quizCardsContainer;
    @FXML private Label studentNameLabel;

    private final NoteService noteService = new NoteService();
    private final UserImpl userService = new UserImpl();
    private final eleveservice eleveService = new eleveservice();
    private eleve currentStudent;

    @FXML
    public void initialize() {
        loadLoggedInParentStudent();
        loadQuizHistoryCards();
    }

    private void loadQuizHistoryCards() {
        quizCardsContainer.getChildren().clear();
        quizCardsContainer.setSpacing(10);
        quizCardsContainer.setStyle("-fx-padding: 15;");

        try {
            if (currentStudent != null) {
                List<Note> quizResults = noteService.getNotesByStudent(currentStudent.getId());

                if (quizResults.isEmpty()) {
                    Label noResultsLabel = new Label("No quiz history available for " + currentStudent.getNom());
                    noResultsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
                    quizCardsContainer.getChildren().add(noResultsLabel);
                    return;
                }

                for (Note note : quizResults) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/QuizHistoryCard.fxml"));
                    Parent card = loader.load();

                    QuizHistoryCardController cardController = loader.getController();
                    cardController.setQuizData(
                            note.getQuiz().getNom(),
                            note.getScore()
                    );

                    quizCardsContainer.getChildren().add(card);
                }
            }
        } catch (IOException | SQLException e) {
            showAlert("Error", "Failed to load quiz history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLoggedInParentStudent() {
        try {
            User parent = getLoggedInParent();
            if (parent != null) {
                this.currentStudent = getEleveByParent(parent);
                if (currentStudent != null) {
                    studentNameLabel.setText(currentStudent.getNom() + " " + currentStudent.getPrenom());
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load student information: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Aziz/ParentDashboard.fxml"));
            Stage stage = (Stage) quizCardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not return to dashboard: " + e.getMessage());
        }
    }

    private User getLoggedInParent() {
        int loggedInUserId = session.getUserSession();
        if (loggedInUserId == 0) {
            showAlert("Error", "No user logged in");
            return null;
        }

        User loggedInUser = userService.getSpeceficUser(loggedInUserId);
        if (loggedInUser == null) {
            showAlert("Error", "User not found");
            return null;
        }

        if (!loggedInUser.getRoles().contains(Role.Parent)) {
            showAlert("Error", "Only parent accounts can access this feature");
            return null;
        }

        return loggedInUser;
    }

    private eleve getEleveByParent(User parent) {
        if (parent == null) return null;

        try {
            List<eleve> enfants = eleveService.getEnfantsParParent(parent.getId());
            return enfants.isEmpty() ? null : enfants.get(0);
        } catch (Exception e) {
            showAlert("Error", "Failed to load student: " + e.getMessage());
            return null;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}