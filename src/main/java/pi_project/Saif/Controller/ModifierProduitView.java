package pi_project.Saif.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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
    private TextField tfid;

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
        tfid.setText(String.valueOf(p.getId()));
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

//    @FXML
//    private void modifierProduit() {
//        try {
//            String nom = tfNom.getText();
//            int id=Integer.parseInt(tfid.getText());
//            String description = tfDescription.getText();
//            double prix = Double.parseDouble(tfPrix.getText());
//            int stock = Integer.parseInt(tfStock.getText());
//
//            // Récupérer la catégorie sélectionnée et son ID
//            String categorieNom = categorieComboBox.getSelectionModel().getSelectedItem();
//            int categorieId = getCategorieIdByName(categorieNom);
//
//            if (categorieId == -1) {
//                throw new Exception("Catégorie invalide");
//            }
//
//            // Sauvegarder l'image si elle a été changée
//            String imagePath = saveImage();
//
//            produit.setNom(nom);
//            produit.setDescription(description);
//            produit.setPrix(prix);
//            produit.setStock(stock);
//            produit.setCategorieId(categorieId);
//            produit.setImage(imagePath);
//
//            produitService.modifier(produit);
//
//            if (produitView != null) {
//                produitView.refreshTable();
//            }
//
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Produit modifié");
//            alert.setHeaderText(null);
//            alert.setContentText("Le produit a été modifié avec succès !");
//            alert.showAndWait();
//
//            Stage stage = (Stage) tfNom.getScene().getWindow();
////            stage.close();
//            retourListe();
//        } catch (NumberFormatException e) {
//            showAlert(Alert.AlertType.ERROR, "Veuillez entrer des valeurs valides pour le prix et le stock.");
//        } catch (Exception e) {
//            showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification du produit : " + e.getMessage());
//        }
//    }
@FXML
private void modifierProduit() {
    try {
        String nom = tfNom.getText().trim();
        String description = tfDescription.getText().trim();
        String prixText = tfPrix.getText().trim();
        String stockText = tfStock.getText().trim();
        String categorieNom = categorieComboBox.getSelectionModel().getSelectedItem();

        // ✅ Contrôle du nom
        if (nom.isEmpty() || !nom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            throw new Exception("Le nom doit contenir uniquement des lettres et ne pas être vide.");
        }

        // ✅ Contrôle de la description
        if (description.isEmpty()) {
            throw new Exception("La description ne doit pas être vide.");
        }

        // ✅ Contrôle du prix
        double prix;
        try {
            prix = Double.parseDouble(prixText);
            if (prix <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new Exception("Le prix doit être un nombre positif.");
        }

        // ✅ Contrôle du stock
        int stock;
        try {
            stock = Integer.parseInt(stockText);
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new Exception("Le stock doit être un entier positif.");
        }

        // ✅ Contrôle de la catégorie
        if (categorieNom == null || categorieNom.isEmpty()) {
            throw new Exception("Veuillez sélectionner une catégorie.");
        }

        int categorieId = getCategorieIdByName(categorieNom);
        if (categorieId == -1) {
            throw new Exception("Catégorie invalide.");
        }

        // ✅ Image (si modifiée)
        String imagePath = saveImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            produit.setImage(imagePath);
        }

        // ✅ Mettre à jour l'objet produit
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(prix);
        produit.setStock(stock);
        produit.setCategorieId(categorieId);

        // ✅ Mise à jour dans la base
        produitService.modifier(produit);

        if (produitView != null) {
            produitView.refreshTable();
        }

        showAlert(Alert.AlertType.INFORMATION, "Produit modifié", "Le produit a été modifié avec succès !");
        retourListe();

    } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
    }
}
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
//"E:/version_pidev/symfony_project-/educo_platform/public/uploads/"
        File destFile = new File("C:/Users/21690/Desktop/projet_pi/symfony_project-/educo_platform/public/uploads" + selectedImage.getName());
        Files.copy(selectedImage.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return  selectedImage.getName();
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void annuler() {
        // Réinitialiser les champs avec les valeurs initiales
        tfNom.setText(produit.getNom());
        tfDescription.setText(produit.getDescription());
        tfPrix.setText(String.valueOf(produit.getPrix()));
        tfStock.setText(String.valueOf(produit.getStock()));

        // Réinitialiser le ComboBox des catégories
        categorieComboBox.getItems().clear();  // Vider les éléments du ComboBox
        List<Categorie> categories = categorieService.getAll();
        for (Categorie c : categories) {
            categorieComboBox.getItems().add(c.getNom());
        }

        // Sélectionner la catégorie de produit actuelle dans le ComboBox
        categorieComboBox.setValue(categorieService.getNomCategorieParId(produit.getCategorieId()));

        // Réinitialiser l'image
        if (produit.getImage() != null && !produit.getImage().isEmpty()) {
            imageView.setImage(new Image(produit.getImage()));
        } else {
            imageView.setImage(null);  // Si aucune image n'est définie
        }

        // Réinitialiser le chemin de l'image sélectionnée
        selectedImage = new File(produit.getImage() != null ? produit.getImage() : "");
    }

//    @FXML
//    private void annuler() {
//        // Vider les champs de texte
//        tfNom.clear();
//        tfDescription.clear();
//        tfPrix.clear();
//        tfStock.clear();
//        categorieComboBox.getSelectionModel().clearSelection();
//        imageView.setImage(null);
//        selectedImage = null;
//    }

//    @FXML
//    private void retourListe() {
//        try {
//            // Charger la vue de la liste des produits
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ProduitView.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage) tfNom.getScene().getWindow();
//            stage.setScene(new Scene(root));
//
//        } catch (IOException e) {
//            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour à la liste des produits.");
//        }
//    }
@FXML
private void retourListe() {
    try {
        // Charger la vue de la liste des produits
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ProduitView.fxml"));
        Parent produitView = loader.load();

        // Chercher le StackPane contentPane (le conteneur central de ton layout)
        StackPane contentPane = (StackPane) tfNom.getScene().lookup("#contentPane");

        // Remplacer le contenu central avec la vue des produits
        contentPane.getChildren().setAll(produitView);
    } catch (IOException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur lors du retour à la liste des produits.");
    }
}

}
