package pi_project.Zayed.Interface;

import pi_project.Zayed.Entity.User;

import java.sql.SQLException;

public interface AuthenticationService {

    boolean login(User user);

    void logout();

    void forgetPassword(String email);

    boolean findUserByEmail(String Email) throws SQLException;

}
