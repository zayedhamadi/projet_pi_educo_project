package pi_project.Saif.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import pi_project.Saif.Entity.Produit;
import pi_project.Saif.Service.ProduitService;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ProduitView {

    @FXML
    private TableView<Produit> tableView;

    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, String> colDescription;
    @FXML private TableColumn<Produit, Double> colPrix;
    @FXML private TableColumn<Produit, Integer> colStock;
    @FXML private TableColumn<Produit, String> colImage;
    @FXML private TableColumn<Produit, String> colActions;

    private final ProduitService service = new ProduitService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image")); // IMPORTANT !
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

//        colImage.setCellFactory(param -> new TableCell<>() {
//            private final ImageView imageView = new ImageView();
//
//            @Override
//            protected void updateItem(String imagePath, boolean empty) {
//                super.updateItem(imagePath, empty);
//                if (empty || imagePath == null) {
//                    setGraphic(null);
//                } else {
//                    try {
//                        File file = new File(imagePath);
//                        if (file.exists()) {
//                            Image image = new Image(file.toURI().toString(), 60, 60, true, true);
//                            imageView.setImage(image);
//                            setGraphic(imageView);
//                        } else {
//                            setGraphic(null);  // Si l'image n'existe pas
//                        }
//                    } catch (Exception e) {
//                        setGraphic(null);
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        colImage.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    try {
//                        File file = new File("E:/version_pidev/symfony_project-/educo_platform/public/uploads/" + imagePath);  // Make sure to include the full path
                        Properties props = new Properties();
                        props.load(new FileInputStream("config.properties"));
                        String uploadPath = props.getProperty("upload.path");

                        File file = new File(uploadPath + "/" + imagePath);

                        if (file.exists()) {
                            Image image = new Image(file.toURI().toString(), 60, 60, true, true);  // Ensure the image is resized to fit 60x60
                            imageView.setImage(image);
                            imageView.setFitWidth(60);  // Set width
                            imageView.setFitHeight(60); // Set height
                            imageView.setPreserveRatio(true); // Maintain the aspect ratio
                            setGraphic(imageView);
                        } else {
                            setGraphic(null);  // If the image doesn't exist, set the graphic to null
                        }
                    } catch (Exception e) {
                        setGraphic(null);
                        e.printStackTrace();
                    }
                }
            }
        });


        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Produit, String> call(TableColumn<Produit, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {

                            Button editBtn = new Button("Modifier");
                            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                            Button deleteBtn = new Button("Supprimer");
                            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                            editBtn.setOnAction(event -> {
                                Produit produit = getTableView().getItems().get(getIndex());
                                modifierProduit(produit);
                            });

                            deleteBtn.setOnAction(event -> {
                                Produit produit = getTableView().getItems().get(getIndex());
                                supprimerProduit(produit);
                            });

                            HBox box = new HBox(10, editBtn, deleteBtn);
                            setGraphic(box);
                        }
                    }
                };
            }
        });

        loadProduits();
    }

    @FXML
    private Pagination pagination;

    private static final int ROWS_PER_PAGE = 10;
    private ObservableList<Produit> allProduits = FXCollections.observableArrayList();

//    public void loadProduits() {
//        ObservableList<Produit> produits = FXCollections.observableArrayList(service.getAll());
//        tableView.setItems(produits);
//    }
public void loadProduits() {
    allProduits = FXCollections.observableArrayList(service.getAll());
    int pageCount = (int) Math.ceil((double) allProduits.size() / ROWS_PER_PAGE);
    pagination.setPageCount(pageCount > 0 ? pageCount : 1);
    pagination.setCurrentPageIndex(0);
    pagination.setPageFactory(this::createPage);
}
    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, allProduits.size());

        ObservableList<Produit> pageData = FXCollections.observableArrayList(allProduits.subList(fromIndex, toIndex));
        tableView.setItems(pageData);

        return new VBox(tableView);
    }

    private void modifierProduit(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ModifierProduitView.fxml"));
            Parent view = loader.load();

            // Récupérer la zone de contenu du layout principal
            StackPane contentPane = (StackPane) tableView.getScene().lookup("#contentPane");

            // Remplacer le contenu central (sans toucher à la sidebar)
            contentPane.getChildren().setAll(view);

            // Transmettre le produit au contrôleur de modification
            ModifierProduitView controller = loader.getController();
            controller.setProduit(produit);
            controller.setProduitView(this);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue de modification");
        }
    }

//    private void modifierProduit(Produit produit) {
//        try {
//            // Charger la vue de modification de produit
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ModifierProduitView.fxml"));
//            Parent root = loader.load();
//
//            // Récupérer la scène actuelle
//            Stage currentStage = (Stage) tableView.getScene().getWindow();
//
//            // Changer le contenu de la scène en y insérant la vue de modification
//            currentStage.getScene().setRoot(root);
//
//            // Récupérer le contrôleur de la vue de modification et lui passer le produit à modifier
//            ModifierProduitView controller = loader.getController();
//            controller.setProduit(produit);  // Passer l'objet produit à la vue de modification
//            controller.setProduitView(this);  // Lien vers la vue principale pour actualiser la table
//
//            currentStage.setTitle("Modifier un produit");
//            currentStage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue de modification");
//        }
//    }


//    private void modifierProduit(Produit produit) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/ModifierProduitView.fxml"));
//            Stage stage = new Stage();
//            stage.setTitle("Modifier un produit");
//            stage.setScene(new Scene(loader.load()));
//
//            ModifierProduitView controller = loader.getController();
//            controller.setProduit(produit);
//            controller.setProduitView(this);
//
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification");
//        }
//    }

    private void supprimerProduit(Produit produit) {
        try {
            service.supprimer(produit.getId());
            loadProduits();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le produit");
        }
    }

//    @FXML
//    private void ajouterProduit() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/AddProduit.fxml"));
//            Stage stage = new Stage();
//            stage.setTitle("Ajouter un produit");
//            stage.setScene(new Scene(loader.load()));
//
//            AddProduit controller = loader.getController();  // Assure-toi que ce contrôleur existe
//            controller.setProduitView(this);  // Lien vers la vue principale
//
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout");
//        }
//    }
//@FXML
//private void ajouterProduit() {
//    try {
//        // Charger la vue d'ajout de produit
//
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/AddProduit.fxml"));
//        Parent root = loader.load();
//
//        // Récupérer la scène actuelle
//        Stage currentStage = (Stage) tableView.getScene().getWindow();
//
//        // Changer le contenu de la scène en y insérant la vue d'ajout
//        currentStage.getScene().setRoot(root);
//
//        // Récupérer le contrôleur de la vue d'ajout et lui passer la vue principale pour mettre à jour la table
//        AddProduit controller = loader.getController();
//        controller.setProduitView(this);  // Lien vers la vue principale
//
//        currentStage.setTitle("Ajouter un produit");
//        currentStage.show();
//    } catch (Exception e) {
//        e.printStackTrace();
//        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue d'ajout");
//    }
//}
@FXML
private void ajouterProduit() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Saif/AddProduit.fxml"));
        Parent view = loader.load();

        // Récupérer la vue principale avec le StackPane (contentPane)
        StackPane contentPane = (StackPane) tableView.getScene().lookup("#contentPane");

        // Remplacer le contenu central
        contentPane.getChildren().setAll(view);

        // Transmettre le contrôleur si nécessaire
        AddProduit controller = loader.getController();
        controller.setProduitView(this);

    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher la vue d'ajout");
    }
}

    public void refreshTable() {
        loadProduits();  // Recharger les produits
        tableView.refresh();  // Rafraîchir la table pour afficher les nouveaux éléments
    }


    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
