/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdg.servicios;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.NamingException;

/**
 *
 * @author edwin
 */
@Stateless
@LocalBean
public class CorreoFacade {

    @Resource(name = "jdni/correoSgd")
    private Session jmscorreo;

    private final String correo = "louis.fercho@gmail.com";

    public void enviarCorreo(String correo, String asunto, String cuerpo) throws MessagingException, UnsupportedEncodingException {
        try {
            sendMail(correo, asunto, cuerpo);
        } catch (NamingException ex) {
            Logger.getLogger(CorreoFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendMail(String email, String subject, String body) throws NamingException, MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(jmscorreo);
        message.setSubject(subject);

        message.setFrom(new InternetAddress(
                correo,
                "Autodeclaración Bomberos"));
        message.setRecipients(RecipientType.TO, InternetAddress.parse(email, false));
        Multipart multipart = new MimeMultipart("alternative");
        MimeBodyPart textPart = new MimeBodyPart();
        String textContent = "Bienvenid@";
        textPart.setText(textContent);
        MimeBodyPart htmlPart = new MimeBodyPart();
        String htmlContent = body;
        htmlPart.setContent(htmlContent, "text/html;  charset=utf-8");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);
        message.setContent(multipart);
        Transport.send(message);
    }

    public void enviarCorreoFile(String correo, String motivo, String cuerpo, File pdf) throws MessagingException, UnsupportedEncodingException {
        try {
            sendMailFile(correo, motivo, cuerpo, pdf);
        } catch (NamingException ex) {
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public void sendMailFile(String email, String subject, String body, File pdf)
            throws NamingException, MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(jmscorreo);
        message.setSubject(subject);

        message.setFrom(new InternetAddress(
                correo,
                subject));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        Multipart multipart = new MimeMultipart();
        String textContent = "Bienvenid@";
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(body, "text/html ; charset = ISO-8859-1");
        multipart.addBodyPart(htmlPart);
        DataSource dsPdf = new FileDataSource(pdf);
        // esto es para el pdf 
        htmlPart = new MimeBodyPart();
        htmlPart.setDataHandler(new DataHandler(dsPdf));
        htmlPart.setFileName(pdf.getName());
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);
        Transport.send(message);
    }
}
