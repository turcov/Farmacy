/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui;

import com.iucosoft.farmacy.dao.FarmacyBalanceDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDaoIntf;
import com.iucosoft.farmacy.dao.impl.FarmacyBalanceDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.gui.models.FinSubjComboBoxModel;
import com.iucosoft.farmacy.gui.models.InvoiceTableModel;
import com.iucosoft.farmacy.utils.ExportReports;
import com.iucosoft.farmacy.model.FinanceSubject;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.reports.InvoicesReport;
import com.iucosoft.farmacy.reports.Report;
import com.iucosoft.farmacy.utils.Util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;
import java.awt.print.PrinterException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;

/**
 *
 * @author Turkov S
 */
public class JInternalFrameInvoices extends javax.swing.JInternalFrame {

    private static final Logger LOG = Logger.getLogger(JInternalFrameInvoices.class.getName());

    /**
     * Creates new form JInternalFrameInvoices
     */
    public JInternalFrameInvoices(Class clas, boolean isJDatePicker) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        invoiceClass = clas;
        this.isJDatePicker = isJDatePicker;
        finSubj = ((Invoice) invoiceClass.newInstance()).getNewFinanceSubject();
        invoiceDao = new InvoiceDaoImpl(invoiceClass);
        aFinSubjComboBoxModel = new FinSubjComboBoxModel(finSubj.getClass());
        farmDao = new FarmacyBalanceDaoImpl();

