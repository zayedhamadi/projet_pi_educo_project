package pi_project.Zayed.Interface;

import pi_project.Zayed.Entity.LoginHistory;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginHistoryService {


    void addLoginHistory(LoginHistory loginHistory);

    Optional<LocalDateTime> getLastLoginTimeByUserId(int idUser);

}
