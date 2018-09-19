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
import javax.swing.JPanel;

// TODO Timer between windows and L press
// TODO Force computer no sleep/readme.txt note
// TODO Carrier support other than ATT
// TODO Location services/laptop large sound playing
// TODO SQL database

/**
 * MainAlarm allows a user to lock their computer to a location upon lock and
 * be notified via SMS text if their computer changes state.
 *
 * @author Kevin Te
 */
public class MainAlarm extends JFrame implements KeyListener {
    private static final long serialVersionUID = 1L;

    // Destination and sender, respectively
    private static final String CONTACT_EMAIL = "5098991671@txt.att.net";
    private static final String EMAIL_SERVER = "uberfun1997@gmail.com";
    private static final String SERVER_PASSWORD = ""; // Omitted

    // These customizable messages are sent when computer locks or alarm is
    // activated respectively
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
     * @param args - command line arguments, unused
     */
    public static void main(String[] args) {
        new MainAlarm();       // Instantiate key listener
        Kernel32.SYSTEM_POWER_STATUS batteryStatus =
                new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);

        while (true) {         // Detect whether laptop has been locked
            if (isWindowsLocked()) {
                sendText(VERIFY_MESSAGE);
                while (true) { // Laptop has changed to unsafe state
                    if (hasChangedState(batteryStatus)) {
                        sendText(ALARM_TEXT);
                        break; // Continue to check if windows is locked again
                    }
                }
            }
        }
    }

    /**
     * Checks and returns if Windows is locked.
     *
     * @return true if Windows operating system is in a locked state, otherwise
     *         false
     */
    private static boolean isWindowsLocked() {
        return (windowsKeyPressed && charLPressed);
    }

    /**
     * Checks and returns if laptop has changed state, specifically that the
     * laptop adapter has been unplugged. Returns false if laptop is unplugged.
     *
     * @return if laptop is in an unsafe state
     */
    private static boolean
            hasChangedState(Kernel32.SYSTEM_POWER_STATUS batteryStatus) {
        return batteryStatus.getACLineStatusString().equals("Offline");
    }

    /**
     * Sends predefined text to predefined email.
     *
     * @param emailBody - actual message in body of email
     */
    private static void sendText(String emailBody) {
        // Start TLS, store port/POP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Create a session and authenticate password
        Session session = Session.getInstance(properties,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_SERVER, SERVER_PASSWORD);
                }
            }
        );

        try { // Send text message
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
     * MainAlarm constructs a small listener GUI that checks for certain
     * keyboard keys being pressed.
     *
     * @author COD3BOY, Kevin Te
     */
    public MainAlarm() {
        super();
        JPanel p = new JPanel(); // GUI object
        add(p);                  // Add GUI to container
        addKeyListener(this);
        setSize(10, 10);         // GUI size
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
