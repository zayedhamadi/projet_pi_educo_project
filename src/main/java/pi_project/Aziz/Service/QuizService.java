package pi_project.Aziz.Service;

import pi_project.Aziz.Entity.Quiz;
import pi_project.db.DataSource;
import pi_project.Aziz.Interface.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements Service<Quiz> {

    private Connection cnx;

    public QuizService() {
        cnx = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quiz(titre, description, date_ajout, classe_id, matiere_id, cours_id) VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, quiz.getNom()); // or getTitre() if renamed
        ps.setString(2, quiz.getDescription()); // Add this line
        ps.setDate(3, new java.sql.Date(quiz.getDateAjout().getTime()));
        ps.setInt(4, quiz.getClasseId());
        ps.setInt(5, quiz.getMatiereId());
        ps.setInt(6, quiz.getCoursId());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Quiz quiz) throws SQLException {
        String sql = "UPDATE quiz SET titre = ?,description = ?, date_ajout = ?, classe_id = ?, matiere_id = ?, cours_id = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, quiz.getNom());
        ps.setString(2, quiz.getDescription());

        ps.setDate(3, new java.sql.Date(quiz.getDateAjout().getTime()));
        ps.setInt(4, quiz.getClasseId()); // Setting classeId
        ps.setInt(5, quiz.getMatiereId()); // Setting matiereId
        ps.setInt(6, quiz.getCoursId()); // Setting coursId
        ps.setInt(7, quiz.getId()); // Set the ID for update
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Quiz quiz) throws SQLException {
        String sql = "DELETE FROM quiz WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, quiz.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Quiz> recuperer() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.*, c.nom_classe, m.nom AS matiere_nom, co.name AS cours_nom " +
                "FROM quiz q " +
                "JOIN classe c ON q.classe_id = c.id " +
                "JOIN matiere m ON q.matiere_id = m.id " +
                "JOIN cours co ON q.cours_id = co.id";

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Quiz quiz = new Quiz();
            quiz.setId(rs.getInt("id"));
            quiz.setNom(rs.getString("titre"));
            quiz.setDescription(rs.getString("description"));
            quiz.setDateAjout(rs.getDate("date_ajout"));
            quiz.setClasseId(rs.getInt("classe_id"));
            quiz.setMatiereId(rs.getInt("matiere_id"));
            quiz.setCoursId(rs.getInt("cours_id"));
            quiz.setClasseNom(rs.getString("nom_classe"));
            quiz.setMatiereNom(rs.getString("matiere_nom"));
            quiz.setCoursNom(rs.getString("cours_nom"));

            quizzes.add(quiz);
        }

        return quizzes;
    }


    // Method to get a single Quiz by its ID
    public Quiz getQuizById(int id) throws SQLException {
        String sql = "SELECT * FROM quiz WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Quiz quiz = new Quiz();
            quiz.setId(rs.getInt("id"));
            quiz.setNom(rs.getString("nom"));
            quiz.setDateAjout(rs.getDate("date_ajout"));
            quiz.setClasseId(rs.getInt("classe_id"));
            quiz.setMatiereId(rs.getInt("matiere_id"));
            quiz.setCoursId(rs.getInt("cours_id"));
            return quiz;
        }
        return null;  // Return null if Quiz not found
    }
}
