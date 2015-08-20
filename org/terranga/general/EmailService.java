package org.terranga.general;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailService {

	
	public EmailService(){
		
	}
	
	
	public static boolean sendEmail(String recipient, String content, String subject){
		boolean confirmation = false;
		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	    try {
	        Message msg = new MimeMessage(session);
            InternetAddress from = new InternetAddress("info@terranga.org", "Terranga");
            msg.setFrom(from);
            InternetAddress to = new InternetAddress(recipient, recipient);
            msg.addRecipient(Message.RecipientType.TO, to);
            msg.setSubject(subject);
            
            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html");
            mp.addBodyPart(htmlPart);
            
            msg.setContent(mp);
            Transport.send(msg);
            return true;

	    }
        catch (UnsupportedEncodingException e){ 
        	e.printStackTrace(); 
        	confirmation = false;
        }
	    catch (AddressException e) {
        	e.printStackTrace(); 
        	confirmation = false;
	    }
	    catch (MessagingException e) {
        	e.printStackTrace(); 
        	confirmation = false;
	    }	
	    return confirmation;
	}

}
