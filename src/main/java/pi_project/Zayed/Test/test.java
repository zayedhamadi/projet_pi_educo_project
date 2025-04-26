package pi_project.Zayed.Test;


import pi_project.Zayed.Entity.User;
import pi_project.Zayed.Enum.EtatCompte;
import pi_project.Zayed.Enum.Genre;
import pi_project.Zayed.Service.AuthenticationImpl;
import pi_project.Zayed.Service.UserImpl;
import pi_project.Zayed.Utils.GeminiRolePredictor;

import java.time.LocalDate;

public class test {
    public static void main(String[] args) throws Exception {
        UserImpl userService = new UserImpl();
        AuthenticationImpl authentication = new AuthenticationImpl();
        User user = new User("zayedh80@gmail.com", "9CpS%SaJpe");
//        /* testing add User */
//        newUser.setId(40);
//        newUser.setNom("Doejjlnomoisdvdsvneee");
//        newUser.setPrenom("Johnnndopojkvsdvjjjnnnnn");
//        newUser.setAdresse("12ssss3 Rue Principale");
//        newUser.setDescription("Utilisateur de test");
//        newUser.setNum_tel(123475359);
//        newUser.setDate_naissance(LocalDate.of(1995, 5, 15));
//        newUser.setEmail("jomnn@example.com");
//        newUser.setImage("imajjjkjbkjbkbkbkge.jpg");
//        newUser.setPassword("passopmopjpjpojopjhvhvidhviodhvword1223333333");
//        newUser.setRoles(Set.of(Role.Enseignant));
//        newUser.setGenre(Genre.femme);
//        try {
//            userService.addUser(newUser);
//            System.out.println("Test réussi : utilisateur ajouté avec succès !");
//        } catch (Exception e) {
//            System.err.println("Test échoué : " + e.getMessage());
//        }
//
//
//        /* testing gettAllActifUser*/
//        System.out.println();
//        System.out.println("testing gettAllActifUser");
//        System.out.println(userService.getActifUser());
//
//        /* testing getInactifUser*/
//        System.out.println();
//        System.out.println("testing getInactifUser");
//        System.out.println(userService.getInactifUser());
//
//        /* testing getSpeceficUSer*/
//        System.out.println();
//        System.out.println();
//        System.out.println("testing getSpeceficUSer");
//        System.out.println(userService.getSpeceficUser(1));


//authentication.forgetPassword("zayedh80@gmail.com");
//authentication.forgetPasswordByNumTel("90264626");
        User testUser = new User();
        testUser.setNom("Ali");
    //    testUser.setPrenom("Ali");
        testUser.setEmail("ali@example.com");
        testUser.setEtat_compte(EtatCompte.active);
        testUser.setGenre(Genre.homme);
        testUser.setDescription("Prof expérimenté dans un lycée public.");
      //  testUser.setAdresse("mrjel");
        //testUser.setDate_naissance(LocalDate.of(1985, 5, 15));
        //testUser.setNum_tel(223456789);
        //testUser.setPassword("password123");
        //testUser.setImage(null);

// Appel API
        String role = GeminiRolePredictor.predictRole(testUser);
        System.out.println("Rôle prédit par Gemini : " + role);


    }
}