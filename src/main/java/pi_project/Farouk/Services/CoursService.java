package pi_project.Farouk.Services;

import pi_project.Farouk.Models.Cours;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import pi_project.Farouk.utils.MyDatabase;

public class CoursService implements IService<Cours> {
    private Connection cnx;
    public CoursService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Cours cours) throws SQLException {
        // Check if the matiere exists
        String checkMatiereSql = "SELECT COUNT(*) FROM matiere WHERE id = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkMatiereSql)) {
            checkStmt.setInt(1, cours.getIdMatiere());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("Matiere with ID " + cours.getIdMatiere() + " does not exist.");
            }
        }

        // Check if the classe exists
        String checkClasseSql = "SELECT COUNT(*) FROM classe WHERE id = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkClasseSql)) {
            checkStmt.setInt(1, cours.getClasse());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new SQLException("Classe with ID " + cours.getClasse() + " does not exist.");
            }
        }

        // Now insert the course (with PDF filename if you have it)
        String insertSql = "INSERT INTO cours (name, id_matiere_id, classe_id, chapter_number, pdf_filename) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = cnx.prepareStatement(insertSql)) {
            insertStmt.setString(1, cours.getName());
            insertStmt.setInt(2, cours.getIdMatiere());
            insertStmt.setInt(3, cours.getClasse());
            insertStmt.setInt(4, cours.getChapterNumber());
            insertStmt.setString(5, cours.getPdfFilename());  // add this in your Cours model
            insertStmt.executeUpdate();
        }
    }



    @Override
    public boolean modifier(Cours cours) throws SQLException {
        String sql = "UPDATE cours SET name = ?, id_matiere = ?, classe = ?, pdf_filename = ? WHERE id = ?";
        PreparedStatement stmt = cnx.prepareStatement(sql);
        stmt.setString(1, cours.getName());
        stmt.setInt(2, cours.getIdMatiere());
        stmt.setInt(3, cours.getClasse());
        stmt.setString(4, cours.getPdfFilename());
        stmt.setInt(5, cours.getId());

        return stmt.executeUpdate() > 0;
    }


    @Override
    public boolean  supprimer(Cours cours) throws SQLException {
        String query = "DELETE FROM cours WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, cours.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public List<Cours> rechercher() {
        return List.of();
    }

    public List<Cours> recupererTous() throws SQLException {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT * FROM cours";

        try (PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cours cours = new Cours(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("id_matiere_id"),
                        rs.getInt("classe_id"),
                        rs.getInt("chapter_number"),
                        rs.getString("pdf_filename")
                );
                coursList.add(cours);
            }
        }

        return coursList;
    }



}
