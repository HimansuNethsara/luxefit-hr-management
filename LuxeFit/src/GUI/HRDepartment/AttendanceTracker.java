package GUI.HRDepartment;

import Model.MySQL;
import Model.PanelController;
import Util.PanelNotificationUtils;
import Util.SystemLogger;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import validations.EmployeeIDValidation;

public final class AttendanceTracker extends javax.swing.JPanel {

    private final PanelController panelController;
    private final EmployeeIDValidation idValidation = new EmployeeIDValidation();
    
    private static final Logger logger = SystemLogger.getLogger();
    
    private static HashMap<String,String> DepartmentMap = new HashMap<>();
    private static HashMap<String, String> RoleMap = new HashMap<>();
    private static HashMap<String, String> ShiftMap = new HashMap<>();
    
    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, ON_LEAVE
    }

    public enum Months {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    }

    
    public AttendanceTracker(PanelController panelController) {
        initComponents();
        this.panelController = panelController;
        
        loadPickers();
        loadStatus(); 
        loadMonth();
        loadDepartment();
        loadRoles(null);
        loadShift();
        loadCheckInLogs(null);
        loadAllAttendanceRecords();
    }
    
    private void loadPickers(){
        datePicker1.setEditor(jFormattedTextField6);
        datePicker2.setEditor(jFormattedTextField7);
        datePicker3.setEditor(jFormattedTextField8);
        
        timePicker1.set24HourView(true);
        timePicker1.setOrientation(SwingConstants.HORIZONTAL);
        timePicker1.setEditor(jFormattedTextField2);
        
        timePicker2.set24HourView(true);
        timePicker2.setOrientation(SwingConstants.HORIZONTAL);
        timePicker2.setEditor(jFormattedTextField3);
        
        timePicker3.set24HourView(true);
        timePicker3.setOrientation(SwingConstants.HORIZONTAL);
        timePicker3.setEditor(jFormattedTextField4);
        
        timePicker4.set24HourView(true);
        timePicker4.setOrientation(SwingConstants.HORIZONTAL);
        timePicker4.setEditor(jFormattedTextField5);
        
        LocalDate today = LocalDate.now();
        datePicker1.setSelectedDate(today);
        
        int currentYear = LocalDate.now().getYear();
        SpinnerNumberModel yearModel = new SpinnerNumberModel(currentYear, 1900, 2100, 1);
        yearSpinner.setModel(yearModel);
        yearSpinner1.setModel(yearModel);
        
        JSpinner.NumberEditor editor1 = new JSpinner.NumberEditor(yearSpinner, "####");
        yearSpinner.setEditor(editor1);

        JSpinner.NumberEditor editor2 = new JSpinner.NumberEditor(yearSpinner1, "####");
        yearSpinner1.setEditor(editor2);
        
        datePicker2.addPropertyChangeListener("selectedDate", evt -> {
            LocalDate selectedDate = datePicker2.getSelectedDate();
            boolean hasDate = selectedDate != null;
            jComboBox8.setEnabled(!hasDate);
            yearSpinner.setEnabled(!hasDate);
        });
    }
    
    private void loadStatus() {
        jComboBox6.removeAllItems();
        for (AttendanceStatus result : AttendanceStatus.values()) {
            jComboBox6.addItem(result.name());
        }
    }
    
    private void loadMonth() {
        for (Months months : Months.values()) {
            jComboBox8.addItem(months.name());
            jComboBox11.addItem(months.name());
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
            
            DefaultComboBoxModel model1 = new DefaultComboBoxModel<>(new Vector<>(vector));
            DefaultComboBoxModel model2 = new DefaultComboBoxModel<>(new Vector<>(vector));
            DefaultComboBoxModel model3 = new DefaultComboBoxModel<>(new Vector<>(vector));

            jComboBox2.setModel(model1);
            jComboBox10.setModel(model2);
            jComboBox13.setModel(model3);
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

            jComboBox4.setModel(comboBoxModel);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load Shifts from database: {0}", e.getMessage());
        }
    }
        
    public void loadCheckInLogs(String empId) {
        try {
            String query = "SELECT " +
                "l.date AS log_date, " +
                "e.id, e.name, d.name AS department, r.name AS role, " +
                "s.name AS shift_name, s.start_time, s.end_time, " +
                "l.check_in, l.check_out " +
                "FROM employee_checkin_log l " +
                "INNER JOIN employee e ON e.id = l.employee_id " +
                "LEFT JOIN department_has_role dhr ON e.department_role_id = dhr.id " +
                "LEFT JOIN department d ON dhr.department_id = d.id " +
                "LEFT JOIN role r ON dhr.role_id = r.id " +
                "LEFT JOIN employee_shift es ON es.employee_id = e.id " +
                "LEFT JOIN shift s ON s.id = es.shift_id ";

            if (empId != null && !empId.trim().isEmpty()) {
                query += "WHERE e.id LIKE '" + empId + "%' ";
            } else {
                query += "WHERE l.date = CURDATE() ";
            }

            query += "ORDER BY l.date DESC, l.check_in ASC";

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("department"));
                row.add(rs.getString("role"));
                row.add(rs.getString("shift_name"));
                row.add(rs.getTime("start_time") != null ? rs.getTime("start_time").toString() : "");
                row.add(rs.getTime("end_time") != null ? rs.getTime("end_time").toString() : "");
                row.add(rs.getTime("check_in") != null ? rs.getTime("check_in").toString() : "");
                row.add(rs.getTime("check_out") != null ? rs.getTime("check_out").toString() : "");
                row.add(rs.getDate("log_date") != null ? rs.getDate("log_date").toString() : "");

                model.addRow(row);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load check-in logs", e);
            JOptionPane.showMessageDialog(null, "Error loading check-in logs", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadAllAttendanceRecords() {
        try {
            String query = "SELECT " +
                "e.id AS employee_id, " +
                "e.name AS employee_name, " +
                "d.name AS department_name, " +
                "r.name AS role_name, " +
                "a.date, " +
                "s.name AS shift_name, " +
                "a.check_in, " +
                "a.check_out, " +
                "a.status " +
                "FROM attendance a " +
                "INNER JOIN employee e ON e.id = a.employee_id " +
                "LEFT JOIN department_has_role dhr ON e.department_role_id = dhr.id " +
                "LEFT JOIN department d ON dhr.department_id = d.id " +
                "LEFT JOIN role r ON dhr.role_id = r.id " +
                "LEFT JOIN employee_shift es ON es.employee_id = e.id " +
                "LEFT JOIN shift s ON es.shift_id = s.id " +
                " WHERE a.date = CURDATE()\n" +
"            ORDER BY a.check_in ASC";

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel(); 
            model.setRowCount(0); 

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("employee_name"));
                row.add(rs.getString("department_name"));
                row.add(rs.getString("role_name"));
                row.add(rs.getDate("date") != null ? rs.getDate("date").toString() : "");
                row.add(rs.getString("shift_name"));
                row.add(rs.getTime("check_in") != null ? rs.getTime("check_in").toString() : "");
                row.add(rs.getTime("check_out") != null ? rs.getTime("check_out").toString() : "");
                row.add(rs.getString("status"));

                model.addRow(row);
            }
            
            int statusColumnIndex = 8; 
            Util.TableRowColorRenderer renderer = new Util.TableRowColorRenderer(statusColumnIndex);
            for (int i = 0; i < jTable2.getColumnCount(); i++) {
                jTable2.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }


        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load attendance records", e);
            JOptionPane.showMessageDialog(this, "Error loading attendance data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        datePicker1 = new raven.datetime.component.date.DatePicker();
        datePicker2 = new raven.datetime.component.date.DatePicker();
        timePicker1 = new raven.datetime.component.time.TimePicker();
        timePicker2 = new raven.datetime.component.time.TimePicker();
        timePicker3 = new raven.datetime.component.time.TimePicker();
        timePicker4 = new raven.datetime.component.time.TimePicker();
        datePicker3 = new raven.datetime.component.date.DatePicker();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        jFormattedTextField4 = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        jFormattedTextField5 = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jFormattedTextField6 = new javax.swing.JFormattedTextField();
        jComboBox4 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox<>();
        filterSearch = new javax.swing.JButton();
        jFormattedTextField7 = new javax.swing.JFormattedTextField();
        yearSpinner = new javax.swing.JSpinner();
        btncheckout3 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jFormattedTextField8 = new javax.swing.JFormattedTextField();
        jLabel28 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jComboBox13 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        attendanceSearch = new javax.swing.JButton();
        btnfilter2 = new javax.swing.JButton();
        yearSpinner1 = new javax.swing.JSpinner();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

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
        jLabel1.setText("Attendance Tracker");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 801, Short.MAX_VALUE)
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

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(250, 623));

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel2.setText("Employee ID");

        jTextField1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel3.setText("Employee Name");

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel13.setText("Department");

        jComboBox2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel14.setText("Role");

        jComboBox3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel15.setText("Shift Name");

        jLabel16.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel16.setText("Shift Start Time");

        jFormattedTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField2ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel17.setText("Shift End Time");

        jFormattedTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField3ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel18.setText("Check In Time");

        jFormattedTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField4ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel19.setText("Check Out Time");

        jFormattedTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField5ActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel21.setText("Final Status");

        jComboBox6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));

        jButton3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton3.setText("Add");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton5.setText("Clear");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel22.setText("Date");

        jFormattedTextField6.setEditable(false);
        jFormattedTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField6ActionPerformed(evt);
            }
        });

        jComboBox4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel14)
                                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(8, 8, 8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFormattedTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5)
                    .addComponent(jButton3))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel1, java.awt.BorderLayout.LINE_START);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setPreferredSize(new java.awt.Dimension(700, 100));

        jLabel23.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel23.setText("Date :");

        jLabel24.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel24.setText("Month :");

        jComboBox8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        jComboBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox8ActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel25.setText("Year :");

        jLabel26.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel26.setText("Department :");

        jComboBox10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox10ActionPerformed(evt);
            }
        });

        filterSearch.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        filterSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/search.png"))); // NOI18N
        filterSearch.setText("Search");
        filterSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterSearchActionPerformed(evt);
            }
        });

        yearSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                yearSpinnerStateChanged(evt);
            }
        });

        btncheckout3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btncheckout3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/refresh.png"))); // NOI18N
        btncheckout3.setText("Refresh");
        btncheckout3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncheckout3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox8, 0, 109, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField7))
                .addGap(48, 48, 48)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox10, 0, 109, Short.MAX_VALUE)
                    .addComponent(yearSpinner))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncheckout3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jLabel25)
                            .addComponent(filterSearch)
                            .addComponent(yearSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jFormattedTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24)
                        .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26)
                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btncheckout3))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Name", "Department", "Role", "Shift Name", "Shift Start", "Shift End", "Check-In", "Check-Out", "Date"
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

        jPanel6.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel2, java.awt.BorderLayout.CENTER);

        jTabbedPane2.addTab("Record Attendace", jPanel5);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jLabel27.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel27.setText("Date :");

        jLabel28.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel28.setText("Month :");

        jComboBox11.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        jComboBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox11ActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel29.setText("Department :");

        jLabel30.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel30.setText("Year :");

        jComboBox13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox13ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel4.setText("Employee ID :");

        jTextField3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        attendanceSearch.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        attendanceSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/search.png"))); // NOI18N
        attendanceSearch.setText("Search");
        attendanceSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceSearchActionPerformed(evt);
            }
        });

        btnfilter2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnfilter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/refresh.png"))); // NOI18N
        btnfilter2.setText("Refresh");
        btnfilter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnfilter2ActionPerformed(evt);
            }
        });

        yearSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                yearSpinner1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addGap(24, 24, 24)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox11, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30))
                .addGap(28, 28, 28)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox13, 0, 109, Short.MAX_VALUE)
                    .addComponent(yearSpinner1))
                .addGap(49, 49, 49)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attendanceSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnfilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(jLabel30)
                        .addComponent(jLabel4)
                        .addComponent(attendanceSearch)
                        .addComponent(yearSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jFormattedTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel29))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnfilter2))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jPanel9.setPreferredSize(new java.awt.Dimension(400, 389));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 402, Short.MAX_VALUE)
        );

        jPanel7.add(jPanel9, java.awt.BorderLayout.LINE_START);

        jPanel10.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Name", "Department", "Role", "Date", "Shift", "Check-In", "Check-Out", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel10.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel7.add(jPanel10, java.awt.BorderLayout.CENTER);

        jTabbedPane2.addTab("View Attendance Records", jPanel7);

        add(jTabbedPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        panelController.removePanel();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jFormattedTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField2ActionPerformed

    private void jFormattedTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField3ActionPerformed

    private void jFormattedTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField4ActionPerformed

    private void jFormattedTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField5ActionPerformed

    private void jFormattedTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField6ActionPerformed

    private void jComboBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox10ActionPerformed

    private void jComboBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox11ActionPerformed

    private void jComboBox13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox13ActionPerformed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3KeyReleased

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        String deptName = (String) jComboBox2.getSelectedItem();
        String deptId = DepartmentMap.get(deptName);
        loadRoles(deptId);
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        clear();
        
        int row = jTable1.rowAtPoint(evt.getPoint());
        if (row >= 0 && row < jTable1.getRowCount()) {
            jTable1.setRowSelectionInterval(row, row);

            jTextField1.setText(jTable1.getValueAt(row, 0).toString());
            jTextField2.setText(jTable1.getValueAt(row, 1).toString());
            
            String department = jTable1.getValueAt(row, 2) != null ? jTable1.getValueAt(row, 2).toString() : "";
            jComboBox2.setSelectedItem(department);
            String role = jTable1.getValueAt(row, 3) != null ? jTable1.getValueAt(row, 3).toString() : "";
            jComboBox3.setSelectedItem(role);
            String shiftName = jTable1.getValueAt(row, 4) != null ? jTable1.getValueAt(row, 4).toString() : "";
            jComboBox4.setSelectedItem(shiftName);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            try {
                String start = jTable1.getValueAt(row, 5) != null ? jTable1.getValueAt(row, 5).toString() : null;
                timePicker1.setSelectedTime(start != null ? LocalTime.parse(start, timeFormatter) : null);

                String end = jTable1.getValueAt(row, 6) != null ? jTable1.getValueAt(row, 6).toString() : null;
                timePicker2.setSelectedTime(end != null ? LocalTime.parse(end, timeFormatter) : null);

                String checkIn = jTable1.getValueAt(row, 7) != null ? jTable1.getValueAt(row, 7).toString() : null;
                timePicker3.setSelectedTime(checkIn != null ? LocalTime.parse(checkIn, timeFormatter) : null);

                String checkOut = jTable1.getValueAt(row, 8) != null ? jTable1.getValueAt(row, 8).toString() : null;
                if (checkOut != null) {
                    timePicker4.setSelectedTime(LocalTime.parse(checkOut, timeFormatter));
                } else {
                    timePicker4.clearSelectedTime(); 
                }

                
                String dateValue = jTable1.getValueAt(row, 9) != null ? jTable1.getValueAt(row, 9).toString() : null;
                if (dateValue != null) {
                    datePicker1.setSelectedDate(LocalDate.parse(dateValue)); 
                } else {
                    datePicker1.clearSelectedDate(); 
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Time parsing error", e);
            }
            
            loadStatus();
            jComboBox6.setSelectedItem("PRESENT");
        }
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void filterSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterSearchActionPerformed
  
        String baseQuery =
            "SELECT l.date, " +  
            "e.id, e.name, d.name AS department, r.name AS role, " +
            "s.name AS shift_name, s.start_time, s.end_time, " +
            "l.check_in, l.check_out " +
            "FROM employee_checkin_log l " +
            "INNER JOIN employee e ON e.id = l.employee_id " +
            "LEFT JOIN department_has_role dhr ON e.department_role_id = dhr.id " +
            "LEFT JOIN department d ON dhr.department_id = d.id " +
            "LEFT JOIN role r ON dhr.role_id = r.id " +
            "LEFT JOIN employee_shift es ON es.employee_id = e.id " +
            "LEFT JOIN shift s ON s.id = es.shift_id " +
            "WHERE 1 = 1 ";

        String filter = "";

        if (datePicker2.getSelectedDate() != null) {
            LocalDate selectedDate = datePicker2.getSelectedDate();
            filter += "AND l.date = '" + Date.valueOf(selectedDate) + "' ";
        } else {
            if (jComboBox8.getSelectedIndex() > 0) {
                int month = jComboBox8.getSelectedIndex();
                filter += "AND MONTH(l.date) = " + month + " ";
            }

            int year = (int) yearSpinner.getValue();
            if (year > 0) {
                filter += "AND YEAR(l.date) = " + year + " ";
            }
        }

        if (jComboBox10.getSelectedIndex() > 0) {
            String dept = jComboBox10.getSelectedItem().toString();
            filter += "AND d.name = '" + dept + "' ";
        }

        String finalQuery = baseQuery + filter + "ORDER BY l.date DESC";

        try {
            ResultSet rs = MySQL.executeSearch(finalQuery);

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("department"));
                row.add(rs.getString("role"));
                row.add(rs.getString("shift_name") != null ? rs.getString("shift_name") : "");
                row.add(rs.getTime("start_time") != null ? rs.getTime("start_time").toString() : "");
                row.add(rs.getTime("end_time") != null ? rs.getTime("end_time").toString() : "");
                row.add(rs.getTime("check_in") != null ? rs.getTime("check_in").toString() : "");
                row.add(rs.getTime("check_out") != null ? rs.getTime("check_out").toString() : "");
                row.add(rs.getDate("date") != null ? rs.getDate("date").toString() : ""); // <- added date
                model.addRow(row);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error filtering attendance data", e);
            JOptionPane.showMessageDialog(this, "Failed to load filtered results", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_filterSearchActionPerformed

    private void jComboBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox8ActionPerformed
        if (jComboBox8.getSelectedIndex() > 0) {
            datePicker2.clearSelectedDate();
            datePicker2.setEnabled(false);
        } else {
            datePicker2.setEnabled(true);
        }
    }//GEN-LAST:event_jComboBox8ActionPerformed

    private void yearSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yearSpinnerStateChanged
        if (jComboBox8.getSelectedIndex() > 0) {
            datePicker2.clearSelectedDate();
            datePicker2.setEnabled(false);
        } else {
            datePicker2.setEnabled(true);
        }
    }//GEN-LAST:event_yearSpinnerStateChanged

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        clear();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btncheckout3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncheckout3ActionPerformed
       clearfilter();
    }//GEN-LAST:event_btncheckout3ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String empId = jTextField1.getText().trim();
        if (!empId.isEmpty()) {
            loadCheckInLogs(empId);
        }
        
        try {
            ResultSet rs = MySQL.executeSearch(
                "SELECT " +
                "e.name AS employee_name, d.name AS department_name, " +
                "r.name AS role_name, s.name AS shift_name, " +
                "s.start_time, s.end_time " +
                "FROM employee e " +
                "JOIN department_has_role dhr ON e.department_role_id = dhr.id " +
                "JOIN department d ON dhr.department_id = d.id " +
                "JOIN role r ON dhr.role_id = r.id " +
                "LEFT JOIN employee_shift es ON e.id = es.employee_id " +
                "LEFT JOIN shift s ON es.shift_id = s.id " +
                "WHERE e.id = '" + empId + "'"
            );

            if (rs.next()) {
                jTextField2.setText(rs.getString("employee_name"));
                jComboBox2.setSelectedItem(rs.getString("department_name")); 
                jComboBox3.setSelectedItem(rs.getString("role_name"));      
                jComboBox4.setSelectedItem(rs.getString("shift_name"));     

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                String start = rs.getString("start_time");
                timePicker1.setSelectedTime(start != null ? LocalTime.parse(start, formatter) : null);

                String end = rs.getString("end_time");
                timePicker2.setSelectedTime(end != null ? LocalTime.parse(end, formatter) : null);
                
                loadCheckInLogs(empId);
            } else {
                jTextField2.setText("");
                jComboBox2.setSelectedIndex(-1);
                jComboBox3.setSelectedIndex(-1);
                jComboBox4.setSelectedIndex(-1);
                timePicker1.clearSelectedTime();
                timePicker2.clearSelectedTime();
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading employee info for manual attendance entry", e);
            JOptionPane.showMessageDialog(this, "Error fetching employee data", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            String employeeId = jTextField1.getText().trim();
            LocalDate selectedDate = datePicker1.getSelectedDate();
            String status = jComboBox6.getSelectedItem() != null ? jComboBox6.getSelectedItem().toString() : null;

            LocalTime shiftStart = timePicker1.getSelectedTime();
            LocalTime shiftEnd = timePicker2.getSelectedTime();
            LocalTime checkIn = timePicker3.getSelectedTime();
            LocalTime checkOut = timePicker4.getSelectedTime();

            if (!idValidation.validate(employeeId)) {
                PanelNotificationUtils.showWarning(this, "Invalid Employee ID. Format: 5 uppercase letters + 5 digits.");
                return;
            }

            if (employeeId.isEmpty()) {
                PanelNotificationUtils.showWarning(this, "Please enter or select an Employee ID.");
                return;
            }

            if (selectedDate == null) {
                PanelNotificationUtils.showWarning(this, "Please select the date.");
                return;
            }

            if (status == null || status.isEmpty()) {
                PanelNotificationUtils.showWarning(this, "Please select an attendance status.");
                return;
            }

            ResultSet rs = MySQL.executeSearch(
                "SELECT id FROM attendance WHERE employee_id = '" + employeeId + "' AND date = '" + Date.valueOf(selectedDate) + "'"
            );
            if (rs.next()) {
                PanelNotificationUtils.showWarning(this, "Attendance already exists for this employee on the selected date.");
                return;
            }

            if ("PRESENT".equalsIgnoreCase(status) || "LATE".equalsIgnoreCase(status)) {
                if (checkIn == null || shiftStart == null) {
                    PanelNotificationUtils.showWarning(this, "Check-in and Check-out time must be provided for PRESENT/LATE.");
                    return;
                }
                
                ResultSet logResult = MySQL.executeSearch(
                    "SELECT * FROM employee_checkin_log WHERE employee_id = '" + employeeId + "' AND date = '" + Date.valueOf(selectedDate) + "'"
                );
                if (!logResult.next()) {
                    PanelNotificationUtils.showWarning(this, "Cannot mark PRESENT or LATE. No check-in log found for this employee on the selected date.");
                    return;
                }

                boolean isLate = checkIn.isAfter(shiftStart.plusMinutes(5));
                status = isLate ? "LATE" : "PRESENT";

                if (shiftEnd != null && checkOut != null && checkOut.isBefore(shiftEnd)) {
                    PanelNotificationUtils.showWarning(this, "Warning: Employee checked out before shift end.");
                        // can be used for future accounts gui
                }
            }

            String checkInStr = (checkIn != null) ? "'" + checkIn.toString() + "'" : "NULL";
            String checkOutStr = (checkOut != null) ? "'" + checkOut.toString() + "'" : "NULL";

            String insertQuery = String.format(
                "INSERT INTO attendance (employee_id, date, status, check_in, check_out) VALUES " +
                "('%s', '%s', '%s', %s, %s)",
                employeeId,
                Date.valueOf(selectedDate),
                status,
                checkInStr,
                checkOutStr
            );

            MySQL.executeIUD(insertQuery);

            PanelNotificationUtils.showSuccess(this, "Attendance recorded successfully.");
            loadCheckInLogs(null);
            clear();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to insert attendance", e);
            JOptionPane.showMessageDialog(this, "An error occurred while saving attendance.\nPlease contact support.", "System Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void yearSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yearSpinner1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_yearSpinner1StateChanged

    private void attendanceSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceSearchActionPerformed

        String baseQuery =
            "SELECT a.date, " +
            "e.id AS employee_id, e.name AS employee_name, " +
            "d.name AS department_name, r.name AS role_name, " +
            "s.name AS shift_name, " +
            "a.check_in, a.check_out, a.status " +
            "FROM attendance a " +
            "INNER JOIN employee e ON e.id = a.employee_id " +
            "LEFT JOIN department_has_role dhr ON e.department_role_id = dhr.id " +
            "LEFT JOIN department d ON dhr.department_id = d.id " +
            "LEFT JOIN role r ON dhr.role_id = r.id " +
            "LEFT JOIN employee_shift es ON e.id = es.employee_id " +
            "LEFT JOIN shift s ON es.shift_id = s.id " +
            "WHERE 1 = 1 ";

        String filter = "";

        if (datePicker3.getSelectedDate() != null) {
            LocalDate selectedDate = datePicker3.getSelectedDate();
            filter += "AND a.date = '" + Date.valueOf(selectedDate) + "' ";
        } else {
 
            if (jComboBox11.getSelectedIndex() > 0) {
                int month = jComboBox11.getSelectedIndex(); 
                filter += "AND MONTH(a.date) = " + month + " ";
            }

            int year = (int) yearSpinner1.getValue();
            if (year > 0) {
                filter += "AND YEAR(a.date) = " + year + " ";
            }
        }

        if (jComboBox13.getSelectedIndex() > 0) {
            String dept = jComboBox13.getSelectedItem().toString();
            filter += "AND d.name = '" + dept + "' ";
        }

        String empID = jTextField3.getText().trim();
        if (!empID.isEmpty()) {
            if (!idValidation.validate(empID)) {
                PanelNotificationUtils.showWarning(this, "Invalid Employee ID. Format: 5 uppercase letters + 5 digits.");
                return;
            } else {
                filter += "AND e.id = '" + empID + "' ";
            }
        }


        String finalQuery = baseQuery + filter + "ORDER BY a.date DESC";

        try {
            ResultSet rs = MySQL.executeSearch(finalQuery);

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("employee_name"));
                row.add(rs.getString("department_name"));
                row.add(rs.getString("role_name"));
                row.add(rs.getDate("date") != null ? rs.getDate("date").toString() : "");
                row.add(rs.getString("shift_name"));
                row.add(rs.getTime("check_in") != null ? rs.getTime("check_in").toString() : "");
                row.add(rs.getTime("check_out") != null ? rs.getTime("check_out").toString() : "");
                row.add(rs.getString("status"));

                model.addRow(row);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error filtering attendance data", e);
            JOptionPane.showMessageDialog(this, "Failed to load attendance records.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_attendanceSearchActionPerformed

    private void btnfilter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnfilter2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnfilter2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton attendanceSearch;
    private javax.swing.JButton btncheckout3;
    private javax.swing.JButton btnfilter2;
    private raven.datetime.component.date.DatePicker datePicker1;
    private raven.datetime.component.date.DatePicker datePicker2;
    private raven.datetime.component.date.DatePicker datePicker3;
    private javax.swing.JButton filterSearch;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox13;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JFormattedTextField jFormattedTextField6;
    private javax.swing.JFormattedTextField jFormattedTextField7;
    private javax.swing.JFormattedTextField jFormattedTextField8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private raven.datetime.component.time.TimePicker timePicker1;
    private raven.datetime.component.time.TimePicker timePicker2;
    private raven.datetime.component.time.TimePicker timePicker3;
    private raven.datetime.component.time.TimePicker timePicker4;
    private javax.swing.JSpinner yearSpinner;
    private javax.swing.JSpinner yearSpinner1;
    // End of variables declaration//GEN-END:variables

    private void clear() {
        jTextField1.setText("");
        jTextField2.setText("");
        jComboBox2.setSelectedIndex(-1); 
        jComboBox3.setSelectedIndex(-1); 
        jComboBox4.setSelectedIndex(-1); 
        datePicker1.setSelectedDate(LocalDate.now());
        timePicker1.clearSelectedTime(); 
        timePicker2.clearSelectedTime(); 
        timePicker3.clearSelectedTime(); 
        timePicker4.clearSelectedTime(); 
        jComboBox6.setSelectedIndex(-1);
    }
    
    private void clearfilter() {

        datePicker2.clearSelectedDate();
        datePicker2.setEnabled(true);
        jComboBox8.setSelectedIndex(0); 
        jComboBox8.setEnabled(true);
        jComboBox10.setSelectedIndex(0);
        
        int currentYear = LocalDate.now().getYear();
        yearSpinner.setValue(currentYear);
        yearSpinner.setEnabled(true);
        
    }

}
