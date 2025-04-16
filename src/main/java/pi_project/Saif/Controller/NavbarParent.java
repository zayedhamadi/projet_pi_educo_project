package pi_project.Saif.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class NavbarParent {

    @FXML
    private StackPane navbarparent;

    public void loadBoutiqueView() {
        loadView("/Saif/BoutiqueView.fxml");
    }
    public void initialize() {
        loadBoutiqueView(); // charge la vue par défaut
    }


    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            navbarparent.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
