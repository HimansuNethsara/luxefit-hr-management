package GUI.HRDepartment;

import Model.MySQL;
import Model.PanelController;
import Util.PanelNotificationUtils;
import Util.SystemLogger;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import validations.AddEmployeeValidation;

public class EmployeeManagement extends javax.swing.JPanel {
    
    private boolean isLoading = false;
    private static final Logger logger = SystemLogger.getLogger();
    private String selectedImagePath = null;
    
    public enum EmploymentStatus {
        ACTIVE, ON_LEAVE, RESIGNED, TERMINATED, RETIRED, PROBATION, SUSPENDED
    }

    private static final HashMap<String,String> GenderMap = new HashMap<>();
    private static final HashMap<String,String> DepartmentMap = new HashMap<>();
    private static final HashMap<String, String> RoleMap = new HashMap<>();
    private static final HashMap<String, String> ShiftMap = new HashMap<>();
    
    private final PanelController panelController;

    public EmployeeManagement(PanelController panelController) {
        initComponents();
        this.panelController = panelController;
        
        datePicker1.setEditor(jFormattedTextField1);
        
        loadGender();
        loadDepartment();
        loadRoles(null);
        loadStatus();
        loadShift();
        loadEmployees(jTextField1.getText());
    }
    
    private void loadStatus() {
        for (EmploymentStatus result : EmploymentStatus.values()) {
            jComboBox1.addItem(result.name());
        }
    }
    
    private void loadGender(){
       
        try {
            
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `gender`");
            
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while(resultSet.next()){
                vector.add(resultSet.getString("name"));
                GenderMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            jComboBox4.setModel(model);
            
        } catch (Exception e) { 
            logger.log(Level.SEVERE, "Failed to load gender from database: {0}", e.getMessage());
        }
        
    }
    
    private void loadShift(){
       try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM shift");
            DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
            ShiftMap.clear();

            while (resultSet.next()) {
                String shiftName = resultSet.getString("name");
                comboBoxModel.addElement(shiftName);
                ShiftMap.put(shiftName, resultSet.getString("id"));
            }

            jComboBoxShift.setModel(comboBoxModel);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load Shifts from database: {0}", e.getMessage());
        }
    }
    
