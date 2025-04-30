package pi_project.Farouk.Services;

import pi_project.Farouk.Models.Exam;
import pi_project.Farouk.utils.MyDatabase;
import pi_project.Fedi.entites.classe;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExamService {

    private Connection cnx;

    public ExamService() {
        cnx = MyDatabase.getInstance().getCnx();
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

    }// Method to get all exams from the database
    public List<Exam> getAllExams() throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT id, classe_id, subject, start_time, end_time, location FROM exam";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Exam exam = new Exam(
                        rs.getInt("id"),
                        rs.getInt("classe_id"),
                        rs.getString("subject"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getString("location")
                );
                exams.add(exam);
            }
        }

        return exams;
    }

    // Method to get exams by class ID
    public List<Exam> getExamsByClass(int classId) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT id, classe_id, subject, start_time, end_time, location FROM exam WHERE classe_id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, classId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Exam exam = new Exam(
                            rs.getInt("id"),
                            rs.getInt("classe_id"),
                            rs.getString("subject"),
                            rs.getTimestamp("start_time").toLocalDateTime(),
                            rs.getTimestamp("end_time").toLocalDateTime(),
                            rs.getString("location")
                    );
                    exams.add(exam);
                }
            }
        }

        return exams;
    }

    public List<classe> getAll() {
        List<classe> lst = new ArrayList<>();
        String req = "SELECT * FROM classe";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                classe cl = new classe();
                cl.setId(rs.getInt("id"));
                cl.setNomclasse(rs.getString("nom_classe"));
                cl.setNumsalle(rs.getInt("num_salle"));
                cl.setCapacite(rs.getInt("capacite_max"));
                lst.add(cl);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lst;
    }


    // Method to get exams filtered by class name
    public List<Exam> getExamsByClass(String className) {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT * FROM exam WHERE classe_id = (SELECT id FROM classe WHERE name = ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, className);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }




}
