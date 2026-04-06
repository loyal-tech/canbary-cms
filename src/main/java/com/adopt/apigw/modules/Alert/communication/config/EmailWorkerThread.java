package com.adopt.apigw.modules.Alert.communication.config;

import lombok.SneakyThrows;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Repository.CommunicationRepository;
import com.adopt.apigw.modules.Communication.domain.Communication;
import com.adopt.apigw.spring.SpringContext;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/*@Component*/
public class EmailWorkerThread implements Runnable {
    @Override
    public void run() {
        System.out.println("");
    }

//
//    private static final String MODULE = " [EMAIL CONFIG THREAD] ";
//    private final Communication communication;
//    private final CommunicationRepository communicationRepository;
//
//    public EmailWorkerThread(Communication communication) {
//        this.communication = communication;
//        this.communicationRepository = SpringContext.getBean(CommunicationRepository.class);
//    }
//
//    @SneakyThrows
//    @Override
//    public void run() {
//        sendMail(communication);
//    }
//
//    public void sendMail(Communication communication) throws Exception {
//        final String username = CommunicationConstant.COMM_EMAIL_USERNAME;
//        final String password = CommunicationConstant.COMM_EMAIL_PASSWORD;
//        Properties prop = new Properties();
//        prop.put("mail.smtp.host", CommunicationConstant.COMM_EMAIL_HOST);
//        prop.put("mail.smtp.port", CommunicationConstant.COMM_EMAIL_PORT);
//        prop.put("mail.smtp.auth", true);
//        prop.put("mail.smtp.starttls.enable", true); //TLS
//
//        Session session = Session.getInstance(prop, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        try {
//
//            Message message = new MimeMessage(session);
//
//            MimeMessageHelper helper = new MimeMessageHelper((MimeMessage) message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//                    StandardCharsets.UTF_8.name());
//            message.setFrom(new InternetAddress("Adopt"));
//            message.setRecipients(
//                    Message.RecipientType.TO,
//                    InternetAddress.parse(communication.getEmail())
//            );
//            message.setSubject(communication.getSubject());
//
//            helper.setTo(communication.getEmail());
//            helper.setText(communication.getEmailBody(), true);
//            helper.setSubject(communication.getSubject());
//            Transport.send(message);
//            communicationRepository.updateCommunication(communication.getUuid(), true, null);
//        } catch (Exception e) {
//            communicationRepository.updateCommunication(communication.getUuid(), true, e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