    private void loadDepartment(){
        
        try {
            
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `department`");
            
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while(resultSet.next()){
                vector.add(resultSet.getString("name"));
                DepartmentMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            jComboBox2.setModel(model);
            
        } catch (Exception e) { 
            logger.log(Level.SEVERE, "Failed to load department data from database: {0}", e.getMessage());
        }
        
    }
    
    private void loadRoles(String departmentId) {
        RoleMap.clear();
        try {
            String query = "SELECT r.id, r.name FROM department_has_role dhr " +
                           "JOIN role r ON dhr.role_id = r.id";

            if (departmentId != null && !departmentId.equals("Select")) {
                query += " WHERE dhr.department_id = '" + departmentId + "'";
            }

            ResultSet resultSet = MySQL.executeSearch(query);

            Vector<String> roleVector = new Vector<>();
            roleVector.add("Select");

            while (resultSet.next()) {
                String roleId = resultSet.getString("id");
                String roleName = resultSet.getString("name");
                roleVector.add(roleName);
                RoleMap.put(roleName, roleId);
            }

            jComboBox3.setModel(new DefaultComboBoxModel<>(roleVector));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load roles for selected department: {0}", e.getMessage());
        }
    }

   private void generateEmployeeID() {
        try {
            String department = jComboBox2.getSelectedItem().toString().toUpperCase().trim();
            String role = jComboBox3.getSelectedItem().toString().toUpperCase().trim();

            if (department == null || role == null || department.equals("Select") || role.equals("Select")) {
                PanelNotificationUtils.showWarning(this, "Please select both department and role before generating Employee ID.");
                return;
            }

            String deptCode = department.length() >= 2 ? department.substring(0, 2) : department;
            String roleCode = role.length() >= 3 ? role.substring(0, 3) : role;

            String prefix = deptCode + roleCode;

            String employeeID = "";
            boolean unique = false;

            while (!unique) {
                int randomNum = (int) (Math.random() * 100000); 
                String numPart = String.format("%05d", randomNum);
                employeeID = (prefix + numPart).trim(); 

                ResultSet rs = MySQL.executeSearch("SELECT id FROM employee WHERE id = '" + employeeID + "'");
                if (!rs.next()) {
                    unique = true; 
                }
            }

            employeeID = employeeID.trim(); 
            logger.log(Level.INFO, "Generated Employee ID: {0}", employeeID);
            jTextField3.setText(employeeID);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to generate Employee ID: {0}", e.getMessage());
            PanelNotificationUtils.showError(this, "Failed to generate Employee ID.");
        }
    }

    
    public void loadEmployees(String name) {
        try {
            ResultSet resultSet = MySQL.executeSearch(
                "SELECT e.id, e.name, e.email, e.nic, e.mobile, e.birthday, e.address_line1, e.address_line2, " +
                "g.name AS gender_name, d.name AS department_name, r.name AS role_name " +
                "FROM employee e " +
                "INNER JOIN gender g ON e.gender_id = g.id " +
                "INNER JOIN department_has_role dhr ON e.department_role_id = dhr.id " +
                "INNER JOIN department d ON dhr.department_id = d.id " +
                "INNER JOIN role r ON dhr.role_id = r.id " +
                "WHERE e.name LIKE '" + name + "%'"
            );

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("id"));                          
                vector.add(resultSet.getString("name"));                        
                vector.add(resultSet.getString("email"));                       
                vector.add(resultSet.getString("department_name"));            
                vector.add(resultSet.getString("role_name"));                  
                vector.add(resultSet.getString("gender_name"));                
                vector.add(resultSet.getString("birthday"));                   
                vector.add(resultSet.getString("mobile"));                     
                vector.add(resultSet.getString("nic"));                        
                String address = resultSet.getString("address_line1") + ", " + resultSet.getString("address_line2");
                vector.add(address);                                           

                model.addRow(vector);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load employee data from database.", e);
            JOptionPane.showMessageDialog(this, "Error loading employee data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        datePicker1 = new raven.datetime.component.date.DatePicker();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel21 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jComboBoxShift = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jMenuItem1.setText("Delete");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(1000, 50));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/close.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel1.setText("Employee Management");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 773, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(200, 500));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jButton3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton3.setText("Add");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton4.setText("Update");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton5.setText("Clear");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap(352, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Add & Update", jPanel7);

        jPanel1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel2.setText("Name");

        jTextField1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel3.setText("NIC");

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel17.setText("Gender");

        jComboBox4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jComboBox4KeyReleased(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel19.setText("Profile Picture");

        jToggleButton1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/profile_pic.png"))); // NOI18N
        jToggleButton1.setText("Add Image");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel4.setText("Birthday");

        jLabel25.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel25.setText("Status");

        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jTextField1)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField1)
                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel19)
                            .addComponent(jLabel17)
                            .addComponent(jLabel4)
                            .addComponent(jLabel25))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Personal Info", jPanel1);

        jLabel14.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel14.setText("Mobile");

        jTextField11.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField11KeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel15.setText("Address Line 1");

        jTextField12.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField12KeyReleased(evt);
            }
        });

        jTextField13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel18.setText("Email");

        jTextField14.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField14KeyReleased(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel20.setText("Address Line 2");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField12, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField13)
                    .addComponent(jTextField14)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel20))
                        .addGap(0, 106, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(241, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(" Contact ", jPanel5);

        jLabel13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel13.setText("Department");

        jComboBox2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel22.setText("Role");

        jComboBox3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel23.setText("Shift Assignment");

        jLabel24.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel24.setText("Employee ID");

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel16.setText("Password");

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel21.setText("Confirm Password");

        jComboBoxShift.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBoxShift.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxShiftActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox3, 0, 189, Short.MAX_VALUE)
                    .addComponent(jTextField3)
                    .addComponent(jPasswordField1)
                    .addComponent(jPasswordField2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel22)
                            .addComponent(jLabel24)
                            .addComponent(jLabel16)
                            .addComponent(jLabel21)
                            .addComponent(jLabel23))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jComboBoxShift, javax.swing.GroupLayout.Alignment.TRAILING, 0, 189, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxShift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Employment Details", jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.ipady = 95;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jTabbedPane1, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.LINE_START);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Email", "Department", "Role", "Gender", "Birthday", "Mobile", "NIC", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel4, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.rowAtPoint(evt.getPoint());
        if (row >= 0 && row < jTable1.getRowCount()) {
            jTable1.setRowSelectionInterval(row, row);
            String selectedEmpId = jTable1.getValueAt(row, 0).toString().trim();
            isLoading = true;

            try {
                ResultSet rs = MySQL.executeSearch(
                    "SELECT e.*, g.name AS gender_name, d.name AS department_name, " +
                    "r.name AS role_name, dhr.id AS dhr_id " +
                    "FROM employee e " +
                    "JOIN gender g ON e.gender_id = g.id " +
                    "JOIN department_has_role dhr ON e.department_role_id = dhr.id " +
                    "JOIN department d ON dhr.department_id = d.id " +
                    "JOIN role r ON dhr.role_id = r.id " +
                    "WHERE e.id = '" + selectedEmpId + "'"
                );

                if (rs.next()) {
                    jTextField3.setText(rs.getString("id"));
                    jTextField1.setText(rs.getString("name"));
                    jTextField14.setText(rs.getString("email"));
                    jTextField2.setText(rs.getString("nic"));
                    jTextField11.setText(rs.getString("mobile"));
                    jTextField12.setText(rs.getString("address_line1"));
                    jTextField13.setText(rs.getString("address_line2"));
                    jPasswordField1.setText(rs.getString("password"));
                    jPasswordField2.setText(rs.getString("password"));

                    jComboBox4.setSelectedItem(rs.getString("gender_name")); 
                    jComboBox1.setSelectedItem(rs.getString("status"));       
                    jComboBox2.setSelectedItem(rs.getString("department_name")); 
                    jComboBox3.setSelectedItem(rs.getString("role_name"));       

                    try {
                        LocalDate birthDate = rs.getDate("birthday").toLocalDate();
                        datePicker1.setSelectedDate(birthDate);
                    } catch (SQLException ex) {
                        datePicker1.clearSelectedDate();
                    }

                    selectedImagePath = rs.getString("profile_pic");
                }

                ResultSet shiftRS = MySQL.executeSearch(
                    "SELECT s.name FROM employee_shift es " +
                    "JOIN shift s ON es.shift_id = s.id " +
                    "WHERE es.employee_id = '" + selectedEmpId + "' LIMIT 1"
                );
                if (shiftRS.next()) {
                    jComboBoxShift.setSelectedItem(shiftRS.getString("name"));
                } else {
                    jComboBoxShift.setSelectedIndex(-1); 
                }

                if (evt.getButton() == MouseEvent.BUTTON3 && row >= 0 && row < jTable1.getRowCount()) {
                    jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading full employee details: {0}", e.getMessage());
                JOptionPane.showMessageDialog(this, "Error loading employee details", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                isLoading = false;
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        panelController.removePanel();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        String deptName = (String) jComboBox2.getSelectedItem();
        String deptId = DepartmentMap.get(deptName);
        loadRoles(deptId);

    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        if (isLoading) return; 
        
        String dept = (String) jComboBox2.getSelectedItem();
        String role = (String) jComboBox3.getSelectedItem();

        if (dept != null && !dept.equals("Select") && role != null && !role.equals("Select")) {
            generateEmployeeID();
        }
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
            String uniqueFileName = "emp_" + System.currentTimeMillis() + extension;
            
            File destinationFolder = new File("images/employees");
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs(); 
            }
            File destinationFile = new File(destinationFolder, uniqueFileName);
            
            try {
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = "images/employees/" + uniqueFileName; 
                JOptionPane.showMessageDialog(this, "Profile picture selected successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to copy image file", "Error", JOptionPane.ERROR_MESSAGE);
                 SystemLogger.getLogger().log(Level.SEVERE, "Image copy failed: {0}", ex.getMessage());
            }
         }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        
        try {
            String name = jTextField1.getText().trim();
            String nic = jTextField2.getText().trim();
            String gender =String.valueOf(jComboBox4.getSelectedItem());
            
            LocalDate birthdayLocal = datePicker1.getSelectedDate();  
            Date birthday = Date.from(birthdayLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String formattedBirthday = sdf.format(birthday);
            
            String profilePic = selectedImagePath;
            String status =String.valueOf(jComboBox1.getSelectedItem());
            String email = jTextField14.getText().trim();
            String mobile = jTextField11.getText().trim();
            String address_line1 = jTextField12.getText();
            String address_line2 = jTextField13.getText();
            String department =String.valueOf(jComboBox2.getSelectedItem());
            String role =String.valueOf(jComboBox3.getSelectedItem());
            String id = jTextField3.getText().trim();
            String password = String.valueOf(jPasswordField1.getPassword());
            String confirmPassword = String.valueOf(jPasswordField2.getPassword());
            
            String selectedShift = String.valueOf(jComboBoxShift.getSelectedItem());
            
            AddEmployeeValidation validator = new AddEmployeeValidation();
            if (!validator.validate(email, password, mobile, nic)) return;
            
            boolean validForm = validator.validateEmployeeForm(
                this, name, nic, gender, birthdayLocal, status,
                address_line1, address_line2, department, role,
                id, password, confirmPassword, selectedShift, selectedImagePath
            );
            
            if (!validForm) return;
            
            ResultSet genderRS = MySQL.executeSearch("SELECT id FROM gender WHERE name = '" + gender + "'");
            int genderId = 0;
            if (genderRS.next()) genderId = genderRS.getInt("id");
            
            ResultSet dhrRS = MySQL.executeSearch(
                "SELECT dhr.id FROM department_has_role dhr " +
                "JOIN department d ON d.id = dhr.department_id " +
                "JOIN role r ON r.id = dhr.role_id " +
                "WHERE d.name = '" + department + "' AND r.name = '" + role + "'");
            String departmentRoleId = null;
            if (dhrRS.next()) departmentRoleId = dhrRS.getString("id");

            
            String insertEmp = "INSERT INTO employee " +
                "(id, name, email, nic, mobile, address_line1, address_line2, gender_id, department_role_id, birthday, profile_pic, status, password) " +
                "VALUES ('"+id+"', '"+name+"', '"+email+"', '"+nic+"', '"+mobile+"', '"+address_line1+"', '"+address_line2+"', "+
                genderId + ", " +
                "'" + departmentRoleId + "', " +
                "'" + sdf.format(birthday) + "', " +
                "'" + profilePic.replace("\\", "/") + "', " +
                "'" + status + "', " +
                "'" + password + "')";
            int result = MySQL.executeIUD(insertEmp);
            
            if (result > 0) {
                ResultSet shiftRS = MySQL.executeSearch("SELECT id FROM shift WHERE name = '" + selectedShift + "'");
                if (shiftRS.next()) {
                    int shiftId = shiftRS.getInt("id");
                    MySQL.executeIUD("INSERT INTO employee_shift (employee_id, shift_id) " +
                                     "VALUES ('" + id + "', " + shiftId + ")");
                }
                JOptionPane.showMessageDialog(this, "Employee added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                logger.log(Level.INFO, "Employee added: {0} - {1}", new Object[]{id, name});
                loadEmployees(""); 
                reset();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding employee", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding employee: {0}", e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to add Employee", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        
         loadEmployees(jTextField1.getText());
        
        if (evt.getKeyCode() == 10) {
            jTextField2.grabFocus();
        }
        
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        if (evt.getKeyCode() == 10) {
            jComboBox4.grabFocus();
        }
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jComboBox4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox4KeyReleased
        if (evt.getKeyCode() == 10) {
            datePicker1.grabFocus();
        }
    }//GEN-LAST:event_jComboBox4KeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        reset();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            String name = jTextField1.getText().trim();
            String nic = jTextField2.getText().trim();
            String gender = String.valueOf(jComboBox4.getSelectedItem());

            LocalDate birthdayLocal = datePicker1.getSelectedDate();
            Date birthday = Date.from(birthdayLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedBirthday = sdf.format(birthday);

            String profilePic = selectedImagePath;
            String status = String.valueOf(jComboBox1.getSelectedItem());
            String email = jTextField14.getText().trim();
            String mobile = jTextField11.getText().trim();
            String address_line1 = jTextField12.getText().trim();
            String address_line2 = jTextField13.getText().trim();
            String department = String.valueOf(jComboBox2.getSelectedItem());
            String role = String.valueOf(jComboBox3.getSelectedItem());
            String id = jTextField3.getText().trim();
            String password = String.valueOf(jPasswordField1.getPassword());
            String confirmPassword = String.valueOf(jPasswordField2.getPassword());
            
            String selectedShift = String.valueOf(jComboBoxShift.getSelectedItem());

            AddEmployeeValidation validator = new AddEmployeeValidation();
            if (!validator.validate(email, password, mobile, nic)) return;

            boolean validForm = validator.validateEmployeeForm(
                this, name, nic, gender, birthdayLocal, status,
                address_line1, address_line2, department, role,
                id, password, confirmPassword, selectedShift, selectedImagePath
            );

            if (!validForm) return;

            ResultSet genderRS = MySQL.executeSearch("SELECT id FROM gender WHERE name = '" + gender + "'");
            int genderId = 0;
            if (genderRS.next()) genderId = genderRS.getInt("id");

            ResultSet dhrRS = MySQL.executeSearch("SELECT dhr.id FROM department_has_role dhr JOIN department d ON d.id = dhr.department_id " +
                "JOIN role r ON r.id = dhr.role_id WHERE d.name = '" + department + "' AND r.name = '" + role + "'");
            String departmentRoleId = null;
            if (dhrRS.next()) departmentRoleId = dhrRS.getString("id");

            ResultSet rs = MySQL.executeSearch(
                "SELECT * FROM employee WHERE (nic = '" + nic + "' OR mobile = '" + mobile + "') AND id != '" + id + "'"
            );
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "NIC or mobile already used by another employee", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to update this employee?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String updateEmp = "UPDATE employee SET name = '" + name + "', email = '" + email + "', nic = '" + nic + "', " +
                "mobile = '" + mobile + "', address_line1 = '" + address_line1 + "', address_line2 = '" + address_line2 + "', " +
                "gender_id = " + genderId + ", department_role_id = '" + departmentRoleId + "', birthday = '" + formattedBirthday + "', " +
                "profile_pic = '" + profilePic.replace("\\", "/") + "', status = '" + status + "', password = '" + password + "' " +
                "WHERE id = '" + id + "'";

            int result = MySQL.executeIUD(updateEmp);

            if (result > 0) {
                MySQL.executeIUD("DELETE FROM employee_shift WHERE employee_id = '" + id + "'");

                ResultSet shiftRS = MySQL.executeSearch("SELECT id FROM shift WHERE name = '" + selectedShift + "'");
                if (shiftRS.next()) {
                    int shiftId = shiftRS.getInt("id");
                    MySQL.executeIUD("INSERT INTO employee_shift (employee_id, shift_id) VALUES ('" + id + "', " + shiftId + ")");
                }

                JOptionPane.showMessageDialog(this, "Employee updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                logger.log(Level.INFO, "Employee updated: {0} - {1}", new Object[]{id, name});
                loadEmployees(""); 
                reset(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error updating employee", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating employee: {0}", e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to update Employee", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        int row = jTable1.getSelectedRow();
        if (row != -1) {
            
            String id = String.valueOf(jTable1.getValueAt(row, 0));

            if (id.equals(panelController.getEmployeeID())) {
                JOptionPane.showMessageDialog(this, "You cannot delete your own account while logged in!", "Action Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", 
                                                         "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                try {
                    MySQL.executeIUD("DELETE FROM `employee_shift` WHERE `employee_id` = '" + id + "'");
                    MySQL.executeIUD("DELETE FROM `employee` WHERE `id` = '" + id + "'");
                    
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    model.removeRow(row);

                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    reset();
                    loadEmployees(jTextField1.getText());

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error deleting employee!", e);
                    JOptionPane.showMessageDialog(this, "Please select an employee to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jTextField14KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField14KeyReleased
        if (evt.getKeyCode() == 10) {
            jTextField11.grabFocus();
        }
    }//GEN-LAST:event_jTextField14KeyReleased

    private void jTextField11KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField11KeyReleased
        if (evt.getKeyCode() == 10) {
            jTextField12.grabFocus();
        }
    }//GEN-LAST:event_jTextField11KeyReleased

    private void jTextField12KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyReleased
        if (evt.getKeyCode() == 10) {
            jTextField13.grabFocus();
        }
    }//GEN-LAST:event_jTextField12KeyReleased

    private void jComboBoxShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxShiftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxShiftActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private raven.datetime.component.date.DatePicker datePicker1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBoxShift;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables

    private void reset() {
        jTextField1.setText(""); 
        jTextField2.setText(""); 
        jComboBox4.setSelectedIndex(0); 
        datePicker1.clearSelectedDate();
        selectedImagePath = null; 
        jComboBox1.setSelectedIndex(0); 
        jTextField14.setText("");
        jTextField11.setText(""); 
        jTextField12.setText(""); 
        jTextField13.setText(""); 
        jComboBox2.setSelectedIndex(0); 
        jComboBox3.setSelectedIndex(0); 
        jTextField3.setText("");
        jPasswordField1.setText("");
        jPasswordField2.setText(""); 
        jComboBoxShift.setSelectedIndex(0);
    }

}
