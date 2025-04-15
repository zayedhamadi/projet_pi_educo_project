package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Service.QuizService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AfficherQuizController {

    @FXML
    private VBox quizContainer;
    @FXML
    private Button addButton; // Changed from TableView to VBox

    private QuizService quizService = new QuizService();

    public void initialize() {
        addButton.setOnAction(this::navigateToAddQuiz);

        loadQuizCards();
    }

    private void loadQuizCards() {
        try {
            List<Quiz> quizList = quizService.recuperer();
            quizContainer.getChildren().clear(); // Clear existing cards

            // Create header row


            // Create quiz cards
            for (int i = 0; i < quizList.size(); i++) {
                Quiz quiz = quizList.get(i);
                HBox card = createQuizCard(i + 1, quiz);
                quizContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error (show alert, etc.)
        }
    }


    private HBox createQuizCard(int index, Quiz quiz) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("quiz-card");




        // Title
        Label titleLabel = new Label(quiz.getNom());
        titleLabel.setMinWidth(200);

        // Class
        Label classLabel = new Label(quiz.getClasseName());
        classLabel.setMinWidth(80);

        // Subject
        Label subjectLabel = new Label(quiz.getMatiereName());
        subjectLabel.setMinWidth(150);

        // Course
        Label courseLabel = new Label(quiz.getCoursName());
        courseLabel.setMinWidth(150);

        // Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Label dateLabel = new Label(sdf.format(quiz.getDateAjout()));
        dateLabel.setMinWidth(120);

        // Actions
        HBox actionsBox = new HBox(5);
        Button editBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");
        Button detailsBtn = new Button("Détails"); // NEW BUTTON

        editBtn.getStyleClass().add("action-button");
        deleteBtn.getStyleClass().add("action-button");
        detailsBtn.getStyleClass().add("action-button"); // Style like others

        actionsBox.setMinWidth(100);
        actionsBox.getChildren().addAll(editBtn, deleteBtn,detailsBtn);

        // Add button handlers
        editBtn.setOnAction(e -> handleEditQuiz(quiz));
        deleteBtn.setOnAction(e -> handleDeleteQuiz(quiz));
        detailsBtn.setOnAction(e -> handleQuizDetails(quiz)); // NEW HANDLER


        // Add all components to card
        card.getChildren().addAll(
                 titleLabel, classLabel,
                subjectLabel, courseLabel, dateLabel, actionsBox
        );

        // Apply field styling
        for (Node node : card.getChildren()) {
            if (node instanceof Label) {
                node.getStyleClass().add("quiz-field");
            }
        }

        return card;
    }

    private void handleEditQuiz(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/modifierquiz.fxml"));
            Parent root = loader.load();

            ModifierQuizController controller = loader.getController();
            controller.setQuiz(quiz);

            Stage stage = (Stage) quizContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger le formulaire de modification.");
        }
    }

    private void handleDeleteQuiz(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Suppression du Quiz");
        alert.setContentText("Voulez-vous vraiment supprimer ce quiz ?");

        Button ouiButton = (Button) alert.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK);
        ouiButton.setText("Oui");
        Button nonButton = (Button) alert.getDialogPane().lookupButton(javafx.scene.control.ButtonType.CANCEL);
        nonButton.setText("Non");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    quizService.supprimer(quiz);
                    loadQuizCards(); // Refresh the view
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Erreur", "Une erreur est survenue lors de la suppression.");
                }
            }
        });
    }


    @FXML
    private void navigateToAddQuiz(javafx.event.ActionEvent event) {  // Fully qualified name
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Aziz/ajouterQuiz.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Could not load the add quiz form");
            alert.showAndWait();
        }
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void handleQuizDetails(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/detailsQuiz.fxml"));
            Parent root = loader.load();

            DetailsQuizController controller = loader.getController();
            controller.setQuiz(quiz);

            Stage stage = (Stage) quizContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les détails du quiz.");
        }
    }

}