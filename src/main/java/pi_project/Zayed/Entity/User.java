package pi_project.Zayed.Entity;


import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Validator.PhoneNumber;
import pi_project.Zayed.Validator.dateNaissance;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    int id;
    @NotNull(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom ne doit contenir que des lettres")
    String nom;

    @NotNull(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom ne doit contenir que des lettres")
    String prenom;

    @Size(max = 100, message = "L'adresse ne doit pas dépasser 100 caractères")
    String adresse;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    String description;

    @PhoneNumber
    @NotNull(message = "Le numéro de téléphone est obligatoire")
    int num_tel;

    @NotNull(message = "La date de naissance est obligatoire")
    @dateNaissance
    LocalDate date_naissance;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    String email;

    String image;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 5, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial"
    )
    String password;
    Set<Role> roles;
    EtatCompte etat_compte;
    Genre genre;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
