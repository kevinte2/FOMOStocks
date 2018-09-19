package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// TODO Class comment
// TODO Timer between windows and L press
// TODO Force computer no sleep or readme note
// TODO Carrier support other than ATT
// TODO Location services/laptop large sound playing
// TODO SQL Database

/**
 * @author Kevin Te
 *
 */
public class MainAlarm extends JFrame implements KeyListener {
    // This number is sent a text if laptop changes state
    // private static final long CONTACT_PHONE_NUMBER = 5098991671L;

    // Destination and sender, respectively
    private static final String CONTACT_EMAIL = "5098991671@txt.att.net";
    private static final String EMAIL_SERVER = "uberfun1997@gmail.com";
    // TODO remove password before push
    private static final String SERVER_PASSWORD = "";

    // These messages sent when computer locks or alarm is activated
    private static final String VERIFY_MESSAGE = "Laptop locked.";
    private static final String ALARM_TEXT =
            "Alert: Laptop moved from location.";

    // True if windows key or "L" key are pressed at least once after startup
    // of program, respectively
    private static boolean windowsKeyPressed = false;
    private static boolean charLPressed = false;

    /**
     * Checks whether or not laptop has been locked continually, and sends a
     * verify text if so. After laptop has been locked, if laptop changes
     * state, an alarm text is sent.
     *
     * TODO: Button integration?
     *
     * @param args - command line arguments, unused
     */
    public static void main(String[] args) {
        new MainAlarm("Key listener");
        while (true) {         // Detect whether laptop has been locked
            if (isWindowsLocked()) {
                // sendText(VERIFY_MESSAGE); // TODO Removed for execution
                while (true) { // Laptop has changed to unsafe state
                    if (hasChangedState()) {
                        // sendText(ALARM_TEXT); // TODO Removed for execution
                        break; // Continue to check if windows is locked again
                    }
                }
            }
        }
    }

    /**
     * Checks and returns if Windows is locked.
     *
     * @return true Windows operating system is in a locked state, otherwise
     *         false
     */
    private static boolean isWindowsLocked() {
        return (windowsKeyPressed && charLPressed);
    }

    /**
     * Checks and returns if laptop has changed state, specifically that the
     * laptop adapter has been unplugged.
     *
     * @return if laptop is in an unsafe state
     */
    private static boolean hasChangedState() {
        return true; // TODO true for testing only
    }

    /**
     * Sends predefined text to predefined email.
     *
     * @param emailBody - actual message in body of email
     */
    private static void sendText(String emailBody) {
        Properties properties = new Properties(); // Start TLS, store port/POP
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_SERVER, SERVER_PASSWORD);
                }
            }
        );

        try {
            Message message = new MimeMessage(session); // Wrapper message
            message.setFrom(new InternetAddress(EMAIL_SERVER)); // From
            message.setRecipients(Message.RecipientType.TO,     // To
                    InternetAddress.parse(CONTACT_EMAIL));

            // Subject field (left out because mobile texting requires no
            // subject line)
            message.setSubject("");
            message.setText(emailBody);

            Transport.send(message);
            System.out.println("Message sent!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author COD3BOY, Kevin Te
     */
    public MainAlarm(String s) {
        super();
        JPanel p = new JPanel(); // GUI object
        add(p);                  // Add GUI to container
        addKeyListener(this);
        setSize(200, 100);       // GUI size
        setVisible(true);        // GUI visibility
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPressed(e, KeyEvent.VK_WINDOWS, windowsKeyPressed); // Windows key
        keyPressed(e, KeyEvent.VK_L, charLPressed);            // L key
    }

    /**
     * Flags whether or not keyboard key pressed corresponds to the keyboard
     * key value.
     *
     * @param e - key event for which key has been pressed
     * @param keyboardKey - value corresponding to keyboard key
     * @param keyPressed - flag whether or not key is pressed
     */
    private void keyPressed(KeyEvent e, int keyboardKey, boolean keyPressed) {
        if (e.getKeyCode() == keyboardKey) { // Flag for key press
            keyPressed = true;
            // System.out.println("Key pressed"); // Debug
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // throw new NotYetImplementedException(); // Unnecessary for execution
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // throw new NotYetImplementedException(); // Unnecessary for execution
    }
}
