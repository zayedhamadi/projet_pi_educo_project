package pi_project.Fedi.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pi_project.Fedi.entites.classe;
import pi_project.Fedi.services.classeservice;
import pi_project.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListeOfClasse implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField searchField;

    private final classeservice service = new classeservice();
    private final ObservableList<classe> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les données
        data.addAll(service.getAll());
        afficherCartes();
    }

    private void afficherCartes() {
        gridPane.getChildren().clear();
        int column = 0;
        int row = 0;
        int maxColumns = 3; // Nombre maximum de cartes par ligne

        for (classe c : data) {
            VBox card = createClassCard(c);
            gridPane.add(card, column, row);
            
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createClassCard(classe c) {
        VBox card = new VBox(10);
        card.getStyleClass().add("class-card");

        // Titre de la classe
        Text title = new Text(c.getNomclasse());
        title.getStyleClass().add("class-card-title");

        // Informations de la classe
        Text salleInfo = new Text("Salle: " + c.getNumsalle());
        Text capaciteInfo = new Text("Capacité: " + c.getCapacite());
        salleInfo.getStyleClass().add("class-card-info");
        capaciteInfo.getStyleClass().add("class-card-info");

        // Boutons d'action
        HBox actions = new HBox(10);
        actions.getStyleClass().add("class-card-actions");

        Button btnUpdate = new Button("Modifier");
        Button btnDelete = new Button("Supprimer");
        btnUpdate.getStyleClass().add("class-card-button");
        btnDelete.getStyleClass().add("class-card-button");

        btnUpdate.setOnAction(event -> {
            try {
                ouvrirSceneUpdate(c);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        btnDelete.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Voulez-vous vraiment supprimer cette classe ?");
            confirmation.setContentText(c.getNomclasse());

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    service.delete(c.getId());
                    data.remove(c);
                    afficherCartes();
                }
            });
        });

        actions.getChildren().addAll(btnUpdate, btnDelete);
        card.getChildren().addAll(title, salleInfo, capaciteInfo, actions);

        return card;
    }

    @FXML
    private void handleAjouterClasse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/AjouterClasse.fxml"));
            Scene scene = new Scene(loader.load());
            Main.setRoot("AjouterClasse.fxml");

        } catch (IOException e) {
            e.printStackTrace();
            showErreur("Erreur lors du chargement de la scène AjouterClasse.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleAjouterEleve() {
        try {
            Main.setRoot("AjouterEleve.fxml");
        } catch (Exception e) {
            showErreur("Erreur lors du chargement de la scène AjouterEleve.");
        }
    }

    private void ouvrirSceneUpdate(classe selected) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fedi/UpdateClasse.fxml"));
            javafx.scene.Parent root = loader.load();

            UpdateClasse controller = loader.getController();
            controller.setClasse(selected);

            Scene scene = new Scene(root);
            Main.getPrimaryStage().setScene(scene);
            Main.getPrimaryStage().show();

        } catch (IOException e) {
            e.printStackTrace();
            showErreur("Erreur lors du chargement de la scène UpdateClasse.");
        }
    }

    private void showErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur de chargement");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
