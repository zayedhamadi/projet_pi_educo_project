package pi_project.Zayed.Entity;


import lombok.*;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String nom;
    private String prenom;
    private String adresse;
    private String description;
    private int num_tel;
    private LocalDate date_naissance;
    private String email;
    private String image;
    private String password;
    private Set<Role> roles;
    private EtatCompte etat_compte;
    private Genre genre;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
