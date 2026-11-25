package validations;

public class EmailValidation {
    
    private static final String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public boolean validate(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }
    
}
