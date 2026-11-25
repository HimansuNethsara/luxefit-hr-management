package Util;

import java.awt.Window;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import raven.toast.Notifications;
import javax.swing.JDialog;

public class DialogNotificationUtils {

    private static final Logger LOGGER = SystemLogger.getLogger();

    public static void showSuccess(JDialog dialog, String message) {
        showNotification(dialog, Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, message);
        LOGGER.info("Success Notification (Dialog): " + message);
    }

    public static void showWarning(JDialog dialog, String message) {
        showNotification(dialog, Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, message);
        LOGGER.warning("Warning Notification (Dialog): " + message);
    }

    public static void showError(JDialog dialog, String message) {
        showNotification(dialog, Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, message);
        LOGGER.severe("Error Notification (Dialog): " + message);
    }

    public static void showCustomNotification(JDialog dialog, Notifications.Type type, Notifications.Location location, String message) {
        showNotification(dialog, type, location, message);
        LOGGER.info(type.name() + " Notification at " + location.name() + ": " + message);
    }

    private static void showNotification(JDialog dialog, Notifications.Type type, Notifications.Location location, String message) {
        try {
            Window window = SwingUtilities.getWindowAncestor(dialog);
            if (window instanceof javax.swing.JFrame) {
                javax.swing.JFrame frame = (javax.swing.JFrame) window;
                Notifications.getInstance().setJFrame(frame);
                Notifications.getInstance().show(type, location, message);
            } else {
                throw new IllegalStateException("Dialog is not inside a JFrame");
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to show notification from dialog: " + e.getMessage());
            JOptionPane.showMessageDialog(dialog, message, type.name(), JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
}
