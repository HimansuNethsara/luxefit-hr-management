package validations;

public class PasswordValidation {
    
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\/-]).{10,}$";

    public boolean validate(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }
    
}
