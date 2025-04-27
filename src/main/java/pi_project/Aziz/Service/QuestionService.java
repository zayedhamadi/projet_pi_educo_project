package pi_project.Aziz.Service;


import pi_project.Aziz.Entity.Question;
import pi_project.Aziz.Entity.Quiz;
import pi_project.Aziz.Interface.Service;
import pi_project.db.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService implements Service<Question> {

    private Connection cnx;

    public QuestionService() {
        cnx = DataSource.getInstance().getConn();
    }

    @Override
    public void ajouter(Question question) throws SQLException {
        // Fetch the full Quiz object by its ID
        Quiz quiz = getQuizById(question.getQuiz().getId());  // Fetch full Quiz object using its ID

        // If Quiz is found, set it to the Question
        if (quiz != null) {
            question.setQuiz(quiz);

            // Now insert the Question with the full Quiz object
            String sql = "INSERT INTO question (texte, options, reponse, quiz_id) VALUES(?, ?, ?, ?)";
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, question.getTexte());
            ps.setString(2, convertListToJson(question.getOptions()));  // Convert options to JSON format
            ps.setString(3, question.getReponse());
            ps.setInt(4, question.getQuiz().getId());  // Use quiz_id as a foreign key
            ps.executeUpdate();
        } else {
            throw new SQLException("Quiz not found with ID: " + question.getQuiz().getId());
        }
    }

    @Override
    public void modifier(Question question) throws SQLException {
        // Fetch the full Quiz by ID before modifying the Question
        Quiz quiz = getQuizById(question.getQuiz().getId());
        if (quiz != null) {
            question.setQuiz(quiz);

            // Update logic to modify the question in the database
            String sql = "UPDATE question SET texte = ?, options = ?, reponse = ?, quiz_id = ? WHERE id = ?";
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, question.getTexte());
            ps.setString(2, convertListToJson(question.getOptions()));  // Convert options to JSON
            ps.setString(3, question.getReponse());
            ps.setInt(4, question.getQuiz().getId());
            ps.setInt(5, question.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(Question question) throws SQLException {
        // Deletion logic for removing a Question
        String sql = "DELETE FROM question WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, question.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Question> recuperer() throws SQLException {
        // Retrieve all questions with their associated Quiz
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM question";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String texte = rs.getString("texte");
            String optionsJson = rs.getString("options");
            String reponse = rs.getString("reponse");
            int quizId = rs.getInt("quiz_id");

            // Convert options from JSON string to List<String>
            List<String> options = convertJsonToList(optionsJson);

            // Fetch the full Quiz object by its ID
            Quiz quiz = getQuizById(quizId);

            if (quiz != null) {
                Question question = new Question(id, texte, options, reponse, quiz);
                questions.add(question);
            }
        }
        return questions;
    }

    // Helper method to fetch full Quiz by its ID
    private Quiz getQuizById(int quizId) throws SQLException {
        String sql = "SELECT * FROM quiz WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, quizId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            // Create and populate Quiz object
            Quiz quiz = new Quiz();
            quiz.setId(rs.getInt("id"));
            quiz.setNom(rs.getString("titre"));
            quiz.setDateAjout(rs.getDate("date_ajout"));
            return quiz;
        }
        return null;  // Return null if Quiz not found
    }

    // Helper method to convert List<String> to JSON format for the database
    private String convertListToJson(List<String> options) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < options.size(); i++) {
            json.append("\"").append(options.get(i)).append("\"");
            if (i < options.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    // Helper method to convert JSON string back to List<String>
    private List<String> convertJsonToList(String json) {
        List<String> options = new ArrayList<>();
        if (json != null && json.length() > 2) {  // Ensure it's not an empty JSON array
            json = json.substring(1, json.length() - 1);  // Remove brackets
            String[] optionArray = json.split("\",\"");
            for (String option : optionArray) {
                options.add(option.replace("\"", ""));  // Clean up quotes
            }
        }
        return options;
    }
    public Quiz getQuizByTitle(String titre) throws SQLException {
        String sql = "SELECT * FROM quiz WHERE titre = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, titre);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Quiz quiz = new Quiz();
            quiz.setId(rs.getInt("id"));
            quiz.setNom(rs.getString("titre"));
            // set other quiz fields if needed
            return quiz;
        }
        return null;
    }
    // In QuestionService.java
    public List<Question> getQuestionsForQuiz(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE quiz_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setTexte(rs.getString("texte"));
                question.setOptions(convertJsonToList(rs.getString("options")));
                question.setReponse(rs.getString("reponse"));
                question.setQuizId(quizId);
                questions.add(question);
            }
        }
        return questions;
    }
}

