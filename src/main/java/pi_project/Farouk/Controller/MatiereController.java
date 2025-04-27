package pi_project.Farouk.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.Services.EnseignantService;
import pi_project.Farouk.Services.MatiereService;
import pi_project.Saif.Controller.MainLayoutController;
import pi_project.Zayed.Entity.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MatiereController {
    @FXML private ComboBox<User> enseignantCombo;

    @FXML private TextField enseignantIdField;
    @FXML private TextField nomField;
    @FXML private TextField coeffField;

    private final EnseignantService enseignantService = new EnseignantService();

    private final MatiereService matiereService = new MatiereService();
    private Matiere currentMatiere;


    @FXML
    public void initialize() {
        loadEnseignants();
    }
    private void loadEnseignants() {
        List<User> enseignants = enseignantService.getAllEnseignants();
        ObservableList<User> observableList = FXCollections.observableArrayList(enseignants);

        enseignantCombo.setItems(observableList);
        enseignantCombo.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? "" : user.getNom() + " " + user.getPrenom());
            }
        });
    }


//    @FXML
//    private void handleSave(ActionEvent event) {
//        if (validateInput()) {
//            try {
//                User selectedTeacher = enseignantCombo.getValue();
//                Matiere matiere = new Matiere(
//                        selectedTeacher.getId(),
//                        nomField.getText(),
//                        Double.parseDouble(coeffField.getText())
//                );
//                if (currentMatiere == null) {
////                    // Add new matiere
////                    Matiere matiere = new Matiere(
////                            Integer.parseInt(enseignantIdField.getText()),
////                            nomField.getText(),
////                            Double.parseDouble(coeffField.getText())
////                    );
//                    matiereService.ajouter(matiere);
//                    showAlert("Success", "Matiere added successfully");
//                } else {
////                    // Update existing matiere
////                    currentMatiere.setId_ensg(Integer.parseInt(enseignantIdField.getText()));
////                    currentMatiere.setNom(nomField.getText());
////                    currentMatiere.setCoefficient(Double.parseDouble(coeffField.getText()));
//                    matiereService.modifier(currentMatiere);
////                    showAlert("Success", "Matiere updated successfully");
//                }
//                handleClear(event);
//                goToListMatiere();
//            } catch (NumberFormatException e) {
//                showAlert("Input Error", "Please enter valid numbers");
//            } catch (SQLException e) {
//                showAlert("Database Error", "Error saving matiere: " + e.getMessage());
//            }
//        }
//    }
@FXML
private void handleSave(ActionEvent event) {
    if (!validateInput()) {  // Changed to !validateInput() for better readability
        return;
    }

    try {
        User selectedTeacher = enseignantCombo.getValue();
        double coefficient = Double.parseDouble(coeffField.getText());

        if (currentMatiere == null) {
            // Add new matiere
            Matiere matiere = new Matiere(
                    selectedTeacher.getId(),
                    nomField.getText(),
                    coefficient
            );
            matiereService.ajouter(matiere);
            showAlert("Success", "Subject added successfully");
        } else {
            // Update existing matiere - need to update currentMatiere with new values
            currentMatiere.setId_ensg(selectedTeacher.getId());
            currentMatiere.setNom(nomField.getText());
            currentMatiere.setCoefficient(coefficient);
            matiereService.modifier(currentMatiere);
            showAlert("Success", "Subject updated successfully");
        }

        handleClear(event);
        goToListMatiere();

    } catch (NumberFormatException e) {
        showAlert("Input Error", "Please enter valid coefficient");
    } catch (SQLException e) {
        showAlert("Database Error", "Error saving subject: " + e.getMessage());
    }
}

    @FXML
    private void handleClear(ActionEvent event) {
//        enseignantIdField.clear();
//        nomField.clear();
//        coeffField.clear();
//        currentMatiere = null;
        enseignantCombo.getSelectionModel().clearSelection();
        nomField.clear();
        coeffField.clear();
        currentMatiere = null;
    }

    @FXML
    private void goToListMatiere() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
            Parent root = loader.load();

            // Get the controller and show Matiere view
            MainLayoutController mainController = loader.getController();
            mainController.showMatiereView();

            // Show the Matiere view
            mainController.showMatiereView();

            Stage stage = (Stage) enseignantCombo.getScene().getWindow(); // Changed to enseignantCombo

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Could not load list view");
        }
    }

    public void setMatiereData(Matiere matiere) {
        this.currentMatiere = matiere;
//        enseignantIdField.setText(String.valueOf(matiere.getId_ensg()));
        nomField.setText(matiere.getNom());
        coeffField.setText(String.valueOf(matiere.getCoefficient()));

        enseignantCombo.getItems().stream()
                .filter(user -> user.getId() == matiere.getId_ensg())
                .findFirst()
                .ifPresent(teacher -> enseignantCombo.getSelectionModel().select(teacher));
    }

//    private boolean validateInput() {
//        if (enseignantIdField.getText().isEmpty() ||
//                nomField.getText().isEmpty() ||
//                coeffField.getText().isEmpty()) {
//            showAlert("Input Error", "All fields are required");
//            return false;
//        }
//
//        try {
//            Integer.parseInt(enseignantIdField.getText());
//            Double.parseDouble(coeffField.getText());
//        } catch (NumberFormatException e) {
//            showAlert("Input Error", "Please enter valid numbers");
//            return false;
//        }
//
//        return true;
//    }

//    private boolean validateInput() {
//        if (enseignantCombo.getValue() == null) {  // Check ComboBox selection
//            showAlert("Input Error", "Please select a teacher");
//            return false;
//        }
//        if (nomField.getText().isEmpty()) {
//            showAlert("Input Error", "Subject name is required");
//            return false;
//        }
//        if (coeffField.getText().isEmpty()) {
//            showAlert("Input Error", "Coefficient is required");
//            return false;
//        }
//
//        try {
//            Double.parseDouble(coeffField.getText());  // Only need to check coefficient now
//        } catch (NumberFormatException e) {
//            showAlert("Input Error", "Please enter a valid coefficient");
//            return false;
//        }
//
//        return true;
//    }
private boolean validateInput() {
    boolean isValid = true;

    // Reset styles
    nomField.setStyle(null);
    coeffField.setStyle(null);

    if (enseignantCombo.getValue() == null) {
        showAlert("Input Error", "Please select a teacher");
        return false;
    }

    String subjectName = nomField.getText().trim();
    if (subjectName.isEmpty()) {
        showAlert("Input Error", "Subject name is required");
        nomField.setStyle("-fx-border-color: red;");
        return false;
    }

    // Must start with capital and contain only letters and numbers
    if (!subjectName.matches("[A-Z][a-zA-Z0-9]*")) {
        showAlert("Input Error", "Subject name must start with a capital letter and contain only letters and numbers");
        nomField.setStyle("-fx-border-color: red;");
        return false;
    }

    String coeffText = coeffField.getText().trim();
    if (coeffText.isEmpty()) {
        showAlert("Input Error", "Coefficient is required");
        coeffField.setStyle("-fx-border-color: red;");
        return false;
    }

    try {
        int coeff = Integer.parseInt(coeffText);
        if (coeff < 1 || coeff > 8) {
            showAlert("Input Error", "Coefficient must be a number between 1 and 8");
            coeffField.setStyle("-fx-border-color: red;");
            return false;
        }
    } catch (NumberFormatException e) {
        showAlert("Input Error", "Coefficient must be a valid number");
        coeffField.setStyle("-fx-border-color: red;");
        return false;
    }

    return true;
}


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
