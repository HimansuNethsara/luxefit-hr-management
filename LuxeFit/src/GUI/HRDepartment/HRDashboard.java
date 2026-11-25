package GUI.HRDepartment;

import GUI.Home;
import GUI.SignIn;
import Model.PanelController;
import Util.NotificationUtils;
import Util.SystemLogger;
import Util.ThemeUtils;
import Util.logoutUtils;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSlider;

public class HRDashboard extends javax.swing.JFrame implements PanelController {

    private JPanel panel;
    private int frameState;
    
    private static final java.util.logging.Logger LOGGER = SystemLogger.getLogger();
    
    private String employeeName;
    private String employeeId;
    private String profilePic;
    private String role;
    
    @Override
    public String getEmployeeID(){
        return employeeId;
    }
    
    @Override
    public String getEmployeeName() {
        return employeeName;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public String getProfilePic() {
        return profilePic;
    }
    
    public HRDashboard(String employeeName, String employeeId, String profilePic, String role) {
        try{
            ThemeUtils.applyCurrentTheme();
            initComponents();
            jButton12.setVisible(false);
            SwingUtilities.updateComponentTreeUI(this);
            revalidate();
            repaint();

            this.employeeName = employeeName;
            this.employeeId = employeeId;
            this.profilePic = profilePic;
            this.role = role; 
            
            if (ThemeUtils.getCurrentTheme() == ThemeUtils.ThemeMode.DARK) {
                jSliderTheme.setValue(1);
            } else {
                jSliderTheme.setValue(0);
            }
            
        }catch (Exception e) {
            NotificationUtils.showError(this, "An unexpected error occurred while loading the HR DashBoard.");
            LOGGER.log(Level.SEVERE, "Error initializing HR DashBoard", e);
        }
    }

    @Override
    public void addPanel(JPanel newPanel) {
        if (panel == null) {
            panel = newPanel;
            jPanel3.add(this.panel, BorderLayout.CENTER);
            SwingUtilities.updateComponentTreeUI(jPanel3);
        }

    }

    @Override
    public void removePanel() {
        if (panel != null) {
            jPanel3.remove(panel);
            panel = null;
            SwingUtilities.updateComponentTreeUI(jPanel3);
        }

    }

    private void componentStatus(boolean status) {
        LOGGER.info("Sidebar toggle triggered. Collapsed: " + status);
        if (status) {
            LOGGER.fine("Switching to collapsed sidebar icons.");
            jButton12.setVisible(true);
            jButton10.setVisible(false);
            jLabel1.setText("");
            jButton13.setText("");
            jButton13.setSize(40, 40);
            jButton13.setIcon(new ImageIcon(getClass().getResource("/Resource/home.png")));
            jButton4.setText("");
            jButton4.setSize(40, 40);
            jButton4.setIcon(new ImageIcon(getClass().getResource("/Resource/attendance.png")));
            jButton8.setText("");
            jButton8.setSize(40, 40);
            jButton8.setIcon(new ImageIcon(getClass().getResource("/Resource/leave.png")));
            jButton1.setText("");
            jButton1.setSize(40, 40);
            jButton1.setIcon(new ImageIcon(getClass().getResource("/Resource/worker.png")));
            jButton2.setText("");
            jButton2.setSize(40, 40);
            jButton2.setIcon(new ImageIcon(getClass().getResource("/Resource/payroll.png")));
            jButton3.setText("");
            jButton3.setSize(40, 40);
            jButton3.setIcon(new ImageIcon(getClass().getResource("/Resource/scheduler.png")));
            jButton11.setText("");
            jButton11.setSize(40, 40);
            jButton11.setIcon(new ImageIcon(getClass().getResource("/Resource/compliance.png")));
            jSliderTheme.setPreferredSize(new Dimension(40, 20)); 
            jSliderTheme.revalidate();
            jSliderTheme.repaint();
            jButton9.setText("");
            jButton9.setSize(22, 22);
        } else {
            LOGGER.fine("Switching to expanded sidebar with labels.");
            jButton12.setVisible(false);
            jButton10.setVisible(true);
            jLabel1.setText("Dashboard");
            jButton13.setText("Home");
            jButton13.setSize(188, 30);
            jButton13.setIcon(new ImageIcon(getClass().getResource("")));
            jButton4.setText("Attendance Tracker");
            jButton4.setSize(188, 30);
            jButton4.setIcon(new ImageIcon(getClass().getResource("")));
            jButton8.setText("Leave Management");
            jButton8.setSize(188, 30);
            jButton8.setIcon(new ImageIcon(getClass().getResource("")));
            jButton1.setText("Employee Management");
            jButton1.setSize(188, 30);
            jButton1.setIcon(new ImageIcon(getClass().getResource("")));
            jButton2.setText("Payroll Management");
            jButton2.setSize(188, 30);
            jButton2.setIcon(new ImageIcon(getClass().getResource("")));
            jButton3.setText("Shift Scheduler");
            jButton3.setSize(188, 30);
            jButton3.setIcon(new ImageIcon(getClass().getResource("")));
            jButton11.setText("Compliance Handling");
            jButton11.setSize(188, 30);
            jButton11.setIcon(new ImageIcon(getClass().getResource("")));
            jSliderTheme.setPreferredSize(new Dimension(200, 20));
            jSliderTheme.revalidate();
            jSliderTheme.repaint();
            jButton9.setText("Logout");
            jButton9.setSize(90, 22);
        }

    }

    private void setPanelStatus(boolean status) {

        Thread thread = new Thread(() -> {
            try {
                int start = status ? 200 : 50;
                int end = status ? 50 : 200;
                int step = status ? -1 : 1;

                for (int i = start; i != end + step; i += step) {
                    final int width = i;

                    SwingUtilities.invokeLater(() -> {
                        jPanel2.setPreferredSize(new Dimension(width, jPanel2.getHeight()));
                        jPanel2.revalidate();
                        jPanel2.repaint();
                    });

                    Thread.sleep(2);
                }

                SwingUtilities.invokeLater(() -> componentStatus(status));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                LOGGER.log(Level.SEVERE, "Sidebar animation thread was interrupted", e);
            }
        });
        thread.start();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jSliderTheme = new javax.swing.JSlider();
        jButton13 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 50));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/maximize.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/close.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/iconified.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel5.setText("HR Dashboard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 894, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(200, 500));

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/dash.png"))); // NOI18N
        jLabel1.setText("Dashboard");

        jButton1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton1.setText("Employee Management");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton4.setText("Attendance Tracker");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton8.setText("Leave Management");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/hide.png"))); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/show.png"))); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/logout.png"))); // NOI18N
        jButton9.setText("Logout");
        jButton9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton2.setText("Payroll Management");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton3.setText("Shift Scheduler");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton11.setText("Compliance Handling");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jSliderTheme.setMaximum(1);
        jSliderTheme.setValue(0);
        jSliderTheme.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderThemeStateChanged(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton13.setText("Home");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton10))
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jButton9))
                        .addGap(0, 88, Short.MAX_VALUE))
                    .addComponent(jSliderTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton10)
                    .addComponent(jButton12))
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addGap(17, 17, 17)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSliderTheme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                .addComponent(jButton9)
                .addGap(15, 15, 15))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.LINE_START);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel3.setPreferredSize(new java.awt.Dimension(1000, 600));
        jPanel3.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (this.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            this.setExtendedState(JFrame.NORMAL);
            jButton5.setIcon(new ImageIcon(getClass().getResource("/Resource/maximize.png")));
        } else {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            jButton5.setIcon(new ImageIcon(getClass().getResource("/Resource/minimize.png")));
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        frameState = this.getExtendedState();
        this.setExtendedState(JFrame.ICONIFIED);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        setPanelStatus(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        setPanelStatus(false);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        logoutUtils.performLogout(this);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        removePanel();
        addPanel(new EmployeeManagement(this));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        removePanel();
        //        addPanel(new ComplianceHandling(this));
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        removePanel();
        addPanel(new AttendanceTracker(this));
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jSliderThemeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderThemeStateChanged
        JSlider source = (JSlider) evt.getSource();

        if (!source.getValueIsAdjusting()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    if (source.getValue() == 0 && ThemeUtils.getCurrentTheme() != ThemeUtils.ThemeMode.LIGHT) {
                        ThemeUtils.applyLightTheme();
                        LOGGER.info("Switched to Light Theme.");
                    } else if (source.getValue() == 1 && ThemeUtils.getCurrentTheme() != ThemeUtils.ThemeMode.DARK) {
                        ThemeUtils.applyDarkTheme();
                        LOGGER.info("Switched to Dark Theme.");
                    }
                    SwingUtilities.updateComponentTreeUI(this);
                    revalidate();
                    repaint();
                    LOGGER.info("UI updated successfully after theme switch.");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error occurred while switching themes", e);
                }
            });
        }
    }//GEN-LAST:event_jSliderThemeStateChanged

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        removePanel();
        addPanel(new Home(this, employeeName, employeeId, role, profilePic));
    }//GEN-LAST:event_jButton13ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSlider jSliderTheme;
    // End of variables declaration//GEN-END:variables
}
