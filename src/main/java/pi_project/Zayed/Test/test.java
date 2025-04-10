package pi_project.Zayed.Test;


import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.UserImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

public class test {
    public static void main(String[] args) throws SQLException {
        UserImpl userService = new UserImpl();
        User newUser = new User();
        /* testing add User */
        newUser.setId(40);
        newUser.setNom("Doejjlnomoisdvdsvneee");
        newUser.setPrenom("Johnnndopojkvsdvjjjnnnnn");
        newUser.setAdresse("12ssss3 Rue Principale");
        newUser.setDescription("Utilisateur de test");
        newUser.setNum_tel(123475359);
        newUser.setDate_naissance(LocalDate.of(1995, 5, 15));
        newUser.setEmail("jomnn@example.com");
        newUser.setImage("imajjjkjbkjbkbkbkge.jpg");
        newUser.setPassword("passopmopjpjpojopjhvhvidhviodhvword1223333333");
        newUser.setRoles(Set.of(Role.Enseignant));
        newUser.setGenre(Genre.femme);
        try {
            userService.addUser(newUser);
            System.out.println("Test réussi : utilisateur ajouté avec succès !");
        } catch (Exception e) {
            System.err.println("Test échoué : " + e.getMessage());
        }


        /* testing gettAllActifUser*/
        System.out.println();
        System.out.println("testing gettAllActifUser");
        System.out.println(userService.getActifUser());

        /* testing getInactifUser*/
        System.out.println();
        System.out.println("testing getInactifUser");
        System.out.println(userService.getInactifUser());

        /* testing getSpeceficUSer*/
        System.out.println();
        System.out.println();
        System.out.println("testing getSpeceficUSer");
        System.out.println(userService.getSpeceficUser(1));


        /* testing cesserUser*/
        System.out.println();
        userService.cesserUser(1);


    }
}