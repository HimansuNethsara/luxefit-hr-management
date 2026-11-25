package GUI;

import GUI.HRDepartment.HRDashboard;
import Model.MySQL;
import Util.NotificationUtils;
import Util.SystemLogger;
import Util.ThemeUtils;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import validations.SigninValidation;
import java.sql.ResultSet;
import raven.toast.Notifications;

public class SignIn extends javax.swing.JFrame {

    private static final java.util.logging.Logger LOGGER = SystemLogger.getLogger();

    public SignIn() {
        initComponents();
        jToggleButton2.setSelected(true);
        jPanel2.setBackground(new Color(0, 0, 0, 100));
        jButton1.setBackground(new Color(0, 0, 0, 0));
        jTextField2.grabFocus();
    }

    private static void darkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
        }
    }

    private static void lightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jToggleButton2 = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPasswordField2 = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        SignInButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Employee ID :");

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Password :");

        jToggleButton2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jToggleButton2.setText("Switch Theme");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("LUXEFIT");

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTextField2.setToolTipText("Enter Email Here");
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jPasswordField2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jPasswordField2.setToolTipText("At least 10 char, 1 uppercase, 1 lowercase, 1 special ");
        jPasswordField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordField2KeyReleased(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Sign in to your account");

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/passview.png"))); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        SignInButton.setBackground(new java.awt.Color(0, 35, 102));
        SignInButton.setFont(new java.awt.Font("Poppins", 1, 11)); // NOI18N
        SignInButton.setForeground(new java.awt.Color(255, 255, 255));
        SignInButton.setText("Sign In");
        SignInButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SignInButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SignInButtonMouseExited(evt);
            }
        });
        SignInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SignInButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5)
                            .addComponent(jTextField2)
                            .addComponent(jPasswordField2, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)))
                    .addComponent(SignInButton, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(110, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(105, 105, 105))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton2)
                    .addComponent(jToggleButton1))
                .addGap(18, 18, 18)
                .addComponent(SignInButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 390, 310));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/icons8-close-20.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, 30, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resource/signin.jpeg"))); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(600, 380));
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        if (jToggleButton2.isSelected()) {
            ThemeUtils.applyLightTheme();
        } else {
            ThemeUtils.applyDarkTheme();
        }
        SwingUtilities.updateComponentTreeUI(this);
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void SignInButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SignInButtonMouseEntered
        SignInButton.setBackground(new java.awt.Color(135, 206, 235));
    }//GEN-LAST:event_SignInButtonMouseEntered

    private void SignInButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SignInButtonMouseExited
        SignInButton.setBackground(new java.awt.Color(0, 35, 102));
    }//GEN-LAST:event_SignInButtonMouseExited

    private void SignInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SignInButtonActionPerformed

        String id = jTextField2.getText().trim();
        String password = String.valueOf(jPasswordField2.getPassword());

        Notifications.getInstance().setJFrame(this);

        SigninValidation validator = new SigninValidation();
        if (!validator.validate(id, password)) {
            return;
        }

        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT e.*, d.name AS department_name, r.name AS role_name FROM employee e "
                    + "JOIN department_has_role dr ON e.department_role_id = dr.id "
                    + "JOIN department d ON dr.department_id = d.id "
                    + "JOIN role r ON dr.role_id = r.id "
                    + "WHERE e.id = '" + id + "' AND e.password = '" + password + "'");

            if (resultSet.next()) {
                String employeeId = resultSet.getString("id");
                String employeeName = resultSet.getString("name");

                String profilePic = resultSet.getString("profile_pic");
                String department = resultSet.getString("department_name");
                String role = resultSet.getString("role_name");

                NotificationUtils.showSuccess(this, "Successfully signed in!");

                switch (department.toLowerCase()) {
                    case "hr":
                        ThemeUtils.applyCurrentTheme();
                        new HRDashboard(employeeName, employeeId, profilePic, role).setVisible(true);
                        break;
//                    case "sales":
//                        new SalesDashboard(fullName, employeeEmail, profilePic, role).setVisible(true);
//                        break;
//                    case "inventory":
//                        new InventoryDashboard(fullName, employeeEmail, profilePic, role).setVisible(true);
//                        break;
//                    case "supplier & procurement":
//                        new SupplierProcurementDashboard(fullName, employeeEmail, profilePic, role).setVisible(true);
//                        break;
//                    case "marketing & promotions":
//                        new MarketingPromotionsDashboard(fullName, employeeEmail, profilePic, role).setVisible(true);
//                        break;
//                    case "accounts & finance":
//                        new AccountsFinanceDashboard(fullName, employeeEmail, profilePic, role).setVisible(true);
//                        break;
                    default:
                        NotificationUtils.showError(this, "Department not recognized. Cannot load dashboard.");
                        return;
                }

                this.dispose();

            } else {
                NotificationUtils.showWarning(this, "Invalid employee ID or password.");
            }
        } catch (Exception e) {
            NotificationUtils.showError(this, "An unexpected error occurred during sign-in.");
            SystemLogger.getLogger().log(Level.SEVERE, "Unexpected error during sign-in", e);
        }


    }//GEN-LAST:event_SignInButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        if (evt.getKeyCode() == 10) {
            jPasswordField2.grabFocus();
        }
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jPasswordField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField2KeyReleased
        if (evt.getKeyCode() == 10) {
            SignInButton.grabFocus();
        }

        if (evt.getKeyCode() == 38) {
            jTextField2.grabFocus();
        }
    }//GEN-LAST:event_jPasswordField2KeyReleased

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if (jToggleButton1.isSelected()) {
            jPasswordField2.setEchoChar(((char) 0));
        } else {
            jPasswordField2.setEchoChar('*');
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        ThemeUtils.applyLightTheme();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SignIn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SignInButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    // End of variables declaration//GEN-END:variables
}
