package Model;

import javax.swing.JPanel;

public interface PanelController {
    
    void removePanel();
    void addPanel(JPanel panel);
    
    String getEmployeeID();
    String getEmployeeName();
    String getRole();
    String getProfilePic();
    
}
