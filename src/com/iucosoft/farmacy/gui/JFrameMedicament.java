/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui;

import com.iucosoft.farmacy.dao.AnalogDaoIntf;
import com.iucosoft.farmacy.dao.FarmacyBalanceDaoIntf;
import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.dao.PriceDaoIntf;
import com.iucosoft.farmacy.dao.StockDaoIntf;
import com.iucosoft.farmacy.dao.impl.AnalogDaoImpl;
import com.iucosoft.farmacy.dao.impl.FarmacyBalanceDaoImpl;
import com.iucosoft.farmacy.dao.impl.MedicamentDaoImpl;
import com.iucosoft.farmacy.dao.impl.PriceDaoImpl;
import com.iucosoft.farmacy.dao.impl.StockDaoImpl;
import com.iucosoft.farmacy.exceptions.DeletingException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.gui.models.AnalogListModel;
import com.iucosoft.farmacy.gui.models.CategoryComboBoxModel;
import com.iucosoft.farmacy.gui.models.MedicamentTableModel;
import com.iucosoft.farmacy.gui.models.PriceTableModel;
import com.iucosoft.farmacy.gui.models.StockTableModel;
import com.iucosoft.farmacy.model.Analog;
import com.iucosoft.farmacy.model.Category;
import com.iucosoft.farmacy.model.Medicament;
import com.iucosoft.farmacy.model.Price;
import com.iucosoft.farmacy.model.Stock;
import com.iucosoft.farmacy.reports.MedicamentsReport;
import com.iucosoft.farmacy.reports.PriceReportAdmin;
import com.iucosoft.farmacy.reports.PriceReportManager;
import com.iucosoft.farmacy.reports.Report;
import com.iucosoft.farmacy.reports.StockReport;
import com.iucosoft.farmacy.utils.ExportReports;
import com.iucosoft.farmacy.utils.Util;
import static com.iucosoft.farmacy.utils.Util.selectRowInTable;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Serguei
 */
public class JFrameMedicament extends javax.swing.JFrame {

    private static final Logger LOG = Logger.getLogger(JFrameMedicament.class.getName());

    /**
     * Creates new form JFrameMedicament
     */
    public JFrameMedicament(int tab, StringBuilder mode) throws Exception {
        this.activeTab = tab;
        this.mode = mode;
        initComponents();
        initGuiComponents();
        addListeners();
        setLocationRelativeTo(null);
    }

    private void initGuiComponents() throws Exception {
        medDao = new MedicamentDaoImpl();
        aMedicamentTableModel = new MedicamentTableModel();
        jTableMedicaments.setModel(aMedicamentTableModel);
        jTableMedicaments.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTableMedicaments.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableMedicaments.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTableMedicaments.getColumnModel().getColumn(3).setPreferredWidth(100);

        aCategoryComboBoxModelMedList = new CategoryComboBoxModel();
        jComboBoxSearchCategoriesMedList.setModel(aCategoryComboBoxModelMedList);

        aCategorySearchComboBoxModelPriceList = new CategoryComboBoxModel();
        jComboBoxCategoryPriceList.setModel(aCategorySearchComboBoxModelPriceList);

        analogsListModel = new AnalogListModel();
        jListAnalogs.setModel(analogsListModel);
        aPriceTableModel = new PriceTableModel(mode.toString().equals("Administrator"));
        jTablePrices.setModel(aPriceTableModel);
        jTablePrices.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTablePrices.getColumnModel().getColumn(1).setPreferredWidth(100);
        if (mode.toString().equals("Administrator")) {
            jTablePrices.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTablePrices.getColumnModel().getColumn(3).setPreferredWidth(30);
            jTablePrices.getColumnModel().getColumn(4).setPreferredWidth(70);
        } else {
            jTablePrices.getColumnModel().getColumn(2).setPreferredWidth(70);
        }
        priceDao = new PriceDaoImpl();

        aStockTableModel = new StockTableModel();
        jTableStock.setModel(aStockTableModel);
        jTableStock.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTableStock.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTableStock.getColumnModel().getColumn(2).setPreferredWidth(40);

        stockDao = new StockDaoImpl();
        listStock = stockDao.findAllStockList();

        farmDao = new FarmacyBalanceDaoImpl();
//        jTableMedicaments.getColumn(jTableMedicaments.getColumnName(2)).
//                setCellEditor(new DefaultCellEditor(new JComboBox(new CategoryComboBoxModel())));
        if (mode.toString().equals("Manager")) {
            jPanelEditPrice.setVisible(false);
            jButtonCreateMedicament.setEnabled(false);
            jButtonEditMedicament.setEnabled(false);
            jButtonRemoveMedicament.setEnabled(false);
            jButtonAddRemoveAnalogs.setEnabled(false);
            jPanelFarmacyBalance.setVisible(false);
        }
        //       ((AbstractDocument)jFormattedTextFieldUnitPrice.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
        //     ((AbstractDocument)jFormattedTextFieldMargin.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
        //   ((AbstractDocument)jFormattedTextFieldSaleUnitPrice.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
//        ((AbstractDocument)jFormattedTextFieldBalanceMin.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
        //      ((AbstractDocument)jFormattedTextFieldBalanceMax.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());

    }

