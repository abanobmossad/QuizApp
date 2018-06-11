package com.example.abano.quizyourbrain;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailing {

    private Properties emailProperties;
    private String username;//change accordingly
    private String password;//change accordingly
    private String from;
    private String to;
    private String messageBody;
    private String subject;
    private Context context;
    private Session mailSession;
    private Message emailMessage;

    public Mailing(Context context, String from, String to, String username, String password, String subject, String messageBody) {
        this.messageBody = messageBody;
        this.context = context;
        this.from = from;
        this.to = to;
        this.username = username;
        this.password = password;
        this.subject = subject;

        setMailServerProperties();
        try {
            createEmailMessage();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }


    private void setMailServerProperties() {

        String emailPort = "587";//gmail's smtp port
        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");

    }

    private void createEmailMessage() throws MessagingException {
        String[] toEmails = {to};
        mailSession = Session.getDefaultInstance(emailProperties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        emailMessage = new MimeMessage(mailSession);

        for (String toEmail : toEmails) {
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        }

        emailMessage.setSubject(subject);
        emailMessage.setContent(messageBody, "text/html");//for a html email
        //emailMessage.setText(emailBody);// for a text email

    }

    public void sendEmail() {

        new SendMail().execute();

    }


    private class SendMail extends AsyncTask<Void, Void, Void> {
        protected void onProgressUpdate() {
            //called when the background task makes any progress
        }

        @Override
        protected Void doInBackground(Void... strings) {
            try {
                String emailHost = "smtp.gmail.com";
                Transport transport = mailSession.getTransport("smtp");

                transport.connect(emailHost, username, password);
                transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
                transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPreExecute() {
            //called before doInBackground() is started
            Toast.makeText(context, "Sent message successfully....", Toast.LENGTH_SHORT).show();

        }
        protected void onPostExecute() {
            //called after doInBackground() has finished
        }
    }

}
