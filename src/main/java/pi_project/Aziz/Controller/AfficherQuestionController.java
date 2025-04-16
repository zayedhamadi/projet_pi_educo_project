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
import javafx.stage.Stage;
import pi_project.Aziz.Entity.Question;
import pi_project.Aziz.Service.QuestionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherQuestionController {

    @FXML
    private VBox questionContainer;
    @FXML
    private Button addButton;

    private QuestionService questionService = new QuestionService();

    public void initialize() {
        addButton.setOnAction(this::navigateToAddQuestion);
        loadQuestionCards();
    }

    private void loadQuestionCards() {
        try {
            List<Question> questionList = questionService.recuperer();
            questionContainer.getChildren().clear();

            for (Question q : questionList) {
                HBox card = createQuestionCard(q);
                questionContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les questions.");
        }
    }

    private HBox createQuestionCard(Question q) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("question-card");

        Label texteLabel = new Label(q.getTexte());
        texteLabel.setMinWidth(250);

        Label reponseLabel = new Label(q.getReponse());
        reponseLabel.setMinWidth(100);

        Label quizLabel = new Label(q.getQuiz() != null ? q.getQuiz().getNom() : "N/A");
        quizLabel.setMinWidth(150);

        Button editBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");
        Button detailsBtn = new Button("Détails");

        editBtn.getStyleClass().add("action-button");
        deleteBtn.getStyleClass().add("action-button");
        detailsBtn.getStyleClass().add("action-button");

        HBox actionBox = new HBox(5, editBtn, deleteBtn, detailsBtn);
        actionBox.setMinWidth(120);

        deleteBtn.setOnAction(e -> handleDeleteQuestion(q));
        editBtn.setOnAction(e -> handleEditQuestion(q));
        detailsBtn.setOnAction(e -> handleQuestionDetails(q));



        card.getChildren().addAll(texteLabel, reponseLabel, quizLabel, actionBox);

        return card;
    }

    private void handleEditQuestion(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/modifierquestion.fxml"));
            Parent root = loader.load();

            ModifierQuestionController controller = loader.getController();
            controller.setQuestion(question);

            Stage stage = (Stage) questionContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger le formulaire de modification.");
        }
    }

    private void handleDeleteQuestion(Question question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Suppression de la Question");
        alert.setContentText("Voulez-vous vraiment supprimer cette question ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    questionService.supprimer(question);
                    loadQuestionCards();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Erreur", "Une erreur est survenue lors de la suppression.");
                }
            }
        });
    }
    private void handleQuestionDetails(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/detailsquestion.fxml"));
            Parent root = loader.load();

            DetailsQuestionController controller = loader.getController();
            controller.setQuestion(question);

            Stage stage = (Stage) questionContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les détails de la question.");
        }
    }


    private void navigateToAddQuestion(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Aziz/ajouterquestion.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger le formulaire d'ajout.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
