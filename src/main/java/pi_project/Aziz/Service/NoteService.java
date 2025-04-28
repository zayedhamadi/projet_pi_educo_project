package pi_project.Aziz.Service;

import pi_project.Aziz.Entity.Note;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Fedi.entites.eleve;
import pi_project.db.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteService {
    private Connection cnx;

    public NoteService() {
        cnx = DataSource.getInstance().getConn();
    }

    public void saveNote(Note note) throws SQLException {
        String sql = "INSERT INTO note (score, eleve_id, quiz_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setFloat(1, note.getScore());
            ps.setInt(2, note.getEleve().getId());
            ps.setInt(3, note.getQuiz().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    note.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Note> getNotesByStudent(int eleveId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, q.titre as quiz_name, e.nom as eleve_nom, e.prenom as eleve_prenom " +
                "FROM note n " +
                "JOIN quiz q ON n.quiz_id = q.id " +
                "JOIN eleve e ON n.eleve_id = e.id " +
                "WHERE n.eleve_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setScore(rs.getFloat("score"));

                // Create and populate Quiz object
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("quiz_id"));
                quiz.setNom(rs.getString("quiz_name"));
                note.setQuiz(quiz);

                // Create and populate Eleve object
                eleve student = new eleve();
                student.setId(rs.getInt("eleve_id"));
                student.setNom(rs.getString("eleve_nom"));
                student.setPrenom(rs.getString("eleve_prenom"));
                note.setEleve(student);

                notes.add(note);
            }
        }
        return notes;
    }

    public List<Note> getNotesByQuiz(int quizId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, e.nom as eleve_nom, e.prenom as eleve_prenom " +
                "FROM note n JOIN eleve e ON n.eleve_id = e.id " +
                "WHERE n.quiz_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setScore(rs.getFloat("score"));

                eleve student = new eleve();
                student.setId(rs.getInt("eleve_id"));
                student.setNom(rs.getString("eleve_nom"));
                student.setPrenom(rs.getString("eleve_prenom"));
                note.setEleve(student);

                notes.add(note);
            }
        }
        return notes;
    }
}