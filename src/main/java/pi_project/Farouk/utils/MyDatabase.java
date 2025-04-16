package pi_project.Farouk.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    final String url = "jdbc:mysql://localhost:3307/projet_pidev";
    private final String user = "root";
    private final String password = "";
    private Connection cnx;
    private static MyDatabase instance;


    private MyDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cnx = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }
    }
    public static MyDatabase getInstance(){
        if(instance == null)
            instance = new MyDatabase();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }

}