package pi_project.Fedi.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "edsaif5@gmail.com"; // À remplacer par votre email Gmail
    private static final String PASSWORD = "ahuppkzeuewxsuht"; // À remplacer par votre mot de passe d'application Gmail

    public void sendClassAssignmentEmail(String recipientEmail, String teacherName, String className, int roomNumber, int capacity) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Nouvelle affectation de classe");

            String content = String.format(
                "Bonjour %s,\n\n" +
                "Vous avez été affecté(e) à la classe suivante :\n\n" +
                "Nom de la classe : %s\n" +
                "Numéro de salle : %d\n" +
                "Capacité maximale : %d\n\n" +
                "Cordialement,\n" +
                "L'équipe de gestion scolaire",
                teacherName, className, roomNumber, capacity
            );

            message.setText(content);
            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendStudentAssignmentEmail(String parentEmail, String parentName, String studentName, String className) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(parentEmail));
            message.setSubject("Inscription de votre enfant");

            String content = String.format(
                "Bonjour %s,\n\n" +
                "Nous vous confirmons l'inscription de votre enfant %s dans notre établissement.\n\n" +
                "Détails de l'inscription :\n" +
                "Nom de l'élève : %s\n" +
                "Classe : %s\n\n" +
                "Cordialement,\n" +
                "L'équipe de gestion scolaire",
                parentName, studentName, studentName, className
            );

            message.setText(content);
            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + parentEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendStudentPerformanceEmail(String parentEmail, String parentName, String studentName, 
                                          double moyenne, int absences, String className, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(USERNAME));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(parentEmail));
            emailMessage.setSubject("Information sur les performances de votre enfant");

            String performanceLevel;
            if (moyenne >= 15) {
                performanceLevel = "Excellent";
            } else if (moyenne >= 12) {
                performanceLevel = "Bien";
            } else if (moyenne >= 10) {
                performanceLevel = "Moyen";
            } else {
                performanceLevel = "Nécessite une attention particulière";
            }

            String content = String.format(
                "Cher/Chère %s,\n\n" +
                "Nous vous contactons concernant les performances scolaires de votre enfant %s.\n\n" +
                "Détails :\n" +
                "- Classe : %s\n" +
                "- Moyenne actuelle : %.2f/20 (%s)\n" +
                "- Nombre d'absences : %d\n\n" +
                "Message de l'établissement :\n%s\n\n" +
                "Nous vous encourageons à prendre contact avec l'établissement pour discuter des moyens " +
                "d'accompagner au mieux votre enfant dans sa scolarité.\n\n" +
                "Cordialement,\n" +
                "L'équipe pédagogique",
                parentName, studentName, className, moyenne, performanceLevel, absences, message
            );

            emailMessage.setText(content);
            Transport.send(emailMessage);
            System.out.println("Email de performance envoyé avec succès à " + parentEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
} 