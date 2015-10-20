/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 *
 * @author Turkov S
 */
public class JWindowIconMedicament extends JWindow {
    JLabel jLabelMedIcon ;
    JPanel jPanel;

    public JWindowIconMedicament() {
        super.setSize(190, 90);
        super.setAlwaysOnTop(true);
        jLabelMedIcon = new JLabel();
  //      jLabelMedIcon.setMinimumSize(new Dimension(185, 85));
    //    jLabelMedIcon.setMaximumSize(new Dimension(185, 85));
      //  jLabelMedIcon.setPreferredSize(new Dimension(185, 85));
      //  jLabelMedIcon.setSize(185, 85);
        jLabelMedIcon.setIcon(new ImageIcon("C:\\Medicaments_images\\Decaris.jpeg"));
        jPanel=new JPanel();
        jPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));
        jPanel.setLayout(new BorderLayout());
        jPanel.add(jLabelMedIcon);
        super.add(jPanel);
    }

    public void setIcon(byte[] iconByte) {
        if (iconByte == null) {
            jLabelMedIcon.setIcon(new ImageIcon());
            jLabelMedIcon.setText("No Image");
        } else {
            jLabelMedIcon.setText("");
            jLabelMedIcon.setIcon(new ImageIcon(iconByte));
        }
    }

    public static void main(String[] args) {
        JWindowIconMedicament jWindow = new JWindowIconMedicament();
        jWindow.setVisible(true);
    }

}
