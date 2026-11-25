package Util;

import GUI.SignIn;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class logoutUtils {
    
     public static void performLogout(JFrame currentFrame) {
        int isConfirmed = JOptionPane.showConfirmDialog(
                currentFrame,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (isConfirmed == JOptionPane.YES_OPTION) {
            new SignIn().setVisible(true);
            currentFrame.dispose();
        }
    }

    
}
