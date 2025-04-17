package pi_project.Saif.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainLayoutController {

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

    public void showClassView(ActionEvent actionEvent) {
        loadView("/Fedi/ListeOfClasse.fxml");

    }

    public void showEleveView(ActionEvent actionEvent) {
        loadView("/Fedi/ListeOfEleve.fxml");
    }
}
