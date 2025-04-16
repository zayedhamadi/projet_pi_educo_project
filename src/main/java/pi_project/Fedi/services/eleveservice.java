package pi_project.Fedi.services;

import pi_project.Fedi.entites.eleve;
import pi_project.Fedi.interfaces.idsevice;
import pi_project.db.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class eleveservice implements idsevice<eleve>  {

    //connexion de base de donne
    DataSource dbconx=new  DataSource().getInstance();


    @Override
    public void add(eleve eleve) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void update(eleve eleve) {

    }

    @Override
    public List<eleve> getAll() {
        return List.of();
    }

    @Override
    public eleve getOne(int id) {
        return null;
    }

    ///////////////////////////////////////////////
    public List<eleve> getEnfantsParParent(int idParent) {
        List<eleve> enfants = new ArrayList<>();
        String req = "SELECT * FROM eleve WHERE id_parent_id = ?";

        try (PreparedStatement pstmt = dbconx.getConn().prepareStatement(req)) {
            pstmt.setInt(1, idParent);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                eleve e = new eleve();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setDate_naissance(rs.getDate("date_de_naissance"));
                e.setMoyenne(rs.getFloat("moyenne"));
                e.setNbre_absence(rs.getInt("nbre_abscence"));
                e.setDate_inscription(rs.getDate("date_inscription"));
                e.setQr_code(rs.getString("qr_code_data_uri"));
                e.setId_parent(rs.getInt("id_parent_id"));

                // Si tu veux charger aussi la classe, tu peux faire un appel à ton service Classe ici :
                // classe c = classeService.getOne(rs.getInt("id_classe"));
                // e.setId_classe(c);

                enfants.add(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des enfants du parent avec ID: " + idParent, e);
        }

        return enfants;
    }

}
