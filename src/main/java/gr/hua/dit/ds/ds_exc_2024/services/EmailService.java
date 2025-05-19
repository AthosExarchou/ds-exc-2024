package gr.hua.dit.ds.ds_exc_2024.services;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Apartment;
import gr.hua.dit.ds.ds_exc_2024.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendEmailNotification(String to, String name, Apartment apartment, String emailType) {
        try {
            System.out.println("Sending email to: " + to);

            /* MIME email creation */
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            /* content preparation */
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("apartment", apartment);

            String subject;
            String template;

            switch (emailType) {
                case "tenantApproval":
                    subject = "Your apartment application has been approved";
                    template = "email/application-approved.html";
                    break;
                case "ownerCreated":
                    subject = "Your apartment has been submitted for approval";
                    template = "email/apartment-created.html";
                    break;
                case "adminApproved":
                    subject = "Your apartment has been approved by the administrator";
                    template = "email/apartment-approved-admin.html";
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported email type: " + emailType);
            }

            String htmlContent = templateEngine.process(template, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML content

            mailSender.send(mimeMessage);

            System.out.println("Email sent successfully to: " + to);

        } catch (MailException | MessagingException e) {
            System.err.println("Failed to send email to: " + to);
            e.printStackTrace();
        }
    }

    public void sendUserDetailsChangedEmail(String to, String newUsername, String newEmail,
                                            String oldUsername, String oldEmail,
                                            boolean usernameChanged, boolean emailChanged) {
        try {
            System.out.println("Sending email to: " + to);

            /* MIME email creation */
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            /* content preparation */
            Context context = new Context();
            context.setVariable("newUsername", newUsername);
            context.setVariable("oldUsername", oldUsername);
            context.setVariable("newEmail", newEmail);
            context.setVariable("oldEmail", oldEmail);
            context.setVariable("usernameChanged", usernameChanged);
            context.setVariable("emailChanged", emailChanged);

            String htmlContent = templateEngine.process("email/user-details-edited.html", context);

            helper.setTo(to);
            helper.setSubject("Your account details have been updated");
            helper.setText(htmlContent, true); // HTML content

            mailSender.send(mimeMessage);

            System.out.println("Email sent successfully to: " + to);
        } catch (MailException | MessagingException e) {
            System.err.println("Failed to send email to: " + to);
            e.printStackTrace();
        }
    }

    public void sendApartmentDeletionEmail(String to, Apartment apartment) {
        try {
            System.out.println("Sending email to: " + to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            /* content preparation */
            Context context = new Context();
            context.setVariable("apartment", apartment);
            context.setVariable("ownerName", apartment.getOwner().getUser().getUsername());

            String htmlContent = templateEngine.process("email/apartment-deleted.html", context);

            helper.setTo(to);
            helper.setSubject("Your Apartment Has Been Deleted");
            helper.setText(htmlContent, true); // HTML content

            mailSender.send(mimeMessage);

            System.out.println("Email sent successfully to: " + to);

        } catch (MailException | MessagingException e) {
            System.err.println("Failed to send email to: " + to);
            e.printStackTrace();
        }
    }

    public void sendAccountDeletionEmail(String recipientEmail, User user) {
        try {
            System.out.println("Sending email to: " + recipientEmail);
            String subject = "Your Account Has Been Deleted";

            /* content preparation */
            Context context = new Context();
            context.setVariable("username", user.getUsername());
            String body = templateEngine.process("email/user-account-deleted.html", context);

            /* MIME email creation */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // HTML content

            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            System.err.println("Failed to send email to: " + recipientEmail);
            e.printStackTrace();
        }
    }

    public void sendWelcomeEmail(String recipientEmail, User user) {
        try {
            System.out.println("Sending welcome email to: " + recipientEmail);
            String subject = "Welcome to Our Platform!";

            /* content preparation */
            Context context = new Context();
            context.setVariable("username", user.getUsername());

            String body = templateEngine.process("email/new-user-welcome.html", context);

            /* MIME email creation */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // HTML content

            mailSender.send(message);

            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MailException | MessagingException e) {
            System.err.println("Failed to send welcome email to: " + recipientEmail);
            e.printStackTrace();
        }
    }

}
