package com.scanner.bth.http;

import android.os.Environment;

import com.sun.mail.smtp.SMTPMessage;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * Created by shaon on 6/24/2015.
 */
public class LogMail {
    public static Message buildSimpleMessage(Session session, String payload)

            throws MessagingException, IOException {

        payload = payload.replace("\n", "<br>");
        SMTPMessage m = new SMTPMessage(session);
        MimeMultipart content = new MimeMultipart("related");

        // ContentID is used by both parts
        String cid_top_logo = ContentIdGenerator.getContentId();
        String cid_bottom_logo = ContentIdGenerator.getContentId();

        // HTML part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("<html><head>"
                        + "<title>This is not usually displayed</title>"
                        + "</head>\n"
                        + "<body>"
                        + "<div><img src=\"cid:"
                        + cid_top_logo
                        + "\" /></div>\n"
                        + "<div><p><strong>Report Summary</strong></p></div>\n"

                        + "<div>" + payload + "</div></body></html>"
                        + "<div><img src=\"cid:"
                        + cid_bottom_logo
                        + "\" /></div>\n",
                "US-ASCII", "html");
        content.addBodyPart(textPart);

        // Image part
        File rootPath = new File(Environment.getExternalStorageDirectory(), "mousetrap");
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.attachFile(new File(rootPath, "logo.jpg").getAbsolutePath());
        imagePart.setContentID("<" + cid_top_logo + ">");
        imagePart.setDisposition(MimeBodyPart.INLINE);
        content.addBodyPart(imagePart);


        // Image part
        MimeBodyPart imageBottomPart = new MimeBodyPart();
        imageBottomPart.attachFile(new File(rootPath, "sw_logo.jpg").getAbsolutePath());
        imageBottomPart.setContentID("<" + cid_bottom_logo + ">");
        imageBottomPart.setDisposition(MimeBodyPart.INLINE);
        content.addBodyPart(imageBottomPart);

        m.setContent(content);
        m.setSubject("Report Summary");
        return m;

    }

    public static Session buildGoogleSession() {

        Properties mailProps = new Properties();

        mailProps.put("mail.transport.protocol", "smtp");

        mailProps.put("mail.host", "smtp.gmail.com");

        mailProps.put("mail.from", "example@gmail.com");

        mailProps.put("mail.smtp.starttls.enable", "true");

        mailProps.put("mail.smtp.port", "587");

        mailProps.put("mail.smtp.auth", "true");

        // final, because we're using it in the   closure below

        final PasswordAuthentication usernamePassword =

                new PasswordAuthentication("smartwavedemo@gmail.com", "!QAZXSW@");

        Authenticator auth = new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return usernamePassword;

            }

        };

        Session session = Session.getInstance(mailProps, auth);

        session.setDebug(true);

        return session;

    }

    public static void sendDemoMessage(String payload, String email) throws MessagingException, IOException {
        Session session = buildGoogleSession();
        Message message = buildSimpleMessage(session, payload);
        String cid = ContentIdGenerator.getContentId();
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));



        Transport.send(message);
    }

}
