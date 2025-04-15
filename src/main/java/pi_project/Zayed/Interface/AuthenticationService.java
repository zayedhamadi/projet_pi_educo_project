package pi_project.Zayed.Interface;

import pi_project.Zayed.Entity.User;

public interface AuthenticationService {

    boolean login(User user);
    void logout();
    void forgetPassword(String email);
}
