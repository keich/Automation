package ru.keich.mon.automation.scripting;

import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;

public class MailManager {

	private final JavaMailSender mailSender;

	private final String ADD_TO = "to";
	private final String ADD_BCC = "bcc";
	private final String ADD_CC = "cc";

	public MailManager(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void send(Map<String, String> address, String from, String subject, String text, boolean html) throws MessagingException {
		var message = mailSender.createMimeMessage();
		var helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setTo(address.get(ADD_TO));
		if (address.containsKey(ADD_BCC)) {
			helper.setBcc(text);
		}
		if (address.containsKey(ADD_CC)) {
			helper.setCc(text);
		}
		helper.setSubject(subject);
		helper.setText(text, html);
		mailSender.send(message);
	}

}
