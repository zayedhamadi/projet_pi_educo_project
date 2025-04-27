package pi_project.Farouk.Services;

import pi_project.Farouk.Models.Cours;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        String sql = "UPDATE cours SET id_matiere_id = ?, classe_id = ?, name = ?, chapter_number = ?, pdf_filename = ? WHERE id = ?";
        PreparedStatement stmt = cnx.prepareStatement(sql);

        stmt.setInt(1, cours.getIdMatiere());
        stmt.setInt(2, cours.getClasse());
        stmt.setString(3, cours.getName());
        stmt.setInt(4, cours.getChapterNumber());
        stmt.setString(5, cours.getPdfFilename());
        stmt.setInt(6, cours.getId());  // use id to locate the correct row

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
    // Enhanced method to get courses grouped by subject and filtered by class
    public Map<String, List<Cours>> getCoursesByMatiereAndClass(String className) throws SQLException {
        // First get the class ID
        int classId = getClassIdByName(className);

        // Get all courses for this class with subject names
        String sql = """
            SELECT c.id, c.name, c.chapter_number, c.pdf_filename, 
                   m.id as matiere_id, m.nom as matiere_name
            FROM cours c
            JOIN matiere m ON c.id_matiere_id = m.id
            WHERE c.classe_id = ?
            ORDER BY m.nom, c.chapter_number
            """;

        List<Cours> courses = new ArrayList<>();
        Map<String, List<Cours>> coursesByMatiere = new TreeMap<>();

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cours cours = new Cours(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("matiere_id"),
                        classId,
                        rs.getInt("chapter_number"),
                        rs.getString("pdf_filename")
                );

                String matiereName = rs.getString("matiere_name");
                coursesByMatiere.computeIfAbsent(matiereName, k -> new ArrayList<>()).add(cours);
            }
        }

        return coursesByMatiere;
    }

    // Helper method to get class ID by name
    private int getClassIdByName(String className) throws SQLException {
        String sql = "SELECT id FROM classe WHERE nom_classe = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Class not found: " + className);
        }
    }

    // Method to get all available class names
    public List<String> getAllClassNames() throws SQLException {
        List<String> classNames = new ArrayList<>();
        String sql = "SELECT nom_classe FROM classe ORDER BY nom_classe";

        try (PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                classNames.add(rs.getString("nom_classe"));
            }
        }
        return classNames;
    }

    // Method to get courses for a specific class
    public List<Cours> getCoursesByClass(String className) throws SQLException {
        int classId = getClassIdByName(className);
        String sql = "SELECT * FROM cours WHERE classe_id = ? ORDER BY id_matiere_id, chapter_number";

        List<Cours> courses = new ArrayList<>();
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cours cours = new Cours(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("id_matiere_id"),
                        rs.getInt("classe_id"),
                        rs.getInt("chapter_number"),
                        rs.getString("pdf_filename")
                );
                courses.add(cours);
            }
        }
        return courses;
    }



}
