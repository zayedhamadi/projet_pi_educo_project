package pi_project.db;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class DataSource {

    private static pi_project.db.DataSource data;
    private Connection conn;


    public DataSource() {

        try {
     final String url = "jdbc:mysql://localhost:3307/projet_pidev";
       //     final String url = "jdbc:mysql://localhost:3306/testingreconnaisance";
            final String user = "root";
            final String password = "";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static pi_project.db.DataSource getInstance() {
        if (data == null) {

            data = new pi_project.db.DataSource();
        }
        return data;
    }


}
