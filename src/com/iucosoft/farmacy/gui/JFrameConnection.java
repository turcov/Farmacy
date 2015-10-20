/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui;

import com.iucosoft.farmacy.db.Config;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionErrorException;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.Painter;

/**
 *
 * @author Turkov S
 */
public class JFrameConnection extends javax.swing.JFrame {

    private static final Logger LOG = Logger.getLogger(JFrameConnection.class.getName());

    String args[] = null;

    /**
     * Creates new form JFrameConnection
     */
    public JFrameConnection() {

        initComponents();
        initGuiComponents();
        addListeners();
        setLocationRelativeTo(null);
    }

    private void initGuiComponents() {
        jFrameAbout.setVisible(true);

        props = Config.loadProperties();
        jTextFieldHost.setText(props.getProperty("host/ip"));
        jTextFieldPort.setText(props.getProperty("port"));
        jTextFieldUser.setText(props.getProperty("user"));
        jPassword.setText(props.getProperty("password"));
        jLabeldbURL.setText(props.getProperty("url") + props.getProperty("host/ip") + ':' + props.getProperty("port") + '/' + props.getProperty("database_name"));
        DefaultComboBoxModel dfd = new DefaultComboBoxModel();
        dfd.addElement(props.getProperty("driver"));
        jComboBoxDriverName.setModel(dfd);
        jButtonConnect.requestFocus();
    }

    void addListeners() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                if (jFrameFarmacy == null) {
                    LOG.info("jFrameFarmacy=N U L L ");
                    if (JFrameConnection.dataSource != null) {
                        JFrameConnection.dataSource.disconnect();
                    }
                    System.exit(0);
                }
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelProperties = new javax.swing.JPanel();
        jTextFieldHost = new javax.swing.JTextField();
        jPassword = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldUser = new javax.swing.JTextField();
        jTextFieldPort = new javax.swing.JTextField();
        jComboBoxDriverName = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButtonSaveProperties = new javax.swing.JButton();
        jButtonChangePropertiesAllow = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButtonStart = new javax.swing.JButton();
        jLabeldbURL = new javax.swing.JLabel();
        jButtonConnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database connection");

        jTextFieldHost.setEnabled(false);
        jTextFieldHost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldHostKeyReleased(evt);
            }
        });

        jPassword.setEnabled(false);
        jPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordKeyReleased(evt);
            }
        });

        jLabel3.setText("Port");
        jLabel3.setEnabled(false);

        jLabel1.setText("Driver");
        jLabel1.setEnabled(false);

        jLabel5.setText("Password");
        jLabel5.setEnabled(false);

        jTextFieldUser.setEnabled(false);
        jTextFieldUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldUserKeyReleased(evt);
            }
        });

        jTextFieldPort.setEnabled(false);
        jTextFieldPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPortKeyReleased(evt);
            }
        });

        jComboBoxDriverName.setEnabled(false);
        jComboBoxDriverName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxDriverNameItemStateChanged(evt);
            }
        });

        jLabel2.setText("Host/IP");
        jLabel2.setEnabled(false);

        jLabel4.setText("User");
        jLabel4.setEnabled(false);

        jButtonSaveProperties.setText("Save properties");
        jButtonSaveProperties.setEnabled(false);
        jButtonSaveProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSavePropertiesActionPerformed(evt);
            }
        });

        jButtonChangePropertiesAllow.setText("Change properties");
        jButtonChangePropertiesAllow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangePropertiesAllowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPropertiesLayout = new javax.swing.GroupLayout(jPanelProperties);
        jPanelProperties.setLayout(jPanelPropertiesLayout);
        jPanelPropertiesLayout.setHorizontalGroup(
            jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(7, 7, 7)
                .addGroup(jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelPropertiesLayout.createSequentialGroup()
                        .addComponent(jTextFieldHost, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPassword)
                    .addComponent(jTextFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxDriverName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonSaveProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonChangePropertiesAllow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelPropertiesLayout.setVerticalGroup(
            jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxDriverName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSaveProperties))
                .addGap(26, 26, 26)
                .addGroup(jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonChangePropertiesAllow))
                .addGap(28, 28, 28)
                .addGroup(jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("db.url:");

        jButtonStart.setText("Start");
        jButtonStart.setEnabled(false);
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        jButtonConnect.setText("Connect");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonConnect, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(jButtonStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabeldbURL, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConnect)
                    .addComponent(jLabel6)
                    .addComponent(jLabeldbURL, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonStart)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanelProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldHostKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldHostKeyReleased
        jLabeldbURL.setText("jdbc:mysql://" + jTextFieldHost.getText()
                + ":" + jTextFieldPort.getText() + "/farmacy");
        jButtonSaveProperties.setEnabled(true);
        props.setProperty("host/ip", jTextFieldHost.getText());
    }//GEN-LAST:event_jTextFieldHostKeyReleased

    private void jTextFieldPortKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPortKeyReleased
        jTextFieldHostKeyReleased(evt);
        props.setProperty("port", jTextFieldPort.getText());
    }//GEN-LAST:event_jTextFieldPortKeyReleased

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed
        if (dataSource == null) {
            try {
                dataSource = DataSourceFarmacy.getInstance(
                        (String) jComboBoxDriverName.getSelectedItem(), jLabeldbURL.getText(),
                        jTextFieldUser.getText(), jPassword.getPassword());
                //            conn = dataSource.getConnection();
                dataSource.testConnection();
                JOptionPane.showMessageDialog(this, "Connection succeeded");
                jButtonConnect.setText("Disconnect");
                jButtonStart.setEnabled(true);
                jComboBoxDriverName.setEnabled(false);
                jTextFieldHost.setEnabled(false);
                jTextFieldPort.setEnabled(false);
                jTextFieldUser.setEnabled(false);
                jPassword.setEnabled(false);
                jButtonStart.requestFocus();
            } catch (ConnectionErrorException ex) {
                LOG.log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
                dataSource.disconnect();
                dataSource = null;
            }
        } else {
            dataSource.disconnect();
            dataSource = null;
            jButtonConnect.setText("Connect");
            JOptionPane.showMessageDialog(this, "Database disconnected");
            jButtonStart.setEnabled(false);
            jComboBoxDriverName.setEnabled(true);
            jTextFieldHost.setEnabled(true);
            jTextFieldPort.setEnabled(true);
            jTextFieldUser.setEnabled(true);
            jPassword.setEnabled(true);
        }
