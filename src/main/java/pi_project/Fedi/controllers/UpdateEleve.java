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
import javafx.util.StringConverter;
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

    @FXML
    private void initialize() {
        setupComboBoxes();
        loadComboBoxes();
    }

    private void setupComboBoxes() {
        // Configuration pour afficher le nom de la classe
        classeComboBox.setConverter(new StringConverter<classe>() {
            @Override
            public String toString(classe classe) {
                return classe == null ? "" : classe.getNomclasse();
            }

            @Override
            public classe fromString(String string) {
                return null;
            }
        });

        // Configuration pour afficher l'email du parent
        parentComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getEmail();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }

    // Appelée pour injecter l'élève à modifier
    public void setEleve(eleve e) {
        this.currentEleve = e;

        nomField.setText(e.getNom());
        prenomField.setText(e.getPrenom());
        dateNaissanceField.setValue(LocalDate.ofEpochDay(e.getDateNaissance().getTime() / (24 * 60 * 60 * 1000)));
        moyenneField.setText(String.valueOf(e.getMoyenne()));
        absenceField.setText(String.valueOf(e.getNbreAbsence()));
        dateInscriptionField.setValue(LocalDate.ofEpochDay(e.getDateInscription().getTime() / (24 * 60 * 60 * 1000)));

        loadComboBoxes();

        // Sélectionner la classe et le parent actuels
        for (classe c : classeComboBox.getItems()) {
            if (c.getId() == e.getClasse().getId()) {
                classeComboBox.getSelectionModel().select(c);
                break;
            }
        }

        for (User p : parentComboBox.getItems()) {
            if (p.getId() == e.getParent().getId()) {
                parentComboBox.getSelectionModel().select(p);
                break;
            }
        }
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
        boolean hasError = false;

        try {
            // Validation du nom
            if (nomField.getText().trim().isEmpty()) {
                nomErreur.setText("Le nom est requis");
                hasError = true;
            }

            // Validation du prénom
            if (prenomField.getText().trim().isEmpty()) {
                prenomErreur.setText("Le prénom est requis");
                hasError = true;
            }

            // Validation de la classe
            if (classeComboBox.getValue() == null) {
                classeErreur.setText("La classe est requise");
                hasError = true;
            }

            // Validation du parent
            if (parentComboBox.getValue() == null) {
                parentErreur.setText("Le parent est requis");
                hasError = true;
            }

            // Validation de la date de naissance
            if (dateNaissanceField.getValue() == null) {
                dateNaissanceErreur.setText("La date de naissance est requise");
                hasError = true;
            }

            // Validation de la moyenne
            try {
                double moyenne = Double.parseDouble(moyenneField.getText());
                if (moyenne < 0 || moyenne > 20) {
                    moyenneErreur.setText("La moyenne doit être entre 0 et 20");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                moyenneErreur.setText("La moyenne doit être un nombre valide");
                hasError = true;
            }

            // Validation du nombre d'absences
            try {
                int absences = Integer.parseInt(absenceField.getText());
                if (absences < 0) {
                    absenceErreur.setText("Le nombre d'absences ne peut pas être négatif");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                absenceErreur.setText("Le nombre d'absences doit être un nombre entier");
                hasError = true;
            }

            // Si des erreurs sont présentes, ne pas continuer
            if (hasError) {
                return;
            }

            // Mise à jour des données de l'élève
            currentEleve.setNom(nomField.getText().trim());
            currentEleve.setPrenom(prenomField.getText().trim());
            currentEleve.setDateNaissance(java.sql.Date.valueOf(dateNaissanceField.getValue()));
            
            classe selectedClasse = classeComboBox.getValue();
            if (selectedClasse != null) {
                currentEleve.setClasse(selectedClasse);
            } else {
                throw new IllegalStateException("Une classe doit être sélectionnée");
            }
            
            User selectedParent = parentComboBox.getValue();
            if (selectedParent != null) {
                currentEleve.setParent(selectedParent);
            } else {
                throw new IllegalStateException("Un parent doit être sélectionné");
            }
            
            currentEleve.setMoyenne(Double.parseDouble(moyenneField.getText()));
            currentEleve.setNbreAbsence(Integer.parseInt(absenceField.getText()));
            currentEleve.setDateInscription(java.sql.Date.valueOf(dateInscriptionField.getValue()));

            eleveService.update(currentEleve);

            showAlert("Succès", "L'élève a été modifié avec succès.");
            handleBack(event);

        } catch (IllegalStateException e) {
            showAlert("Erreur de validation", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la mise à jour de l'élève : " + e.getMessage());
        }
    }

//    @FXML
//    private void handleListEleves(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/ListEleve.fxml"));
//            Parent root = loader.load();
//
//            Scene scene = new Scene(root);
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setScene(scene);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            showAlert("Erreur", "Impossible de charger la liste des élèves.");
//        }
//    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the main layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
            Parent root = loader.load();

            // Get controller and show eleve list
            pi_project.Saif.Controller.MainLayoutController mainController = loader.getController();
            mainController.showListeleve();

            // Set the new scene
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

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
