package Util;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ThemeUtils {

    public enum ThemeMode {
        LIGHT, DARK
    }

    private static ThemeMode currentTheme = ThemeMode.LIGHT;

    public static void applyLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            currentTheme = ThemeMode.LIGHT;
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void applyDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            currentTheme = ThemeMode.DARK;
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static ThemeMode getCurrentTheme() {
        return currentTheme;
    }

    public static void applyCurrentTheme() {
        if (currentTheme == ThemeMode.DARK) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
    }
}
