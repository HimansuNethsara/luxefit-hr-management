package validations;

import javax.swing.JOptionPane;

public class SigninValidation {

    private final EmployeeIDValidation idValidation = new EmployeeIDValidation();
    private final PasswordValidation passwordValidation = new PasswordValidation();

    public boolean validate(String id, String password) {
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter your Employee ID", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!idValidation.validate(id)) {
            JOptionPane.showMessageDialog(null, "Invalid Employee ID format", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter your password", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!passwordValidation.validate(password)) {
            JOptionPane.showMessageDialog(null, "Invalid password", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
