package pi_project.Saif.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Service.CategorieService;

import java.io.IOException;

public class CategorieAdd {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfDescription;

    private final CategorieService service = new CategorieService();
    private CategorieView categorieView; // référence au contrôleur principal

    public void setCategorieView(CategorieView categorieView) {
        this.categorieView = categorieView;
    }

    @FXML
    private void ajouterCategorie() {
        String nom = tfNom.getText().trim();
        String desc = tfDescription.getText().trim();

        if (nom.isEmpty() || desc.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        Categorie categorie = new Categorie(0, nom, desc);
        service.ajouter(categorie);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée avec succès !");
//        tfNom.clear();
//        tfDescription.clear();
        retourListe(null);
        // 🔁 Actualiser le tableau dans la vue principale
        if (categorieView != null) {
            categorieView.loadCategories();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void annulerFormulaire() {
        tfNom.clear();
        tfDescription.clear();
    }


    @FXML
    private void retourListe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/CategorieView.fxml"));
            Parent root = loader.load();

            // Remplacer la scène actuelle par la nouvelle vue
            tfNom.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
