package validations;

public class MobileValidation {
    
    private static final String MOBILE_PATTERN = "^07[01245678]{1}[0-9]{7}$";
    
    public boolean validate(String mobile) {
        return mobile != null && mobile.matches(MOBILE_PATTERN);
    }
    
}
