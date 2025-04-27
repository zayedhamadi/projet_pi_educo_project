package pi_project.louay.Utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class sms {
    public static final String ACCOUNT_SID = "ACcd3ce9565fdb4475a530755d657a0f8e";
    public static final String AUTH_TOKEN = "c646480e0e44a73af456830c1a43be9c";
    public static final String FROM_PHONE = "+19035641839";

    public sms() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void envoyerSms(String toPhoneNumber, String message) {
        try {
            Message.creator(
                    new com.twilio.type.PhoneNumber(formatPhoneNumber(toPhoneNumber)),
                    new com.twilio.type.PhoneNumber(FROM_PHONE),
                    message
            ).create();
            System.out.println("SMS envoyé à " + toPhoneNumber);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }

    private String formatPhoneNumber(String numero) {
        numero = numero.replaceAll("[^0-9]", "");
        if (numero.startsWith("0")) {
            numero = "+216" + numero.substring(1);
        } else if (!numero.startsWith("+216")) {
            numero = "+216" + numero;
        }
        return numero;
    }
}