//        pack();
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed
        StringBuilder mode = new StringBuilder("");
        new JDialogAuthentification(this, true, mode).setVisible(true);
        if (jFrameFarmacy == null) {
            try {
                jFrameFarmacy = new JFrameFarmacy(mode, dataSource);
            } catch (ConnectionInterruptedException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
            } catch (InstantiationException ex) {
                LOG.log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
            } catch (IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
            }
        }
        if (!mode.toString().equals("")) {
            jButtonConnect.setEnabled(false);
            jFrameConnection.dispose();
            jFrameConnection = null;
            jFrameFarmacy.setVisible(true);
        }
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    while (conn.isValid(0)) {
//                        Thread.sleep(500);
//                        LOG.info("checking connection");
//                    }
//                    synchronized (conn) {
//                        LOG.info("connection interrupted.Trying to reconnect");
//                        conn = dataSource.getConnection();
//                        LOG.info("re-connection succeeded");
//                    }
//                } catch (InterruptedException ex) {
//                    LOG.log(Level.SEVERE, null, ex);
//                } catch (SQLException ex) {
//                    LOG.log(Level.SEVERE, null, ex);
//                }
//            }
//        }).start();
    }//GEN-LAST:event_jButtonStartActionPerformed

    private void jTextFieldUserKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldUserKeyReleased
        jButtonSaveProperties.setEnabled(true);
        props.setProperty("user", jTextFieldUser.getText());
    }//GEN-LAST:event_jTextFieldUserKeyReleased

    private void jPasswordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordKeyReleased
        jButtonSaveProperties.setEnabled(true);
        props.setProperty("password", new String(jPassword.getPassword()));

    }//GEN-LAST:event_jPasswordKeyReleased

    private void jComboBoxDriverNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxDriverNameItemStateChanged
        jButtonSaveProperties.setEnabled(true);
        props.setProperty("driver", (String) jComboBoxDriverName.getSelectedItem());
    }//GEN-LAST:event_jComboBoxDriverNameItemStateChanged

    private void jButtonSavePropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSavePropertiesActionPerformed
        Config.saveProperties(props);
        for (int i = 0; i < jPanelProperties.getComponentCount(); i++) {
            jPanelProperties.getComponent(i).setEnabled(false);
        }
        jButtonChangePropertiesAllow.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Properties saved succesfully!");
    }//GEN-LAST:event_jButtonSavePropertiesActionPerformed

    private void jButtonChangePropertiesAllowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangePropertiesAllowActionPerformed
        for (int i = 0; i < jPanelProperties.getComponentCount(); i++) {
            jPanelProperties.getComponent(i).setEnabled(true);
        }
        jButtonSaveProperties.setEnabled(false);
    }//GEN-LAST:event_jButtonChangePropertiesAllowActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        Properties props = Config.loadProperties();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (props.getProperty("skin").equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    javax.swing.UIManager.getLookAndFeelDefaults().put(
                            "DesktopPane[Enabled].backgroundPainter", new DesktopPainter());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrameConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrameConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrameConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        jFrameConnection = new JFrameConnection();
        jFrameConnection.setVisible(true);
        jFrameConnection.pack();
    }

    static class DesktopPainter implements Painter<JComponent> {

        @Override
        public void paint(Graphics2D g, JComponent object, int width, int height) {
            g.drawImage(null, 0, 0, width, height, null);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonChangePropertiesAllow;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonSaveProperties;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JComboBox jComboBoxDriverName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabeldbURL;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelProperties;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JTextField jTextFieldHost;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldUser;
    // End of variables declaration//GEN-END:variables

    static DataSourceFarmacy dataSource = null;
    //static Connection conn = null;
    static JFrameConnection jFrameConnection;
    static JFrameFarmacy jFrameFarmacy;
    Properties props;
    JDialogAbout jFrameAbout = new JDialogAbout(this, true);
}
