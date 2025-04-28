package pi_project.Fedi.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Aziz.Controller.EnseignantlyoutController;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;
import pi_project.Fedi.services.EmailService;
import pi_project.Saif.Controller.MainLayoutController;
import pi_project.Zayed.Entity.User;
import pi_project.db.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AjouterEleve {
    // Champs correspondant au FXML
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissanceField;
    @FXML private ComboBox<String> classeComboBox;
    @FXML private ComboBox<String> parentComboBox;
    @FXML private TextField moyenneField;
    @FXML private TextField absenceField;
    @FXML private DatePicker dateInscriptionField;

    // Labels d'erreur
    @FXML private Label nomErreur;
    @FXML private Label prenomErreur;
    @FXML private Label dateNaissanceErreur;
    @FXML private Label classeErreur;
    @FXML private Label parentErreur;
    @FXML private Label moyenneErreur;
    @FXML private Label absenceErreur;
    @FXML private Label dateInscriptionErreur;

    @FXML private VBox formContainer;

    private final eleveservice eleveService = new eleveservice();
    private final EmailService emailService = new EmailService();
    private List<classe> toutesLesClasses;
    private List<User> tousLesParents;

    @FXML
    public void initialize() {
        chargerDonneesInitiales();
    }

    private void chargerDonneesInitiales() {
        try {
            System.out.println("Début du chargement des données...");

            // Charger les classes
            toutesLesClasses = eleveService.getAllClasses();
            System.out.println("Nombre de classes récupérées: " + toutesLesClasses.size());

            List<String> nomsClasses = toutesLesClasses.stream()
                    .map(classe::getNomclasse)
                    .collect(Collectors.toList());

            System.out.println("Noms de classes: " + nomsClasses);
            classeComboBox.setItems(FXCollections.observableArrayList(nomsClasses));

            // Charger les parents
            tousLesParents = eleveService.getAllParents();
            System.out.println("Nombre de parents récupérés: " + tousLesParents.size());

            List<String> emailsParents = tousLesParents.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            System.out.println("Emails des parents: " + emailsParents);
            parentComboBox.setItems(FXCollections.observableArrayList(emailsParents));

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur de chargement",
                    "Impossible de charger les données initiales: " + e.getMessage());
        }
    }
    @FXML
    private void handleListEleves(ActionEvent event) {
        try {

            // Load the main layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
            Parent root = loader.load();

            // Get controller and show cours list
            MainLayoutController mainController = loader.getController();
            mainController.showListeleve();
            // Obtenir la scène actuelle
            Scene scene = ((Node) event.getSource()).getScene();

          scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Vous pourriez aussi afficher un message d'erreur à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la liste des élèves");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSave() {
        clearErrorMessages();

        if (!validateFields()) {
            return;
        }

        try {
            eleve nouvelEleve = createEleveFromForm();
            eleveService.add(nouvelEleve);
            
            // Envoi de l'email au parent
            String parentEmail = parentComboBox.getValue();
            User parent = tousLesParents.stream()
                    .filter(p -> p.getEmail().equals(parentEmail))
                    .findFirst()
                    .orElse(null);

            if (parent != null) {
                System.out.println("Parent trouvé: " + parent.getEmail());
                System.out.println("Nom du parent: " + parent.getNom());
                System.out.println("Prénom du parent: " + parent.getPrenom());
                
                String parentName = parent.getPrenom() + " " + parent.getNom();
                String className = classeComboBox.getValue();
                String studentName = nomField.getText() + " " + prenomField.getText();

                System.out.println("Envoi de l'email avec les informations suivantes:");
                System.out.println("- Nom complet du parent: " + parentName);
                System.out.println("- Email du parent: " + parentEmail);
                System.out.println("- Nom de l'élève: " + studentName);
                System.out.println("- Classe: " + className);

                emailService.sendStudentAssignmentEmail(
                    parentEmail,
                    parentName,
                    studentName,
                    className
                );
            } else {
                System.err.println("Parent non trouvé pour l'email: " + parentEmail);
            }

            showAlert("Succès", "Élève ajouté",
                    "L'élève " + nouvelEleve.getPrenom() + " " + nouvelEleve.getNom() + " a été ajouté avec succès!");
            
            // Get the current stage using nomField which is guaranteed to be initialized
            Stage stage = (Stage) nomField.getScene().getWindow();
            
            // Load the main layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
            Parent root = loader.load();

            // Get controller and show cours list
            MainLayoutController mainController = loader.getController();
            mainController.showListeleve();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout", e.getMessage());
        }
    }



    @FXML
    private void handleBack() {
        try {
            // Load the main layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
            Parent root = loader.load();

            // Get controller and show cours list
            MainLayoutController mainController = loader.getController();
            mainController.showListeleve();


        } catch (IOException e) {
            System.err.println("Erreur de navigation:");
            e.printStackTrace();
            showAlert("Erreur", "Navigation impossible",
                    "Impossible de charger la liste des classes: " + e.getMessage());
        }
    }

    private eleve createEleveFromForm() {
        // Récupérer la classe sélectionnée
        String nomClasse = classeComboBox.getValue();
        classe classeSelectionnee = toutesLesClasses.stream()
                .filter(c -> c.getNomclasse().equals(nomClasse))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));

        // Récupérer le parent sélectionné
        String emailParent = parentComboBox.getValue();
        User parentSelectionne = tousLesParents.stream()
                .filter(p -> p.getEmail().equals(emailParent))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Parent non trouvé"));

        return new eleve(
                classeSelectionnee,
                parentSelectionne,
                nomField.getText().trim(),
                prenomField.getText().trim(),
                Date.from(dateNaissanceField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Double.parseDouble(moyenneField.getText()),
                Integer.parseInt(absenceField.getText()),
                Date.from(dateInscriptionField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                genererQrCode()
        );
    }

    private String genererQrCode() {
        return "QR_" + System.currentTimeMillis() + "_"
                + nomField.getText().substring(0, Math.min(3, nomField.getText().length())).toUpperCase();
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Validation du nom
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            nomErreur.setText("Le nom est obligatoire");
            isValid = false;
        }

        // Validation du prénom
        if (prenomField.getText() == null || prenomField.getText().trim().isEmpty()) {
            prenomErreur.setText("Le prénom est obligatoire");
            isValid = false;
        }

        // Validation date naissance
        if (dateNaissanceField.getValue() == null) {
            dateNaissanceErreur.setText("Date de naissance requise");
            isValid = false;
        }

        // Validation classe
        if (classeComboBox.getValue() == null) {
            classeErreur.setText("Veuillez sélectionner une classe");
            isValid = false;
        }

        // Validation parent
        if (parentComboBox.getValue() == null) {
            parentErreur.setText("Veuillez sélectionner un parent");
            isValid = false;
        }

        // Validation moyenne
        try {
            double moyenne = Double.parseDouble(moyenneField.getText());
            if (moyenne < 0 || moyenne > 20) {
                moyenneErreur.setText("Doit être entre 0 et 20");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            moyenneErreur.setText("Moyenne invalide");
            isValid = false;
        }

        // Validation absences
        try {
            int absences = Integer.parseInt(absenceField.getText());
            if (absences < 0) {
                absenceErreur.setText("Ne peut pas être négatif");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            absenceErreur.setText("Nombre invalide");
            isValid = false;
        }

        // Validation date inscription
        if (dateInscriptionField.getValue() == null) {
            dateInscriptionErreur.setText("Date d'inscription requise");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrorMessages() {
        nomErreur.setText("");
        prenomErreur.setText("");
        dateNaissanceErreur.setText("");
        classeErreur.setText("");
        parentErreur.setText("");
        moyenneErreur.setText("");
        absenceErreur.setText("");
        dateInscriptionErreur.setText("");
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) formContainer.getScene().getWindow();
        stage.close();
    }
}