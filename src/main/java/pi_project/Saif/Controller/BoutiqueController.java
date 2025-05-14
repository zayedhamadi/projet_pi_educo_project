package pi_project.Saif.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import pi_project.Saif.Entity.Categorie;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.CategorieService;
import pi_project.Saif.Service.ProduitService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BoutiqueController {

    @FXML private FlowPane produitsContainer;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private TextField searchField;
    @FXML private Button filtrerBtn;
    @FXML private Button voirPanierBtn;

    private final ProduitService produitService = new ProduitService();
    private final CategorieService categorieService = new CategorieService();
    private final ObservableList<Produit> allProduits = FXCollections.observableArrayList();
    private final ObservableList<Produit> panier = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadCategories();
        loadProduits();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filtrerProduitsEtCategories());
        categorieCombo.valueProperty().addListener((observable, oldValue, newValue) -> filtrerProduitsEtCategories());

//        filtrerBtn.setOnAction(e -> filtrerProduitsEtCategories());
        voirPanierBtn.setOnAction(e -> ouvrirPanier());

//        updatePanierCount();
    }


    private void loadCategories() {
        List<Categorie> categories = categorieService.getAll();
        categorieCombo.getItems().add(null); // Pour "Toutes les catégories"
        categorieCombo.getItems().addAll(categories);
        categorieCombo.setCellFactory(param -> new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie categorie, boolean empty) {
                super.updateItem(categorie, empty);
                if (empty || categorie == null) {
                    setText("Tous les categories");
                } else {
                    // Afficher seulement le nom de la catégorie
                    setText(categorie.getNom());
                }
            }
        });

        // Personnaliser l'affichage de l'élément sélectionné
        categorieCombo.setButtonCell(new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie categorie, boolean empty) {
                super.updateItem(categorie, empty);
                if (empty || categorie == null) {
                    setText("Tous les categories");
                } else {
                    // Afficher seulement le nom de la catégorie
                    setText(categorie.getNom());
                }
            }
        });
    }

    private void loadProduits() {
        List<Produit> produits = produitService.getAll();
        allProduits.setAll(produits);
        afficherProduits(allProduits);
    }

//    private void filtrerProduits() {
//        String recherche = searchField.getText().toLowerCase().trim();
//        Categorie selectedCategorie = categorieCombo.getValue();
//
//        List<Produit> filtres = allProduits.stream()
//                .filter(p -> p.getNom().toLowerCase().contains(recherche))
//                .filter(p -> selectedCategorie == null || p.getCategorieId() == selectedCategorie.getId())
//                .collect(Collectors.toList());
//
//        afficherProduits(filtres);
//    }
private void filtrerProduitsEtCategories() {
    // Récupérer la valeur de recherche depuis le champ de texte
    String recherche = searchField.getText().toLowerCase().trim();

    // Récupérer la catégorie sélectionnée depuis le ComboBox
    Categorie selectedCategorie = categorieCombo.getValue();
    Integer categorieId = selectedCategorie != null ? selectedCategorie.getId() : null;

    // Rechercher les produits filtrés
    List<Produit> produitsFiltres = produitService.rechercherProduits(recherche, categorieId);

    // Rechercher les catégories filtrées
    List<Categorie> categoriesFiltres = categorieService.searchCategories(recherche);

    // Afficher les produits filtrés
    afficherProduits(produitsFiltres);


}
    @FXML
    public void afficherProduits(List<Produit> produits) {
        produitsContainer.getChildren().clear(); // Vide le conteneur des produits avant d'ajouter de nouveaux produits

        for (Produit p : produits) {

                if (p.getStock() <= 0) {
                    continue;
                }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/card_produit.fxml"));
                AnchorPane card = loader.load(); // Charge la carte du produit

                // Récupère le contrôleur de la carte
                CardProduitController controller = loader.getController();

                // Assigne les données du produit à la carte
                controller.setData(p, () -> {
                    boolean found = false;
                    for (Produit item : panier) {
                        if (item.getId() == p.getId()) {
                            item.setQuantite(item.getQuantite() + 1); // Incrémente la quantité
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Produit nouveauProduit = new Produit(
                                p.getId(), p.getNom(), p.getDescription(), p.getPrix(),
                                p.getStock(), p.getImage(), p.getCategorieId()
                        );
                        nouveauProduit.setQuantite(1);
                        panier.add(nouveauProduit);
                    }

//                    updatePanierCount();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Produit ajouté au panier !");
                    alert.showAndWait();
                });


                // Ajoute la carte dans le FlowPane
                produitsContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace(); // Gère les erreurs de chargement de la carte
            }
        }
    }
    // Add this method to BoutiqueController to open the cart view
    private void ouvrirPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/Panier.fxml"));
            AnchorPane panierPane = loader.load();

            PanierController controller = loader.getController();
            double total = panier.stream().mapToDouble(Produit::getPrix).sum();
            controller.setCartData(panier, total);

            Stage stage = new Stage();
            stage.setTitle("Votre Panier");
            stage.setScene(new Scene(panierPane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, "Impossible d'ouvrir le panier");
            alert.showAndWait();
        }
    }
//    private void updatePanierCount() {
//        voirPanierBtn.setText("🛒 Panier (" + panier.size() + ")");
//    }
    private void ajouterAuPanier(Produit produit) {
        for (Produit p : panier) {
            if (p.getId() == produit.getId()) {
                p.setQuantite(p.getQuantite() + 1);
//                updatePanierCount();
                return;
            }
        }
        produit.setQuantite(1);
        panier.add(produit);
//        updatePanierCount();
    }


}
