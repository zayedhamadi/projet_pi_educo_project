package pi_project.Aziz.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import pi_project.Aziz.Entity.Question;
import pi_project.Aziz.Service.QuestionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherQuestionController {

    @FXML
    private TableView<Question> questionTable;
    @FXML
    private TableColumn<Question, String> texteColumn;
    @FXML
    private TableColumn<Question, String> reponseColumn;
    @FXML
    private TableColumn<Question, String> quizColumn;
    @FXML
    private TableColumn<Question, Void> actionsColumn;
    @FXML
    private Button addButton;

    private QuestionService questionService = new QuestionService();

    @FXML
    public void initialize() {
        // Set up columns
        texteColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTexte()));

        reponseColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getReponse()));

        quizColumn.setCellValueFactory(cellData -> {
            Question question = cellData.getValue();
            if (question.getQuiz() != null) {
                return new SimpleStringProperty(question.getQuiz().getNom());
            } else if (question.getQuizName() != null) {
                return new SimpleStringProperty(question.getQuizName());
            }
            return new SimpleStringProperty("N/A");
        });

        // Set up actions column
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final Button detailsBtn = new Button("Détails");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn, detailsBtn);

            {
                editBtn.getStyleClass().add("action-button");
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                deleteBtn.getStyleClass().add("action-button");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                detailsBtn.getStyleClass().add("action-button");
                detailsBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");


                editBtn.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    handleEditQuestion(question);
                });

                deleteBtn.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    handleDeleteQuestion(question);
                });

                detailsBtn.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    handleQuestionDetails(question);
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
        loadQuestions();

        // Set button action
        addButton.setOnAction(event -> navigateToAddQuestion());
    }
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<Question, Void>, TableCell<Question, Void>>() {
            @Override
            public TableCell<Question, Void> call(final TableColumn<Question, Void> param) {
                return new TableCell<Question, Void>() {
                    private final Button editBtn = new Button("Modifier");
                    private final Button deleteBtn = new Button("Supprimer");
                    private final Button detailsBtn = new Button("Détails");
                    private final HBox buttons = new HBox(5, editBtn, deleteBtn, detailsBtn);

                    {
                        editBtn.getStyleClass().add("action-button");
                        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                        deleteBtn.getStyleClass().add("action-button");
                        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                        detailsBtn.getStyleClass().add("action-button");
                        detailsBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");


                        editBtn.setOnAction(event -> {
                            Question question = getTableView().getItems().get(getIndex());
                            handleEditQuestion(question);
                        });

                        deleteBtn.setOnAction(event -> {
                            Question question = getTableView().getItems().get(getIndex());
                            handleDeleteQuestion(question);
                        });

                        detailsBtn.setOnAction(event -> {
                            Question question = getTableView().getItems().get(getIndex());
                            handleQuestionDetails(question);
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
                };
            }
        });
    }

    private void loadQuestions() {
        try {
            List<Question> questionList = questionService.recuperer();
            questionTable.getItems().setAll(questionList);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les questions.");
        }
    }

    private void handleEditQuestion(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/modifierquestion.fxml"));
            Parent root = loader.load();

            ModifierQuestionController controller = loader.getController();
            controller.setQuestion(question);

            Stage newStage = new Stage();
            newStage.setTitle("Modifier Question");
            newStage.setScene(new Scene(root));
            newStage.setOnHiding(event -> loadQuestions());

            newStage.show();
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
                    loadQuestions(); // Refresh the table
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

            Stage newStage = new Stage();
            newStage.setTitle("Détails de la Question");
            newStage.setScene(new Scene(root));

            // Optional: refresh on close if the details view allows modifications
            newStage.setOnHiding(event -> loadQuestions());

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible de charger les détails de la question.");
        }
    }
    private void navigateToAddQuestion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/ajouterquestion.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Ajouter Question");
            newStage.setScene(new Scene(root));

            // Refresh the table when the window closes
            newStage.setOnHiding(event -> loadQuestions());

            newStage.show();
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