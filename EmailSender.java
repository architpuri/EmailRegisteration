import org.springframework.mail.SimpleMailMessage;

public class EmailSender {

	static CommonAPIConstants constants;
	
	public static void sendEmail(String email,String subject,String message) {
		// Create a mail sender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(constants.SPRING_MAIL_HOST);
        mailSender.setPort(constants.SPRING_MAIL_PORT);
        mailSender.setUsername(constants.SPRING_MAIL_USERNAME);
        mailSender.setPassword(constants.SPRING_MAIL_PASSWORD);

        // Create an email instance
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(constants.EMAIL_FROM);
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        // Send mail
        mailSender.send(mailMessage);
	}
}
