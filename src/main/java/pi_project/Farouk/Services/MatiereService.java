package pi_project.Farouk.Services;

import pi_project.Farouk.Models.Cours;
import pi_project.Farouk.Models.Matiere;
import pi_project.Farouk.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereService  implements  IService<Matiere>{

    private Connection cnx;
    public MatiereService() {
        cnx = MyDatabase.getInstance().getCnx();
    }
    @Override

    public void ajouter(Matiere matiere) throws SQLException{

        String req = "INSERT INTO matiere(id_ensg_id, nom, coefficient) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, matiere.getId_ensg());
        ps.setString(2, matiere.getNom());
        ps.setDouble(3, matiere.getCoefficient());
        ps.executeUpdate();
        System.out.println("Matiere added successfully!");
    }


    @Override
    public boolean modifier(Matiere matiere) throws SQLException {
        String req = "UPDATE matiere SET id_ensg_id = ?, nom = ?, coefficient = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, matiere.getId_ensg());
        ps.setString(2, matiere.getNom());
        ps.setDouble(3, matiere.getCoefficient());
        ps.setInt(4, matiere.getId());

        int rowsUpdated = ps.executeUpdate();
        return rowsUpdated > 0;
    }

    @Override
    public boolean supprimer(Matiere matiere) throws SQLException {

        String req = "DELETE FROM matiere WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, matiere.getId());

        int rowsDeleted = ps.executeUpdate();
        return rowsDeleted > 0;
    }

    @Override
    public List<Matiere> rechercher() throws SQLException {
        return recupererTous();
    }

    public List<Matiere> recupererTous() throws SQLException {
            List<Matiere> matieres = new ArrayList<>();
            String req = "SELECT * FROM matiere";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                Matiere m = new Matiere(
                        rs.getInt("id"),
                        rs.getInt("id_ensg_id"),
                        rs.getString("nom"),
                        rs.getDouble("coefficient")
                );
                matieres.add(m);
            }
            return matieres;
        }
    public List<Matiere> getByEnseignantId(int enseignantId) throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String req = "SELECT * FROM matiere WHERE id_ensg_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, enseignantId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Matiere m = new Matiere(
                    rs.getInt("id"),
                    rs.getInt("id_ensg_id"),
                    rs.getString("nom"),
                    rs.getDouble("coefficient")
            );
            matieres.add(m);
        }
        return matieres;
    }
}



