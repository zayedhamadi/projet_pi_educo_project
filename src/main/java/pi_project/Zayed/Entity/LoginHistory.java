package pi_project.Zayed.Entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class LoginHistory {
    int id;
    String email;
    LocalDateTime loginTime;
    int idUser;

    public LoginHistory(String email, LocalDateTime loginTime, int idUser) {
        this.email = email;
        this.loginTime = loginTime;
        this.idUser = idUser;
    }

}