        if (isJDatePicker) {
            SqlDateModel modelMin = new SqlDateModel(invoiceDao.getMinDate(null));
            JDatePanelImpl jDatePanelMin = new JDatePanelImpl(modelMin);
            jDatePickerMin = new JDatePickerImpl(jDatePanelMin);
            SqlDateModel modelMax = new SqlDateModel(invoiceDao.getMaxDate(null));
            JDatePanelImpl jDatePanelMax = new JDatePanelImpl(modelMax);
            jDatePickerMax = new JDatePickerImpl(jDatePanelMax);
        } else {
            jSpinnerDateMin = new JSpinner();
            jSpinnerDateMax = new JSpinner();

            jSpinnerDateMin.setModel(new SpinnerDateModel());
            jSpinnerDateMax.setModel(new SpinnerDateModel());

            jSpinnerDateMin.setEditor(new JSpinner.DateEditor(jSpinnerDateMin, "dd.MM.yyyy"));
            jSpinnerDateMax.setEditor(new JSpinner.DateEditor(jSpinnerDateMax, "dd.MM.yyyy"));
        }
        initComponents();
        initGuiComponents();
        addListeners();
    }

    private void initGuiComponents() throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        super.setTitle(invoiceClass.getSimpleName());
        aInvoiceTableModel = new InvoiceTableModel(invoiceClass);
        jTableInvoices.setModel(aInvoiceTableModel);
        jTableInvoices.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTableInvoices.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableInvoices.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTableInvoices.getColumnModel().getColumn(3).setPreferredWidth(100);
        jComboBoxFinSubject.setModel(aFinSubjComboBoxModel);
        clearForm();
        refreshInvoiceInformationByFinSubjDateSum(indexFinSubject);
        if (isJDatePicker) {
            jPanelInvoices.add(jDatePickerMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 20, -1, -1));
            jPanelInvoices.add(jDatePickerMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, -1, -1));
        } else {
            jPanelInvoices.add(jSpinnerDateMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, -1, -1));
            jPanelInvoices.add(jSpinnerDateMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, -1, -1));
        }
    }

    private void addListeners() {
        jTableInvoices.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                int selectedRow = jTableInvoices.getSelectedRow();
                if (selectedRow >= 0) {
                    int idInvoice = (Integer) jTableInvoices.getValueAt(selectedRow, 0);
                    selectedInvoice = invoiceDao.findInvoiceById(idInvoice);
                } else {
                    selectedInvoice = null;
                }
            }
        });
        if (isJDatePicker) {
            jDatePickerMin.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    refreshInvoiceInformationByFinSubjDateSum(indexFinSubject);
                }
            });

            jDatePickerMax.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    refreshInvoiceInformationByFinSubjDateSum(indexFinSubject);
                }
            });
        } else {
            jSpinnerDateMin.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    //             if (!blockedRefreshing) {
                    refreshInvoiceInformationByFinSubjDateSum(indexFinSubject);
                    //           }
                }
            });
            jSpinnerDateMax.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    //         if (!blockedRefreshing) {
                    refreshInvoiceInformationByFinSubjDateSum(indexFinSubject);
                    //       }
                }
            });
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItemNewInvoice = new javax.swing.JMenuItem();
        jMenuItemEditInvoice = new javax.swing.JMenuItem();
        jMenuItemDeleteInvoice = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableInvoices = new javax.swing.JTable();
        jPanelInvoices = new javax.swing.JPanel();
        jButtonClear = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jFormattedTextFieldSum2 = new javax.swing.JFormattedTextField();
        jFormattedTextFieldSum1 = new javax.swing.JFormattedTextField();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonNewInvoice = new javax.swing.JButton();
        jButtonEditInvoice = new javax.swing.JButton();
        jButtonDeleteInvoice = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonExportExcel = new javax.swing.JButton();
        jButtonExportPDF = new javax.swing.JButton();
        jButtonExportCSV = new javax.swing.JButton();
        jButtonExportJxls = new javax.swing.JButton();
        jButtonPrintTable = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jFormattedTextFieldTotalOfInvoices = new javax.swing.JFormattedTextField();
        jComboBoxFinSubject = new javax.swing.JComboBox();

        jMenuItemNewInvoice.setText("New Invoice");
        jMenuItemNewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewInvoiceActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemNewInvoice);

        jMenuItemEditInvoice.setText("Edit Invoice");
        jMenuItemEditInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditInvoiceActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemEditInvoice);

        jMenuItemDeleteInvoice.setText("Delete Invoice");
        jMenuItemDeleteInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteInvoiceActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemDeleteInvoice);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jTableInvoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableInvoices.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTableInvoices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableInvoices.getTableHeader().setReorderingAllowed(false);
        jTableInvoices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableInvoicesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableInvoicesMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTableInvoices);

        jPanelInvoices.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanelInvoices.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, 60, -1));

        jLabel1.setText("Date Between");
        jPanelInvoices.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, -1, -1));
        jLabel1.getAccessibleContext().setAccessibleName("10");

        jLabel2.setText("and");
        jPanelInvoices.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 25, -1, -1));

        jLabel3.setText("Sum Between");
        jPanelInvoices.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, -1, -1));
        jLabel3.getAccessibleContext().setAccessibleName("10");

        jLabel5.setText("and");
        jPanelInvoices.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 25, -1, -1));

        jFormattedTextFieldSum2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldSum2KeyReleased(evt);
            }
        });
        jPanelInvoices.add(jFormattedTextFieldSum2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 20, 60, -1));

        jFormattedTextFieldSum1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldSum1KeyReleased(evt);
            }
        });
        jPanelInvoices.add(jFormattedTextFieldSum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 20, 60, -1));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButtonNewInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070149_Add file.png"))); // NOI18N
        jButtonNewInvoice.setToolTipText("New Invoice");
        jButtonNewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonNewInvoice);

        jButtonEditInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423072138_bookmarks-edit.png"))); // NOI18N
        jButtonEditInvoice.setToolTipText("Edit Invoice");
        jButtonEditInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonEditInvoice);

        jButtonDeleteInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070622_Cancel File.png"))); // NOI18N
        jButtonDeleteInvoice.setToolTipText("Delete Invoice");
        jButtonDeleteInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDeleteInvoice);
        jToolBar1.add(jSeparator1);

        jButtonExportExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070960_Microsoft Office Excel32.png"))); // NOI18N
        jButtonExportExcel.setToolTipText("Export in Excel");
        jButtonExportExcel.setFocusable(false);
        jButtonExportExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportExcelActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonExportExcel);

        jButtonExportPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423071193_Adobe Acrobat Professional32.png"))); // NOI18N
        jButtonExportPDF.setToolTipText("Export in PDF");
        jButtonExportPDF.setFocusable(false);
        jButtonExportPDF.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportPDF.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportPDFActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonExportPDF);

        jButtonExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/file_csv32.png"))); // NOI18N
        jButtonExportCSV.setToolTipText("Export to CSV");
        jButtonExportCSV.setFocusable(false);
        jButtonExportCSV.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportCSV.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportCSVActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonExportCSV);

        jButtonExportJxls.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1424199566_application-vnd.ms-excel.png"))); // NOI18N
        jButtonExportJxls.setToolTipText("Export Report");
        jButtonExportJxls.setFocusable(false);
        jButtonExportJxls.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportJxls.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportJxls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportJxlsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonExportJxls);

        jButtonPrintTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/printer.png"))); // NOI18N
        jButtonPrintTable.setToolTipText("");
        jButtonPrintTable.setFocusable(false);
        jButtonPrintTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPrintTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPrintTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintTableActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonPrintTable);

        jLabel6.setText("Total of Invoices:  ");
        jToolBar1.add(jLabel6);

        jFormattedTextFieldTotalOfInvoices.setEditable(false);
        jFormattedTextFieldTotalOfInvoices.setBackground(new java.awt.Color(204, 204, 255));
        jFormattedTextFieldTotalOfInvoices.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(",##0.00 MDL"))));
        jToolBar1.add(jFormattedTextFieldTotalOfInvoices);

        jComboBoxFinSubject.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxFinSubjectItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelInvoices, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxFinSubject, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jComboBoxFinSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelInvoices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    void clearForm() {
        jFormattedTextFieldSum1.setText("" + Util.truncDouble(invoiceDao.getMinSumInvoice(indexFinSubject)));
        jFormattedTextFieldSum2.setText("" + Util.roundUp(invoiceDao.getMaxSumInvoice(indexFinSubject)));

        Date minDate = invoiceDao.getMinDate(indexFinSubject);
        Date maxDate = invoiceDao.getMaxDate(indexFinSubject);
        if (isJDatePicker) {
            ((SqlDateModel) jDatePickerMin.getModel()).setValue(invoiceDao.getMinDate(indexFinSubject));
            ((SqlDateModel) jDatePickerMax.getModel()).setValue(invoiceDao.getMaxDate(indexFinSubject));
        } else {
            Date date = invoiceDao.getMinDate(indexFinSubject);
            if (date != null) {
                jSpinnerDateMin.setValue(date);
            } else {
                jSpinnerDateMin.setValue(new Date(0L));
            }
            date = invoiceDao.getMaxDate(indexFinSubject);
            if (date != null) {
                jSpinnerDateMax.setValue(date);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.DAY_OF_YEAR, 365);
                calendar.set(Calendar.MONTH, 12);
                calendar.set(Calendar.YEAR, 2099);
                jSpinnerDateMax.setValue(calendar.getTime());
            }
        }
        jComboBoxFinSubject.setSelectedIndex(0);
    }

    private void editInvoice() {
        try {
            jDialogInvoiceDetails = new JDialogInvoiceDetails(null, true, selectedInvoice, isJDatePicker);
            jDialogInvoiceDetails.setVisible(true);
            aInvoiceTableModel.refreshModel();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void jButtonNewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewInvoiceActionPerformed
        try {
            selectedInvoice = (Invoice) invoiceClass.newInstance();
            selectedInvoice.setDateInvoice(new Date(new java.util.Date().getTime()));
            invoiceDao.addInvoice(selectedInvoice);
        } catch (InstantiationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        editInvoice();
    }//GEN-LAST:event_jButtonNewInvoiceActionPerformed

    private void jButtonEditInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditInvoiceActionPerformed
        if (selectedInvoice != null) {
            editInvoice();
        } else {
            JOptionPane.showMessageDialog(this, "Please select invoice to edit");
        }
    }//GEN-LAST:event_jButtonEditInvoiceActionPerformed

    private void jButtonDeleteInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteInvoiceActionPerformed
        if (selectedInvoice != null) {
            int rez = JOptionPane.showConfirmDialog(this, "Are you sure to delete invoice " + selectedInvoice + "?");
            if (rez == JOptionPane.OK_OPTION) {
                try {
                    invoiceDao.deleteInvoice(selectedInvoice);
                    aInvoiceTableModel.refreshModel();
                } catch (ConnectionInterruptedException | StockException | NoSuchMoneyException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select invoice to delete");
        }
    }//GEN-LAST:event_jButtonDeleteInvoiceActionPerformed

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated

    }//GEN-LAST:event_formInternalFrameActivated

    private void jTableInvoicesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInvoicesMousePressed
        if (evt.getButton() == BUTTON1 && evt.getClickCount() == 2) {
            //jTableInvoices.getCellEditor().stopCellEditing();
            editInvoice();
        }
        if (evt.isPopupTrigger()) {
            jPopupMenu1.show(jTableInvoices, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableInvoicesMousePressed

    private void jTableInvoicesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInvoicesMouseReleased
        if (evt.getButton() == BUTTON3) {
            int row = jTableInvoices.rowAtPoint(evt.getPoint());
            jTableInvoices.setRowSelectionInterval(row, row);
        }
        if (evt.isPopupTrigger()) {
            jPopupMenu1.show(jTableInvoices, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableInvoicesMouseReleased

    private void jMenuItemNewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewInvoiceActionPerformed
        jButtonNewInvoiceActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemNewInvoiceActionPerformed

    private void jMenuItemEditInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditInvoiceActionPerformed
        jButtonEditInvoiceActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemEditInvoiceActionPerformed

    private void jMenuItemDeleteInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteInvoiceActionPerformed
        jButtonDeleteInvoiceActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemDeleteInvoiceActionPerformed

    private Date[] getPeriod() {
        Date date1;
        Date date2;
        if (isJDatePicker) {
            date1 = (Date) jDatePickerMin.getModel().getValue();
            date2 = (Date) jDatePickerMax.getModel().getValue();
        } else {
            date1 = Util.parseToDateSql((java.util.Date) jSpinnerDateMin.getModel().getValue());
            date2 = Util.parseToDateSql((java.util.Date) jSpinnerDateMax.getModel().getValue());
        }
        return new Date[]{date1, date2};
    }

    private Object[] createExportView() {
        String header;
        String footer;
        Date minDate = getPeriod()[0];
        Date maxDate = getPeriod()[1];
        header = "period of invoices from " + minDate.toString()
                + " to " + maxDate.toString();
        footer = "Total sum of invoices is " + Util.roundTo((double) jFormattedTextFieldTotalOfInvoices.getValue()) + " MDL";

        return new Object[]{jTableInvoices, header, footer};
    }

    private void jButtonExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportExcelActionPerformed
        Object views[] = createExportView();
        ExportReports.exportTableModelDialog(
                "xls", (JTable) views[0], (String) views[1], (String) views[2]);

    }//GEN-LAST:event_jButtonExportExcelActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        clearForm();
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jComboBoxFinSubjectItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxFinSubjectItemStateChanged
//        if (blockedRefreshing) {
//            return;
//        }
        if (jComboBoxFinSubject.getSelectedIndex() == 0) {
            indexFinSubject = null;
        } else {
            finSubj = (FinanceSubject) jComboBoxFinSubject.getSelectedItem();
            indexFinSubject = finSubj.getId();
        }
        refreshInvoiceInformationByFinSubjDateSum(indexFinSubject);
    }//GEN-LAST:event_jComboBoxFinSubjectItemStateChanged

    private void jButtonExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportPDFActionPerformed
        Object views[] = createExportView();
        ExportReports.exportTableModelDialog(
                "pdf", (JTable) views[0], (String) views[1], (String) views[2]);

    }//GEN-LAST:event_jButtonExportPDFActionPerformed

    private void jButtonPrintTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintTableActionPerformed
        try {
            Object views[] = createExportView();
            MessageFormat header = new MessageFormat((String) views[2]);
            MessageFormat footer = new MessageFormat((String) views[1] + ". Page - {0}");
            JTable jTable = (JTable) views[0];
            boolean complete = jTable.print(JTable.PrintMode.FIT_WIDTH, header, footer, false, null, false);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Table was printed succesfuly");
            } else {
                JOptionPane.showMessageDialog(this, "Printing has been cancelled");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", ERROR_MESSAGE);
            LOG.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonPrintTableActionPerformed

    private void jButtonExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportCSVActionPerformed
        Object views[] = createExportView();
        ExportReports.exportTableModelDialog(
                "csv", (JTable) views[0], null, null);
    }//GEN-LAST:event_jButtonExportCSVActionPerformed

    private void jFormattedTextFieldSum2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldSum2KeyReleased
        refreshInvoiceInformationByFinSubjDateSum(indexFinSubject);
    }//GEN-LAST:event_jFormattedTextFieldSum2KeyReleased

    private void jButtonExportJxlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportJxlsActionPerformed
        Report report = new Report();
        report.setFarmacyName(farmDao.getFarmacyName());
        report.setInnerData(jTableInvoices.getColumnName(1));
        report.setHeader("Period: " + getPeriod()[0].toString()
                + "-" + getPeriod()[1].toString()
                + "Summ between " + jFormattedTextFieldSum1.getText()
                + " and " + jFormattedTextFieldSum2.getText());
        report.setFooter(jFormattedTextFieldTotalOfInvoices.getText());
        int rows = jTableInvoices.getRowCount();
        List reportList = new ArrayList();
        for (int i = 0; i < rows; i++) {
            reportList.add(new InvoicesReport(
                    (Integer) jTableInvoices.getValueAt(i, 0),
                    (String) jTableInvoices.getValueAt(i, 1),
                    (Date) jTableInvoices.getValueAt(i, 2),
                    (String) jTableInvoices.getValueAt(i, 3)));
        }
        report.setData(reportList);
        ExportReports.exportJxlsDialog(report, "InvoicesReport", "invoicesreport.xls");
    }//GEN-LAST:event_jButtonExportJxlsActionPerformed

    private void jFormattedTextFieldSum1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldSum1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextFieldSum1KeyReleased

    private void refreshInvoiceInformationByFinSubjDateSum(Integer idFinSubj) {
        Date dateMinSql = getPeriod()[0];
        Date dateMaxSql = getPeriod()[1];
        if (jFormattedTextFieldSum1.getText().equals("")) {
            jFormattedTextFieldSum1.setText("" + Util.roundTo(invoiceDao.getMinSumInvoice(finSubj.getId())));
        }
        if (jFormattedTextFieldSum2.getText().equals("")) {
            jFormattedTextFieldSum2.setText("" + Util.roundUp(invoiceDao.getMaxSumInvoice(finSubj.getId())));
        }
        invoicesList = invoiceDao.findInvoicesByIdFinSubjDateSumma(idFinSubj,
                dateMinSql, dateMaxSql,
                Double.parseDouble(jFormattedTextFieldSum1.getText()),
                Double.parseDouble(jFormattedTextFieldSum2.getText()));
        aInvoiceTableModel.refreshModel(invoicesList);
        jFormattedTextFieldTotalOfInvoices.setValue(
                invoiceDao.getSumOfInvoices(idFinSubj,
                        dateMinSql, dateMaxSql,
                        Double.parseDouble(jFormattedTextFieldSum1.getText()),
                        Double.parseDouble(jFormattedTextFieldSum2.getText())));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonDeleteInvoice;
    private javax.swing.JButton jButtonEditInvoice;
    private javax.swing.JButton jButtonExportCSV;
    private javax.swing.JButton jButtonExportExcel;
    private javax.swing.JButton jButtonExportJxls;
    private javax.swing.JButton jButtonExportPDF;
    private javax.swing.JButton jButtonNewInvoice;
    private javax.swing.JButton jButtonPrintTable;
    private javax.swing.JComboBox jComboBoxFinSubject;
    private javax.swing.JFormattedTextField jFormattedTextFieldSum1;
    private javax.swing.JFormattedTextField jFormattedTextFieldSum2;
    private javax.swing.JFormattedTextField jFormattedTextFieldTotalOfInvoices;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuItem jMenuItemDeleteInvoice;
    private javax.swing.JMenuItem jMenuItemEditInvoice;
    private javax.swing.JMenuItem jMenuItemNewInvoice;
    private javax.swing.JPanel jPanelInvoices;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTable jTableInvoices;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    InvoiceDaoIntf invoiceDao;
    InvoiceTableModel aInvoiceTableModel;
    FinanceSubject finSubj;
    Class invoiceClass;
    Invoice selectedInvoice;
    List<Invoice> invoicesList;
    JDialogInvoiceDetails jDialogInvoiceDetails;
    FinSubjComboBoxModel aFinSubjComboBoxModel;
    Integer indexFinSubject;
    FarmacyBalanceDaoIntf farmDao;
    boolean isJDatePicker;
    JDatePickerImpl jDatePickerMin;
    JDatePickerImpl jDatePickerMax;
    JSpinner jSpinnerDateMin;
    JSpinner jSpinnerDateMax;
}
