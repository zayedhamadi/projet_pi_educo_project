package pi_project.Farouk.Services;

import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.db.DataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnseignantService {
    private final Connection cnx;
    private final Gson gson = new Gson();

    public EnseignantService() {
        cnx = DataSource.getInstance().getConn();
    }

    public List<User> getAllEnseignants() {
        List<User> enseignants = new ArrayList<>();
        String query = "SELECT * FROM user WHERE roles LIKE '%Enseignant%' AND etat_compte = 'active'";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Set<Role> roles = gson.fromJson(rs.getString("roles"),
                        new TypeToken<Set<Role>>(){}.getType());

                enseignants.add(new User(
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
                        Genre.valueOf(rs.getString("genre"))));
            }
        } catch (SQLException e) {
            System.out.println("Error getting enseignants: " + e.getMessage());
        }
        return enseignants;
    }
}