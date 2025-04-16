package pi_project;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Saif/addProduct.fxml"));
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Saif/addProduct.fxml")));
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.setTitle("------------Gestion des clients------------");
//        stage.show();


//        Parent root = FXMLLoader.load(getClass().getResource("/Farouk/AjouterCours.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/Farouk/AjouterMatiere.fxml"));
       // Parent root = FXMLLoader.load(getClass().getResource("/Farouk/cours_list.fxml"));
 Parent root = FXMLLoader.load(getClass().getResource("/Farouk/MatiereList.fxml"));

        Scene scene = new Scene(root);

        // Set up the scene and stage
        stage.setScene(scene);
        stage.setTitle("Course Management System");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.show();

    }
}
