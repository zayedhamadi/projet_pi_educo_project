package pi_project.Zayed.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import pi_project.db.DataSource;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class StatistiqueAdmin implements Initializable {

    @FXML
    private Label totalUsersLabel;
    @FXML
    private PieChart genrePieChart;
    @FXML
    private PieChart etatComptePieChart;
    @FXML
    private PieChart rolePieChart;

    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.connection = DataSource.getInstance().getConn();
        afficherTotalUtilisateurs();
        remplirPieChart(genrePieChart, "SELECT genre, COUNT(*) FROM user GROUP BY genre", "Répartition par Genre");
        remplirPieChart(etatComptePieChart, "SELECT etat_compte, COUNT(*) FROM user GROUP BY etat_compte", "État des Comptes");
        remplirPieChart(rolePieChart, "SELECT roles, COUNT(*) FROM user GROUP BY roles", "Répartition par Rôle");
    }


    private void afficherTotalUtilisateurs() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user");
            if (rs.next()) {
                totalUsersLabel.setText("Total utilisateurs : " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void remplirPieChart(PieChart chart, String query, String title) {
        chart.getData().clear();
        chart.setTitle(title);

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String label = rs.getString(1);
                int count = rs.getInt(2);
                chart.getData().add(new PieChart.Data(label + " (" + count + ")", count));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
