package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pi_project.Saif.Entity.Produit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CardProduitController {
    @FXML private ImageView imageView;
    @FXML private Label nomLabel;
    @FXML private Label prixLabel;
    @FXML private Button addBtn;

    private Produit produit;
    private Runnable onAjout;

    // Cette méthode configure chaque carte avec les données du produit
//    public void setData(Produit produit, Runnable onAjoutCallback) {
//        this.produit = produit;
//        this.onAjout = onAjoutCallback;
//
//        // Remplir les informations dans la carte
//        nomLabel.setText(produit.getNom());
//        prixLabel.setText(produit.getPrix() + " DT");
//
//        // Charger l'image
//        File imageFile = new File("E:/version_pidev/symfony_project-/educo_platform/public/uploads/" + produit.getImage());
//        Image image = imageFile.exists()
//                ? new Image(imageFile.toURI().toString())
//                : new Image("file:src/main/resources/images/default.png"); // Image par défaut
//        imageView.setImage(image);
//
//        // Action du bouton "Ajouter au panier"
//        addBtn.setOnAction(e -> {
//            onAjout.run(); // Exécute l'ajout au panier
//        });
//    }

    public void setData(Produit produit, Runnable onAjoutCallback) {
        this.produit = produit;
        this.onAjout = onAjoutCallback;

        // Remplir les informations dans la carte
        nomLabel.setText(produit.getNom());
        prixLabel.setText(produit.getPrix() + " DT");

        // Charger le chemin d'upload depuis config.properties
        Properties props = new Properties();
        try {
            FileInputStream in = new FileInputStream("config.properties");
            props.load(in);
            in.close();

            String uploadPath = props.getProperty("upload.path"); // Exemple: E:/xampp/htdocs/uploads

            // Charger l'image
            File imageFile = new File(uploadPath + "/" + produit.getImage());
            Image image = imageFile.exists()
                    ? new Image(imageFile.toURI().toString())
                    : new Image("file:src/main/resources/images/default.png"); // Image par défaut
            imageView.setImage(image);

        } catch (IOException e) {
            e.printStackTrace();
            // Si erreur, afficher l’image par défaut
            imageView.setImage(new Image("file:src/main/resources/images/default.png"));
        }

        // Action du bouton "Ajouter au panier"
        addBtn.setOnAction(e -> {
            onAjout.run(); // Exécute l'ajout au panier
        });
    }

}

