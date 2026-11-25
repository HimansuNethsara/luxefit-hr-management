package Util;

import java.awt.Window;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import raven.toast.Notifications;

public class PanelNotificationUtils {

    private static final Logger LOGGER = SystemLogger.getLogger();

    public static void showSuccess(JPanel panel, String message) {
        showNotification(panel, Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, message);
        LOGGER.info("Success Notification (Panel): " + message);
    }

    public static void showWarning(JPanel panel, String message) {
        showNotification(panel, Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, message);
        LOGGER.warning("Warning Notification (Panel): " + message);
    }

    public static void showError(JPanel panel, String message) {
        showNotification(panel, Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, message);
        LOGGER.severe("Error Notification (Panel): " + message);
    }

    public static void showCustomNotification(JPanel panel, Notifications.Type type, Notifications.Location location, String message) {
        showNotification(panel, type, location, message);
        LOGGER.info(type.name() + " Notification at " + location.name() + ": " + message);
    }

    private static void showNotification(JPanel panel, Notifications.Type type, Notifications.Location location, String message) {
        try {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window instanceof javax.swing.JFrame) {
                javax.swing.JFrame frame = (javax.swing.JFrame) window;
                Notifications.getInstance().setJFrame(frame);
                Notifications.getInstance().show(type, location, message);
            } else {
                throw new IllegalStateException("Panel is not inside a JFrame");
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to show notification from panel: " + e.getMessage());
            JOptionPane.showMessageDialog(panel, message, type.name(), JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
