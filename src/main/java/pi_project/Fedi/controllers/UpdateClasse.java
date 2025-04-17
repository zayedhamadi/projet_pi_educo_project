package pi_project.Fedi.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import pi_project.Main;

import java.io.IOException;

public class UpdateClasse {
    @FXML
    private BorderPane rootPane;
    @FXML
    private TextField nomClasseField;

    @FXML
    private TextField numSalleField;

    @FXML
    private TextField capaciteField;

    private classe selectedClasse;
    private final classeservice service = new classeservice();

    public void setClasse(classe c) {
        this.selectedClasse = c;
        nomClasseField.setText(c.getNomclasse());
        numSalleField.setText(String.valueOf(c.getNumsalle()));
        capaciteField.setText(String.valueOf(c.getCapacite()));
    }
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/ListeOfClasse.fxml"));
            Parent listeView = loader.load();

            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(listeView);
        } catch (Exception e) {
            System.out.println(("Erreur lors du retour à la liste"));
            e.printStackTrace();
        }
    }
    @FXML
    public void handleUpdate() {
        try {
            String nom = nomClasseField.getText();
            int salle = Integer.parseInt(numSalleField.getText());
            int capacite = Integer.parseInt(capaciteField.getText());

            selectedClasse.setNomclasse(nom);
            selectedClasse.setNumsalle(salle);
            selectedClasse.setCapacite(capacite);

            service.update(selectedClasse);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Classe mise à jour !");
            alert.showAndWait();

         this.handleBack();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : " + e.getMessage());
            alert.showAndWait();
        }
    }


}