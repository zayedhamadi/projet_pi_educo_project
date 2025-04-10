package pi_project.Zayed.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.mindrot.jbcrypt.BCrypt;
import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Interface.UserService;
import pi_project.Zayed.Utils.Mail;
import pi_project.db.DataSource;

import javax.mail.MessagingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserImpl implements UserService<User> {
    protected final String addUserrr = "INSERT INTO user (nom, prenom, adresse, description, num_tel, date_naissance, email, image, password, roles, etat_compte, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    protected final String updateUserrr = "UPDATE user SET nom=?, prenom=?, adresse=?, description=?, num_tel=?, date_naissance=?, email=?, image=?, roles=?, etat_compte=?, genre=? WHERE id=?";
    protected final String cesserUserrr = "UPDATE user SET etat_compte = ? WHERE id = ?";
    protected final String getSpeceficUserrr = "SELECT * FROM user WHERE id = ?";
    private final Connection cnx;
    private final Gson gson = new Gson();
    Mail mail = new Mail();

    public UserImpl() {
        cnx = DataSource.getInstance().getConn();
    }

    @Override
    public void addUser(User user) {
        String hashedPwd = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
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
    public void cesserUser(int id) {
        try (PreparedStatement pst = cnx.prepareStatement(this.cesserUserrr)) {
            pst.setString(1, EtatCompte.inactive.name());
            pst.setInt(2, id);
            pst.executeUpdate();
            System.out.println("Utilisateur " + id + " désactivé.");
        } catch (SQLException e) {
            System.out.println("Erreur désactivation : " + e.getMessage());
        }
    }

    @Override
    public void updateUser(User user) {
        try (PreparedStatement pst = cnx.prepareStatement(this.updateUserrr)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getAdresse());
            pst.setString(4, user.getDescription());
            pst.setInt(5, user.getNum_tel());
            pst.setDate(6, java.sql.Date.valueOf(user.getDate_naissance()));
            pst.setString(7, user.getEmail());
            pst.setString(8, user.getImage());
            pst.setString(9, gson.toJson(user.getRoles()));
            pst.setString(10, user.getEtat_compte().name());
            pst.setString(11, user.getGenre().name());
            pst.setInt(12, user.getId());
            pst.executeUpdate();
            System.out.println("Utilisateur " + user.getId() + " mis à jour.");
        } catch (SQLException e) {
            System.out.println("Erreur update : " + e.getMessage());
        }
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
