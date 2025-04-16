package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML
    private StackPane contentPane;

    public void initialize() {
        goToProduit(); // charge la vue par défaut
    }

    @FXML
    private void goToProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ProduitView.fxml"));
            Parent produitView = loader.load();
            contentPane.getChildren().setAll(produitView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAddProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/AddProduit.fxml"));
            Parent addProduitView = loader.load();
            contentPane.getChildren().setAll(addProduitView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
