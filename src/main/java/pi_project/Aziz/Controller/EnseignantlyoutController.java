package pi_project.Aziz.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class EnseignantlyoutController {

    @FXML
    private StackPane contentPane;

    // Méthode pour afficher ProduitView dans le center
    @FXML
    public void showProduitView() {
        loadView("/Saif/ProduitView.fxml");
    }

    @FXML
    public void showCategorieView() {
        loadView("/Saif/CategorieView.fxml");
    }
    @FXML

    public void showquiz() {
        loadView("/Aziz/afficherquiz.fxml");
    }
    public void initialize() {
        showCategorieView(); // charge la vue par défaut
    }
    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showCoursView() {
        loadView("/Farouk/cours_list.fxml") ;
    }
}