    private void addListeners() {
        jTableMedicaments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                selectedRowTableMedicaments = jTableMedicaments.getSelectedRow();
                if (selectedRowTableMedicaments >= 0) {
                    int idMedicament = (Integer) jTableMedicaments.getValueAt(selectedRowTableMedicaments, 0);
                    selectedMedicament = medDao.findByIdMedicament(idMedicament);
                    analogsListModel.refreshModel(selectedMedicament);
                    refreshIcon();
                } else {
                    selectedMedicament = null;
                    analogsListModel.removeAllElements();
                }
                refreshIcon();
            }
        });
        jTablePrices.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (jButtonSavePrice.isEnabled() && selectedPrice != null) {
                    savePrice();
                }
                selectedRowTablePrices = jTablePrices.getSelectedRow();
                if (selectedRowTablePrices >= 0) {
                    int idMedicament = (Integer) jTablePrices.getValueAt(selectedRowTablePrices, 0);
                    selectedPrice = priceDao.findPriceById(idMedicament);
                    fillPriceForm(selectedPrice);
                } else {
                    selectedPrice = null;
                    clearPriceForm();
                }
                refreshIcon();
            }
        });

        jTableStock.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                selectedRowTableStock = jTableStock.getSelectedRow();
                if (selectedRowTableStock >= 0) {
                    int idMedicament = (Integer) jTableStock.getValueAt(selectedRowTableStock, 0);
                    selectedStock = stockDao.findByIdStock(idMedicament);
                } else {
                    selectedStock = null;
                    clearSearchingStock();
                }
                refreshIcon();
            }
        });

        jTabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                previousTabIndex = currentTabIndex;
                currentTabIndex = ((JTabbedPane) ce.getSource()).getSelectedIndex();
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

        jPopupMenuTable = new javax.swing.JPopupMenu();
        jMenuItemCreateMedicament = new javax.swing.JMenuItem();
        jMenuItemEditMEdicament = new javax.swing.JMenuItem();
        jMenuItemRemoveMedicament = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAddRemoveAnalogs = new javax.swing.JMenuItem();
        jPopupMenuAnalogList = new javax.swing.JPopupMenu();
        jMenuItemAddAnalog = new javax.swing.JMenuItem();
        jMenuItemRemoveAnalog = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelMedicamentList = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListAnalogs = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMedicaments = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jTextFieldSearchLineMedList = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButtonClearSearchLineMed = new javax.swing.JButton();
        jComboBoxSearchCategoriesMedList = new javax.swing.JComboBox();
        jButtonClose = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jButtonRemoveMedicament = new javax.swing.JButton();
        jButtonCreateMedicament = new javax.swing.JButton();
        jButtonAddRemoveAnalogs = new javax.swing.JButton();
        jButtonEditMedicament = new javax.swing.JButton();
        jButtonExportExcelMed = new javax.swing.JButton();
        jButtonExportPdfMed = new javax.swing.JButton();
        jButtonExportCSVMed = new javax.swing.JButton();
        jButtonPrintMed = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelMedIcon = new javax.swing.JLabel();
        jButtonExportJxls = new javax.swing.JButton();
        jPanelPriceList = new javax.swing.JPanel();
        jPanelEditPrice = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButtonCalcSellPrice = new javax.swing.JButton();
        jTextFieldNameMedicament = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jButtonSavePrice = new javax.swing.JButton();
        jButtonRevertPrice = new javax.swing.JButton();
        jFormattedTextFieldUnitPrice = new javax.swing.JFormattedTextField();
        jFormattedTextFieldMargin = new javax.swing.JFormattedTextField();
        jFormattedTextFieldSaleUnitPrice = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jTextFieldSearchLinePriceList = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButtonClearSearchLinePrice = new javax.swing.JButton();
        jComboBoxCategoryPriceList = new javax.swing.JComboBox();
        jButtonClose2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablePrices = new javax.swing.JTable();
        jButtonPrintPrice = new javax.swing.JButton();
        jButtonExportPdfPrice = new javax.swing.JButton();
        jButtonExportExcelPrice = new javax.swing.JButton();
        jButtonExportCSVPrice = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabelMedIcon1 = new javax.swing.JLabel();
        jButtonExportJxlsPrice = new javax.swing.JButton();
        jPanelStock = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableStock = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jTextFieldSearchLineStock = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jButtonClearSearchLineStock = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButtonSearchByBalance = new javax.swing.JButton();
        jFormattedTextFieldBalanceMin = new javax.swing.JFormattedTextField();
        jFormattedTextFieldBalanceMax = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        jButtonExportPdfStock = new javax.swing.JButton();
        jButtonPrintPriceStock = new javax.swing.JButton();
        jButtonExportCSVStock = new javax.swing.JButton();
        jButtonClose3 = new javax.swing.JButton();
        jButtonExportExcelStock = new javax.swing.JButton();
        jButtonExportJxlsStock = new javax.swing.JButton();
        jPanelFarmacyBalance = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jFormattedTextFieldFarmacyBalance = new javax.swing.JFormattedTextField();
        jButtonSetFarmacyBalance = new javax.swing.JButton();
        jButtonEditBalance = new javax.swing.JButton();
        jButtonAccessStockEditing = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabelMedIcon2 = new javax.swing.JLabel();

        jPopupMenuTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenuItemCreateMedicament.setText("Create Medicament");
        jMenuItemCreateMedicament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCreateMedicamentActionPerformed(evt);
            }
        });
        jPopupMenuTable.add(jMenuItemCreateMedicament);

        jMenuItemEditMEdicament.setText("Edit Medicament");
        jMenuItemEditMEdicament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditMEdicamentActionPerformed(evt);
            }
        });
        jPopupMenuTable.add(jMenuItemEditMEdicament);

        jMenuItemRemoveMedicament.setText("Remove Medicament");
        jMenuItemRemoveMedicament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoveMedicamentActionPerformed(evt);
            }
        });
        jPopupMenuTable.add(jMenuItemRemoveMedicament);
        jPopupMenuTable.add(jSeparator1);

        jMenuItemAddRemoveAnalogs.setText("Add/Remove Analogs");
        jMenuItemAddRemoveAnalogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddRemoveAnalogsActionPerformed(evt);
            }
        });
        jPopupMenuTable.add(jMenuItemAddRemoveAnalogs);

        jMenuItemAddAnalog.setText("Add Analog");
        jMenuItemAddAnalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddAnalogActionPerformed(evt);
            }
        });
        jPopupMenuAnalogList.add(jMenuItemAddAnalog);

        jMenuItemRemoveAnalog.setText("Remove Analog");
        jMenuItemRemoveAnalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoveAnalogActionPerformed(evt);
            }
        });
        jPopupMenuAnalogList.add(jMenuItemRemoveAnalog);

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanelMedicamentList.setName("jPanelMedicamentList"); // NOI18N
        jPanelMedicamentList.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelMedicamentListComponentShown(evt);
            }
        });

        jListAnalogs.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)), "Analogs"));
        jListAnalogs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListAnalogsMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListAnalogsMouseReleased(evt);
            }
        });
        jListAnalogs.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAnalogsValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jListAnalogs);

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jScrollPane1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseReleased(evt);
            }
        });

        jTableMedicaments.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTableMedicaments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableMedicaments.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTableMedicaments.setColumnSelectionAllowed(true);
        jTableMedicaments.setName(""); // NOI18N
        jTableMedicaments.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableMedicaments.getTableHeader().setReorderingAllowed(false);
        jTableMedicaments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableMedicamentsMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableMedicamentsMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTableMedicaments);
        jTableMedicaments.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)), "Search medicaments"));

        jTextFieldSearchLineMedList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchLineMedListKeyReleased(evt);
            }
        });

        jLabel2.setText("Filter By Category");

        jButtonClearSearchLineMed.setText("Clear");
        jButtonClearSearchLineMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearSearchLineMedActionPerformed(evt);
            }
        });

        jComboBoxSearchCategoriesMedList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxSearchCategoriesMedListItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxSearchCategoriesMedList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jTextFieldSearchLineMedList, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonClearSearchLineMed))
                            .addComponent(jLabel2))
                        .addGap(0, 18, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSearchLineMedList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonClearSearchLineMed))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(jComboBoxSearchCategoriesMedList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0))));

        jButtonRemoveMedicament.setText("Remove Medicament");
        jButtonRemoveMedicament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveMedicamentActionPerformed(evt);
            }
        });

        jButtonCreateMedicament.setText("Create Medicament");
        jButtonCreateMedicament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateMedicamentActionPerformed(evt);
            }
        });

        jButtonAddRemoveAnalogs.setText("Add/Remove Analogs");
        jButtonAddRemoveAnalogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddRemoveAnalogsActionPerformed(evt);
            }
        });

        jButtonEditMedicament.setText("Edit Medicament");
        jButtonEditMedicament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditMedicamentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButtonAddRemoveAnalogs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonRemoveMedicament, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonEditMedicament, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonCreateMedicament, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAddRemoveAnalogs, jButtonCreateMedicament, jButtonEditMedicament, jButtonRemoveMedicament});

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonCreateMedicament)
                .addGap(18, 18, 18)
                .addComponent(jButtonEditMedicament)
                .addGap(18, 18, 18)
                .addComponent(jButtonRemoveMedicament)
                .addGap(18, 18, 18)
                .addComponent(jButtonAddRemoveAnalogs)
                .addContainerGap())
        );

        jButtonExportExcelMed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070960_Microsoft Office Excel32.png"))); // NOI18N
        jButtonExportExcelMed.setToolTipText("Export to Excel");
        jButtonExportExcelMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportExcelMedActionPerformed(evt);
            }
        });

        jButtonExportPdfMed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423071193_Adobe Acrobat Professional32.png"))); // NOI18N
        jButtonExportPdfMed.setToolTipText("Export to Pdf");
        jButtonExportPdfMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportPdfMedActionPerformed(evt);
            }
        });

        jButtonExportCSVMed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/file_csv32.png"))); // NOI18N
        jButtonExportCSVMed.setToolTipText("Export to CSV");
        jButtonExportCSVMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportCSVMedActionPerformed(evt);
            }
        });

        jButtonPrintMed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/printer.png"))); // NOI18N
        jButtonPrintMed.setToolTipText("Print Table");
        jButtonPrintMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintMedActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel2.setMaximumSize(new java.awt.Dimension(190, 90));
        jPanel2.setMinimumSize(new java.awt.Dimension(190, 90));
        jPanel2.setName(""); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(190, 90));

        jLabelMedIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMedIcon.setMaximumSize(new java.awt.Dimension(185, 85));
        jLabelMedIcon.setMinimumSize(new java.awt.Dimension(185, 85));
        jLabelMedIcon.setPreferredSize(new java.awt.Dimension(185, 85));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMedIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMedIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jButtonExportJxls.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1424199566_application-vnd.ms-excel.png"))); // NOI18N
        jButtonExportJxls.setToolTipText("Export Report");
        jButtonExportJxls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportJxlsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMedicamentListLayout = new javax.swing.GroupLayout(jPanelMedicamentList);
        jPanelMedicamentList.setLayout(jPanelMedicamentListLayout);
        jPanelMedicamentListLayout.setHorizontalGroup(
            jPanelMedicamentListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMedicamentListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMedicamentListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonClose)
                    .addGroup(jPanelMedicamentListLayout.createSequentialGroup()
                        .addComponent(jButtonExportExcelMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExportPdfMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExportCSVMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExportJxls, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPrintMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelMedicamentListLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        jPanelMedicamentListLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel6, jPanel7});

        jPanelMedicamentListLayout.setVerticalGroup(
            jPanelMedicamentListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMedicamentListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMedicamentListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMedicamentListLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelMedicamentListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonExportPdfMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExportExcelMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExportCSVMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonPrintMed, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExportJxls, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonClose))
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelMedicamentListLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonExportCSVMed, jButtonExportPdfMed});

        jTabbedPane.addTab("Medicaments List", jPanelMedicamentList);

        jPanelPriceList.setName("jPanelPriceList"); // NOI18N
        jPanelPriceList.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelPriceListComponentShown(evt);
            }
        });

        jPanelEditPrice.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)), "Edit price"));
        jPanelEditPrice.setToolTipText("");

        jLabel6.setText("MDL");

        jLabel3.setText("MDL");

        jLabel5.setText("Sale Unit Price");

        jLabel4.setText("Margin");

        jLabel7.setText("Name of the Medicament");

        jButtonCalcSellPrice.setText("Calc Sell Price");
        jButtonCalcSellPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCalcSellPriceActionPerformed(evt);
            }
        });

        jLabel8.setText("Unit Price");

        jButtonSavePrice.setText("Save");
        jButtonSavePrice.setEnabled(false);
        jButtonSavePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSavePriceActionPerformed(evt);
            }
        });

        jButtonRevertPrice.setText("Revert");
        jButtonRevertPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRevertPriceActionPerformed(evt);
            }
        });

        jFormattedTextFieldUnitPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFormattedTextFieldUnitPriceFocusGained(evt);
            }
        });
        jFormattedTextFieldUnitPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldUnitPriceKeyReleased(evt);
            }
        });

        jFormattedTextFieldMargin.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jFormattedTextFieldMarginCaretUpdate(evt);
            }
        });
        jFormattedTextFieldMargin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFormattedTextFieldMarginFocusGained(evt);
            }
        });
        jFormattedTextFieldMargin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldMarginActionPerformed(evt);
            }
        });
        jFormattedTextFieldMargin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldMarginKeyReleased(evt);
            }
        });

        jFormattedTextFieldSaleUnitPrice.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jFormattedTextFieldSaleUnitPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFormattedTextFieldSaleUnitPriceFocusGained(evt);
            }
        });
        jFormattedTextFieldSaleUnitPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldSaleUnitPriceKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanelEditPriceLayout = new javax.swing.GroupLayout(jPanelEditPrice);
        jPanelEditPrice.setLayout(jPanelEditPriceLayout);
        jPanelEditPriceLayout.setHorizontalGroup(
            jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                        .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                                .addComponent(jFormattedTextFieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3))
                            .addComponent(jLabel8))
                        .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(146, 146, 146))
                            .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                                        .addComponent(jFormattedTextFieldMargin, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonCalcSellPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                                    .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                                        .addComponent(jButtonRevertPrice)
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                        .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jTextFieldNameMedicament, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                                .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jButtonSavePrice)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextFieldSaleUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanelEditPriceLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextFieldMargin, jFormattedTextFieldSaleUnitPrice, jFormattedTextFieldUnitPrice});

        jPanelEditPriceLayout.setVerticalGroup(
            jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditPriceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNameMedicament, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jButtonCalcSellPrice)
                    .addComponent(jFormattedTextFieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextFieldMargin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jFormattedTextFieldSaleUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelEditPriceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSavePrice)
                    .addComponent(jButtonRevertPrice))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)), "Search Medicaments"));
        jPanel5.setAlignmentX(1.0F);
        jPanel5.setAlignmentY(1.0F);

        jTextFieldSearchLinePriceList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchLinePriceListKeyReleased(evt);
            }
        });

        jLabel9.setText("By Category");

        jLabel10.setText("By Name");

        jButtonClearSearchLinePrice.setText("Clear");
        jButtonClearSearchLinePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearSearchLinePriceActionPerformed(evt);
            }
        });

        jComboBoxCategoryPriceList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCategoryPriceListItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9))
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jTextFieldSearchLinePriceList)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonClearSearchLinePrice))
                    .addComponent(jComboBoxCategoryPriceList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldSearchLinePriceList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonClearSearchLinePrice))
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jComboBoxCategoryPriceList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );

        jButtonClose2.setText("Close");
        jButtonClose2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClose2ActionPerformed(evt);
            }
        });

        jTablePrices.setModel(new javax.swing.table.DefaultTableModel(
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
        jTablePrices.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTablePrices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTablePrices.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jTablePrices);

        jButtonPrintPrice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/printer.png"))); // NOI18N
        jButtonPrintPrice.setToolTipText("Print Table");
        jButtonPrintPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintPriceActionPerformed(evt);
            }
        });

        jButtonExportPdfPrice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423071193_Adobe Acrobat Professional32.png"))); // NOI18N
        jButtonExportPdfPrice.setToolTipText("Export to Pdf");
        jButtonExportPdfPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportPdfPriceActionPerformed(evt);
            }
        });

        jButtonExportExcelPrice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070960_Microsoft Office Excel32.png"))); // NOI18N
        jButtonExportExcelPrice.setToolTipText("Export to Excel");
        jButtonExportExcelPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportExcelPriceActionPerformed(evt);
            }
        });

        jButtonExportCSVPrice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/file_csv32.png"))); // NOI18N
        jButtonExportCSVPrice.setToolTipText("Export to CSV");
        jButtonExportCSVPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportCSVPriceActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel3.setMaximumSize(new java.awt.Dimension(190, 90));
        jPanel3.setMinimumSize(new java.awt.Dimension(190, 90));
        jPanel3.setName(""); // NOI18N

        jLabelMedIcon1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMedIcon1.setMaximumSize(new java.awt.Dimension(185, 85));
        jLabelMedIcon1.setMinimumSize(new java.awt.Dimension(185, 85));
        jLabelMedIcon1.setPreferredSize(new java.awt.Dimension(185, 85));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMedIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMedIcon1, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
        );

        jButtonExportJxlsPrice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1424199566_application-vnd.ms-excel.png"))); // NOI18N
        jButtonExportJxlsPrice.setToolTipText("Export Report");
        jButtonExportJxlsPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportJxlsPriceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPriceListLayout = new javax.swing.GroupLayout(jPanelPriceList);
        jPanelPriceList.setLayout(jPanelPriceListLayout);
        jPanelPriceListLayout.setHorizontalGroup(
            jPanelPriceListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPriceListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelPriceListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPriceListLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPriceListLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelPriceListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelPriceListLayout.createSequentialGroup()
                                .addComponent(jButtonExportExcelPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonExportPdfPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonExportCSVPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonExportJxlsPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonPrintPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jButtonClose2))
                            .addComponent(jPanelEditPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelPriceListLayout.setVerticalGroup(
            jPanelPriceListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPriceListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPriceListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanelPriceListLayout.createSequentialGroup()
                        .addComponent(jPanelEditPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelPriceListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonClose2)
                            .addGroup(jPanelPriceListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonExportPdfPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonExportExcelPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonExportCSVPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonPrintPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExportJxlsPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTabbedPane.addTab("Price List", jPanelPriceList);

        jPanelStock.setName("jPanelStock"); // NOI18N
        jPanelStock.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelStockComponentShown(evt);
            }
        });

        jTableStock.setModel(new javax.swing.table.DefaultTableModel(
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
        jTableStock.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTableStock.setAutoscrolls(false);
        jTableStock.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableStock.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTableStock);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)), "Search Medicaments"));
        jPanel8.setAlignmentX(1.0F);
        jPanel8.setAlignmentY(1.0F);

        jTextFieldSearchLineStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchLineStockKeyReleased(evt);
            }
        });

        jLabel12.setText("By Name");

        jButtonClearSearchLineStock.setText("Clear");
        jButtonClearSearchLineStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearSearchLineStockActionPerformed(evt);
            }
        });

        jLabel1.setText("Balance beteen");

        jLabel11.setText("and");

        jButtonSearchByBalance.setText("Search");
        jButtonSearchByBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchByBalanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jTextFieldSearchLineStock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonClearSearchLineStock))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jFormattedTextFieldBalanceMin, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(jFormattedTextFieldBalanceMax, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSearchByBalance)))
                .addContainerGap())
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonClearSearchLineStock, jButtonSearchByBalance});

        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextFieldSearchLineStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonClearSearchLineStock))
                .addGap(25, 25, 25)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel11)
                    .addComponent(jButtonSearchByBalance)
                    .addComponent(jFormattedTextFieldBalanceMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextFieldBalanceMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jButtonExportPdfStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423071193_Adobe Acrobat Professional32.png"))); // NOI18N
        jButtonExportPdfStock.setToolTipText("Export to Pdf");
        jButtonExportPdfStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportPdfStockActionPerformed(evt);
            }
        });

        jButtonPrintPriceStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/printer.png"))); // NOI18N
        jButtonPrintPriceStock.setToolTipText("Print Table");
        jButtonPrintPriceStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintPriceStockActionPerformed(evt);
            }
        });

        jButtonExportCSVStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/file_csv32.png"))); // NOI18N
        jButtonExportCSVStock.setToolTipText("Export to CSV");
        jButtonExportCSVStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportCSVStockActionPerformed(evt);
            }
        });

        jButtonClose3.setText("Close");
        jButtonClose3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClose3ActionPerformed(evt);
            }
        });

        jButtonExportExcelStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070960_Microsoft Office Excel32.png"))); // NOI18N
        jButtonExportExcelStock.setToolTipText("Export to Excel");
        jButtonExportExcelStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportExcelStockActionPerformed(evt);
            }
        });

        jButtonExportJxlsStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1424199566_application-vnd.ms-excel.png"))); // NOI18N
        jButtonExportJxlsStock.setToolTipText("Export Report");
        jButtonExportJxlsStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportJxlsStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonExportExcelStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonExportPdfStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonExportCSVStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jButtonExportJxlsStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonPrintPriceStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButtonClose3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonExportJxlsStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jButtonClose3))
                    .addComponent(jButtonExportPdfStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExportExcelStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExportCSVStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPrintPriceStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelFarmacyBalance.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)), "Farmacy balance"));

        jLabel13.setText("Farmacy balance is");

        jFormattedTextFieldFarmacyBalance.setEditable(false);
        jFormattedTextFieldFarmacyBalance.setBackground(new java.awt.Color(204, 204, 255));
        jFormattedTextFieldFarmacyBalance.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("0.00 MDL"))));
        jFormattedTextFieldFarmacyBalance.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFormattedTextFieldFarmacyBalanceFocusGained(evt);
            }
        });

        jButtonSetFarmacyBalance.setText("Set new balance");
        jButtonSetFarmacyBalance.setEnabled(false);
        jButtonSetFarmacyBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetFarmacyBalanceActionPerformed(evt);
            }
        });

        jButtonEditBalance.setText("Edit balance");
        jButtonEditBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditBalanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFarmacyBalanceLayout = new javax.swing.GroupLayout(jPanelFarmacyBalance);
        jPanelFarmacyBalance.setLayout(jPanelFarmacyBalanceLayout);
        jPanelFarmacyBalanceLayout.setHorizontalGroup(
            jPanelFarmacyBalanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFarmacyBalanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFarmacyBalanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFarmacyBalanceLayout.createSequentialGroup()
                        .addComponent(jButtonEditBalance)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonSetFarmacyBalance))
                    .addGroup(jPanelFarmacyBalanceLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(jFormattedTextFieldFarmacyBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFarmacyBalanceLayout.setVerticalGroup(
            jPanelFarmacyBalanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFarmacyBalanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFarmacyBalanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jFormattedTextFieldFarmacyBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelFarmacyBalanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSetFarmacyBalance)
                    .addComponent(jButtonEditBalance))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonAccessStockEditing.setText("Allow stock editing");
        jButtonAccessStockEditing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAccessStockEditingActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel4.setMaximumSize(new java.awt.Dimension(190, 90));
        jPanel4.setMinimumSize(new java.awt.Dimension(190, 90));
        jPanel4.setName(""); // NOI18N

        jLabelMedIcon2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMedIcon2.setMaximumSize(new java.awt.Dimension(185, 85));
        jLabelMedIcon2.setMinimumSize(new java.awt.Dimension(185, 85));
        jLabelMedIcon2.setPreferredSize(new java.awt.Dimension(185, 85));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMedIcon2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMedIcon2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanelStockLayout = new javax.swing.GroupLayout(jPanelStock);
        jPanelStock.setLayout(jPanelStockLayout);
        jPanelStockLayout.setHorizontalGroup(
            jPanelStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStockLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelStockLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelStockLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonAccessStockEditing, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelFarmacyBalance, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(170, Short.MAX_VALUE))
        );
        jPanelStockLayout.setVerticalGroup(
            jPanelStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStockLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanelStockLayout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(jButtonAccessStockEditing)
                        .addGap(18, 18, 18)
                        .addComponent(jPanelFarmacyBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane.addTab("Stock", jPanelStock);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refreshIcon() {
        byte[] iconByte;
        switch (jTabbedPane.getSelectedIndex()) {
            case 0:
                if (selectedMedicament != null) {
                    iconByte = selectedMedicament.getIconMedicament();
                    if (iconByte != null) {
                        jLabelMedIcon.setText("");
                        jLabelMedIcon.setIcon(new ImageIcon(iconByte));
                        break;
                    }
                }
                jLabelMedIcon.setIcon(new ImageIcon());
                jLabelMedIcon.setText("No Image");
                break;
            case 1:
                if (selectedPrice != null) {
                    iconByte = medDao.findByIdMedicament(selectedPrice.getIdMedicament()).getIconMedicament();
                    if (iconByte != null) {
                        jLabelMedIcon1.setText("");
                        jLabelMedIcon1.setIcon(new ImageIcon(iconByte));
                        break;
                    }
                }
                jLabelMedIcon1.setIcon(new ImageIcon());
                jLabelMedIcon1.setText("No Image");
                break;
            case 2:
                if (selectedStock != null) {
                    iconByte = medDao.findByIdMedicament(selectedStock.getIdMedicament()).getIconMedicament();
                    if (iconByte != null) {
                        jLabelMedIcon2.setText("");
                        jLabelMedIcon2.setIcon(new ImageIcon(iconByte));
                        break;
                    }
                }
                jLabelMedIcon2.setIcon(new ImageIcon());
                jLabelMedIcon2.setText("No Image");
        }
    }


    private void jButtonRemoveMedicamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveMedicamentActionPerformed
        if (selectedMedicament != null) {
            int rez = JOptionPane.showConfirmDialog(this, "Are you sure to delete " + selectedMedicament.getNameMedicament());
            if (rez == JOptionPane.OK_OPTION) {
                try {
                    int row = jTableMedicaments.getSelectedRow();
                    medDao.removeMedicament(selectedMedicament);
//                JOptionPane.showMessageDialog(this, selectedMedicament.getNameMedicament() + " was successfullly deleted");
//                clearSearchingLineMed();
                    clearSearchingPriceList();
                    jButtonClearSearchLineStockActionPerformed(null);
                    refreshTableMedicaments(-row - 1, selectedMedicament);
                } catch (DeletingException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select medicament to remove");
        }

    }//GEN-LAST:event_jButtonRemoveMedicamentActionPerformed

    private void clearSearchingLineMed() {
        jTextFieldSearchLineMedList.setText("");
        jComboBoxSearchCategoriesMedList.setSelectedIndex(0);
    }

    private void jButtonClearSearchLineMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearSearchLineMedActionPerformed
        clearSearchingLineMed();
        refreshTableMedicaments(0);
    }//GEN-LAST:event_jButtonClearSearchLineMedActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    //Refresh all table and select a row,after cancel of searching
    private void refreshTableMedicaments(int row) {
        aMedicamentTableModel.refreshModel();
        selectRowInTable(jTableMedicaments, row);
    }

    //Refresh model after insert a record
    private void refreshTableMedicaments(Medicament medicament) {
        aMedicamentTableModel.refreshModel(medicament);
        int row = jTableMedicaments.getRowCount() - 1;
        selectRowInTable(jTableMedicaments, row);
    }

    //Refreshing model after update or remove record
    private void refreshTableMedicaments(int row, Medicament medicament) {
        aMedicamentTableModel.refreshModel(row, medicament);
        if (row < 0) {
            {
                row = Math.abs(row) - 1;
            }
        }
        selectRowInTable(jTableMedicaments, row);
    }

    //Refreshing model after searching
    private void refreshTableMedicaments(List<Medicament> list) {
        aMedicamentTableModel.refreshModel(list);
        selectRowInTable(jTableMedicaments, 0);
    }

    private void jTextFieldSearchLineMedListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchLineMedListKeyReleased
        searchByNameMedList = true;
        if (jComboBoxSearchCategoriesMedList.getSelectedIndex() != 0) {
            jComboBoxSearchCategoriesMedList.setSelectedIndex(0);
        }
        String searchLine = jTextFieldSearchLineMedList.getText();
        List<Medicament> listMed = medDao.findByNameMedicament(searchLine);
        refreshTableMedicaments(listMed);
        searchByNameMedList = false;
    }//GEN-LAST:event_jTextFieldSearchLineMedListKeyReleased

    private void jComboBoxSearchCategoriesMedListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxSearchCategoriesMedListItemStateChanged
        if (jComboBoxSearchCategoriesMedList.getSelectedIndex() > 0) {
            Category cat = (Category) jComboBoxSearchCategoriesMedList.getSelectedItem();
            List<Medicament> listMed = medDao.findByIdCategoryMedicament(cat.getIdCategory());
            refreshTableMedicaments(listMed);
        } else {
            if (!searchByNameMedList) {
                refreshTableMedicaments(0);
            }
        }//end if

        if (!searchByNameMedList) {
            jTextFieldSearchLineMedList.setText("");
        }
    }//GEN-LAST:event_jComboBoxSearchCategoriesMedListItemStateChanged

    private void jButtonCreateMedicamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateMedicamentActionPerformed
        try {
            if (jdialogMedicament != null) {
                jdialogMedicament = null;
            }
            selectedMedicament = new Medicament();
            jdialogMedicament = new JDialogMedicamentSaveUpdate(this, true, "Save", selectedMedicament, dir);
            jdialogMedicament.setVisible(true);
//            Medicament medicament = selectedMedicament;
            clearSearchingLineMed();
            clearSearchingPriceList();
            jButtonClearSearchLineStockActionPerformed(null);
            refreshTableMedicaments(jTableMedicaments.getRowCount() - 1);
//            selectRowInTable(jTableMedicaments, jTableMedicaments.getRowCount() - 1);
//            if (selectedMedicament.getIdMedicament() != 0) {
//                refreshTableMedicaments(medicament);
//            }

        } catch (Exception ex) {
            Logger.getLogger(JFrameMedicament.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonCreateMedicamentActionPerformed

    private void jButtonEditMedicamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditMedicamentActionPerformed
        int row = jTableMedicaments.getSelectedRow();
        editMedicament(row);
    }//GEN-LAST:event_jButtonEditMedicamentActionPerformed

    private void editMedicament(int row) {
        if (selectedMedicament != null) {
            try {
                if (jdialogMedicament != null) {
                    jdialogMedicament = null;
                }
                jdialogMedicament = new JDialogMedicamentSaveUpdate(this, true, "Update", selectedMedicament, dir);
                //jdialogMedicament.fillForm(selectedMedicament);
                jdialogMedicament.setVisible(true);
                refreshTableMedicaments(row, selectedMedicament);
                clearSearchingPriceList();
                jButtonClearSearchLineStockActionPerformed(null);
            } catch (Exception ex) {
                Logger.getLogger(JFrameMedicament.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select medicament to edit");
        }
    }

    private void jTableMedicamentsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMedicamentsMousePressed
        if (mode.toString().equals("Manager")) {
            return;
        }
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            int row = jTableMedicaments.getSelectedRow();
            //jTableMedicaments.getCellEditor().stopCellEditing();
            editMedicament(row);
        }
        if (evt.isPopupTrigger()) {
            jPopupMenuTable.show(jTableMedicaments, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableMedicamentsMousePressed

    private void jMenuItemCreateMedicamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreateMedicamentActionPerformed
        jButtonCreateMedicamentActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemCreateMedicamentActionPerformed

    private void jMenuItemEditMEdicamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditMEdicamentActionPerformed
        jButtonEditMedicamentActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemEditMEdicamentActionPerformed

    private void jMenuItemRemoveMedicamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoveMedicamentActionPerformed
        jButtonRemoveMedicamentActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemRemoveMedicamentActionPerformed

    private void jTableMedicamentsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMedicamentsMouseReleased
        if (mode.toString().equals("Manager")) {
            return;
        }
        if (evt.getButton() == MouseEvent.BUTTON3) {
            int row = jTableMedicaments.rowAtPoint(evt.getPoint());
            //int column = jTableMedicaments.columnAtPoint(evt.getPoint());
            jTableMedicaments.setRowSelectionInterval(row, row);
            //jTableMedicaments.setColumnSelectionInterval(column, column);
        }

        if (evt.isPopupTrigger()) {
            jPopupMenuTable.show(jTableMedicaments, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableMedicamentsMouseReleased

    private void addRemoveAnalogs() {
        if (selectedMedicament != null) {
            try {
                if (jFrameAnalogs == null) {
                    jFrameAnalogs = new JFrameAnalogs();
                }
                jFrameAnalogs.setMedicament(selectedMedicament);
                jFrameAnalogs.setVisible(true);
                //

            } catch (Exception ex) {
                Logger.getLogger(JFrameMedicament.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select medicament to edit");
        }

    }

    private void jButtonAddRemoveAnalogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddRemoveAnalogsActionPerformed
        addRemoveAnalogs();
    }//GEN-LAST:event_jButtonAddRemoveAnalogsActionPerformed

    private void jMenuItemAddRemoveAnalogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddRemoveAnalogsActionPerformed
        addRemoveAnalogs();
    }//GEN-LAST:event_jMenuItemAddRemoveAnalogsActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        if (selectedMedicament != null) {
            analogsListModel.refreshModel(selectedMedicament);
        }
    }//GEN-LAST:event_formWindowActivated

    private void jListAnalogsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAnalogsMousePressed
        if (evt.isPopupTrigger()) {
            jPopupMenuAnalogList.show(jListAnalogs, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jListAnalogsMousePressed

    private void jMenuItemAddAnalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddAnalogActionPerformed
        addRemoveAnalogs();
    }//GEN-LAST:event_jMenuItemAddAnalogActionPerformed

    private void jListAnalogsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAnalogsMouseReleased
        if (mode.toString().equals("Manager")) {
            return;
        }
        if (evt.isPopupTrigger()) {
            jPopupMenuAnalogList.show(jListAnalogs, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jListAnalogsMouseReleased

    private void jListAnalogsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAnalogsValueChanged
        selectedAnalog = (Medicament) jListAnalogs.getSelectedValue();
    }//GEN-LAST:event_jListAnalogsValueChanged

    private void jMenuItemRemoveAnalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoveAnalogActionPerformed
        if (mode.toString().equals("Manager")) {
            return;
        }

        if (selectedAnalog != null) {
            int rez = JOptionPane.showConfirmDialog(this, "Are you sure to delete analog" + selectedAnalog.getNameMedicament());
            if (rez == JOptionPane.OK_OPTION) {
                try {
                    analogsDao = new AnalogDaoImpl();
                    analogsDao.removeAnalog(new Analog(selectedMedicament.getIdMedicament(), selectedAnalog.getIdMedicament()));
                    analogsListModel.refreshModel(selectedMedicament);

                } catch (Exception ex) {
                    Logger.getLogger(JFrameMedicament.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a medicament to remove");
        }
    }//GEN-LAST:event_jMenuItemRemoveAnalogActionPerformed

    private int getRowNumberByIdMedicamentInTable(JTable jTable, int idMedicament) {
        DefaultTableModel dft = (DefaultTableModel) jTable.getModel();
        Vector vector = dft.getDataVector();
        int id;
        for (int i = 0; i < jTable.getRowCount(); i++) {
            id = (Integer) ((Vector) vector.elementAt(i)).elementAt(0);
            if (id == idMedicament) {
                return i;
            }
        }
        return -1;
    }

    private int getRowNumberByIdMedicamentInTablePrices(int idMedicament) {
        Vector vector = aPriceTableModel.getDataVector();
        int id = -1;
        for (int i = 0; i < jTablePrices.getRowCount(); i++) {
            id = (Integer) ((Vector) vector.elementAt(i)).elementAt(0);
            if (id == idMedicament) {
                return i;
            }
        }
        return -1;
    }

    private void tryToSelectRecordInTableByIdMedicament(JTable jTable, int idMedicament) {
        int row = getRowNumberByIdMedicamentInTable(jTable, idMedicament);
        selectRowInTable(jTable, row);
    }

    private void clearSearchingPriceList() {
        jTextFieldSearchLinePriceList.setText("");
        jComboBoxCategoryPriceList.setSelectedIndex(0);
        refreshTablePrices(0);
    }

    //Refresh all table and select a row,after cancel of searching
    private void refreshTablePrices(int row) {
        aPriceTableModel.refreshModel();
        selectRowInTable(jTablePrices, row);
    }

    //Refresh all table and select a row,after cancel of searching
    private void refreshTableStock(int row) {
        aStockTableModel.refreshModel();
        selectRowInTable(jTableStock, row);
    }

    //Refreshing model after searching
    private void refreshTablePrices(List<Price> list) {
        aPriceTableModel.refreshModel(list);
        selectRowInTable(jTablePrices, 0);
    }

    //Refreshing model after searching
    private void refreshTableStock(List<Stock> list) {
        aStockTableModel.refreshModel(list);
        selectRowInTable(jTableStock, 0);
    }

    //Refreshing model after update record
    private void refreshTablePrices(int row, Price price) {
        aPriceTableModel.refreshModel(row, price);
        selectRowInTable(jTablePrices, row);
    }


    private void jPanelMedicamentListComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelMedicamentListComponentShown
        int row = jTableMedicaments.getSelectedRow();
        //aMedicamentTableModel.refreshModel();

        switch (previousTabIndex) {
            case 0:
                break;
            case 1:
                if (selectedRowTablePrices >= 0) {
                    tryToSelectRecordInTableByIdMedicament(jTableMedicaments, selectedPrice.getIdMedicament());
                } else {
                    selectRowInTable(jTableMedicaments, row);
                }
                break;
            case 2:
                if (selectedRowTableStock >= 0) {
                    tryToSelectRecordInTableByIdMedicament(jTableMedicaments, selectedStock.getIdMedicament());
                } else {
                    selectRowInTable(jTableMedicaments, row);
                }
        }
        refreshIcon();
    }//GEN-LAST:event_jPanelMedicamentListComponentShown

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        jTabbedPane.setSelectedIndex(activeTab);
        aCategoryComboBoxModelMedList.refreshModel();
        aCategorySearchComboBoxModelPriceList.refreshModel();
        //jFormattedTextFieldFarmacyBalance.setValue(farmDao.getFarmacyBalance());
        switch (activeTab) {
            case 0:
                aMedicamentTableModel.refreshModel();
                break;
            case 1:
                aPriceTableModel.refreshModel();
                break;
            case 2:
                aStockTableModel.refreshModel();
        }
    }//GEN-LAST:event_formComponentShown

    private void clearSearchingStock() {
        jTextFieldSearchLineStock.setText("");
        jFormattedTextFieldBalanceMin.setText("");
        jFormattedTextFieldBalanceMax.setText("");
    }

    private Object[] createExportView() {
        String header = "";
        String footer = "";
        JTable jTable = null;
        String filter1 = "";
        String filter2 = "";
        switch (jTabbedPane.getSelectedIndex()) {
            case 0:
                if (jComboBoxSearchCategoriesMedList.getSelectedIndex() > 0) {
                    filter1 = "Category=" + jComboBoxSearchCategoriesMedList.getSelectedItem() + ".";
                } else {
                    filter1 = "All categories.";
                }
                if (!jTextFieldSearchLineMedList.getText().equals("")) {
                    filter2 = "Name contains " + jTextFieldSearchLineMedList.getText() + ".";
                } else {
                    filter2 = "Without filter";
                }
                header = "Table of medicaments";
                jTable = jTableMedicaments;
                break;
            case 1:
                if (jComboBoxCategoryPriceList.getSelectedIndex() > 0) {
                    filter1 = "Category=" + jComboBoxCategoryPriceList.getSelectedItem() + ".";
                } else {
                    filter1 = "All categories.";
                }
                if (!jTextFieldSearchLinePriceList.getText().equals("")) {
                    filter2 = "Name contains " + jTextFieldSearchLinePriceList.getText() + ".";
                } else {
                    filter2 = "Without filter";
                }
                header = "Price list of medicaments";
                jTable = jTablePrices;
                break;
            case 2:
                if (!jTextFieldSearchLineStock.getText().equals("")) {
                    filter2 = "Name contains " + jTextFieldSearchLineStock.getText() + ".";
                } else {
                    filter2 = "Without filter";
                }
                if (!jFormattedTextFieldBalanceMin.getText().equals("") || !jFormattedTextFieldBalanceMax.getText().equals("")) {
                    filter1 = "Balance Between " + jFormattedTextFieldBalanceMin.getText() + " and "
                            + jFormattedTextFieldBalanceMax.getText() + ".";
                }
                header = "Stock of medicaments";
                jTable = jTablePrices;
        }
        footer = filter1 + filter2;
        return new Object[]{jTable, header, footer};
    }

    private void jButtonExportExcelMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportExcelMedActionPerformed
        Object views[] = createExportView();
        ExportReports.exportTableModelDialog(
                "xls", (JTable) views[0], (String) views[1], (String) views[2]);
    }//GEN-LAST:event_jButtonExportExcelMedActionPerformed

    private void jButtonExportPdfMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportPdfMedActionPerformed
        Object views[] = createExportView();
        ExportReports.exportTableModelDialog(
                "pdf", (JTable) views[0], (String) views[1], (String) views[2]);
    }//GEN-LAST:event_jButtonExportPdfMedActionPerformed

    private void jButtonExportCSVMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportCSVMedActionPerformed
        Object views[] = createExportView();
        ExportReports.exportTableModelDialog(
                "csv", (JTable) views[0], null, null);
    }//GEN-LAST:event_jButtonExportCSVMedActionPerformed

    private void jButtonPrintMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintMedActionPerformed
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
    }//GEN-LAST:event_jButtonPrintMedActionPerformed

    private void jScrollPane1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MousePressed
        if (evt.isPopupTrigger()) {
            jPopupMenuTable.show(jTableMedicaments, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jScrollPane1MousePressed

    private void jScrollPane1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseReleased
        if (evt.isPopupTrigger()) {
            jPopupMenuTable.show(jTableMedicaments, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jScrollPane1MouseReleased

    private void jPanelPriceListComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelPriceListComponentShown
        int row = jTablePrices.getSelectedRow();
        //aPriceTableModel.refreshModel();
        switch (previousTabIndex) {
            case 0:
                if (selectedRowTableMedicaments >= 0) {
                    tryToSelectRecordInTableByIdMedicament(jTablePrices, selectedMedicament.getIdMedicament());
                } else {
                    selectRowInTable(jTablePrices, row);
                }
                break;
            case 1:
                break;
            case 2:
                if (selectedRowTableStock >= 0) {
                    tryToSelectRecordInTableByIdMedicament(jTableMedicaments, selectedStock.getIdMedicament());
                } else {
                    selectRowInTable(jTableMedicaments, row);
                }
        }
        refreshIcon();
        //clearSearchPriceList();
    }//GEN-LAST:event_jPanelPriceListComponentShown

    private void jButtonExportCSVPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportCSVPriceActionPerformed
        jButtonExportCSVMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonExportCSVPriceActionPerformed

    private void jButtonExportExcelPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportExcelPriceActionPerformed
        jButtonExportExcelMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonExportExcelPriceActionPerformed

    private void jButtonExportPdfPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportPdfPriceActionPerformed
        jButtonExportPdfMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonExportPdfPriceActionPerformed

    private void jButtonPrintPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintPriceActionPerformed
        jButtonPrintMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonPrintPriceActionPerformed

    private void jButtonClose2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClose2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonClose2ActionPerformed

    private void jComboBoxCategoryPriceListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCategoryPriceListItemStateChanged
        Price price = selectedPrice;
        if (jComboBoxCategoryPriceList.getSelectedIndex() > 0) {
            jTextFieldSearchLinePriceList.setText("");
            Category categ = (Category) jComboBoxCategoryPriceList.getSelectedItem();
            List<Price> listPrice = priceDao.findByCategoryPriceList(categ);
            refreshTablePrices(listPrice);
        } else {
            if (!searchByNamePriceList) {
                jTextFieldSearchLinePriceList.setText("");
                refreshTablePrices(0);
            }
        }
        //        tryToSelectRowInTableById(jTablePrices, price.getIdMedicament());
    }//GEN-LAST:event_jComboBoxCategoryPriceListItemStateChanged

    private void jButtonClearSearchLinePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearSearchLinePriceActionPerformed
        clearSearchingPriceList();
    }//GEN-LAST:event_jButtonClearSearchLinePriceActionPerformed

    private void jTextFieldSearchLinePriceListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchLinePriceListKeyReleased
        String searchLine = jTextFieldSearchLinePriceList.getText();
        searchByNamePriceList = true;
        jComboBoxCategoryPriceList.setSelectedIndex(0);
        searchByNamePriceList = false;
        List<Price> listPrice = priceDao.findByNameMedicamentPriceList(searchLine);
        refreshTablePrices(listPrice);
    }//GEN-LAST:event_jTextFieldSearchLinePriceListKeyReleased

    private void jButtonRevertPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRevertPriceActionPerformed
        fillPriceForm(selectedPrice);
    }//GEN-LAST:event_jButtonRevertPriceActionPerformed

    private void savePrice() {
        selectedPrice.setUnitPrice(Double.parseDouble(jFormattedTextFieldUnitPrice.getText()));
        selectedPrice.setMargin(Double.parseDouble(jFormattedTextFieldMargin.getText()));
        selectedPrice.setSaleUnitPrice(Double.parseDouble(jFormattedTextFieldSaleUnitPrice.getText()));
        priceDao.updatePrice(selectedPrice);
        jButtonSavePrice.setEnabled(false);
        aPriceTableModel.refreshModel(selectedRowTablePrices, selectedPrice);
    }


    private void jButtonSavePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSavePriceActionPerformed
        //int row = jTablePrices.getSelectedRow();
        savePrice();
    }//GEN-LAST:event_jButtonSavePriceActionPerformed

    private void jButtonCalcSellPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCalcSellPriceActionPerformed
        double unitPrice = Double.parseDouble(jFormattedTextFieldUnitPrice.getText());
        double margin = Double.parseDouble(jFormattedTextFieldMargin.getText());
        double salePrice = Util.roundTo(unitPrice * margin);
        jFormattedTextFieldSaleUnitPrice.setText("" + salePrice);
        jButtonSavePrice.setEnabled(true);
    }//GEN-LAST:event_jButtonCalcSellPriceActionPerformed

    private void jButtonExportJxlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportJxlsActionPerformed
        Report report = new Report();
        report.setFarmacyName(farmDao.getFarmacyName());
        report.setInnerData("");

        if (!jTextFieldSearchLineMedList.getText().equals("")) {
            report.setHeader("Name contains " + jTextFieldSearchLineMedList.getText() + ".");
        } else {
            report.setHeader("");
        }
        if (jComboBoxSearchCategoriesMedList.getSelectedIndex() > 0) {
            report.setFooter("Category=" + jComboBoxSearchCategoriesMedList.getSelectedItem() + ".");
        } else {
            report.setFooter("");
        }

        int rows = jTableMedicaments.getRowCount();
        List reportList = new ArrayList();
        for (int i = 0; i < rows; i++) {
            reportList.add(new MedicamentsReport(
                    (Integer) jTableMedicaments.getValueAt(i, 0),
                    (String) jTableMedicaments.getValueAt(i, 1),
                    (String) jTableMedicaments.getValueAt(i, 2),
                    (String) jTableMedicaments.getValueAt(i, 3)));
        }
        report.setData(reportList);
        ExportReports.exportJxlsDialog(report, "MedicamentsReport", "medicamentsreport.xls");

    }//GEN-LAST:event_jButtonExportJxlsActionPerformed

    private void jPanelStockComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelStockComponentShown
        int row = jTableStock.getSelectedRow();
        //refreshTableStock(stockDao.findAllStockList());
        switch (previousTabIndex) {
            case 0:
                if (selectedRowTableMedicaments >= 0) {
                    tryToSelectRecordInTableByIdMedicament(jTableStock, selectedMedicament.getIdMedicament());
                } else {
                    selectRowInTable(jTableStock, row);
                }
                break;
            case 1:
                if (selectedRowTablePrices >= 0) {
                    tryToSelectRecordInTableByIdMedicament(jTableStock, selectedPrice.getIdMedicament());
                } else {
                    selectRowInTable(jTableStock, row);
                }
                break;
            case 2:
        }
