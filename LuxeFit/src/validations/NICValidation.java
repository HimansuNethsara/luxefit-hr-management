package validations;

public class NICValidation {
    
    public boolean validate(String nic) {
        if (nic == null) return false;

        nic = nic.trim();

        if (nic.toLowerCase().endsWith("v")) {
            return nic.length() == 10 && nic.substring(0, 9).matches("\\d+");
        } else {
            return nic.length() == 12 && nic.matches("\\d+");
        }
    }
    
}
