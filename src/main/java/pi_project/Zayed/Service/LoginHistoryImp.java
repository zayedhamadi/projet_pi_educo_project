package pi_project.Zayed.Service;

import pi_project.Zayed.Entity.LoginHistory;
import pi_project.Zayed.Interface.LoginHistoryService;
import pi_project.db.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class LoginHistoryImp implements LoginHistoryService {
    protected final String addLoginHistorySQL = "INSERT INTO login_history (email, login_time, idUser) VALUES (?, ?, ?)";
    protected final String getLastLoginTimeByUserId = "SELECT login_time FROM login_history WHERE idUser = ? ORDER BY login_time DESC LIMIT 1";

    private final Connection connection;

    public LoginHistoryImp() {
        connection = DataSource.getInstance().getConn();

    }


    @Override
    public void addLoginHistory(LoginHistory loginHistory) {
        try (PreparedStatement pst = connection.prepareStatement(addLoginHistorySQL)) {
            pst.setString(1, loginHistory.getEmail());
            pst.setTimestamp(2, Timestamp.valueOf(loginHistory.getLoginTime()));
            pst.setInt(3, loginHistory.getIdUser());
            pst.executeUpdate();
            System.out.println("Login history added successfully for user : " + " " + loginHistory);
        } catch (Exception e) {
            System.out.println("Error adding login history");
            System.out.println(e.getMessage());
        }

    }

    @Override
    public Optional<LocalDateTime> getLastLoginTimeByUserId(int idUser) {

        try (PreparedStatement st = connection.prepareStatement(getLastLoginTimeByUserId)) {
            st.setInt(1, idUser);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("login_time");
                return Optional.of(timestamp.toLocalDateTime());
            }

        } catch (Exception e) {
            System.out.println("Error getting last login time");
            System.out.println(e.getMessage());
        }


        return Optional.empty();
    }
}
