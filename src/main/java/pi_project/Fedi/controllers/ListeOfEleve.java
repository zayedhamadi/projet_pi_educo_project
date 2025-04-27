package pi_project.Fedi.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.services.eleveservice;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeOfEleve implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private GridPane gridPane;

    private final eleveservice eleveService = new eleveservice();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerEleves();
    }

    private void chargerEleves() {
        gridPane.getChildren().clear();

        List<eleve> eleves = eleveService.getAll();
        int column = 0;
        int row = 0;

        for (eleve e : eleves) {
            VBox card = creerCarteEleve(e);
            gridPane.add(card, column, row);

            column++;
            if (column == 3) { // 3 cartes par ligne
                column = 0;
                row++;
            }
        }
    }

    private VBox creerCarteEleve(eleve e) {
        VBox card = new VBox(10);
        card.getStyleClass().add("eleve-card");
        card.setPadding(new Insets(15));

        Label nomLabel = new Label(e.getNom() + " " + e.getPrenom());
        nomLabel.getStyleClass().add("eleve-card-title");

        Label classeLabel = new Label("Classe: " + (e.getClasse() != null ? e.getClasse().getNomclasse() : "N/A"));
        System.out.println(e.getClasse().getNomclasse());
        Label moyenneLabel = new Label("Moyenne: " + e.getMoyenne());

        classeLabel.getStyleClass().add("eleve-card-info");
        moyenneLabel.getStyleClass().add("eleve-card-info");

        // Boutons d’action
        HBox actions = new HBox(10);
        actions.getStyleClass().add("eleve-card-actions");

        Button btnUpdate = new Button("Modifier");
        Button btnDelete = new Button("Supprimer");

        btnUpdate.getStyleClass().add("eleve-card-button");
        btnDelete.getStyleClass().add("eleve-card-button");

        // Action modifier
        btnUpdate.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/UpdateEleve.fxml"));
                Parent root = loader.load();
                UpdateEleve controller = loader.getController();
                controller.setEleve(e);

                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
                showErreur("Erreur lors du chargement de la page de modification.");
            }
        });

        // Action supprimer
        btnDelete.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Voulez-vous vraiment supprimer cet élève ?");
            confirmation.setContentText(e.getNom() + " " + e.getPrenom());

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    eleveService.delete(e.getId());
                    chargerEleves();
                }
            });
        });

        actions.getChildren().addAll(btnUpdate, btnDelete);

        card.getChildren().addAll(nomLabel, classeLabel, moyenneLabel, actions);
        return card;
    }

    private void showErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAjouterEleve() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/AjouterEleve.fxml"));
            Parent listeView = loader.load();

            Scene currentScene = rootPane.getScene();
            currentScene.setRoot(listeView);
        } catch (Exception e) {
            System.out.println(("Erreur lors du retour à la liste"));
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Fedi/AjouterEleve.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de retourner au menu");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page.");
            alert.showAndWait();
        }
    }
}