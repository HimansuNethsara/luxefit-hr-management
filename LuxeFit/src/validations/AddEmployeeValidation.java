package validations;

import java.awt.Component;
import java.time.LocalDate;
import java.time.Period;
import javax.swing.JOptionPane;

public class AddEmployeeValidation {
    
    private final PasswordValidation passwordValidation = new PasswordValidation();
    private final EmailValidation emailValidation = new EmailValidation();
    private final MobileValidation mobileValidation = new MobileValidation();
    private final NICValidation nicValidation = new NICValidation();
    
    public boolean validateEmployeeForm(Component parentComponent, String name, String nic, String gender, 
                                    LocalDate birthdayLocal, String status, String address1, String address2, 
                                    String department, String role, String id, String password, 
                                    String confirmPassword, String selectedShift, String profilePic) {
        
        Period age = Period.between(birthdayLocal, LocalDate.now());
        int years = age.getYears();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Please enter employee name", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (gender.equals("Select")) {
            JOptionPane.showMessageDialog(parentComponent, "Please select employee gender", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (birthdayLocal == null) {
            JOptionPane.showMessageDialog(parentComponent, "Please select a valid birthday", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (birthdayLocal.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(parentComponent, "Birthday cannot be in the future", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (years < 18) {
            JOptionPane.showMessageDialog(parentComponent, "Employee must be at least 18 years old", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (years > 65) {
            JOptionPane.showMessageDialog(parentComponent, "Employee must be 65 years old or younger", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            int birthYear = birthdayLocal.getYear();
            LocalDate date2016 = LocalDate.of(2016, 1, 1);
            int ageAt2016 = Period.between(birthdayLocal, date2016).getYears();

            boolean isOldNIC = nic.endsWith("v");
            String birthYearStr = String.valueOf(birthYear);

            if (ageAt2016 < 16) {
                if (isOldNIC) {
                    JOptionPane.showMessageDialog(parentComponent, "Employee was under 16 in 2016 and must have a new NIC format ", "Warning", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                String nicYear = nic.substring(0, 4);
                if (!nicYear.equals(birthYearStr)) {
                    JOptionPane.showMessageDialog(parentComponent, "NIC year does not match birth year", "Warning", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } else {
                if (isOldNIC) {
                    String nicYear = nic.substring(0, 2);
                    String birthYearLast2 = birthYearStr.substring(2);
                    if (!nicYear.equals(birthYearLast2)) {
                        JOptionPane.showMessageDialog(parentComponent, "NIC year does not match birth year", "Warning", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                } else {
                    String nicYear = nic.substring(0, 4);
                    if (!nicYear.equals(birthYearStr)) {
                        JOptionPane.showMessageDialog(parentComponent, "NIC year does not match birth year", "Warning", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            }
        }
        
        if (profilePic == null || profilePic.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Please select a profile picture", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }else if (status.equals("Select")) {
            JOptionPane.showMessageDialog(parentComponent, "Please select the employee status", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (address1.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Please enter address line 1", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (address2.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Please enter address line 2", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (department.equals("Select")) {
            JOptionPane.showMessageDialog(parentComponent, "Please select the department", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (role.equals("Select")) {
            JOptionPane.showMessageDialog(parentComponent, "Please select the role", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (id.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Please enter employee ID", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Please confirm the password", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(parentComponent, "Passwords do not match", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (selectedShift == null || selectedShift.equals("Select")) {
            JOptionPane.showMessageDialog(parentComponent, "Please select a shift", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
    
    public boolean validate(String email, String password, String mobile, String nic) {
        
        if (mobile.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter employee mobile number", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!mobileValidation.validate(mobile)) {
            JOptionPane.showMessageDialog(null, "Invalid mobile format", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter employee Email", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!emailValidation.validate(email)) {
            JOptionPane.showMessageDialog(null, "Invalid Email format", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter employee password", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!passwordValidation.validate(password)) {
            JOptionPane.showMessageDialog(null, "Invalid password", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (nic.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter employee nic number", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!nicValidation.validate(nic)) {
            JOptionPane.showMessageDialog(null, "Invalid nic format", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
}
