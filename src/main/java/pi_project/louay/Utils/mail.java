package pi_project.louay.Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class mail {
    private static final String FROM_EMAIL = "zayedh80@gmail.com";
    private static final String PASSWORD = "uktbchhdvakaovji";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SUBJECT = "Votre réclamation - EDUCO";
    private static final String LOGIN_URL = "https://www.educo.com/login";

    public void sendReclamationTraiteeMail(String toEmail, String objetReclamation) throws MessagingException {
        String content = "<h3>Votre réclamation a été traitée</h3>" +
                "<p>Bonjour,</p>" +
                "<p>Nous vous informons que votre réclamation concernant : <b>" + objetReclamation + "</b> a été traitée avec succès.</p>" +
                "<p>Merci de votre confiance et de votre patience.</p>" +
                "<p>Cordialement,<br>L'équipe Educo</p>";
        sendEmail(toEmail, content);
    }

    private void sendEmail(String toEmail, String content) throws MessagingException {
        Session session = createSession();
        Message message = createMessage(session, toEmail);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

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
        properties.put("mail.smtp.ssl.trust", SMTP_HOST);

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
}
