package pi_project.Zayed.Utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMS {
//    private static final Logger logger = LoggerFactory.getLogger(SMS.class);
//    private static final String Account_SID = "AC61e18db9f9f3e880d26de05ba3265717";
//    private static final String FROM_PHONE = "+16202675477";
//    private static final String PASSWORD = "f163fac02d21536569cd5e5f62cab3e4";

    private static final Logger logger = LoggerFactory.getLogger(SMS.class);
    private static final String Account_SID = "AC61e18db9f9f3e880d26de05ba3265717";
    private static final String FROM_PHONE = "+16202675477";
    private static final String PASSWORD = "a4bc8d29fb4560da2ff419cf58df5699";
    public SMS() {
        Twilio.init(Account_SID, PASSWORD);
    }

    public void envoyerSms(String toPhoneNumber, String message) {
        try {
            Message.creator(
                    new com.twilio.type.PhoneNumber(formatPhoneNumber(toPhoneNumber)),
                    new com.twilio.type.PhoneNumber(FROM_PHONE),
                    message
            ).create();
            logger.info("✅ SMS envoyé à {} | SID: {}");
        } catch (Exception e) {
            logger.error("❌ Erreur d'envoi SMS : {}", e.getMessage());
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