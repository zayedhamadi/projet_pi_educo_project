package pi_project.Aziz.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Service.QuizService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AfficherQuizController {

    @FXML
    private TableView<Quiz> quizTable;
    @FXML
    private TableColumn<Quiz, String> titreColumn;
    @FXML
    private TableColumn<Quiz, String> classeColumn;
    @FXML
    private TableColumn<Quiz, String> matiereColumn;
    @FXML
    private TableColumn<Quiz, String> coursColumn;
    @FXML
    private TableColumn<Quiz, String> dateColumn;
    @FXML
    private TableColumn<Quiz, Void> actionsColumn;
    @FXML
    private Button addButton;

    private QuizService quizService = new QuizService();

    @FXML
    public void initialize() {
        // Set up columns
        titreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom()));

        classeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClasseName()));

        matiereColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMatiereName()));

        coursColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCoursName()));

        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return new SimpleStringProperty(sdf.format(cellData.getValue().getDateAjout()));
        });

        // Set up actions column
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final Button detailsBtn = new Button("Détails");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn, detailsBtn);

            {
                editBtn.getStyleClass().add("action-button");
                deleteBtn.getStyleClass().add("action-button");
                detailsBtn.getStyleClass().add("action-button");

                editBtn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    handleEditQuiz(quiz);
                });

                deleteBtn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    handleDeleteQuiz(quiz);
                });

                detailsBtn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    handleQuizDetails(quiz);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        // Load data
        loadQuizzes();

        // Set button action
        addButton.setOnAction(event -> navigateToAddQuiz());
    }

    private void loadQuizzes() {
        try {
            List<Quiz> quizList = quizService.recuperer();
            quizTable.getItems().setAll(quizList);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les quiz.");
        }
    }

    private void handleEditQuiz(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/modifierquiz.fxml"));
            Parent root = loader.load();

            ModifierQuizController controller = loader.getController();
            controller.setQuiz(quiz);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(e -> loadQuizzes()); // Refresh when window closes
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

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    quizService.supprimer(quiz);
                    loadQuizzes(); // Refresh the table
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Erreur", "Une erreur est survenue lors de la suppression.");
                }
            }
        });
    }

    private void handleQuizDetails(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/detailsQuiz.fxml"));
            Parent root = loader.load();

            DetailsQuizController controller = loader.getController();
            controller.setQuiz(quiz);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les détails du quiz.");
        }
    }

    private void navigateToAddQuiz() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/ajouterQuiz.fxml"));
            Parent view = loader.load();

            // Get the content pane from the main layout
            StackPane contentPane = (StackPane) quizTable.getScene().lookup("#contentPane");

            // Replace the center content
            contentPane.getChildren().setAll(view);



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