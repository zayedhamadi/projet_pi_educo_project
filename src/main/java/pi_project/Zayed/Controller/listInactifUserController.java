package pi_project.Zayed.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.Constant;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class listInactifUserController {
    Constant constant = new Constant();
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

    private ObservableList<User> userObservableList;
    private ObservableList<User> filteredList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadUsers();
        setupSearchFilter();
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
            return new SimpleObjectProperty<>(constant.loadImageFromPath(path));
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
    }

    private void setupSearchFilter() {
        search.textProperty().addListener((obs, oldVal, newVal) -> filterUser(newVal));
    }

    private void loadUsers() {
        UserImpl userImpl = new UserImpl();
        try {
            List<User> users = userImpl.getInactifUser();
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
        int totalPage = (int) Math.ceil((double) filteredList.size() / constant.nombreDePage);
        pagination.setPageCount(Math.max(totalPage, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int index) {
        int from = index * constant.nombreDePage;
        int to = Math.min(from + constant.nombreDePage, filteredList.size());
        listUser.setItems(FXCollections.observableArrayList(filteredList.subList(from, to)));

        VBox box = new VBox();
        box.getChildren().add(listUser);
        return box;
    }
}