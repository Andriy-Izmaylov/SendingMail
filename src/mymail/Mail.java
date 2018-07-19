package mymail;

import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.internet.*;

public class Mail {

    public final static String PASSWORD = "123456789";
    public final static String EMAIL = "izmaylov.andriy@gmail.com";
    public final static String MSG = "Hello";
    public final static String SUB = "test";
    public final static int ONE = 1;

    public static void send(String from, String password, String to, String sub, String msg) throws IOException, AddressException, MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(sub);
            message.setContent(MSG, "text/plain");
            Transport.send(message);

            System.out.println("Message sent successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public static Message getMessageFromMail(String email, String password) {
        Message message = null;
        Properties props = new Properties();
        try {
            props.setProperty("mail.store.protocol", "imaps");
            Session sessions = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });
            Store emailStore = sessions.getStore("imaps");
            emailStore.connect("imap.gmail.com", email, password);
            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            Message[] messages = emailFolder.getMessages();
            message = messages[messages.length - 1];
            System.out.println("Sent Date: " + message.getSentDate());
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Subject match between sent and received mail: " + message.getSubject().equals(SUB));
            System.out.println("From: " + message.getFrom()[0]);
            System.out.println("Recived mail matches with sended: " + (message.getFrom()[0].toString().trim()).equals(EMAIL));
            System.out.println("The number of recipients is coincided: " + ((message.getAllRecipients().length) == ONE));
        } catch (NoSuchProviderException nspe) {
            nspe.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
            System.out.println("Result of reading mail is text type : " + message.getContent());
        } else if (message.isMimeType("multipart/*")) {
            System.out.println("Result of reading mail is multipart : " + result);
        }
        return result;
    }

    public static void main(String[] args) throws IOException, MessagingException {
        send(EMAIL, PASSWORD, EMAIL, SUB, MSG);
        System.out.println("Match text between sent and received mail: " + getTextFromMessage(getMessageFromMail(EMAIL, PASSWORD)).trim().equals(MSG));

    }

}
