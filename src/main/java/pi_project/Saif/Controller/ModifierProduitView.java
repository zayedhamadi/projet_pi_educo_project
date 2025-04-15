package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Service.CategorieService;
import pi_project.Saif.Service.ProduitService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ModifierProduitView {

    @FXML
    private TextField tfNom;

    @FXML
    private TextArea tfDescription;

    @FXML
    private TextField tfPrix;

    @FXML
    private TextField tfStock;

    @FXML
    private ComboBox<String> categorieComboBox;

    @FXML
    private ImageView imageView;

    private File selectedImage;
    private Produit produit;
    private final ProduitService produitService = new ProduitService();
    private final CategorieService categorieService = new CategorieService();
    private ProduitView produitView; // Pour rafraîchir après modif

    public void setProduit(Produit p) {
        this.produit = p;
        tfNom.setText(p.getNom());
        tfDescription.setText(p.getDescription());
        tfPrix.setText(String.valueOf(p.getPrix()));
        tfStock.setText(String.valueOf(p.getStock()));
        imageView.setImage(new Image("file:" + p.getImage()));

        List<Categorie> categories = categorieService.getAll();
        for (Categorie c : categories) {
            categorieComboBox.getItems().add(c.getNom());
        }

        // Utilise la méthode getNomCategorieParId pour récupérer le nom de la catégorie
        categorieComboBox.setValue(categorieService.getNomCategorieParId(p.getCategorieId()));
    }

    public void setProduitView(ProduitView view) {
        this.produitView = view;
    }

    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImage = file;
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void modifierProduit() {
        try {
            String nom = tfNom.getText();
            String description = tfDescription.getText();
            double prix = Double.parseDouble(tfPrix.getText());
            int stock = Integer.parseInt(tfStock.getText());

            // Récupérer la catégorie sélectionnée et son ID
            String categorieNom = categorieComboBox.getSelectionModel().getSelectedItem();
            int categorieId = getCategorieIdByName(categorieNom);

            if (categorieId == -1) {
                throw new Exception("Catégorie invalide");
            }

            // Sauvegarder l'image si elle a été changée
            String imagePath = saveImage();

            produit.setNom(nom);
            produit.setDescription(description);
            produit.setPrix(prix);
            produit.setStock(stock);
            produit.setCategorieId(categorieId);
            produit.setImage(imagePath);

            produitService.modifier(produit);

            if (produitView != null) {
                produitView.refreshTable();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Produit modifié");
            alert.setHeaderText(null);
            alert.setContentText("Le produit a été modifié avec succès !");
            alert.showAndWait();

            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Veuillez entrer des valeurs valides pour le prix et le stock.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification du produit : " + e.getMessage());
        }
    }

    private int getCategorieIdByName(String nom) {
        for (Categorie categorie : categorieService.getAll()) {
            if (categorie.getNom().equals(nom)) {
                return categorie.getId();
            }
        }
        return -1; // Catégorie non trouvée
    }

    private String saveImage() throws IOException {
        if (selectedImage == null) {
            return produit.getImage();
        }

        File destFile = new File("E:/version_pidev/symfony_project-/educo_platform/public/uploads/" + selectedImage.getName());
        Files.copy(selectedImage.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return  selectedImage.getName();
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
