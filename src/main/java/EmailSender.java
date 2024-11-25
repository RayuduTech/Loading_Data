import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    public static boolean sentmail(long fileCount, String Fname, int Tr, int Vr, int Ir, String filetimestamp) throws IOException {
        boolean res = false;
        String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        FileReader reader = null;
        Properties p = null;
        try {
            reader = new FileReader("src/main/resources/mail.properties");
            p = new Properties();
            p.load(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        // Recipient's email ID needs to be mentioned.
        String to = p.getProperty("mail.receiver");

        // Sender's email ID and password needs to be mentioned
        String from = p.getProperty("mail.sender.username");
        String password = p.getProperty("mail.sender.password");
        // Assuming you are sending email from through gmail's smtp
        String host = p.getProperty("mail.smtp.host");
        String sub1 = p.getProperty("mail.subject.confirmation");
        String msg1 = p.getProperty("mail.body.confirmation");
        String sub2 = p.getProperty("mail.subject.error");
        String msg2 = p.getProperty("mail.body.error");

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", "465");

        // Get the Session object and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Create the HTML table content
            String htmlContent = "<html><body><table border='1'>" +
                    "<tr><th>S_id</th><th>FILE_NAME</th><th>TOTAL_ROWS</th><th>VALID_ROWS</th><th>INVALID_ROWS</th><th>Record_Insert_TM</th><th>File_REC_TM</th></tr>" +
                    "<tr><td>1</td><td>" + Fname + "</td><td>" + Tr + "</td><td>" + Vr + "</td><td>" + Ir + "</td><td>" + timestamp + "</td><td>" + filetimestamp + "</td></tr>" +
                    "</table></body></html>";

            // Set the subject
            if (fileCount > 0) {
                message.setSubject(sub1 + "_" + timestamp);
                message.setContent(msg1 + "<br><br>" + htmlContent, "text/html");
            } else {
                message.setSubject(sub2 + "_" + timestamp);
                message.setContent(msg2 + "<br><br>" + htmlContent, "text/html");
            }

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
            logger.info("Mail sent successfully! ");
            res = true;
        } catch (MessagingException mex) {
            // Log the exception or take appropriate action
            logger.error("Failed to send email: " + mex.getMessage());
            mex.printStackTrace();
            // Set res to false or take appropriate action if the email sending failed
            res = false;
        }
        return res;
    }
}
