package pi_project.Zayed.Interface;

import pi_project.Zayed.Entity.User;

import java.sql.SQLException;
import java.util.List;

public interface UserService<T> {

    void addUser(T user);

    User updateUser(User user);

    User getSpeceficUser(int id) throws SQLException;

    List<T> getActifUser() throws SQLException;

    List<T> getInactifUser() throws SQLException;

}
