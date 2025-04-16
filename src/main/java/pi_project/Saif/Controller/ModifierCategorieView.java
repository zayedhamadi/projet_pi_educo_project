package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Service.CategorieService;
import javafx.event.ActionEvent;

import java.io.IOException;


public class ModifierCategorieView {

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfDescription;

    private final CategorieService service = new CategorieService();
    private Categorie categorie; // Ajouter un attribut pour la catégorie
    private CategorieView categorieView;

    public void setCategorieView(CategorieView categorieView) {
        this.categorieView = categorieView;
    }

    // Méthode pour passer la catégorie à modifier
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
        // Initialiser les champs avec les données de la catégorie
        tfId.setText(String.valueOf(categorie.getId()));
        tfNom.setText(categorie.getNom());
        tfDescription.setText(categorie.getDescription());
    }

    @FXML
    private void modifierCategorie() {
        try {
            int id = Integer.parseInt(tfId.getText().trim());
            String nom = tfNom.getText().trim();
            String description = tfDescription.getText().trim();

            if (nom.isEmpty() || description.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs.");
                return;
            }

            // Mettre à jour la catégorie
            categorie.setNom(nom);
            categorie.setDescription(description);

            // Appeler le service pour modifier la catégorie
            service.modifier(categorie);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie modifiée avec succès !");
            tfId.clear();
            tfNom.clear();
            tfDescription.clear();
            // 🔁 Actualiser le tableau principal
            if (categorieView != null) {
                categorieView.loadCategories();
                // Après modification de la catégorie
                categorieView.refreshTable(); // Cela va rafraîchir la table pour refléter les changements

            }
//// ✅ Fermer la fenêtre après modification
//            tfNom.getScene().getWindow().hide();
            retourListeCateg(null);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "L'ID doit être un nombre.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    @FXML
    private void annuler() {
        // Réinitialiser les champs à leur valeur initiale (avant modification)
        tfId.setText(String.valueOf(categorie.getId()));
        tfNom.setText(categorie.getNom());
        tfDescription.setText(categorie.getDescription());
    }
    @FXML
    public void retourListeCateg(ActionEvent actionEvent) {
        try {
            // Charger la vue des catégories
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/CategorieView.fxml"));
            Parent categorieView = loader.load();

            // Chercher le StackPane ou le conteneur principal qui contient la vue centrale
            StackPane contentPane = (StackPane) tfNom.getScene().lookup("#contentPane");

            // Remplacer uniquement le contenu central avec la vue des catégories
            contentPane.getChildren().setAll(categorieView);
        } catch (IOException e) {
            // Gestion des erreurs si le fichier FXML est introuvable
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation", "Impossible de charger la vue des catégories.");
        }
    }


//    public void retourListeCateg(ActionEvent actionEvent) {
//        try {
//            // Charger la vue des produits
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/CategorieView.fxml"));
//            Parent root = loader.load();
//
//            // Remplacer le contenu de la scène actuelle par la nouvelle vue
//            // Utilise getScene() pour accéder à la scène actuelle
//            // et setRoot() pour remplacer le contenu de la scène avec la nouvelle vue
//            tfNom.getScene().setRoot(root);
//        } catch (IOException e) {
//            // Gestion des erreurs si le fichier FXML est introuvable
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur de navigation", "Impossible de charger la vue des produits.");
//        }
//    }
}
