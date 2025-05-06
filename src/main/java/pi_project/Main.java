package pi_project;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pi_project.Zayed.Service.SchedulerImpl;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {

        new Thread(() -> {
            SchedulerImpl scheduler = new SchedulerImpl();
            scheduler.startScheduler();
            System.out.println("Scheduler démarré avec succès !");

        }).start();

        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Zayed/login.fxml")));
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Saif/AdminCommandeView.fxml")));
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Farouk/Chat_View_PDF.fxml")));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("------------Educo project------------");
        stage.show();
    }
}
