package pi_project.Zayed.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Interface.UserService;
import pi_project.Zayed.Utils.Mail;
import pi_project.Zayed.Utils.PasswordUtils;
import pi_project.db.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.mail.MessagingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserImpl implements UserService<User> {
    protected final String addUserrr = "INSERT INTO user (nom, prenom, adresse, description, num_tel, date_naissance, email, image, password, roles, etat_compte, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    protected final String updateUserrr = "UPDATE user SET nom=?, prenom=?, adresse=?, description=?, num_tel=?, date_naissance=?, email=?, image=?, password=?, roles=?, etat_compte=?, genre=? WHERE id=?";
    protected final String getSpeceficUserrr = "SELECT * FROM user WHERE id = ?";
    private final Connection cnx;
    private final Gson gson = new Gson();
    Mail mail = new Mail();
    private final Validator validator;
    public UserImpl() {
        cnx = DataSource.getInstance().getConn();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public static void showAlert(Alert.AlertType alertType, String message, String headerText, String title) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        alert.show();
    }
    public void validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<User> violation : violations) {
                sb.append(violation.getPropertyPath()).append(" : ").append(violation.getMessage()).append("\n");
            }
            showAlert(Alert.AlertType.ERROR, sb.toString(), "il faut respecter les contraintes de validation ", null);
            throw new IllegalArgumentException("Erreur de validation : \n" + sb);
        }
    }
    @Override
    public void addUser(User user) {
        validateUser(user);
        String hashedPwd = PasswordUtils.cryptPw(user.getPassword());
        user.setEtat_compte(EtatCompte.active);

        try (PreparedStatement pst = cnx.prepareStatement(this.addUserrr)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getAdresse());
            pst.setString(4, user.getDescription());
            pst.setInt(5, user.getNum_tel());
            pst.setDate(6, java.sql.Date.valueOf(user.getDate_naissance()));
            pst.setString(7, user.getEmail());
            pst.setString(8, user.getImage());
            pst.setString(9, hashedPwd);
            pst.setString(10, gson.toJson(user.getRoles()));
            pst.setString(11, user.getEtat_compte().name());
            pst.setString(12, user.getGenre().name());

            int done = pst.executeUpdate();
            if (done > 0) {
                mail.sendAddUserMail(user.getEmail(), user.getPassword());
                System.out.println("Utilisateur ajouté !");
            }
        } catch (SQLException | MessagingException e) {
            System.out.println(e.getCause());
            System.out.println("Erreur ajout utilisateur : " + e.getMessage());
        }
    }


    @Override
    public User updateUser(User user) {
        this.validateUser(user);
        try (PreparedStatement pst = cnx.prepareStatement(this.updateUserrr)) {
            String hashedPwd = user.getPassword();

            PasswordUtils.checkPw(user.getPassword(), hashedPwd);

            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getAdresse());
            pst.setString(4, user.getDescription());
            pst.setInt(5, user.getNum_tel());
            pst.setDate(6, java.sql.Date.valueOf(user.getDate_naissance()));
            pst.setString(7, user.getEmail());
            pst.setString(8, user.getImage());
            pst.setString(9, hashedPwd);
            pst.setString(10, gson.toJson(user.getRoles()));
            pst.setString(11, user.getEtat_compte().name());
            pst.setString(12, user.getGenre().name());
            pst.setInt(13, user.getId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour a échoué, aucune ligne affectée");
            }

            System.out.println("Utilisateur " + user.getId() + " mis à jour.");
            return getSpeceficUser(user.getId());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User getSpeceficUser(int id) {
        try (PreparedStatement pst = cnx.prepareStatement(this.getSpeceficUserrr)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Set<Role> roles = gson.fromJson(rs.getString("roles"), new TypeToken<Set<Role>>() {
                }.getType());
                return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("adresse"),
                        rs.getString("description"),
                        rs.getInt("num_tel"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("image"),
                        rs.getString("password"),
                        roles,
                        EtatCompte.valueOf(rs.getString("etat_compte")),
                        Genre.valueOf(rs.getString("genre"))
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur get user : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> getActifUser() {
        return getUsers("Active");
    }

    @Override
    public List<User> getInactifUser() {
        return getUsers("Inactive");
    }

    private List<User> getUsers(String etat) {
        List<User> users = new ArrayList<>();
        String getUserrr = "SELECT * FROM user WHERE etat_compte = '" + etat + "'";

        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(getUserrr)) {
            while (rs.next()) {
                Set<Role> roles = gson.fromJson(rs.getString("roles"), new TypeToken<Set<Role>>() {
                }.getType());
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("adresse"),
                        rs.getString("description"),
                        rs.getInt("num_tel"),
                        rs.getDate("date_naissance").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("image"),
                        rs.getString("password"),
                        roles,
                        EtatCompte.valueOf(rs.getString("etat_compte")),
                        Genre.valueOf(rs.getString("genre"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur get users : " + e.getMessage());
        }
        return users;
    }
}
