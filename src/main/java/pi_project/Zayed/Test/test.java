package pi_project.Zayed.Test;


import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Enum.Role;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.UserImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

public class test {
    public static void main(String[] args) throws SQLException {
        UserImpl userService = new UserImpl();
        AuthenticationImpl authentication = new AuthenticationImpl();

        /* testing add User */
        User newUser = new User();
        newUser.setId(5000);
        newUser.setNom("Doejjlnineee");
        newUser.setPrenom("Johnnnjjjnnnnn");
        newUser.setAdresse("123 Rue Principale");
        newUser.setDescription("Utilisateur de test");
        newUser.setNum_tel(123472759);
        newUser.setDate_naissance(LocalDate.of(1995, 5, 15));
        newUser.setEmail("johnnnnnnnnmmnn@example.com");
        newUser.setImage("imajjjge.jpg");
        newUser.setPassword("password1223333333");
        newUser.setRoles(Set.of(Role.Enseignant));
        newUser.setGenre(Genre.femme);
        try {
            userService.addUser(newUser);
            System.out.println("Test réussi : utilisateur ajouté avec succès !");
        } catch (Exception e) {
            System.err.println("Test échoué : " + e.getMessage());
        }


//            /* testing gettAllActifUser*/
//        System.out.println(userService.getActifUser());
//
//        /* testing getInactifUser*/
//
//        System.out.println();
//        System.out.println(userService.getInactifUser());
//
//        /* testing getSpeceficUSer*/
//        System.out.println();
//        System.out.println(userService.getSpeceficUser(1));
//
//
//        /* testing cesserUser*/
//        System.out.println();
//        userService.cesserUser(1);


//        /* testing forgetPassword*/
//        System.out.println();
//        authentication.forgetPassword("zayedh80@gmail.com");



        /* testing login*/

//        User user = new User("zayedh80@gmail.com", "d7GLU@vAek");
//        System.out.println();
//        authentication.login(user);
    }
}