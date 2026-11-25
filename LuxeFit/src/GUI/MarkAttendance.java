package GUI;

import Model.MySQL;
import Util.NotificationUtils;
import Util.SystemLogger;
import Util.ThemeUtils;
import java.awt.Color;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MarkAttendance extends javax.swing.JFrame {
    
    private final String employeeId;  
    private static final Logger logger = SystemLogger.getLogger();
    
    public enum AttendanceStatus {
        NOT_CHECKED_IN, CHECKED_IN, CHECKED_OUT
    }
    
    private AttendanceStatus status;
   
    public MarkAttendance(String employeeId) {        
        this.employeeId = employeeId;
        setUndecorated(true);
        setResizable(false);       
        setBackground(new Color(0, 0, 0, 0));

        disableAllOtherFrames();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                enableAllOtherFrames();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                enableAllOtherFrames();
            }
        });

        initComponents();
        
        ThemeUtils.applyCurrentTheme();
        SwingUtilities.updateComponentTreeUI(this);
        revalidate();
        repaint();
        
        initPickers();
        loadStatusDropdownOptions();
        loadTodayStatus();

    }
    
    private void disableAllOtherFrames() {
        for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
            if (frame != this) {
                frame.setEnabled(false);
            }
        }
    }

    private void enableAllOtherFrames() {
        for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
            if (frame != this) {
                frame.setEnabled(true);
            }
        }
    }
    
    private void initPickers() {
        datePicker1.setEditor(jFormattedTextField1);
        datePicker1.setSelectedDate(LocalDate.now());

        timePicker1.set24HourView(true);
        timePicker1.setOrientation(SwingConstants.HORIZONTAL);
        timePicker1.setEditor(jFormattedTextField2);

        timePicker2.set24HourView(true);
        timePicker2.setOrientation(SwingConstants.HORIZONTAL);
        timePicker2.setEditor(jFormattedTextField3);
    }
    
    private void loadStatusDropdownOptions() {
        jComboBox1.removeAllItems();
        for (AttendanceStatus s : AttendanceStatus.values()){
            jComboBox1.addItem(s.name());
        }
        jComboBox1.setEnabled(false); 
    }
    
    private void loadTodayStatus() {
        try {
            LocalDate today = LocalDate.now();
            String query = "SELECT check_in, check_out FROM employee_checkin_log WHERE employee_id = '" + employeeId + "' AND date = '" + today + "'";
            ResultSet rs = MySQL.executeSearch(query);
            
            LocalTime checkIn = null;
            LocalTime checkOut = null;

            if (!rs.next()) {
                status = MarkAttendance.AttendanceStatus.NOT_CHECKED_IN;
            } else {
                checkIn = rs.getTime("check_in") != null ? rs.getTime("check_in").toLocalTime() : null;
                checkOut = rs.getTime("check_out") != null ? rs.getTime("check_out").toLocalTime() : null;

                if (checkIn != null && checkOut != null) {
                    status = MarkAttendance.AttendanceStatus.CHECKED_OUT;
                } else if (checkIn != null) {
                    status = MarkAttendance.AttendanceStatus.CHECKED_IN;
                } else {
                    status = MarkAttendance.AttendanceStatus.NOT_CHECKED_IN;
                }
            }

            jComboBox1.removeAllItems();
            jComboBox1.addItem(status.name());

            btncheckin.setEnabled(status == MarkAttendance.AttendanceStatus.NOT_CHECKED_IN);
            btncheckout.setEnabled(status == MarkAttendance.AttendanceStatus.CHECKED_IN);
            
            if (checkIn != null) {
                timePicker1.setSelectedTime(checkIn);
            } else {
                timePicker1.clearSelectedTime();
            }

            if (checkOut != null) {
                timePicker2.setSelectedTime(checkOut);
            } else {
                timePicker2.clearSelectedTime();
            }
            
            timePicker1.setEnabled(status == AttendanceStatus.NOT_CHECKED_IN || status == AttendanceStatus.CHECKED_IN);
            timePicker2.setEnabled(status == AttendanceStatus.CHECKED_IN);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading attendance status", e);
            NotificationUtils.showError(this, "Error loading attendance status.");
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timePicker1 = new raven.datetime.component.time.TimePicker();
        timePicker2 = new raven.datetime.component.time.TimePicker();
        datePicker1 = new raven.datetime.component.date.DatePicker();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        btncheckin = new javax.swing.JButton();
        btncheckout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 300));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/icons8-close-20.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel2.setText("Mark Attendance");

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel3.setText("Date");

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel4.setText("Status");

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel5.setText("Check Out Time");

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel6.setText("Check In Time");

        jFormattedTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField1ActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));

        jFormattedTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField2ActionPerformed(evt);
            }
        });

        jFormattedTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField3ActionPerformed(evt);
            }
        });

        btncheckin.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btncheckin.setText("Check In");
        btncheckin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncheckinActionPerformed(evt);
            }
        });

        btncheckout.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btncheckout.setText("Check Out");
        btncheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncheckoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btncheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                        .addComponent(btncheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(58, 58, 58)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jFormattedTextField2)
                            .addComponent(jFormattedTextField3)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jFormattedTextField1)
                            .addComponent(jComboBox1, 0, 210, Short.MAX_VALUE))))
                .addGap(20, 20, 20))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(140, 140, 140)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btncheckout)
                    .addComponent(btncheckin))
                .addGap(0, 40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jFormattedTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField1ActionPerformed

    private void jFormattedTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField2ActionPerformed

    private void jFormattedTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField3ActionPerformed

    private void btncheckinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncheckinActionPerformed
       try {
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            LocalTime selectedTime = timePicker1.getSelectedTime();
            
            if (status == AttendanceStatus.CHECKED_IN) {
                JOptionPane.showMessageDialog(this, "You have already checked in today.", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (status == AttendanceStatus.CHECKED_OUT) {
                JOptionPane.showMessageDialog(this, "You have already checked out. Cannot check in again.", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (selectedTime == null) {
                JOptionPane.showMessageDialog(this, "Please select a check-in time.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!selectedTime.equals(now)
                    && (selectedTime.isBefore(now.minusMinutes(1)) || selectedTime.isAfter(now.plusMinutes(1)))) {
                JOptionPane.showMessageDialog(this,
                        "You cannot select a past or future time.\nPlease use the current system time.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "INSERT INTO employee_checkin_log (employee_id, date, check_in) VALUES ('"
            + employeeId + "', '" + today + "', '" + now + "') "
            + "ON DUPLICATE KEY UPDATE check_in = '" + now + "'";

            int result = MySQL.executeIUD(query);

            if (result > 0) {
                NotificationUtils.showSuccess(this, "Check-In recorded at " + now);
                loadTodayStatus();
            } else {
                JOptionPane.showMessageDialog(this, "Check-In failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Check-In Error", e);
            NotificationUtils.showError(this, "Check-In error occurred.");
        }
    }//GEN-LAST:event_btncheckinActionPerformed

    private void btncheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncheckoutActionPerformed
        try {
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            LocalTime selectedTime = timePicker2.getSelectedTime();
            
            if (status == AttendanceStatus.NOT_CHECKED_IN) {
                JOptionPane.showMessageDialog(this, "You have not checked in yet. Please check in first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (status == AttendanceStatus.CHECKED_OUT) {
                JOptionPane.showMessageDialog(this, "You have already checked out today.", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (selectedTime == null) {
                JOptionPane.showMessageDialog(this, "Please select a check-out time.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }else if (!selectedTime.equals(now)
                    && (selectedTime.isBefore(now.minusMinutes(1)) || selectedTime.isAfter(now.plusMinutes(1)))) {
                JOptionPane.showMessageDialog(this,
                        "You cannot select a past or future time.\nPlease use the current system time.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }


            String query = "UPDATE employee_checkin_log SET check_out = '" + now + "' "
            + "WHERE employee_id = '" + employeeId + "' AND date = '" + today + "'";

            int result = MySQL.executeIUD(query);

            if (result > 0) {
                NotificationUtils.showSuccess(this, "Check-Out recorded at " + now);
                loadTodayStatus();
            } else {
                 JOptionPane.showMessageDialog(this, "Check-Out failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Check-Out Error", e);
            NotificationUtils.showError(this, "Check-Out error occurred.");
        }
    }//GEN-LAST:event_btncheckoutActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        for (Window w : Window.getWindows()) {
            if (w instanceof JWindow && w.getBackground().getAlpha() == 120) {
                w.dispose();
            }
        }
        this.dispose(); 
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncheckin;
    private javax.swing.JButton btncheckout;
    private raven.datetime.component.date.DatePicker datePicker1;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private raven.datetime.component.time.TimePicker timePicker1;
    private raven.datetime.component.time.TimePicker timePicker2;
    // End of variables declaration//GEN-END:variables
}
