package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentPane;

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showProduitView() {
        loadView("/Saif/ProduitView.fxml");
    }

    @FXML
    public void showCategorieView() {
        loadView("/Saif/CategorieView.fxml");
    }

    public void initialize() {
        showCategorieView(); //
    }

    public void showProfilAdmin() {
        this.loadView("/Zayed/ProfilAdmin.fxml");
    }

    @FXML
    public void showMatiereView() {
        loadView("/Farouk/MatiereList.fxml"); // Using Farouk's path
    }

}
