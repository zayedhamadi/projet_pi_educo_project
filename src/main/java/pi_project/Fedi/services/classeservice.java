package pi_project.Fedi.services;


import pi_project.Fedi.entites.classe;
import pi_project.Fedi.interfaces.idsevice;
import pi_project.db.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class classeservice implements  idsevice<classe>{

    //connexion de base de donne
    DataSource dbconx=new  DataSource().getInstance();

    @Override
    public void add(classe classe) {
        String  req="INSERT INTO `classe`(`nom_classe`, `num_salle`, `capacite_max`) VALUES ('"+classe.getNomclasse()+"','"+classe.getNumsalle()+"','"+classe.getCapacite()+"')" ;
        try {
            Statement st = dbconx.getConn().createStatement();
            st.executeUpdate(req);
            System.out.println("add is successful");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void delete(int id) {
        String req = "DELETE FROM classe WHERE id = ?";
        try (PreparedStatement pstmt = dbconx.getConn().prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Suppression réussie pour l'ID: " + id);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    /*@Override
    public void delete(classe classeToDelete) {
        String req = "DELETE FROM classe WHERE id = ?";
        try (PreparedStatement pstmt = dbconx.getConn().prepareStatement(req)) {
            pstmt.setInt(1, classeToDelete.getId());
            System.out.println(classeToDelete.getId());
            pstmt.executeUpdate();
            System.out.println("Suppression réussie pour l'ID: " + classeToDelete.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
        }
    }*/

    @Override
    public void update(classe updatedClasse) {
        String req = "UPDATE classe SET nom_classe = ?, num_salle = ?, capacite_max = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbconx.getConn().prepareStatement(req)) {
            pstmt.setString(1, updatedClasse.getNomclasse());
            pstmt.setInt(2, updatedClasse.getNumsalle());
            pstmt.setInt(3, updatedClasse.getCapacite());
            pstmt.setInt(4, updatedClasse.getId()); // ID récupéré depuis l'objet

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Modification réussie pour l'ID: " + updatedClasse.getId());
            } else {
                System.out.println("Aucune classe trouvée avec l'ID: " + updatedClasse.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la classe avec ID: " + updatedClasse.getId(), e);
        }
    }

   /* @Override
    public void update(int id, classe newClasse) {
        String req = "UPDATE classe SET nom_classe = ?, num_salle = ?, capacite_max = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbconx.getCon().prepareStatement(req)) {
            pstmt.setString(1, newClasse.getNomclasse());
            pstmt.setInt(2, newClasse.getNumsalle());
            pstmt.setInt(3, newClasse.getCapacite());
            pstmt.setInt(4, id); // ID de la classe à mettre à jour

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Modification réussie pour l'ID: " + id);
            } else {
                System.out.println("Aucune classe trouvée avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la classe avec ID: " + id, e);
        }
    }*/


    @Override
    public List<classe> getAll() {
        List<classe> lst=new ArrayList<>();
        String req="SELECT * FROM classe";
        try {
            Statement st = dbconx.getConn().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                classe cl;
                cl = new classe();
                cl.setId(rs.getInt("id"));
                cl.setNomclasse(rs.getString("nom_classe"));
                cl.setNumsalle(rs.getInt("num_salle"));
                cl.setCapacite(rs.getInt("capacite_max"));
                lst.add(cl);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  lst;
    }
    @Override
    public classe getOne(int id) {
        String req = "SELECT * FROM classe WHERE id = ?";
        try (PreparedStatement pstmt = dbconx.getConn().prepareStatement(req)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                classe c = new classe();
                c.setId(rs.getInt("id"));
                c.setNomclasse(rs.getString("nom_classe"));
                c.setNumsalle(rs.getInt("num_salle"));
                c.setCapacite(rs.getInt("capacite_max"));
                return c;
            } else {
                System.out.println("Aucune classe trouvée avec l'ID: " + id);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la classe avec ID: " + id, e);
        }
    }

}
