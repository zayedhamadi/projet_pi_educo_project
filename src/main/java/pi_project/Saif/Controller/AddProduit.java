package pi_project.Saif.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.ProduitService;
import pi_project.Saif.Service.CategorieService;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AddProduit {

    @FXML
    private TextField nomField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField stockField;
    @FXML
    private ImageView imageView;
    @FXML
    private ComboBox<String> categorieComboBox;

    private ProduitService produitService;
    private CategorieService categorieService;

    private List<pi_project.Saif.Entity.Categorie> categories;

    public AddProduit() {
        produitService = new ProduitService();
        categorieService = new CategorieService();
    }
    private ProduitView produitView;

    public void setProduitView(ProduitView produitView) {
        this.produitView = produitView;
    }

    public void initialize() {
        // Récupérer les catégories depuis la base de données et les ajouter au ComboBox
        categories = categorieService.getAll();
        for (Categorie categorie : categories) {
            categorieComboBox.getItems().add(categorie.getNom());
        }
    }

    // Méthode pour choisir une image
    @FXML
    private void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Afficher l'image dans l'ImageView
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    // Méthode pour ajouter le produit
    @FXML
    private void ajouterProduit(ActionEvent event) {
        try {
            // Récupérer les valeurs saisies dans le formulaire
            String nom = nomField.getText();
            String description = descriptionField.getText();
            double prix = Double.parseDouble(prixField.getText());
            int stock = Integer.parseInt(stockField.getText());

            // Récupérer l'image choisie et la copier dans le répertoire 'public/uploads'
            String imagePath = saveImage();

            // Récupérer l'ID de la catégorie sélectionnée
            String categorieNom = categorieComboBox.getSelectionModel().getSelectedItem();
            int categorieId = getCategorieIdByName(categorieNom);

            if (categorieId == -1) {
                throw new Exception("Catégorie invalide");
            }

            // Créer un objet Produit avec les données du formulaire
            Produit produit = new Produit(0, nom, description, prix, stock, imagePath, categorieId);

            // Appeler la méthode du service pour ajouter le produit à la base de données
            produitService.ajouter(produit);
            if (produitView != null) {
                produitView.refreshTable();  // Rafraîchir le tableau
            }
            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Produit ajouté");
            alert.setHeaderText(null);
            alert.setContentText("Le produit a été ajouté avec succès !");
            alert.showAndWait();

            // Optionnel : réinitialiser les champs après l'ajout
//            resetForm();
            retourListeProduits(null);
        } catch (NumberFormatException e) {
            // Gérer les erreurs de conversion de type (prix ou stock)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Entrée invalide");
            alert.setContentText("Veuillez entrer des valeurs valides pour le prix et le stock.");
            alert.showAndWait();
        } catch (Exception e) {
            // Gérer les erreurs générales
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Méthode pour récupérer l'ID de la catégorie à partir du nom
    private int getCategorieIdByName(String nom) {
        for (Categorie categorie : categories) {
            if (categorie.getNom().equals(nom)) {
                return categorie.getId();
            }
        }
        return -1; // Catégorie non trouvée
    }

    // Méthode pour sauvegarder l'image dans le répertoire 'public/uploads'
    private String saveImage() throws IOException {
        // Obtenir le fichier image sélectionné
        File imageFile = new File(imageView.getImage().getUrl().replace("file:/", ""));

        // Créer un chemin de destination dans 'public/uploads'
        String fileName = imageFile.getName();
        Path destination = Paths.get("E:/version_pidev/symfony_project-/educo_platform/public/uploads", fileName);
//"C:\\Users\\21690\\Desktop\\projet_pi\\symfony_project-\\educo_platform\\public\\uploads"
        // Vérifier si un fichier avec le même nom existe déjà
        if (Files.exists(destination)) {
            // Supprimer l'ancien fichier s'il existe
            Files.delete(destination);
        }

        // Copier l'image dans le répertoire de destination
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Retourner le chemin relatif pour l'enregistrer dans la base de données
        return fileName;
    }
    // Méthode pour réinitialiser le formulaire après l'ajout d'un produit
    private void resetForm() {
        nomField.clear();
        descriptionField.clear();
        prixField.clear();
        stockField.clear();
        imageView.setImage(null);
        categorieComboBox.getSelectionModel().clearSelection();
    }
    @FXML
    private void annulerFormulaire(ActionEvent event) {
        nomField.clear();
        descriptionField.clear();
        prixField.clear();
        stockField.clear();
        categorieComboBox.getSelectionModel().clearSelection();
        imageView.setImage(null);
    }

//    @FXML
//    private void retourListeProduits(ActionEvent event) {
//        // Naviguer vers la liste des produits, par exemple :
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ProduitView.fxml"));
//            Parent root = loader.load();
//            nomField.getScene().setRoot(root);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
@FXML
private void retourListeProduits(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ProduitView.fxml"));
        Parent produitView = loader.load();

        // Chercher le StackPane contentPane dans la scène actuelle
        StackPane contentPane = (StackPane) nomField.getScene().lookup("#contentPane");

        // Remplacer le contenu central avec la vue des produits
        contentPane.getChildren().setAll(produitView);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}

