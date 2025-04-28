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
import javafx.scene.layout.BorderPane;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import pi_project.Fedi.services.EmailService;
import pi_project.Farouk.Services.EnseignantService;
import pi_project.Main;
import pi_project.Saif.Controller.MainLayoutController;
import pi_project.Zayed.Entity.User;

import java.io.IOException;
import java.util.List;

public class AjouterClasse {

    @FXML
    private BorderPane rootPane;
    @FXML
    private TextField capacite;
    @FXML
    private ComboBox<User> enseignent;
    @FXML
    private TextField nomclasse;
    @FXML
    private TextField numsalle;
    @FXML
    private Label nomErreur;
    @FXML
    private Label salleErreur;
    @FXML
    private Label capaciteErreur;
    @FXML
    private Label enseignantErreur;

    private final classeservice ps = new classeservice();
    private final EnseignantService enseignantService = new EnseignantService();
    private final EmailService emailService = new EmailService();

    @FXML
    public void initialize() {
        loadEnseignants();
    }

    private void loadEnseignants() {
        List<User> enseignants = enseignantService.getAllEnseignants();
        ObservableList<User> observableList = FXCollections.observableArrayList(enseignants);
        enseignent.setItems(observableList);

        enseignent.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getNom() + " " + user.getPrenom() + " (" + user.getEmail() + ")");
                }
            }
        });

        enseignent.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText("Sélectionner un enseignant");
                } else {
                    setText(user.getNom() + " " + user.getPrenom() + " (" + user.getEmail() + ")");
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
            Parent root = loader.load();

            MainLayoutController mainController = loader.getController();
            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(root);
            mainController.showListeClasse();
        } catch (Exception e) {
            System.out.println(("Erreur lors du retour à la liste"));
            e.printStackTrace();
        }
    }

    @FXML
    void save(ActionEvent event) {
        // Nettoyer les erreurs précédentes
        nomErreur.setText("");
        salleErreur.setText("");
        capaciteErreur.setText("");
        enseignantErreur.setText("");

        boolean isValid = true;

        String nom = nomclasse.getText().trim();
        String salleStr = numsalle.getText().trim();
        String capaciteStr = capacite.getText().trim();
        User selectedTeacher = enseignent.getValue();

        // Validation du nom
        if (nom.isEmpty()) {
            nomErreur.setText("Le nom de la classe est obligatoire.");
            isValid = false;
        }

        // Validation du numéro de salle
        int salle = 0;
        try {
            salle = Integer.parseInt(salleStr);
            if (salle <= 0) {
                salleErreur.setText("Le numéro de salle doit être un entier > 0.");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            salleErreur.setText("Le numéro de salle doit être un entier.");
            isValid = false;
        }

        // Validation de la capacité
        int cap = 0;
        try {
            cap = Integer.parseInt(capaciteStr);
            if (cap <= 0) {
                capaciteErreur.setText("La capacité doit être > 0.");
                isValid = false;
            } else if (cap > 30) {
                capaciteErreur.setText("La capacité ne peut pas dépasser 30.");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            capaciteErreur.setText("La capacité doit être un entier.");
            isValid = false;
        }

        // Validation de l'enseignant
        if (selectedTeacher == null) {
            enseignantErreur.setText("Veuillez sélectionner un enseignant.");
            isValid = false;
        }

        // Si tout est valide, on ajoute la classe et envoie l'email
        if (isValid) {
            classe c = new classe(nom, salle, cap);
            ps.add(c);
            System.out.println("Classe ajoutée avec succès !");

            // Envoi de l'email à l'enseignant
            emailService.sendClassAssignmentEmail(
                selectedTeacher.getEmail(),
                selectedTeacher.getNom() + " " + selectedTeacher.getPrenom(),
                nom,
                salle,
                cap
            );

            // Réinitialisation des champs
            nomclasse.clear();
            numsalle.clear();
            capacite.clear();
            enseignent.getSelectionModel().clearSelection();

            // Retour automatique à la liste
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/MainLayout.fxml"));
                Parent root = loader.load();
                MainLayoutController mainController = loader.getController();
                Scene currentScene = rootPane.getScene();
                currentScene.setRoot(root);
                mainController.showListeClasse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
