package pi_project.Farouk.Services;

import pi_project.Farouk.Models.Exam;
import pi_project.Farouk.utils.MyDatabase;
import pi_project.Fedi.entites.classe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamService {

    private Connection cnx;

    public ExamService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    public List<classe> getAllClasses() throws SQLException {
        List<classe> classes = new ArrayList<>();
        String query = "SELECT id, nom_classe FROM classe";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                classe c = new classe();  // create an empty object
                c.setId(rs.getInt("id")); // set the id
                c.setNomclasse(rs.getString("nom_classe")); // set the name
                classes.add(c);
            }
        }
        return classes;
    }


    // Method to get all exams from the database
    public List<Exam> getAllExams() throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT id, classe_id, subject, start_time, end_time, location FROM exam";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Exam exam = new Exam();
                exam.setId(rs.getInt("id"));
                exam.setClasseId(rs.getInt("classe_id"));
                exam.setSubject(rs.getString("subject"));
                exam.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                exam.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                exam.setLocation(rs.getString("location"));
                exams.add(exam);
            }
        }

        return exams;
    }

    // Method to get exams by class
    public List<Exam> getExamsByClass(int classId) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT id, classe_id, subject, start_time, end_time, location FROM exam WHERE classe_id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, classId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Exam exam = new Exam();
                exam.setId(rs.getInt("id"));
                exam.setClasseId(rs.getInt("classe_id"));
                exam.setSubject(rs.getString("subject"));
                exam.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                exam.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                exam.setLocation(rs.getString("location"));
                exams.add(exam);
            }
        }

        return exams;
    }

    // Method to add an exam to the database
    public void addExam(Exam exam) throws SQLException {
        String query = "INSERT INTO exam (classe_id, subject, start_time, end_time, location) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, exam.getClasseId());
            pstmt.setString(2, exam.getSubject());
            pstmt.setTimestamp(3, Timestamp.valueOf(exam.getStartTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(exam.getEndTime()));
            pstmt.setString(5, exam.getLocation());
            pstmt.executeUpdate();
        }
    }

}
