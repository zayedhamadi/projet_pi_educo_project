package pi_project.Fedi.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.classeservice;
import pi_project.Fedi.services.eleveservice;
import pi_project.Main;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class AjouterEleve implements Initializable {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private DatePicker dateNaissanceField;
    @FXML
    private TextField moyenneField;
    @FXML
    private TextField absenceField;
    @FXML
    private DatePicker dateInscriptionField;
    @FXML
    private ComboBox<classe> classeComboBox;
    @FXML
    private TextField parentIdField;

    @FXML
    private Label nomErreur;
    @FXML
    private Label prenomErreur;
    @FXML
    private Label dateNaissanceErreur;
    @FXML
    private Label moyenneErreur;
    @FXML
    private Label absenceErreur;
    @FXML
    private Label dateInscriptionErreur;
    @FXML
    private Label classeErreur;
    @FXML
    private Label parentErreur;

    private final eleveservice service = new eleveservice();
    private final classeservice classeService = new classeservice();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser la ComboBox des classes
        classeComboBox.getItems().addAll(classeService.getAll());
        
        // Initialiser les dates par défaut
        dateNaissanceField.setValue(LocalDate.now());
        dateInscriptionField.setValue(LocalDate.now());
    }

    @FXML
    private void handleSave() {
        if (validateInputs()) {
            eleve newEleve = new eleve(
                classeComboBox.getValue(),
                Integer.parseInt(parentIdField.getText()),
                nomField.getText(),
                prenomField.getText(),
                Date.from(dateNaissanceField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Double.parseDouble(moyenneField.getText()),
                Integer.parseInt(absenceField.getText()),
                Date.from(dateInscriptionField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                generateQRCode()
            );


            service.add(newEleve);
            showSuccess("Élève ajouté avec succès!");
            handleBack();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validation du nom
        if (nomField.getText().isEmpty()) {
            nomErreur.setText("Le nom est requis");
            isValid = false;
        } else {
            nomErreur.setText("");
        }

        // Validation du prénom
        if (prenomField.getText().isEmpty()) {
            prenomErreur.setText("Le prénom est requis");
            isValid = false;
        } else {
            prenomErreur.setText("");
        }

        // Validation de la classe
        if (classeComboBox.getValue() == null) {
            classeErreur.setText("La classe est requise");
            isValid = false;
        } else {
            classeErreur.setText("");
        }

        // Validation de l'ID parent
        try {
            Integer.parseInt(parentIdField.getText());
            parentErreur.setText("");
        } catch (NumberFormatException e) {
            parentErreur.setText("ID parent invalide");
            isValid = false;
        }

        // Validation de la moyenne
        try {
            double moyenne = Double.parseDouble(moyenneField.getText());
            if (moyenne < 0 || moyenne > 20) {
                moyenneErreur.setText("La moyenne doit être entre 0 et 20");
                isValid = false;
            } else {
                moyenneErreur.setText("");
            }
        } catch (NumberFormatException e) {
            moyenneErreur.setText("Moyenne invalide");
            isValid = false;
        }

        // Validation des absences
        try {
            int absences = Integer.parseInt(absenceField.getText());
            if (absences < 0) {
                absenceErreur.setText("Le nombre d'absences ne peut pas être négatif");
                isValid = false;
            } else {
                absenceErreur.setText("");
            }
        } catch (NumberFormatException e) {
            absenceErreur.setText("Nombre d'absences invalide");
            isValid = false;
        }

        return isValid;
    }

    private String generateQRCode() {
        // Logique de génération de QR code à implémenter
        return "QR_" + System.currentTimeMillis();
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("ListeOfClasse.fxml");
        } catch (Exception e) {
            showError("Erreur lors du retour à la liste");
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