//        jFormattedTextFieldFarmacyBalance.setValue(farmDao.getFarmacyBalance());
        refreshIcon();
    }//GEN-LAST:event_jPanelStockComponentShown

    private void jButtonAccessStockEditingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAccessStockEditingActionPerformed
        if (jButtonAccessStockEditing.getText().startsWith("Allow")) {
            aStockTableModel.canEdit[2] = true;
            jButtonAccessStockEditing.setText("Restrict stock editing");
            jTableStock.getCellEditor(0, 2).addCellEditorListener(new CellEditorListener() {

                @Override
                public void editingStopped(ChangeEvent e) {
                    double newBalance = (Double) jTableStock.getValueAt(selectedRowTableStock, 2);
                    if (selectedStock != null && selectedStock.getBalance() != newBalance) {
                        try {
                            selectedStock.setBalance(newBalance);
                            stockDao.updateStock(selectedStock);
                        } catch (StockException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", ERROR_MESSAGE);
                        }
                    }
                    //JOptionPane.showMessageDialog(null, "Editing stopped");
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                    JOptionPane.showMessageDialog(null, "Editing cancelled");
                }
            });
        } else {
            aStockTableModel.canEdit[2] = false;
            jButtonAccessStockEditing.setText("Allow stock editing");
        }
    }//GEN-LAST:event_jButtonAccessStockEditingActionPerformed

    private void jButtonEditBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditBalanceActionPerformed
        if (jButtonEditBalance.getText().startsWith("Edit")) {
            int rez = JOptionPane.showConfirmDialog(this, "STRONGLY not recommended to change this value.Change balance?", "!!!", JOptionPane.YES_NO_OPTION);
            if (rez == YES_OPTION) {
                jFormattedTextFieldFarmacyBalance.setEditable(true);
                jFormattedTextFieldFarmacyBalance.setBackground(new Color(240, 240, 240));
                jButtonSetFarmacyBalance.setEnabled(true);

                jButtonEditBalance.setText("Escape editing");
            }
        } else {
            jFormattedTextFieldFarmacyBalance.setEditable(false);
            jFormattedTextFieldFarmacyBalance.setBackground(new Color(204, 204, 255));
            jButtonSetFarmacyBalance.setEnabled(false);
            jButtonEditBalance.setText("Edit balance");
            jFormattedTextFieldFarmacyBalance.setValue(farmDao.getFarmacyBalance());
        }
    }//GEN-LAST:event_jButtonEditBalanceActionPerformed

    private void jButtonSetFarmacyBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetFarmacyBalanceActionPerformed
        try {
            try {
                farmDao.setFarmacyBalance(((Double) jFormattedTextFieldFarmacyBalance.getValue()));
            } catch (ClassCastException ex) {
                farmDao.setFarmacyBalance(((Long) jFormattedTextFieldFarmacyBalance.getValue()));
            }
        } catch (NoSuchMoneyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        }
        jFormattedTextFieldFarmacyBalance.setEditable(false);
        jFormattedTextFieldFarmacyBalance.setBackground(new Color(204, 204, 255));
        jButtonSetFarmacyBalance.setEnabled(false);
        jButtonEditBalance.setText("Edit balance");
    }//GEN-LAST:event_jButtonSetFarmacyBalanceActionPerformed

    private void jButtonExportExcelStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportExcelStockActionPerformed
        jButtonExportExcelMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonExportExcelStockActionPerformed

    private void jButtonClose3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClose3ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonClose3ActionPerformed

    private void jButtonExportCSVStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportCSVStockActionPerformed
        jButtonExportCSVMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonExportCSVStockActionPerformed

    private void jButtonPrintPriceStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintPriceStockActionPerformed
        jButtonPrintMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonPrintPriceStockActionPerformed

    private void jButtonExportPdfStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportPdfStockActionPerformed
        jButtonExportPdfMedActionPerformed(evt);
    }//GEN-LAST:event_jButtonExportPdfStockActionPerformed

    private void jButtonSearchByBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchByBalanceActionPerformed
        jTextFieldSearchLineStock.setText("");
        String balMinText = jFormattedTextFieldBalanceMin.getText();
        String balMaxText = jFormattedTextFieldBalanceMax.getText();
        if (balMinText == null || balMinText.equals("")) {
            balMinText = "0";
            jFormattedTextFieldBalanceMin.setText("0");
        }
        if (balMaxText == null || balMaxText.equals("")) {
            balMaxText = "10000";
            jFormattedTextFieldBalanceMax.setText("10000");
        }
        try {
            double balMin = Double.parseDouble(balMinText);
            double balMax = Double.parseDouble(balMaxText);
            listStock = stockDao.findByBalanceStockList(balMin, balMax);
            refreshTableStock(listStock);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please input correct number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSearchByBalanceActionPerformed

    private void jButtonClearSearchLineStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearSearchLineStockActionPerformed
        clearSearchingStock();
        refreshTableStock(0);
    }//GEN-LAST:event_jButtonClearSearchLineStockActionPerformed

    private void jTextFieldSearchLineStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchLineStockKeyReleased
        jFormattedTextFieldBalanceMin.setText("");
        jFormattedTextFieldBalanceMax.setText("");
        String searchLine = jTextFieldSearchLineStock.getText();
        listStock = stockDao.findByNameMedicamentStockList(searchLine);
        refreshTableStock(listStock);
    }//GEN-LAST:event_jTextFieldSearchLineStockKeyReleased

    private void jButtonExportJxlsStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportJxlsStockActionPerformed
        Report report = new Report();
        report.setFarmacyName(farmDao.getFarmacyName());
        report.setInnerData("");
        String additionHeader = "";
        if (!jTextFieldSearchLineStock.getText().equals("")) {
            additionHeader = "Name contains " + jTextFieldSearchLineStock.getText() + ".";
        }
        report.setHeader("" + new java.util.Date() + additionHeader);
        if (!jFormattedTextFieldBalanceMin.getText().equals("") || !jFormattedTextFieldBalanceMax.getText().equals("")) {
            report.setFooter("Balance between " + jFormattedTextFieldBalanceMin.getText() + " and " + jFormattedTextFieldBalanceMax.getText());
        } else {
            report.setFooter("");
        }
        int rows = jTableStock.getRowCount();
        List reportList = new ArrayList();
        for (int i = 0; i < rows; i++) {
            reportList.add(new StockReport(
                    (Integer) jTableStock.getValueAt(i, 0),
                    (String) jTableStock.getValueAt(i, 1),
                    (Double) jTableStock.getValueAt(i, 2)));
        }
        report.setData(reportList);
        ExportReports.exportJxlsDialog(report, "StockReport", "stockreport.xls");
    }//GEN-LAST:event_jButtonExportJxlsStockActionPerformed

    private void jButtonExportJxlsPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportJxlsPriceActionPerformed
        Report report = new Report();
        report.setFarmacyName(farmDao.getFarmacyName());
        report.setInnerData("");

        if (!jTextFieldSearchLinePriceList.getText().equals("")) {
            report.setHeader("Name contains " + jTextFieldSearchLinePriceList.getText() + ".");
        } else {
            report.setHeader("");
        }
        if (jComboBoxCategoryPriceList.getSelectedIndex() > 0) {
            report.setFooter("Category=" + jComboBoxCategoryPriceList.getSelectedItem() + ".");
        } else {
            report.setFooter("");
        }
        int rows = jTablePrices.getRowCount();
        List reportList = new ArrayList();
        String nameReport;
        String templateName;
        if (mode.toString().equals("Manager")) {
            nameReport = "PriceReportManager";
            templateName = "pricereportmanager.xls";
            for (int i = 0; i < rows; i++) {
                reportList.add(new PriceReportManager(
                        (Integer) jTablePrices.getValueAt(i, 0),
                        (String) jTablePrices.getValueAt(i, 1),
                        (String) jTablePrices.getValueAt(i, 2)));
            }
        } else {
            nameReport = "PriceReportAdmin";
            templateName = "pricereportadmin.xls";
            for (int i = 0; i < rows; i++) {
                reportList.add(new PriceReportAdmin(
                        (Integer) jTablePrices.getValueAt(i, 0),
                        (String) jTablePrices.getValueAt(i, 1),
                        (String) jTablePrices.getValueAt(i, 2),
                        (Double) jTablePrices.getValueAt(i, 3),
                        (String) jTablePrices.getValueAt(i, 4)));
            }
        }
        report.setData(reportList);
        ExportReports.exportJxlsDialog(report, nameReport, templateName);
    }//GEN-LAST:event_jButtonExportJxlsPriceActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        jFormattedTextFieldFarmacyBalance.setValue(farmDao.getFarmacyBalance());
        int row = jTableStock.getSelectedRow();
        if (row < 0) {
            row = 0;
        }
        refreshTableStock(row);
    }//GEN-LAST:event_formWindowGainedFocus

    private void jFormattedTextFieldMarginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldMarginActionPerformed
        jButtonCalcSellPriceActionPerformed(evt);
    }//GEN-LAST:event_jFormattedTextFieldMarginActionPerformed

    private void jFormattedTextFieldUnitPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldUnitPriceKeyReleased
        jButtonSavePrice.setEnabled(true);
    }//GEN-LAST:event_jFormattedTextFieldUnitPriceKeyReleased

    private void jFormattedTextFieldMarginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldMarginKeyReleased
        jButtonSavePrice.setEnabled(true);
    }//GEN-LAST:event_jFormattedTextFieldMarginKeyReleased

    private void jFormattedTextFieldSaleUnitPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldSaleUnitPriceKeyReleased
        jButtonSavePrice.setEnabled(true);
    }//GEN-LAST:event_jFormattedTextFieldSaleUnitPriceKeyReleased

    private void jFormattedTextFieldMarginCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jFormattedTextFieldMarginCaretUpdate

    }//GEN-LAST:event_jFormattedTextFieldMarginCaretUpdate

    private void jFormattedTextFieldMarginFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFormattedTextFieldMarginFocusGained
        jFormattedTextFieldMargin.setSelectionStart(0);
        jFormattedTextFieldMargin.setSelectionEnd(jFormattedTextFieldMargin.getText().length());
    }//GEN-LAST:event_jFormattedTextFieldMarginFocusGained

    private void jFormattedTextFieldUnitPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFormattedTextFieldUnitPriceFocusGained
        jFormattedTextFieldUnitPrice.setSelectionStart(0);
        jFormattedTextFieldUnitPrice.setSelectionEnd(jFormattedTextFieldUnitPrice.getText().length());
    }//GEN-LAST:event_jFormattedTextFieldUnitPriceFocusGained

    private void jFormattedTextFieldSaleUnitPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFormattedTextFieldSaleUnitPriceFocusGained
        jFormattedTextFieldSaleUnitPrice.setSelectionStart(0);
        jFormattedTextFieldSaleUnitPrice.setSelectionEnd(jFormattedTextFieldSaleUnitPrice.getText().length());
    }//GEN-LAST:event_jFormattedTextFieldSaleUnitPriceFocusGained

    private void jFormattedTextFieldFarmacyBalanceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFormattedTextFieldFarmacyBalanceFocusGained
        jFormattedTextFieldFarmacyBalance.setSelectionStart(0);
        jFormattedTextFieldFarmacyBalance.setSelectionEnd(jFormattedTextFieldFarmacyBalance.getText().length() - 4);
    }//GEN-LAST:event_jFormattedTextFieldFarmacyBalanceFocusGained

    private void clearPriceForm() {
        jTextFieldNameMedicament.setText("");
        jFormattedTextFieldUnitPrice.setText("");
        jFormattedTextFieldMargin.setText("");
        jFormattedTextFieldSaleUnitPrice.setText("");
    }

    private void fillPriceForm(Price price) {
        jTextFieldNameMedicament.setText(price.getNameMedicament());
        jFormattedTextFieldUnitPrice.setText("" + price.getUnitPrice());
        jFormattedTextFieldMargin.setText("" + price.getMargin());
        jFormattedTextFieldSaleUnitPrice.setText("" + price.getSaleUnitPrice());
        jButtonSavePrice.setEnabled(false);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAccessStockEditing;
    private javax.swing.JButton jButtonAddRemoveAnalogs;
    private javax.swing.JButton jButtonCalcSellPrice;
    private javax.swing.JButton jButtonClearSearchLineMed;
    private javax.swing.JButton jButtonClearSearchLinePrice;
    private javax.swing.JButton jButtonClearSearchLineStock;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonClose2;
    private javax.swing.JButton jButtonClose3;
    private javax.swing.JButton jButtonCreateMedicament;
    private javax.swing.JButton jButtonEditBalance;
    private javax.swing.JButton jButtonEditMedicament;
    private javax.swing.JButton jButtonExportCSVMed;
    private javax.swing.JButton jButtonExportCSVPrice;
    private javax.swing.JButton jButtonExportCSVStock;
    private javax.swing.JButton jButtonExportExcelMed;
    private javax.swing.JButton jButtonExportExcelPrice;
    private javax.swing.JButton jButtonExportExcelStock;
    private javax.swing.JButton jButtonExportJxls;
    private javax.swing.JButton jButtonExportJxlsPrice;
    private javax.swing.JButton jButtonExportJxlsStock;
    private javax.swing.JButton jButtonExportPdfMed;
    private javax.swing.JButton jButtonExportPdfPrice;
    private javax.swing.JButton jButtonExportPdfStock;
    private javax.swing.JButton jButtonPrintMed;
    private javax.swing.JButton jButtonPrintPrice;
    private javax.swing.JButton jButtonPrintPriceStock;
    private javax.swing.JButton jButtonRemoveMedicament;
    private javax.swing.JButton jButtonRevertPrice;
    private javax.swing.JButton jButtonSavePrice;
    private javax.swing.JButton jButtonSearchByBalance;
    private javax.swing.JButton jButtonSetFarmacyBalance;
    private javax.swing.JComboBox jComboBoxCategoryPriceList;
    private javax.swing.JComboBox jComboBoxSearchCategoriesMedList;
    private javax.swing.JFormattedTextField jFormattedTextFieldBalanceMax;
    private javax.swing.JFormattedTextField jFormattedTextFieldBalanceMin;
    private javax.swing.JFormattedTextField jFormattedTextFieldFarmacyBalance;
    private javax.swing.JFormattedTextField jFormattedTextFieldMargin;
    private javax.swing.JFormattedTextField jFormattedTextFieldSaleUnitPrice;
    private javax.swing.JFormattedTextField jFormattedTextFieldUnitPrice;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelMedIcon;
    private javax.swing.JLabel jLabelMedIcon1;
    private javax.swing.JLabel jLabelMedIcon2;
    private javax.swing.JList jListAnalogs;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItemAddAnalog;
    private javax.swing.JMenuItem jMenuItemAddRemoveAnalogs;
    private javax.swing.JMenuItem jMenuItemCreateMedicament;
    private javax.swing.JMenuItem jMenuItemEditMEdicament;
    private javax.swing.JMenuItem jMenuItemRemoveAnalog;
    private javax.swing.JMenuItem jMenuItemRemoveMedicament;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelEditPrice;
    private javax.swing.JPanel jPanelFarmacyBalance;
    private javax.swing.JPanel jPanelMedicamentList;
    private javax.swing.JPanel jPanelPriceList;
    private javax.swing.JPanel jPanelStock;
    private javax.swing.JPopupMenu jPopupMenuAnalogList;
    private javax.swing.JPopupMenu jPopupMenuTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableMedicaments;
    private javax.swing.JTable jTablePrices;
    private javax.swing.JTable jTableStock;
    private javax.swing.JTextField jTextFieldNameMedicament;
    private javax.swing.JTextField jTextFieldSearchLineMedList;
    private javax.swing.JTextField jTextFieldSearchLinePriceList;
    private javax.swing.JTextField jTextFieldSearchLineStock;
    // End of variables declaration//GEN-END:variables

    MedicamentTableModel aMedicamentTableModel;
    MedicamentDaoIntf medDao;
    CategoryComboBoxModel aCategoryComboBoxModelMedList;
    boolean searchByNameMedList;
    Medicament selectedMedicament;
    JDialogMedicamentSaveUpdate jdialogMedicament;
    AnalogListModel analogsListModel;
    JFrameAnalogs jFrameAnalogs;
    Medicament selectedAnalog;
    AnalogDaoIntf analogsDao;
    CategoryComboBoxModel aCategorySearchComboBoxModelPriceList;
    PriceTableModel aPriceTableModel;
    boolean searchByNamePriceList;
    Price selectedPrice;
    PriceDaoIntf priceDao;
    int selectedRowTableMedicaments = -1;
    int selectedRowTablePrices = -1;
    int previousTabIndex = 0;
    int currentTabIndex = 0;
    int selectedRowTableStock = -1;
    Stock selectedStock;
    StockDaoIntf stockDao;
    StockTableModel aStockTableModel;
    int activeTab;
    FarmacyBalanceDaoIntf farmDao;
    List<Stock> listStock;
    StringBuilder mode;
    File dir;
}
