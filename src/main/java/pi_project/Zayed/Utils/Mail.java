package pi_project.Zayed.Utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class Mail {

    private static final String FROM_EMAIL = "zayedh80@gmail.com";
    private static final String PASSWORD = "uktbchhdvakaovji";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SUBJECT = "Votre Inscription - EDUCO";
//    private static final String IMAGE_PATH = "/Zayed/images/educo.jpg";
    private static final String IMAGE_FILE_NAME = "educo.jpg";
    private static final String LOGIN_URL = "https://www.educo.com/login";

    public void sendAddUserMail(String toEmail, String userPassword) throws MessagingException {
        sendEmail(toEmail, buildHtmlContent(toEmail, userPassword));
    }

    public void sendForgetPasswordMail(String toEmail, String newPassword) throws MessagingException {
        String content = "<h3>Réinitialisation de votre mot de passe</h3>" +
                "<p>Bonjour,</p>" +
                "<p>Votre mot de passe a été réinitialisé. Voici votre nouveau mot de passe :</p>" +
                "<p><b>" + newPassword + "</b></p>" +
                "<p>Nous vous recommandons de le changer dès votre prochaine connexion.</p>" +
                "<p><a href='" + LOGIN_URL + "'>Connectez-vous ici</a></p>";
        sendEmail(toEmail, content);
    }

    private void sendEmail(String toEmail, String content) throws MessagingException {
        Session session = createSession();
        Message message = createMessage(session, toEmail);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
//        multipart.addBodyPart(createImagePart());

        message.setContent(multipart);
        Transport.send(message);

        System.out.println("Email envoyé avec succès à " + toEmail);
    }

    private Session createSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.trust", SMTP_HOST); // Add trust for Gmail
        properties.put("mail.smtp.connectiontimeout", "5000000000000"); // 5 seconds timeout
        properties.put("mail.smtp.timeout", "5000");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }

    private Message createMessage(Session session, String toEmail) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(SUBJECT);
        return message;
    }

//    private MimeBodyPart createImagePart() throws MessagingException {
//        MimeBodyPart imagePart = new MimeBodyPart();
////        DataSource fds = new FileDataSource(new File(IMAGE_PATH));
//        imagePart.setDataHandler(new DataHandler(fds));
//        imagePart.setFileName(IMAGE_FILE_NAME);
//        return imagePart;
//    }

    private String buildHtmlContent(String toEmail, String userPassword) {
        return "<html>" +
                "<head><style>" +
                "body { font-family: Arial, sans-serif; background-color: #f7f7f7; color: #333; padding: 20px; }" +
                ".container { background-color: #ffffff; border-radius: 8px; padding: 30px; box-shadow: 0 0 15px rgba(0,0,0,0.1); max-width: 600px; margin: auto; }" +
                ".header { background-color: #0046B7; color: white; padding: 15px; text-align: center; border-radius: 8px 8px 0 0; }" +
                ".content { padding: 20px; font-size: 16px; line-height: 1.6; }" +
                ".footer { font-size: 14px; color: #888; text-align: center; margin-top: 20px; }" +
                ".button { background-color: #0046B7; color: white; text-decoration: none; padding: 10px 20px; border-radius: 5px; font-size: 16px; }" +
                "</style></head>" +
                "<body><div class='container'>" +
                "<div class='header'><h2>Bienvenue sur Educo!</h2></div>" +
                "<div class='content'>" +
                "<p>Félicitations ! Votre compte a été créé avec succès.</p>" +
                "<p><b>Voici vos informations de connexion :</b></p>" +
                "<table style='width: 100%;'>" +
                "<tr><td><b>Email :</b></td><td>" + toEmail + "</td></tr>" +
                "<tr><td><b>Mot de passe :</b></td><td>" + userPassword + "</td></tr>" +
                "</table>" +
                "<p>Pour commencer à utiliser votre compte, cliquez ci-dessous :</p>" +
                "<p><a href='" + LOGIN_URL + "' class='button'>Se connecter maintenant</a></p>" +
                "</div>" +
                "<div class='footer'><p>Cordialement,<br>L'équipe Educo</p></div>" +
                "</div></body></html>";
    }



    public void sendMailDeCessation(String toEmail, String motif) throws MessagingException {
        String content = "<h3>Notification de cessation de compte</h3>" +
                "<p>Bonjour,</p>" +
                "<p>Votre compte a été désactivé pour le motif suivant :</p>" +
                "<p><b>" + motif + "</b></p>" +
                "<p>Si vous pensez qu'il s'agit d'une erreur, veuillez contacter notre support.</p>" +
                "<p>Cordialement,<br>L'équipe Educo</p>";
        sendEmail(toEmail, content);
    }

    public void sendMailReactivation(String toEmail, String motif) throws MessagingException {
        String content = "<h3>Notification de réactivation de compte</h3>" +
                "<p>Bonjour,</p>" +
                "<p>Votre compte a été réactivé après la cessation précédente pour le motif :</p>" +
                "<p><b>" + motif + "</b></p>" +
                "<p>Vous pouvez maintenant vous connecter à nouveau à votre compte.</p>" +
                "<p><a href='" + LOGIN_URL + "' class='button'>Se connecter maintenant</a></p>" +
                "<p>Cordialement,<br>L'équipe Educo</p>";
        sendEmail(toEmail, content);
    }
}
