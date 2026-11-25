package validations;

public class EmployeeIDValidation {

    private static final String ID_PATTERN = "^[A-Z]{5}\\d{5}$"; 

    public boolean validate(String id) {
        return id != null && id.matches(ID_PATTERN);
    }
}
