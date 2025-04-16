package pi_project.Zayed.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.Constant;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class listActifUserController {

    private static final int ROWS_PER_PAGE = 10;

    @FXML
    private BorderPane mainPane;
    @FXML
    private TableView<User> listUser;
    @FXML
    private TableColumn<User, Image> image;
    @FXML
    private TableColumn<User, String> nom, prenom, email, role;
    @FXML
    private Pagination pagination;
    @FXML
    private TextField search;
    @FXML
    private TableColumn<User, Void> action;
    @FXML
    private StackPane contentPane;
    @FXML
    private VBox listView;

    private ObservableList<User> userObservableList;
    private ObservableList<User> filteredList;
    private Parent profileView;
    private ConsulterUserProfileController profileController;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadUsers();
        setupSearchFilter();
        loadProfileView();
    }

    public void refreshList() {
        loadUsers();
    }

    private void setupTableColumns() {
        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));

        role.setCellValueFactory(cellData -> {
            String roles = cellData.getValue().getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            return new SimpleObjectProperty<>(roles);
        });

        image.setCellValueFactory(cellData -> {
            String path = cellData.getValue().getImage();
            return new SimpleObjectProperty<>(loadImageFromPath(path));
        });

        image.setCellFactory(column -> new TableCell<User, Image>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Image img, boolean empty) {
                super.updateItem(img, empty);
                if (empty || img == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(img);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);
                }
            }
        });

        action.setCellFactory(column -> new TableCell<User, Void>() {
            private final Button viewButton = new Button("Consulter son profile");

            {
                viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                viewButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    viewUserProfile(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }

    private void loadProfileView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Zayed/ConsulterUserProfile.fxml"));
            profileView = loader.load();
            profileController = loader.getController();
            profileController.setParentController(this);
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue profil", e.getMessage());
        }
    }

    private void viewUserProfile(User user) {
        profileController.initData(user);
        contentPane.getChildren().setAll(profileView);
    }

    public void returnToList() {
        contentPane.getChildren().setAll(listView);
    }

    private void setupSearchFilter() {
        search.textProperty().addListener((obs, oldVal, newVal) -> filterUser(newVal));
    }

    private void loadUsers() {
        UserImpl userImpl = new UserImpl();
        try {
            List<User> users = userImpl.getActifUser();
            userObservableList = FXCollections.observableList(users);
            filteredList = FXCollections.observableArrayList(users);
            updatePagination();
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les utilisateurs", e.getMessage());
        }
    }

    private void filterUser(String query) {
        String searchText = query == null ? "" : query.trim().toLowerCase();

        Predicate<User> match = user ->
                user.getNom().toLowerCase().contains(searchText) ||
                        user.getPrenom().toLowerCase().contains(searchText) ||
                        user.getEmail().toLowerCase().contains(searchText) ||
                        user.getRoles().toString().toLowerCase().contains(searchText);

        filteredList.setAll(userObservableList.filtered(match));
        updatePagination();
    }

    private void updatePagination() {
        int totalPage = (int) Math.ceil((double) filteredList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(totalPage, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int index) {
        int from = index * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, filteredList.size());

        listUser.setItems(FXCollections.observableArrayList(filteredList.subList(from, to)));

        VBox box = new VBox();
        box.getChildren().add(listUser);
        return box;
    }

    private Image loadImageFromPath(String path) {
        if (path == null || path.isEmpty()) return getDefaultImage();

        try {
            String fullPath = "C:/Users/21690/Desktop/projet_pi/symfony_project-/educo_platform/public/uploads/" + path;
            File file = new File(fullPath);
            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        return getDefaultImage();
    }

    private Image getDefaultImage() {
        return new Image("file:src/main/resources/Zayed/images/default-user.png");
    }

    @FXML
    public void goingToAddUser() {
        loadFXMLScene("/Zayed/addUser.fxml", "Ajouter un utilisateur");
    }

    @FXML
    private void profile_user() {
        loadFXMLScene("/Zayed/ProfilAdmin.fxml", "Profil Administrateur");
    }

    @FXML
    private void logout() {
        try {
            new AuthenticationImpl().logout();
            loadFXMLScene("/Zayed/login.fxml", "Connexion");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Déconnexion échouée", "Erreur lors du logout.");
        }
    }

    private void loadFXMLScene(String resourcePath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Parent root = loader.load();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue", e.getMessage());
        }
    }



}