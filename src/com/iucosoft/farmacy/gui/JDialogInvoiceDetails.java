/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui;

import com.iucosoft.farmacy.dao.CategoryDaoIntf;
import com.iucosoft.farmacy.dao.FarmacyBalanceDaoIntf;
import com.iucosoft.farmacy.dao.FinanceSubjectDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDetailDaoIntf;
import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.dao.PriceDaoIntf;
import com.iucosoft.farmacy.dao.StockDaoIntf;
import com.iucosoft.farmacy.dao.impl.CategoryDaoImpl;
import com.iucosoft.farmacy.dao.impl.FarmacyBalanceDaoImpl;
import com.iucosoft.farmacy.dao.impl.FinanceSubjectDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDetailDaoImpl;
import com.iucosoft.farmacy.dao.impl.MedicamentDaoImpl;
import com.iucosoft.farmacy.dao.impl.PriceDaoImpl;
import com.iucosoft.farmacy.dao.impl.StockDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.gui.models.FinSubjComboBoxModel;
import com.iucosoft.farmacy.gui.models.InvoiceDetailsTableModel;
import com.iucosoft.farmacy.gui.models.MedicamentComboBoxModel;
import com.iucosoft.farmacy.utils.ExportReports;
import com.iucosoft.farmacy.model.FinanceSubject;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.model.InvoiceDetail;
import com.iucosoft.farmacy.model.Medicament;
import com.iucosoft.farmacy.model.Stock;
import com.iucosoft.farmacy.reports.InvoiceReport;
import com.iucosoft.farmacy.reports.Report;
import com.iucosoft.farmacy.utils.Util;
import static com.iucosoft.farmacy.utils.Util.selectRowInTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

/**
 *
 * @author Turkov S
 */
