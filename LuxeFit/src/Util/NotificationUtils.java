package Util;

import java.util.logging.Logger;
import javax.swing.JOptionPane;
import raven.toast.Notifications;

public class NotificationUtils {
    
    private static final Logger LOGGER = SystemLogger.getLogger();

    public static void showSuccess(javax.swing.JFrame parent, String message) {
        showNotification(parent, Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, message);
        LOGGER.info("Success Notification: " + message);
    }

    public static void showWarning(javax.swing.JFrame parent, String message) {
        showNotification(parent, Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, message);
        LOGGER.warning("Warning Notification: " + message);
    }

    public static void showError(javax.swing.JFrame parent, String message) {
        showNotification(parent, Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, message);
        LOGGER.severe("Error Notification: " + message);
    }

    public static void showCustomNotification(javax.swing.JFrame parent, Notifications.Type type, Notifications.Location location, String message) {
        showNotification(parent, type, location, message);
        LOGGER.info(type.name() + " Notification at " + location.name() + ": " + message);
    }

    private static void showNotification(javax.swing.JFrame parent, Notifications.Type type, Notifications.Location location, String message) {
        try {
            Notifications.getInstance().setJFrame(parent); 
            Notifications.getInstance().show(type, location, message);
        } catch (Exception e) {
            LOGGER.severe("Failed to show notification: " + e.getMessage());
            JOptionPane.showMessageDialog(parent, message, type.name(), JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
