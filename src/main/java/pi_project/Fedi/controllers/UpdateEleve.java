package pi_project.Fedi.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;
import pi_project.Zayed.Entity.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class UpdateEleve {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissanceField;
    @FXML private ComboBox<classe> classeComboBox;
    @FXML private ComboBox<User> parentComboBox;
    @FXML private TextField moyenneField;
    @FXML private TextField absenceField;
    @FXML private DatePicker dateInscriptionField;

    @FXML private Label nomErreur;
    @FXML private Label prenomErreur;
    @FXML private Label dateNaissanceErreur;
    @FXML private Label classeErreur;
    @FXML private Label parentErreur;
    @FXML private Label moyenneErreur;
    @FXML private Label absenceErreur;
    @FXML private Label dateInscriptionErreur;

    private eleve currentEleve;
    private final eleveservice eleveService = new eleveservice();

    // Appelée pour injecter l'élève à modifier
    public void setEleve(eleve e) {
        this.currentEleve = e;

        nomField.setText(e.getNom());
        prenomField.setText(e.getPrenom());
        dateNaissanceField.setValue(LocalDate.ofEpochDay(e.getDateNaissance().getDate()));
        moyenneField.setText(String.valueOf(e.getMoyenne()));
        absenceField.setText(String.valueOf(e.getNbreAbsence()));
        dateInscriptionField.setValue(LocalDate.ofEpochDay(e.getDateInscription().getDate()));

        loadComboBoxes();

        classeComboBox.getSelectionModel().select(e.getClasse());
        parentComboBox.getSelectionModel().select(e.getParent());
    }

    @FXML
    private void initialize() {
        loadComboBoxes();
    }

    private void loadComboBoxes() {
        ObservableList<classe> classes = FXCollections.observableArrayList(eleveService.getAllClasses());
        classeComboBox.setItems(classes);

        ObservableList<User> parents = FXCollections.observableArrayList(eleveService.getAllParents());
        parentComboBox.setItems(parents);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        clearErrors();

        try {
            currentEleve.setNom(nomField.getText());
            currentEleve.setPrenom(prenomField.getText());
            currentEleve.setDateNaissance(java.sql.Date.valueOf(dateNaissanceField.getValue()));
            currentEleve.setClasse(classeComboBox.getValue());
            currentEleve.setParent(parentComboBox.getValue());
            currentEleve.setMoyenne(Double.parseDouble(moyenneField.getText()));
            currentEleve.setNbreAbsence(Integer.parseInt(absenceField.getText()));
            currentEleve.setDateInscription(java.sql.Date.valueOf(dateInscriptionField.getValue()));

            eleveService.update(currentEleve);

            showAlert("Succès", "L'élève a été modifié avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la mise à jour de l'élève.");
        }
    }

    @FXML
    private void handleListEleves(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/ListEleve.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la liste des élèves.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/ListeOfEleve.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir à la liste des élèves.");
        }
    }

    private void clearErrors() {
        nomErreur.setText("");
        prenomErreur.setText("");
        dateNaissanceErreur.setText("");
        classeErreur.setText("");
        parentErreur.setText("");
        moyenneErreur.setText("");
        absenceErreur.setText("");
        dateInscriptionErreur.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
