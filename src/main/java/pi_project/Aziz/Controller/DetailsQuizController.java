package pi_project.Aziz.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import pi_project.Aziz.Entity.Quiz;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class DetailsQuizController {

    @FXML private Label nomLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateAjoutLabel;
    @FXML private Label classeLabel;
    @FXML private Label matiereLabel;
    @FXML private Label coursLabel;

    private Quiz quiz;

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;

        nomLabel.setText("Titre : " + quiz.getNom());
        descriptionLabel.setText("Description : " + quiz.getDescription());
        dateAjoutLabel.setText("Date d'ajout : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(quiz.getDateAjout()));
        classeLabel.setText("Classe : " + quiz.getClasseName());
        matiereLabel.setText("Matière : " + quiz.getMatiereName());
        coursLabel.setText("Cours : " + quiz.getCoursName());
    }

    @FXML
    private void handleAjouterQuestion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Aziz/ajouterQuestion.fxml"));
            Parent root = loader.load();

            // Get the controller and set the quiz
            Object controller = loader.getController();
            if (controller instanceof AjouterQuestionController) {
                ((AjouterQuestionController)controller).setQuiz(this.quiz);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Aziz/afficherQuiz.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des quiz", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}