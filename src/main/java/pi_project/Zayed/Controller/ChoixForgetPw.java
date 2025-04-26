package pi_project.Zayed.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ChoixForgetPw {
    @FXML
    private BorderPane contentPane;

    public void onEmailClicked() throws IOException {
        loadScene("/Zayed/forgetPw.fxml");
    }

    public void onSMSClicked() throws IOException {
        loadScene("/Zayed/forgetPwByNumTel.fxml");
    }

    public void onLoginClicked() throws IOException {
        loadScene("/Zayed/login.fxml");
    }

    private void loadScene(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Stage currentStage = (Stage) this.contentPane.getScene().getWindow();
        currentStage.setScene(new Scene(root));
        currentStage.setTitle("Se connecter");
        currentStage.show();
    }


}