public class JDialogInvoiceDetails extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(JDialogInvoiceDetails.class.getName());

    /**
     * Creates new form JDialogInvoiceDetails
     */
    public JDialogInvoiceDetails(java.awt.Frame parent, boolean modal, Invoice invoice, boolean isJDatePicker)
            throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        super(parent, modal);
        this.invoice = invoice;
        this.isJDatePicker = isJDatePicker;
        invoiceDao = new InvoiceDaoImpl(invoice.getClass());
        invoiceDetailClass = invoice.getInvoiceDetailClass();
        finSubjClass = invoice.getFinanceSubjectClass();
        finSubjDao = new FinanceSubjectDaoImpl(finSubjClass);
        invoiceDetailDao = new InvoiceDetailDaoImpl(invoiceDetailClass);
        medDao = new MedicamentDaoImpl();
        priceDao = new PriceDaoImpl();
        stockDao = new StockDaoImpl();
        farmDao = new FarmacyBalanceDaoImpl();
        catDao = new CategoryDaoImpl();
        initComponents();
        initGuiComponents();
        addListeners();
        setLocationRelativeTo(parent);
    }

    private void initGuiComponents() throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        super.setTitle(invoiceDetailClass.getSimpleName());
        jLabelFinSubject.setText(finSubjClass.getSimpleName());

        aMedicamentComboBoxModel = new MedicamentComboBoxModel();
        jComboBoxMedicament.setModel(aMedicamentComboBoxModel);

        aFinSubjComboBoxModel = new FinSubjComboBoxModel(finSubjClass);
        jComboBoxNameFinSubj.setModel(aFinSubjComboBoxModel);

        jTextFieldIdInvoice.setText("" + invoice.getIdInvoice());

        if (isJDatePicker) {
            SqlDateModel model = new SqlDateModel(invoice.getDateInvoice());
            JDatePanelImpl jDatePanel = new JDatePanelImpl(model);
            jDatePickerInvoice = new JDatePickerImpl(jDatePanel);
            //jDatePickerInvoice.setMaximumSize(new Dimension(100, 30));
            jPanelInvoiceHeader.add(jDatePickerInvoice, new AbsoluteConstraints(185, 11, -1, -1));
        } else {
            jSpinnerDateInvoice = new JSpinner();
            jSpinnerDateInvoice.setModel(new SpinnerDateModel());
            jSpinnerDateInvoice.setEditor(new DateEditor(jSpinnerDateInvoice, "dd.MM.yyyy"));
            jSpinnerDateInvoice.setValue(invoice.getDateInvoice());
            //jSpinnerDateInvoice.setMaximumSize(new Dimension(100, 30));
            jPanelInvoiceHeader.add(jSpinnerDateInvoice, new AbsoluteConstraints(185, 11, -1, -1));
        }
        int idFinSubj = invoice.getIdFinSubj();
        if (idFinSubj != 0) {
            FinanceSubject finSubj = finSubjDao.findFinSubjectById(idFinSubj);
            jComboBoxNameFinSubj.setSelectedItem(finSubj);
        } else {
            jComboBoxNameFinSubj.setSelectedIndex(0);
        }

        jFormattedTextTotal.setValue(invoice.getTotalInvoice());

        aInvoiceDetailsTableModel = new InvoiceDetailsTableModel(invoice);
        jTableInvoiceDetails.setModel(aInvoiceDetailsTableModel);
        jTableInvoiceDetails.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTableInvoiceDetails.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTableInvoiceDetails.getColumnModel().getColumn(2).setPreferredWidth(50);
        jTableInvoiceDetails.getColumnModel().getColumn(3).setPreferredWidth(70);
        jTableInvoiceDetails.getColumnModel().getColumn(4).setPreferredWidth(100);
        jButtonSaveInvoice.setEnabled(false);

    }

    private void addListeners() {
        jTableInvoiceDetails.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (isAddingMode) {
                    return;
                }
                if (jButtonSaveInvoice.isEnabled()) {
                    try {
                        jButtonSaveInvoiceActionPerformedThrows();
                        jButtonSaveInvoice.setEnabled(false);
                        jButtonSaveDetail.setEnabled(false);
                        jButtonRevertDetail.setEnabled(false);
                        setButtonsExportOnEditingAdding(true);
                    } catch (StockException | NoSuchMoneyException ex) {
                        //JOptionPane.showMessageDialog(null, ex.getMessage() + " at row=" + selectedRow, "ERROR", ERROR_MESSAGE);
                        selectedInvoiceDetail = invoiceDetailDao.findInvoiceDetailByIdIdInvoice(selectedInvoiceDetail.getId(), invoice.getIdInvoice());
                        refreshTableInvoiceDetails(selectedRow);
                        updateInvoiceInformation();
                        //return;
                    }
                }
                selectedRow = jTableInvoiceDetails.getSelectedRow();
                if (selectedRow >= 0) {
                    int idInvoiceDetail = (Integer) jTableInvoiceDetails.getValueAt(selectedRow, 0);
                    selectedInvoiceDetail = invoiceDetailDao.findInvoiceDetailByIdIdInvoice(idInvoiceDetail, invoice.getIdInvoice());
                    fillFormInvoiceDetail();
                    jButtonDeleteDetail.setEnabled(true);
                    enableComponents(true);
                    isAddingMode = false;
                } else {
                    selectedRow = 0;
                    selectedInvoiceDetail = null;
                    jButtonDeleteDetail.setEnabled(false);
                    enableComponents(false);
                }
                setTextAreaInfo();
            }
        });

        if (isJDatePicker) {
            jDatePickerInvoice.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    jButtonSaveInvoice.setEnabled(true);
                }
            });
        } else {
            jSpinnerDateInvoice.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    jButtonSaveInvoice.setEnabled(true);
                }
            });
        }
        final JFormattedTextField jftf = (JFormattedTextField) jSpinnerQuantity.getEditor().getComponent(0);
        jftf.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent ke) {
                try {
                    int oldCaretPos = jftf.getCaretPosition();
                    String value = jftf.getText();
                    Double newValue = Double.valueOf("0" + value);
                    jSpinnerQuantity.setValue(newValue);
                    jftf.setCaretPosition(oldCaretPos);
                } catch (NumberFormatException ex) {

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

        jPopupMenuAnalogs = new javax.swing.JPopupMenu();
        jMenuItemChooseAnalogs = new javax.swing.JMenuItem();
        jPopupMenuFinSubj = new javax.swing.JPopupMenu();
        jMenuItemAddNewFinSubject = new javax.swing.JMenuItem();
        jPopupMenuDetails = new javax.swing.JPopupMenu();
        jMenuItemAddRow = new javax.swing.JMenuItem();
        jMenuItemDeleteRow = new javax.swing.JMenuItem();
        jPanelInvoiceHeader = new javax.swing.JPanel();
        jComboBoxNameFinSubj = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jFormattedTextTotal = new javax.swing.JFormattedTextField();
        jLabelFinSubject = new javax.swing.JLabel();
        jTextFieldIdInvoice = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableInvoiceDetails = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jComboBoxMedicament = new javax.swing.JComboBox();
        jButtonRevertDetail = new javax.swing.JButton();
        jFormattedTextFieldPrice = new javax.swing.JFormattedTextField();
        jButtonAddDetail = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButtonSaveDetail = new javax.swing.JButton();
        jFormattedTextFieldSum = new javax.swing.JFormattedTextField();
        jSpinnerQuantity = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButtonDeleteDetail = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldStock = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaInfoMedicament = new javax.swing.JTextArea();
        jButtonExportExcel = new javax.swing.JButton();
        jButtonExportPdf = new javax.swing.JButton();
        jButtonSaveInvoice = new javax.swing.JButton();
        jButtonPrint = new javax.swing.JButton();
        jButtonExportCSV = new javax.swing.JButton();
        jButtonExportJxls = new javax.swing.JButton();

        jMenuItemChooseAnalogs.setText("Choose Analogs(below)");
        jMenuItemChooseAnalogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChooseAnalogsActionPerformed(evt);
            }
        });
        jPopupMenuAnalogs.add(jMenuItemChooseAnalogs);

        jMenuItemAddNewFinSubject.setText("Add new");
        jMenuItemAddNewFinSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddNewFinSubjectActionPerformed(evt);
            }
        });
        jPopupMenuFinSubj.add(jMenuItemAddNewFinSubject);

        jPopupMenuDetails.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenuDetailsPopupMenuWillBecomeVisible(evt);
            }
        });

        jMenuItemAddRow.setText("Add Row");
        jMenuItemAddRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddRowActionPerformed(evt);
            }
        });
        jPopupMenuDetails.add(jMenuItemAddRow);

        jMenuItemDeleteRow.setText("Delete Row");
        jMenuItemDeleteRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteRowActionPerformed(evt);
            }
        });
        jPopupMenuDetails.add(jMenuItemDeleteRow);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelInvoiceHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBoxNameFinSubj.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxNameFinSubjItemStateChanged(evt);
            }
        });
        jComboBoxNameFinSubj.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jComboBoxNameFinSubjMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jComboBoxNameFinSubjMouseReleased(evt);
            }
        });
        jPanelInvoiceHeader.add(jComboBoxNameFinSubj, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 11, 160, -1));

        jLabel4.setText("Total");
        jPanelInvoiceHeader.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(615, 14, -1, -1));

        jLabel2.setText("Invoice Date");
        jPanelInvoiceHeader.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 14, -1, -1));

        jLabel1.setText("Invoice N");
        jPanelInvoiceHeader.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 14, -1, -1));

        jFormattedTextTotal.setEditable(false);
        jFormattedTextTotal.setBackground(new java.awt.Color(204, 204, 255));
        jFormattedTextTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 MDL"))));
        jPanelInvoiceHeader.add(jFormattedTextTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(645, 11, 91, -1));

        jLabelFinSubject.setText("FinSubj");
        jPanelInvoiceHeader.add(jLabelFinSubject, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 14, -1, -1));

        jTextFieldIdInvoice.setEditable(false);
        jTextFieldIdInvoice.setBackground(new java.awt.Color(204, 204, 255));
        jPanelInvoiceHeader.add(jTextFieldIdInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 11, 38, -1));

        jScrollPane1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseMoved(evt);
            }
        });

        jTableInvoiceDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "NN", "Medicament", "Quantity", "Price", "Summ"
            }
        ));
        jTableInvoiceDetails.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableInvoiceDetails.getTableHeader().setReorderingAllowed(false);
        jTableInvoiceDetails.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jTableInvoiceDetailsMouseMoved(evt);
            }
        });
        jTableInvoiceDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableInvoiceDetailsMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableInvoiceDetailsMouseReleased(evt);
            }
        });
        jTableInvoiceDetails.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTableInvoiceDetailsKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTableInvoiceDetails);

        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel1MouseMoved(evt);
            }
        });

        jComboBoxMedicament.setToolTipText("Press right button to select analog");
        jComboBoxMedicament.setEnabled(false);
        jComboBoxMedicament.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMedicamentItemStateChanged(evt);
            }
        });
        jComboBoxMedicament.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jComboBoxMedicamentMouseMoved(evt);
            }
        });
        jComboBoxMedicament.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jComboBoxMedicamentMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jComboBoxMedicamentMouseReleased(evt);
            }
        });

        jButtonRevertDetail.setText("Revert");
        jButtonRevertDetail.setToolTipText("Cancel Changes");
        jButtonRevertDetail.setEnabled(false);
        jButtonRevertDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRevertDetailActionPerformed(evt);
            }
        });

        jFormattedTextFieldPrice.setEditable(false);
        jFormattedTextFieldPrice.setBackground(new java.awt.Color(204, 204, 255));
        jFormattedTextFieldPrice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 MDL"))));

        jButtonAddDetail.setText("Add row");
        jButtonAddDetail.setToolTipText("Add new record");
        jButtonAddDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddDetailActionPerformed(evt);
            }
        });

        jLabel7.setText("Quantit");

        jButtonSaveDetail.setText("Save");
        jButtonSaveDetail.setToolTipText("Save this row");
        jButtonSaveDetail.setEnabled(false);
        jButtonSaveDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveDetailActionPerformed(evt);
            }
        });

        jFormattedTextFieldSum.setEditable(false);
        jFormattedTextFieldSum.setBackground(new java.awt.Color(204, 204, 255));
        jFormattedTextFieldSum.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 MDL"))));

        jSpinnerQuantity.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(1.0d)));
        jSpinnerQuantity.setEnabled(false);
        jSpinnerQuantity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerQuantityStateChanged(evt);
            }
        });
        jSpinnerQuantity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSpinnerQuantityFocusGained(evt);
            }
        });
        jSpinnerQuantity.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jSpinnerQuantityMouseWheelMoved(evt);
            }
        });
        jSpinnerQuantity.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                jSpinnerQuantityCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jSpinnerQuantityInputMethodTextChanged(evt);
            }
        });
        jSpinnerQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jSpinnerQuantityKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jSpinnerQuantityKeyReleased(evt);
            }
        });

        jLabel6.setText("Medicament");

        jLabel8.setText("Price of unit");

        jButtonDeleteDetail.setText("Delete row");
        jButtonDeleteDetail.setToolTipText("Delete selected record");
        jButtonDeleteDetail.setEnabled(false);
        jButtonDeleteDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteDetailActionPerformed(evt);
            }
        });

        jLabel9.setText("Summ");

        jLabel3.setText("Stock");

        jTextFieldStock.setEditable(false);
        jTextFieldStock.setBackground(new java.awt.Color(204, 204, 255));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextAreaInfoMedicament.setEditable(false);
        jTextAreaInfoMedicament.setBackground(new java.awt.Color(204, 204, 255));
        jTextAreaInfoMedicament.setColumns(20);
        jTextAreaInfoMedicament.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextAreaInfoMedicament.setRows(4);
        jTextAreaInfoMedicament.setText("Latin name:\nLatin name\nCategory:\nCategory");
        jTextAreaInfoMedicament.setToolTipText("Medicament info");
        jScrollPane2.setViewportView(jTextAreaInfoMedicament);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonAddDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonDeleteDetail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxMedicament, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinnerQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldStock, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextFieldPrice)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextFieldSum, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonSaveDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonRevertDetail))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(26, 26, 26))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jFormattedTextFieldSum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFormattedTextFieldPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonAddDetail)
                                .addComponent(jButtonSaveDetail))
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonRevertDetail)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonDeleteDetail)
                                .addComponent(jComboBoxMedicament, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(6, 6, 6))
        );

        jButtonExportExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070960_Microsoft Office Excel32.png"))); // NOI18N
        jButtonExportExcel.setToolTipText("Export to Excel");
        jButtonExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportExcelActionPerformed(evt);
            }
        });

        jButtonExportPdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423071193_Adobe Acrobat Professional32.png"))); // NOI18N
        jButtonExportPdf.setToolTipText("Export to PDF");
        jButtonExportPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportPdfActionPerformed(evt);
            }
        });

        jButtonSaveInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1421167929_Go.png"))); // NOI18N
        jButtonSaveInvoice.setToolTipText("Save Invoice");
        jButtonSaveInvoice.setEnabled(false);
        jButtonSaveInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveInvoiceActionPerformed(evt);
            }
        });

        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/printer.png"))); // NOI18N
        jButtonPrint.setToolTipText("Print");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });

        jButtonExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/file_csv32.png"))); // NOI18N
        jButtonExportCSV.setToolTipText("Export to CSV");
        jButtonExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportCSVActionPerformed(evt);
            }
        });

        jButtonExportJxls.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1424199566_application-vnd.ms-excel.png"))); // NOI18N
        jButtonExportJxls.setToolTipText("Export Report");
        jButtonExportJxls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportJxlsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelInvoiceHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonSaveInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExportCSV, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExportExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExportJxls, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelInvoiceHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonSaveInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jButtonExportExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExportCSV, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExportJxls, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRevertDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRevertDetailActionPerformed
        if (isAddingMode) {
            jButtonSaveDetail.setEnabled(false);
            if (jTableInvoiceDetails.getRowCount() > 1) {
                int idInvoiceDetail = (Integer) jTableInvoiceDetails.getValueAt(selectedRow, 0);
                selectedInvoiceDetail = invoiceDetailDao.findInvoiceDetailByIdIdInvoice(idInvoiceDetail, invoice.getIdInvoice());
                aInvoiceDetailsTableModel.removeRow(aInvoiceDetailsTableModel.getRowCount() - 1);
                isAddingMode = false;
                jTableInvoiceDetails.setRowSelectionInterval(selectedRow, selectedRow);
            } else {
                aInvoiceDetailsTableModel.removeRow(0);
                enableComponents(false);
                isAddingMode = false;
            }
        } else if (jTableInvoiceDetails.getRowCount() == 0) {
            clearFormInvoiceDetail();
        } else {
            refreshTableInvoiceDetails(selectedRow);
        }
        fillFormInvoiceDetail();
        jButtonSaveDetail.setEnabled(false);
        if (jTableInvoiceDetails.getRowCount() > 0) {
            jButtonDeleteDetail.setEnabled(true);
        }
        jTableInvoiceDetails.setEnabled(true);
        jButtonAddDetail.setEnabled(true);
        jButtonRevertDetail.setEnabled(false);
    }//GEN-LAST:event_jButtonRevertDetailActionPerformed

    private void enableComponents(boolean enable) {
        jComboBoxMedicament.setEnabled(enable);
        jSpinnerQuantity.setEnabled(enable);
        jFormattedTextFieldPrice.setEnabled(enable);
        jFormattedTextFieldSum.setEnabled(enable);
    }

    private void clearFormInvoiceDetail() {
        jComboBoxMedicament.setSelectedIndex(0);
        jSpinnerQuantity.setValue(0.0);
        jFormattedTextFieldPrice.setValue(0.0);
        jFormattedTextFieldSum.setValue(0.0);
        jTextFieldStock.setText("");
    }

    private void fillFormInvoiceDetail() {
        if (jTableInvoiceDetails.getRowCount() == 0) {
            return;
        }
        selectedMedicament = medDao.findByIdMedicament(selectedInvoiceDetail.getIdMedicament());
        resfreshStockInformation();
        jSpinnerQuantity.setValue(selectedInvoiceDetail.getQuantity());
        jFormattedTextFieldPrice.setValue(selectedInvoiceDetail.getPrice());
        jFormattedTextFieldSum.setValue(selectedInvoiceDetail.getTotal());
        jComboBoxMedicament.setSelectedItem(selectedMedicament);
    }

    private boolean saveUpdateInvoice() throws StockException, NoSuchMoneyException {
        if (jButtonSaveDetail.isEnabled()) {
            jButtonSaveDetailActionPerformedThrows();
        }
        if (isJDatePicker) {
            invoice.setDateInvoice((Date) jDatePickerInvoice.getModel().getValue());
        } else {
            invoice.setDateInvoice(Util.parseToDateSql((java.util.Date) jSpinnerDateInvoice.getValue()));
        }
        invoice.setIdFinObj(((FinanceSubject) jComboBoxNameFinSubj.getSelectedItem()).getId());
        invoice.setTotalInvoice(invoiceDao.findInvoiceById(invoice.getIdInvoice()).getTotalInvoice());
        if (jComboBoxNameFinSubj.getSelectedIndex() != 0) {
            invoiceDao.updateInvoice(invoice);
            return true;
        }
        return false;
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            if (saveUpdateInvoice()) {
                dispose();
            } else {
                int rez = JOptionPane.showConfirmDialog(this, "has no choosed a " + finSubjClass.getSimpleName() + ". Delete invoice?");
                if (rez == JOptionPane.OK_OPTION) {
                    invoiceDao.deleteInvoice(invoice);
                    dispose();
                }
            }
        } catch (ConnectionInterruptedException | StockException | NoSuchMoneyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
            if(!isAddingMode)selectedInvoiceDetail = invoiceDetailDao.findInvoiceDetailByIdIdInvoice(selectedInvoiceDetail.getId(), selectedInvoiceDetail.getIdInvoice());
        }
    }//GEN-LAST:event_formWindowClosing

    private void setRowsPreview() {
        int row = selectedRow;
        if (isAddingMode) {
            row = jTableInvoiceDetails.getRowCount() - 1;
            if (row < 0) {
                return;
            }
        }
        if (jComboBoxMedicament.getSelectedIndex() > 0) {
            jTableInvoiceDetails.setValueAt(selectedMedicament.getNameMedicament(), row, 1);
            jTableInvoiceDetails.setValueAt(jSpinnerQuantity.getValue(), row, 2);
            jTableInvoiceDetails.setValueAt(jFormattedTextFieldPrice.getText(), row, 3);
            jTableInvoiceDetails.setValueAt(jFormattedTextFieldSum.getText(), row, 4);
        } else {
            jTableInvoiceDetails.setValueAt("", row, 1);
            jTableInvoiceDetails.setValueAt("", row, 2);
            jTableInvoiceDetails.setValueAt("", row, 3);
            jTableInvoiceDetails.setValueAt("", row, 4);
        }
    }

    private void setButtonsExportOnEditingAdding(boolean enable) {
        jButtonExportExcel.setEnabled(enable);
        jButtonExportPdf.setEnabled(enable);
        jButtonExportCSV.setEnabled(enable);
        jButtonExportJxls.setEnabled(enable);
        jButtonPrint.setEnabled(enable);
    }

    private void setTextAreaInfo() {
        if (selectedMedicament != null) {
            String latinName = "" + selectedMedicament.getLatinNameMedicament();
            int idCat = selectedMedicament.getIdCategory();
            String category;
            if (idCat == 0) {
                category = "No category";
            } else {
                category = "" + catDao.findByIdCategory(idCat).getNameCategory();
            }
            jTextAreaInfoMedicament.setText("Latin Name:\n" + latinName + "\nCategory:\n" + category);
        } else {
            jTextAreaInfoMedicament.setText("");
        }
    }

    private void jComboBoxMedicamentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxMedicamentItemStateChanged
        if (jComboBoxMedicament.getSelectedIndex() > 0) {
            Medicament newMedicament = (Medicament) jComboBoxMedicament.getSelectedItem();
            //JOptionPane.showMessageDialog(this,newMedicament);
            if (!newMedicament.equals(selectedMedicament)) {
                selectedMedicament = newMedicament;
                resfreshStockInformation();
                setButtonsExportOnEditingAdding(false);
                jButtonSaveDetail.setEnabled(true);
                jButtonRevertDetail.setEnabled(true);
                jButtonAddDetail.setEnabled(false);
                jButtonDeleteDetail.setEnabled(false);
                jButtonSaveInvoice.setEnabled(true);
                double newPrice = Util.getPriceofMedicament(priceDao.findPriceById(selectedMedicament.getIdMedicament()), invoice);
                jFormattedTextFieldPrice.setValue(newPrice);
                jFormattedTextFieldSum.setValue((Double) jSpinnerQuantity.getValue() * newPrice);
                jTextAreaInfoMedicament.setText("");
            }
        } else {
            jFormattedTextFieldPrice.setValue(0.0);
            jFormattedTextFieldSum.setValue(0.0);
            setButtonsExportOnEditingAdding(false);
            jButtonSaveDetail.setEnabled(false);
            jButtonSaveInvoice.setEnabled(false);
            jButtonRevertDetail.setEnabled(true);
            selectedMedicament = null;
        }
        setTextAreaInfo();
        setRowsPreview();
    }//GEN-LAST:event_jComboBoxMedicamentItemStateChanged

    private void jSpinnerQuantityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerQuantityStateChanged
        double newQuantity = (Double) jSpinnerQuantity.getValue();
        if (jComboBoxMedicament.getSelectedIndex() > 0 && selectedInvoiceDetail != null && newQuantity != selectedInvoiceDetail.getQuantity()) {
            setButtonsExportOnEditingAdding(false);
            jButtonSaveDetail.setEnabled(true);
            jButtonRevertDetail.setEnabled(true);
            jButtonAddDetail.setEnabled(false);
            jButtonDeleteDetail.setEnabled(false);
            jButtonSaveInvoice.setEnabled(true);
            jFormattedTextFieldSum.setValue(newQuantity * (Double) jFormattedTextFieldPrice.getValue());
            setRowsPreview();
        }
    }//GEN-LAST:event_jSpinnerQuantityStateChanged

    private void refreshTableInvoiceDetails() {
        aInvoiceDetailsTableModel.refreshModel(selectedInvoiceDetail);
        int row = jTableInvoiceDetails.getRowCount() - 1;
        selectRowInTable(jTableInvoiceDetails, row);
    }

    private void refreshTableInvoiceDetails(int row) {
        aInvoiceDetailsTableModel.refreshModel(row, selectedInvoiceDetail);
    }

    private void updateInvoiceInformation() {
        invoice = invoiceDao.findInvoiceById(invoice.getIdInvoice());
        jFormattedTextTotal.setValue((Double) invoice.getTotalInvoice());
    }

    private void resfreshStockInformation() {
        if (selectedMedicament != null) {
            Stock stock = stockDao.findByIdStock(selectedMedicament.getIdMedicament());
            jTextFieldStock.setText("" + stock.getBalance());
        } else {
            jTextFieldStock.setText("");
        }
    }

    private void jButtonSaveDetailActionPerformedThrows() throws StockException, NoSuchMoneyException {
        if (selectedMedicament != null) {
            selectedInvoiceDetail.setIdInvoice(invoice.getIdInvoice());
            selectedInvoiceDetail.setIdMedicament(selectedMedicament.getIdMedicament());
            selectedInvoiceDetail.setQuantity((Double) jSpinnerQuantity.getValue());
            selectedInvoiceDetail.setPrice((Double) jFormattedTextFieldPrice.getValue());
            selectedInvoiceDetail.setTotal(Util.roundTo((Double) jFormattedTextFieldSum.getValue()));
            if (isAddingMode) {
                int id = invoiceDetailDao.addInvoiceDetail(selectedInvoiceDetail);
                aInvoiceDetailsTableModel.removeRow(aInvoiceDetailsTableModel.getRowCount() - 1);
                selectedInvoiceDetail.setId(id);
                isAddingMode = false;
                jButtonSaveDetail.setEnabled(false);
                refreshTableInvoiceDetails();
            } else {
                invoiceDetailDao.updateInvoiceDetail(selectedInvoiceDetail);
                jButtonSaveDetail.setEnabled(false);
                refreshTableInvoiceDetails(selectedRow);
            }
            updateInvoiceInformation();
            resfreshStockInformation();
            jTableInvoiceDetails.setEnabled(true);
            jButtonAddDetail.setEnabled(true);
            jButtonRevertDetail.setEnabled(false);
            setButtonsExportOnEditingAdding(true);
        } else {
            JOptionPane.showMessageDialog(this, "please select the medicament or delete this row");
        }
    }

    private void rollback() {

    }


    private void jButtonSaveDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveDetailActionPerformed
        try {
            jButtonSaveDetailActionPerformedThrows();
        } catch (StockException | NoSuchMoneyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
            if(!isAddingMode)selectedInvoiceDetail = invoiceDetailDao.findInvoiceDetailByIdIdInvoice(selectedInvoiceDetail.getId(), selectedInvoiceDetail.getIdInvoice());
        }
    }//GEN-LAST:event_jButtonSaveDetailActionPerformed

    private void jButtonDeleteDetailActionPerformedThrows() throws StockException, NoSuchMoneyException {
        if (selectedInvoiceDetail != null) {

            int rez = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete: "
                    + medDao.findByIdMedicament(
                            selectedInvoiceDetail.getIdMedicament())
                    + " " + selectedInvoiceDetail.getQuantity()
                    + " piece(s)?",
                    "Delete from invoice", JOptionPane.YES_NO_OPTION);
            if (rez == JOptionPane.YES_OPTION) {
                invoiceDetailDao.revomeInvoiceDetail(selectedInvoiceDetail);
                refreshTableInvoiceDetails(-selectedRow - 1);
                updateInvoiceInformation();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete");
        }
    }

    private void jButtonDeleteDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteDetailActionPerformed
        try {
            jButtonDeleteDetailActionPerformedThrows();
        } catch (StockException | NoSuchMoneyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonDeleteDetailActionPerformed

    private void jButtonAddDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddDetailActionPerformed
        try {
            selectedInvoiceDetail = (InvoiceDetail) invoiceDetailClass.newInstance();
            isAddingMode = true;
            aInvoiceDetailsTableModel.addRow(new Vector());
            jTableInvoiceDetails.setRowSelectionInterval(jTableInvoiceDetails.getRowCount() - 1, jTableInvoiceDetails.getRowCount() - 1);
            clearFormInvoiceDetail();
            enableComponents(true);
            jButtonDeleteDetail.setEnabled(false);
            jTableInvoiceDetails.setEnabled(false);
            jButtonAddDetail.setEnabled(false);
        } catch (InstantiationException | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonAddDetailActionPerformed

    private void jTableInvoiceDetailsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInvoiceDetailsMousePressed
        if (evt.isPopupTrigger()) {
            jMenuItemAddRow.setEnabled(jButtonAddDetail.isEnabled());
            jMenuItemDeleteRow.setEnabled(jButtonDeleteDetail.isEnabled());
            jPopupMenuDetails.show(jTableInvoiceDetails, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableInvoiceDetailsMousePressed

    private void jSpinnerQuantityMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSpinnerQuantityMouseWheelMoved
        double quantity = (Double) jSpinnerQuantity.getValue();
        if (evt.getPreciseWheelRotation() < 0 || quantity > 0) {
            jSpinnerQuantity.setValue(quantity - evt.getPreciseWheelRotation());
        }

    }//GEN-LAST:event_jSpinnerQuantityMouseWheelMoved

    private void showAnalogsPopupMenu(java.awt.event.MouseEvent evt) {
        if (jComboBoxMedicament.getSelectedIndex() < 1) {
            return;
        }
        List<Medicament> listMed = medDao.findAllAnalogs(selectedMedicament);
        List<JMenuItem> menuItems = new ArrayList<>();
        for (int i = jPopupMenuAnalogs.getComponentCount() - 1; i > 0; i--) {
            ((JMenuItem) jPopupMenuAnalogs.getComponent(i)).removeActionListener(null);
            jPopupMenuAnalogs.remove(i);
        }

        for (int i = 0; i < listMed.size(); i++) {
            final Medicament med = listMed.get(i);
            Stock stock = stockDao.findByIdStock(med.getIdMedicament());
            double price = Util.getPriceofMedicament(priceDao.findPriceById(med.getIdMedicament()), invoice);
            menuItems.add(new JMenuItem(med.getNameMedicament() + " (STOCK=" + stock.getBalance() + " PRICE=" + price + ")"));
            menuItems.get(i).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    jComboBoxMedicament.setSelectedItem(med);
                }
            });
            jPopupMenuAnalogs.add(menuItems.get(i));
        }
        jPopupMenuAnalogs.show(jComboBoxMedicament, evt.getX(), evt.getY());
    }

    private void jComboBoxMedicamentMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBoxMedicamentMouseReleased
        showIcon(0, 0, null);
        if (jComboBoxMedicament.isEnabled() && evt.isPopupTrigger()) {
            showAnalogsPopupMenu(evt);
        }
    }//GEN-LAST:event_jComboBoxMedicamentMouseReleased

    private void jComboBoxMedicamentMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBoxMedicamentMousePressed
        showIcon(0, 0, null);
        if (jComboBoxMedicament.isEnabled() && evt.isPopupTrigger()) {
            showAnalogsPopupMenu(evt);
        }
    }//GEN-LAST:event_jComboBoxMedicamentMousePressed

    private Object[] createExportView() {
        String header = "Invoice №" + invoice.getIdInvoice() + "," + invoice.getDateInvoice() + "," + finSubjClass.getSimpleName() + ":"
                + finSubjDao.findFinSubjectById(invoice.getIdFinSubj());
        String footer = "Total of invoice is " + (Double) jFormattedTextTotal.getValue() + " MDL";

        return new Object[]{jTableInvoiceDetails, header, footer};
    }


    private void jButtonExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportExcelActionPerformed
        if (jComboBoxNameFinSubj.getSelectedIndex() != 0) {
            Object views[] = createExportView();
            ExportReports.exportTableModelDialog(
                    "xls", (JTable) views[0], (String) views[1], (String) views[2]);
        } else {
            JOptionPane.showMessageDialog(this, "Please select " + finSubjClass.getSimpleName());
        }
    }//GEN-LAST:event_jButtonExportExcelActionPerformed

    private void jButtonSaveInvoiceActionPerformedThrows() throws StockException, NoSuchMoneyException {
        if (saveUpdateInvoice()) {
            jButtonSaveInvoice.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Cannot Save.Has no choosed a " + finSubjClass.getSimpleName());
        }

    }

    private void jButtonSaveInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveInvoiceActionPerformed
        try {
            jButtonSaveInvoiceActionPerformedThrows();
        } catch (StockException | NoSuchMoneyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
            if(!isAddingMode)selectedInvoiceDetail = invoiceDetailDao.findInvoiceDetailByIdIdInvoice(selectedInvoiceDetail.getId(), selectedInvoiceDetail.getIdInvoice());
        }
    }//GEN-LAST:event_jButtonSaveInvoiceActionPerformed

    private void jButtonExportPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportPdfActionPerformed
        if (jComboBoxNameFinSubj.getSelectedIndex() != 0) {
            Object views[] = createExportView();
            ExportReports.exportTableModelDialog(
                    "pdf", (JTable) views[0], (String) views[1], (String) views[2]);
        } else {
            JOptionPane.showMessageDialog(this, "Please select " + finSubjClass.getSimpleName());
        }
    }//GEN-LAST:event_jButtonExportPdfActionPerformed

    private void jSpinnerQuantityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSpinnerQuantityKeyReleased

    }//GEN-LAST:event_jSpinnerQuantityKeyReleased

    private void jDialogFinSubjectsCreateAndShow(JDialogFinSubjSaveUpdate jDialogClientsSaveUpdate, Class finSubjectClass, FinanceSubject finSubj) {
        if (jDialogClientsSaveUpdate != null) {
            jDialogClientsSaveUpdate = null;
        }
        String type = null;
        if (finSubj == null) {
            try {
                finSubj = (FinanceSubject) finSubjectClass.newInstance();
                type = "add";

            } catch (InstantiationException ex) {
                Logger.getLogger(JFrameFinSubjects.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JFrameFinSubjects.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            type = "edit";
        }
        try {
            jDialogClientsSaveUpdate = new JDialogFinSubjSaveUpdate(null, true, finSubjectClass, type, finSubj);

        } catch (Exception ex) {
            Logger.getLogger(JFrameFinSubjects.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        jDialogClientsSaveUpdate.setVisible(true);
    }


    private void jMenuItemAddNewFinSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddNewFinSubjectActionPerformed
        jDialogFinSubjectsCreateAndShow(jDialogFinSubjSaveUpdate, finSubjClass, null);
        aFinSubjComboBoxModel.refreshModel();
        jComboBoxNameFinSubj.setSelectedIndex(jComboBoxNameFinSubj.getModel().getSize() - 1);
    }//GEN-LAST:event_jMenuItemAddNewFinSubjectActionPerformed

    private void jTableInvoiceDetailsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableInvoiceDetailsKeyReleased
        if (evt.getKeyCode() == 155 && jButtonAddDetail.isEnabled()) {
            jButtonAddDetailActionPerformed(null);
        }
        if (evt.getKeyCode() == 127 && jButtonDeleteDetail.isEnabled()) {
            try {
                jButtonDeleteDetailActionPerformedThrows();
            } catch (StockException | NoSuchMoneyException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTableInvoiceDetailsKeyReleased

    private void jMenuItemDeleteRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteRowActionPerformed
        try {
            jButtonDeleteDetailActionPerformedThrows();
        } catch (StockException | NoSuchMoneyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItemDeleteRowActionPerformed

    private void jMenuItemAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddRowActionPerformed
        jButtonAddDetailActionPerformed(null);
    }//GEN-LAST:event_jMenuItemAddRowActionPerformed

    private void jTableInvoiceDetailsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInvoiceDetailsMouseReleased
        jTableInvoiceDetailsMousePressed(evt);
    }//GEN-LAST:event_jTableInvoiceDetailsMouseReleased

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        try {
            Object views[] = createExportView();
            MessageFormat header = new MessageFormat((String) views[2]);
            MessageFormat footer = new MessageFormat((String) views[1] + ". Page - {0}");
            JTable jTable = (JTable) views[0];
            boolean complete = jTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Table was printed succesfuly");
            } else {
                JOptionPane.showMessageDialog(this, "Printing has been cancelled");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", ERROR_MESSAGE);
            LOG.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonPrintActionPerformed

    private void jButtonExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportCSVActionPerformed
        if (jComboBoxNameFinSubj.getSelectedIndex() != 0) {
            Object views[] = createExportView();
            ExportReports.exportTableModelDialog(
                    "csv", (JTable) views[0], null, null);
        } else {
            JOptionPane.showMessageDialog(this, "Please select " + finSubjClass.getSimpleName());
        }
    }//GEN-LAST:event_jButtonExportCSVActionPerformed

    private void showIcon(int x, int y, byte[] iconByte) {
        if (x != 0 && y != 0) {
            if (jWindowIconMedicament == null) {
                jWindowIconMedicament = new JWindowIconMedicament();
                jWindowIconMedicament.setVisible(true);
            }
            jWindowIconMedicament.setLocation(x, y);
            jWindowIconMedicament.setIcon(iconByte);
            if (!jWindowIconMedicament.isVisible()) {
                jWindowIconMedicament.setVisible(true);
            }
        } else {
            if (jWindowIconMedicament != null) {
                jWindowIconMedicament.dispose();
            }
        }
    }

    private void jTableInvoiceDetailsMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInvoiceDetailsMouseMoved
        int x = 0;
        int y = 0;
        byte[] iconByte = null;
        if (jTableInvoiceDetails.columnAtPoint(evt.getPoint()) == 1) {
            if (jPopupMenuDetails.isVisible()) {
                jPopupMenuDetails.setVisible(false);
            }
            int row = jTableInvoiceDetails.rowAtPoint(evt.getPoint());
            if (jTableInvoiceDetails.getValueAt(row, 0) != null) {
                InvoiceDetail invDet = invoiceDetailDao.findInvoiceDetailByIdIdInvoice(
                        (Integer) jTableInvoiceDetails.getValueAt(row, 0), invoice.getIdInvoice());
                iconByte = medDao.findByIdMedicament(invDet.getIdMedicament()).getIconMedicament();
                x = evt.getXOnScreen() + 5;
                y = evt.getYOnScreen() + 5;
            }
        }
        showIcon(x, y, iconByte);
    }//GEN-LAST:event_jTableInvoiceDetailsMouseMoved

    private void jScrollPane1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseMoved
        showIcon(0, 0, null);
    }//GEN-LAST:event_jScrollPane1MouseMoved

    private void jComboBoxMedicamentMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBoxMedicamentMouseMoved
        if (jPopupMenuAnalogs.isVisible()) {
            jPopupMenuAnalogs.setVisible(false);
        }
        int x = 0;
        int y = 0;
        byte[] iconByte = null;
        if (!jComboBoxMedicament.isPopupVisible() && jComboBoxMedicament.getSelectedIndex() > 0) {
            iconByte = medDao.findByIdMedicament(
                    ((Medicament) jComboBoxMedicament.getSelectedItem())
                    .getIdMedicament()).getIconMedicament();
            x = evt.getXOnScreen();
            y = evt.getYOnScreen() + 15;
        }
        showIcon(x, y, iconByte);
    }//GEN-LAST:event_jComboBoxMedicamentMouseMoved

    private void jPanel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseMoved
        showIcon(0, 0, null);
    }//GEN-LAST:event_jPanel1MouseMoved

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        showIcon(0, 0, null);
    }//GEN-LAST:event_formMouseMoved

    private void jPopupMenuDetailsPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenuDetailsPopupMenuWillBecomeVisible
        showIcon(0, 0, null);
    }//GEN-LAST:event_jPopupMenuDetailsPopupMenuWillBecomeVisible

    private void jButtonExportJxlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportJxlsActionPerformed
        Report report = new Report();
        report.setFarmacyName(farmDao.getFarmacyName());
        report.setInnerData("");
        report.setHeader(invoice.getIdInvoice() + ".    Date:"
                + invoice.getDateInvoice() + ".");
        report.setInnerData(finSubjClass.getSimpleName() + ":"
                + finSubjDao.findFinSubjectById(invoice.getIdFinSubj()));
        report.setFooter((Double) jFormattedTextTotal.getValue() + " MDL");
        int rows = jTableInvoiceDetails.getRowCount();
        List reportList = new ArrayList();
        for (int i = 0; i < rows; i++) {
            reportList.add(new InvoiceReport(
                    (Integer) jTableInvoiceDetails.getValueAt(i, 0),
                    (String) jTableInvoiceDetails.getValueAt(i, 1),
                    (Double) jTableInvoiceDetails.getValueAt(i, 2),
                    (String) jTableInvoiceDetails.getValueAt(i, 3),
                    (String) jTableInvoiceDetails.getValueAt(i, 4)));
        }
        report.setData(reportList);
        ExportReports.exportJxlsDialog(report, "InvoiceReport", "invoicereport.xls");
    }//GEN-LAST:event_jButtonExportJxlsActionPerformed

    private void jComboBoxNameFinSubjMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBoxNameFinSubjMouseReleased
        if (evt.isPopupTrigger()) {
            jPopupMenuFinSubj.show(jComboBoxNameFinSubj, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jComboBoxNameFinSubjMouseReleased

    private void jComboBoxNameFinSubjMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBoxNameFinSubjMousePressed
        if (evt.isPopupTrigger()) {
            jPopupMenuFinSubj.show(jComboBoxNameFinSubj, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jComboBoxNameFinSubjMousePressed

    private void jComboBoxNameFinSubjItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxNameFinSubjItemStateChanged
        jButtonSaveInvoice.setEnabled(true);
    }//GEN-LAST:event_jComboBoxNameFinSubjItemStateChanged

    private void jMenuItemChooseAnalogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChooseAnalogsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemChooseAnalogsActionPerformed

    private void jSpinnerQuantityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSpinnerQuantityFocusGained

    }//GEN-LAST:event_jSpinnerQuantityFocusGained

    private void jSpinnerQuantityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSpinnerQuantityKeyPressed

        jSpinnerQuantityStateChanged(null);

    }//GEN-LAST:event_jSpinnerQuantityKeyPressed

    private void jSpinnerQuantityCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jSpinnerQuantityCaretPositionChanged

    }//GEN-LAST:event_jSpinnerQuantityCaretPositionChanged

    private void jSpinnerQuantityInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jSpinnerQuantityInputMethodTextChanged

    }//GEN-LAST:event_jSpinnerQuantityInputMethodTextChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddDetail;
    private javax.swing.JButton jButtonDeleteDetail;
    private javax.swing.JButton jButtonExportCSV;
    private javax.swing.JButton jButtonExportExcel;
    private javax.swing.JButton jButtonExportJxls;
    private javax.swing.JButton jButtonExportPdf;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonRevertDetail;
    private javax.swing.JButton jButtonSaveDetail;
    private javax.swing.JButton jButtonSaveInvoice;
    private javax.swing.JComboBox jComboBoxMedicament;
    private javax.swing.JComboBox jComboBoxNameFinSubj;
    private javax.swing.JFormattedTextField jFormattedTextFieldPrice;
    private javax.swing.JFormattedTextField jFormattedTextFieldSum;
    private javax.swing.JFormattedTextField jFormattedTextTotal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelFinSubject;
    private javax.swing.JMenuItem jMenuItemAddNewFinSubject;
    private javax.swing.JMenuItem jMenuItemAddRow;
    private javax.swing.JMenuItem jMenuItemChooseAnalogs;
    private javax.swing.JMenuItem jMenuItemDeleteRow;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelInvoiceHeader;
    private javax.swing.JPopupMenu jPopupMenuAnalogs;
    private javax.swing.JPopupMenu jPopupMenuDetails;
    private javax.swing.JPopupMenu jPopupMenuFinSubj;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinnerQuantity;
    private javax.swing.JTable jTableInvoiceDetails;
    private javax.swing.JTextArea jTextAreaInfoMedicament;
    private javax.swing.JTextField jTextFieldIdInvoice;
    private javax.swing.JTextField jTextFieldStock;
    // End of variables declaration//GEN-END:variables

    Class finSubjClass;
    FinanceSubjectDaoIntf finSubjDao;
    InvoiceDaoIntf invoiceDao;
    Invoice invoice;
    Class invoiceDetailClass;
    InvoiceDetail selectedInvoiceDetail;
    MedicamentComboBoxModel aMedicamentComboBoxModel;
    FinSubjComboBoxModel aFinSubjComboBoxModel;
    InvoiceDetailsTableModel aInvoiceDetailsTableModel;
    InvoiceDetailDaoIntf invoiceDetailDao;
    MedicamentDaoIntf medDao;
    PriceDaoIntf priceDao;
    StockDaoIntf stockDao;
    Medicament selectedMedicament;
    boolean isAddingMode = false;
    int selectedRow;
    JDialogFinSubjSaveUpdate jDialogFinSubjSaveUpdate;
    JWindowIconMedicament jWindowIconMedicament;
    FarmacyBalanceDaoIntf farmDao;
    boolean isJDatePicker;
    JDatePickerImpl jDatePickerInvoice;
    JSpinner jSpinnerDateInvoice;
    Component dateComponent;
    CategoryDaoIntf catDao;
}
