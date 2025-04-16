package pi_project.Saif.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarController {

    @FXML
    private void handleAccueil(ActionEvent event) {
        System.out.println("Go to Accueil");
        // Change view, update scene, etc.
    }

    @FXML
    private void handleProduit(ActionEvent event) {
        changeView("Saif/ProduitView.fxml");;
    }
    @FXML
    private VBox sidebar;
    @FXML
    private void handleCategorie(ActionEvent event) {
        changeView("Saif/CategorieView.fxml");;
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        System.out.println("Go to Paramètres");
    }
    private void changeView(String fxmlFile) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane newView = loader.load();

            // Récupérer la scène actuelle
            Stage currentStage = (Stage) sidebar.getScene().getWindow();

            // Mettre à jour le contenu de la scène avec la nouvelle vue
            currentStage.getScene().setRoot(newView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}