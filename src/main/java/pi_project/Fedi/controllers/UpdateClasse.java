package pi_project.Fedi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import pi_project.Main;

public class UpdateClasse {

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
    private void handleRetour() {
        try {
            Main.setRoot("ListeOfClasse.fxml");
        } catch (Exception e) {
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

            // Rediriger vers la liste des classes
            Main.setRoot("ListeOfClasse.fxml");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : " + e.getMessage());
            alert.showAndWait();
        }
    }

}