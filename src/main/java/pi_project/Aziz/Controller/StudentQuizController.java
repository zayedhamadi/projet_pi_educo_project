package pi_project.Aziz.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Service.QuizService;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StudentQuizController {
    @FXML
    private FlowPane quizContainer;

    private final QuizService quizService = new QuizService();
    private final UserImpl userService = new UserImpl(); // Changed to UserImpl
    private final eleveservice eleveService = new eleveservice();

    private eleve currentStudent;

    @FXML
    public void initialize() {
        loadLoggedInStudent();
        loadQuizzes();
    }

    private void loadLoggedInStudent() {
        try {
            // 1. Get the logged-in user ID from session
            int loggedInUserId = session.getUserSession();
            if (loggedInUserId == 0) {
                showAlert("Error", "No user logged in");
                return;
            }

            // 2. Get the user from UserImpl service
            User loggedInUser = userService.getSpeceficUser(loggedInUserId);
            if (loggedInUser == null) {
                showAlert("Error", "User not found");
                return;
            }

            // 3. Verify this is actually a parent
            if (!loggedInUser.getRoles().contains(Role.Parent)) {
                showAlert("Error", "Only parent accounts can access this feature");
                return;
            }

            // 4. Get their associated eleve (child)
            List<eleve> enfants = eleveService.getEnfantsParParent(loggedInUserId);

            if (enfants.isEmpty()) {
                showAlert("Error", "No student associated with this parent account");
                return;
            }

            this.currentStudent = enfants.get(0);

        } catch (RuntimeException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void loadQuizzes() {
        quizContainer.getChildren().clear();
        try {
            List<Quiz> quizzes = quizService.recuperer();

            for (Quiz quiz : quizzes) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/QuizCard.fxml"));
                    Parent quizCard = loader.load();

                    QuizCardController controller = loader.getController();
                    controller.setQuizData(quiz);
                    controller.setCurrentStudent(currentStudent);

                    quizContainer.getChildren().add(quizCard);
                } catch (IOException e) {
                    showAlert("UI Error", "Failed to load quiz card: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load quizzes: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML

    private void navigateToChatbot() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Aziz/chatbot.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // This makes it a popup modal
            stage.setTitle("Quiz Chatbot Assistant");


            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load chatbot.");
        }
    }


}