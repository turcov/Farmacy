/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui;

import com.iucosoft.farmacy.dao.FarmacyBalanceDaoIntf;
import com.iucosoft.farmacy.dao.FinanceSubjectDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDetailDaoIntf;
import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.dao.PriceDaoIntf;
import com.iucosoft.farmacy.dao.StockDaoIntf;
import com.iucosoft.farmacy.dao.impl.FarmacyBalanceDaoImpl;
import com.iucosoft.farmacy.dao.impl.FinanceSubjectDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDetailDaoImpl;
import com.iucosoft.farmacy.dao.impl.MedicamentDaoImpl;
import com.iucosoft.farmacy.dao.impl.PriceDaoImpl;
import com.iucosoft.farmacy.dao.impl.StockDaoImpl;
import com.iucosoft.farmacy.db.Config;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.DeletingException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.NothingToExportException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.gui.models.ActStockTableModel;
import com.iucosoft.farmacy.gui.models.FinSubjListModel;
import com.iucosoft.farmacy.gui.models.InvoiceTableModel;
import com.iucosoft.farmacy.gui.models.StockTableModel;
import com.iucosoft.farmacy.utils.ExportReports;
import com.iucosoft.farmacy.model.Client;
import com.iucosoft.farmacy.model.FinanceSubject;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.model.InvoiceDetail;
import com.iucosoft.farmacy.model.InvoiceDetailPurchase;
import com.iucosoft.farmacy.model.InvoiceDetailSale;
import com.iucosoft.farmacy.model.InvoicePurchase;
import com.iucosoft.farmacy.model.InvoiceSale;
import com.iucosoft.farmacy.model.Medicament;
import com.iucosoft.farmacy.model.Stock;
import com.iucosoft.farmacy.model.Supplier;
import com.iucosoft.farmacy.reports.ActReport;
import com.iucosoft.farmacy.reports.InvoicesReport;
import com.iucosoft.farmacy.reports.Report;
import com.iucosoft.farmacy.reports.StockReport;
import com.iucosoft.farmacy.utils.Util;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;

/**
 *
 * @author Turkov S
 */
public class JFrameFarmacy extends javax.swing.JFrame {

    private static final Logger LOG = Logger.getLogger(JFrameFarmacy.class.getName());

    String args[] = null;

    /**
     * Creates new form JFrameFarmacy
     */
    public JFrameFarmacy(StringBuilder mode, DataSourceFarmacy dataSource) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        this.mode = mode;
        this.dataSource = dataSource;
        Properties props = Config.loadProperties();
        isJDatePicker = Boolean.valueOf(props.getProperty("jdatepicker"));
        invoiceClass = InvoiceSale.class;
        invoiceDao = new InvoiceDaoImpl(invoiceClass);
        stockDao = new StockDaoImpl();
        aStockTableModel = new StockTableModel();
        farmDao = new FarmacyBalanceDaoImpl();

        //    if (isJDatePicker) {
        SqlDateModel modelMin = new SqlDateModel(invoiceDao.getMinDate(selectedIndexFinSubject));
        JDatePanelImpl jDatePanelMin = new JDatePanelImpl(modelMin);
        jDatePickerMin = new JDatePickerImpl(jDatePanelMin);
//        jDatePickerMin.setSize(150, 30);
        SqlDateModel modelMax = new SqlDateModel(invoiceDao.getMaxDate(selectedIndexFinSubject));
        JDatePanelImpl jDatePanelMax = new JDatePanelImpl(modelMax);
        jDatePickerMax = new JDatePickerImpl(jDatePanelMax);
        //      jDatePickerMax.setSize(150, 30);
        //    } else {
        jSpinnerDateMin = new JSpinner();
        jSpinnerDateMax = new JSpinner();

        jSpinnerDateMin.setModel(new SpinnerDateModel());
        jSpinnerDateMax.setModel(new SpinnerDateModel());

        jSpinnerDateMin.setEditor(new DateEditor(jSpinnerDateMin, "dd.MM.yyyy"));
        jSpinnerDateMax.setEditor(new DateEditor(jSpinnerDateMax, "dd.MM.yyyy"));

        // jSpinnerDateMin.setValue(invoiceDao.getMinDate(selectedIndexFinSubject));
        //jSpinnerDateMax.setValue(invoiceDao.getMaxDate(selectedIndexFinSubject));
        //    jSpinnerDateMin.setSize(100, 30);
        //  jSpinnerDateMax.setSize(100, 30);
        //   }
        initComponents();
        initGuiComponents();
        addListeners();
        setLocationRelativeTo(null);
    }

    private void initGuiComponents() throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {

        clientDao = new FinanceSubjectDaoImpl(Client.class);
        clientListModel = new FinSubjListModel(Client.class);
        jListClients.setModel(clientListModel);
        supplierDao = new FinanceSubjectDaoImpl(Supplier.class);
        supplierListModel = new FinSubjListModel(Supplier.class);
        jListSuppliers.setModel(supplierListModel);
        farmDao = new FarmacyBalanceDaoImpl();

        aInvoiceTableModel = new InvoiceTableModel(invoiceClass);
        jTableFarmacy.setModel(aInvoiceTableModel);
        jTabbedFinSubjects.setPreferredSize(new Dimension(200, 200));

        jListClients.setSelectedIndex(0);
        //  if (isJDatePicker) {
        jPanelFarmacy.add(jDatePickerMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));
        jPanelFarmacy.add(jDatePickerMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));
        //   } else {
        jPanelFarmacy.add(jSpinnerDateMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));
        jPanelFarmacy.add(jSpinnerDateMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));
        //   }
        changePickerSpinner();
        //jPanelFarmacy.validate();
        //    jFormattedTextFieldSum1.setText("" + Util.truncDouble(invoiceDao.getMinSumInvoice(selectedIndexFinSubject)));
        //  jFormattedTextFieldSum2.setText("" + Util.roundTo(invoiceDao.getMaxSumInvoice(selectedIndexFinSubject)));
//        jFormattedTextFieldTotalOfInvoices.setValue(invoiceDao.getSumOfInvoices(selectedIndexFinSubject));

//        jFormattedTextFieldBalanceMin.setText("" + stockDao.getMinBalance());
        //      jFormattedTextFieldBalanceMax.setText("" + stockDao.getMaxBalance());
        jTableStock.setModel(aStockTableModel);
        fillJPanelFarmacy();
        Util.setColumnWidthTable(jTableStock, 30, 150, 50);
        Util.setColumnWidthTable(jTableFarmacy, 20, 150, 80, 80);
        medDao = new MedicamentDaoImpl();
        aActStockTableModel = new ActStockTableModel(null);
        modeRestrictionsApply();
        //       ((AbstractDocument)jFormattedTextFieldSum1.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
        //     ((AbstractDocument)jFormattedTextFieldSum2.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
        //   ((AbstractDocument)jFormattedTextFieldBalanceMin.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
        //((AbstractDocument)jFormattedTextFieldBalanceMax.getDocument()).setDocumentFilter(new PositiveNumberDocumentFilter());
    }

    private void addListeners() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                dataSource.disconnect();
                System.exit(0);
            }
        });
        jTableStock.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                int row = jTableStock.getSelectedRow();
                if (row >= 0) {
                    selectedIdMedicament = (Integer) jTableStock.getValueAt(row, 0);
                    selectedMedicament = medDao.findByIdMedicament(selectedIdMedicament);
                    jScrollPaneTableFarmacy.setBorder(BorderFactory.createTitledBorder("Sale and purchase history for " + selectedMedicament.getNameMedicament()));
                } else {
                    selectedIdMedicament = null;
                    selectedMedicament = null;
                }
                aActStockTableModel.refreshModel(selectedIdMedicament);
            }
        });

//        if (isJDatePicker) {
            jDatePickerMin.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
                    jFormattedTextFieldTotalOfInvoices.setValue(getTotalOfInvoices());
                }
            });

            jDatePickerMax.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
                    jFormattedTextFieldTotalOfInvoices.setValue(getTotalOfInvoices());
                }
            });
  //      } else {
            jSpinnerDateMin.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (!blockedRefreshing) {
                        refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
                        jFormattedTextFieldTotalOfInvoices.setValue(getTotalOfInvoices());
                    }
                }
            });
            jSpinnerDateMax.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (!blockedRefreshing) {
                        refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
                        jFormattedTextFieldTotalOfInvoices.setValue(getTotalOfInvoices());
                    }
                }
            });
//        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuInvoices = new javax.swing.JPopupMenu();
        jMenuItemNewInvoice = new javax.swing.JMenuItem();
        jMenuItemEditInvoice = new javax.swing.JMenuItem();
        jMenuItemDeleteInvoice = new javax.swing.JMenuItem();
        jPopupMenuFinSubjects = new javax.swing.JPopupMenu();
        jMenuItemAddFinSubject = new javax.swing.JMenuItem();
        jMenuItemEditFinSubject = new javax.swing.JMenuItem();
        jMenuItemDeleteFinSubject = new javax.swing.JMenuItem();
        jPopupMenuStock = new javax.swing.JPopupMenu();
        jMenuItemOrder = new javax.swing.JMenuItem();
        jMenuItemSale = new javax.swing.JMenuItem();
        jDesktopPane = new javax.swing.JDesktopPane();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonNewInvoice = new javax.swing.JButton();
        jButtonEditInvoice = new javax.swing.JButton();
        jButtonDeleteInvoice = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonCatalogueMedicaments = new javax.swing.JButton();
        jButtonPriceListMedicaments = new javax.swing.JButton();
        jButtonStockMedicaments = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonExportExcel = new javax.swing.JButton();
        jButtonExportPDF = new javax.swing.JButton();
        jButtonExportCSV = new javax.swing.JButton();
        jButtonExportReport = new javax.swing.JButton();
        jButtonPrint = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButtonChangeUser = new javax.swing.JButton();
        jButtonSettings = new javax.swing.JButton();
        jButtonAbout = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabelFarmacyBalance = new javax.swing.JLabel();
        jTextFieldFarmacyBalance = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedFinSubjects = new javax.swing.JTabbedPane();
        jScrollPaneClients = new javax.swing.JScrollPane();
        jListClients = new javax.swing.JList();
        jScrollPaneSuppliers = new javax.swing.JScrollPane();
        jListSuppliers = new javax.swing.JList();
        jScrollPaneStock = new javax.swing.JScrollPane();
        jTableStock = new javax.swing.JTable();
        jScrollPaneTableFarmacy = new javax.swing.JScrollPane();
        jTableFarmacy = new javax.swing.JTable();
        jPanelFarmacy = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jFormattedTextFieldTotalOfInvoices = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextFieldBalanceMin = new javax.swing.JFormattedTextField();
        jFormattedTextFieldBalanceMax = new javax.swing.JFormattedTextField();
        jFormattedTextFieldSum1 = new javax.swing.JFormattedTextField();
        jFormattedTextFieldSum2 = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuSales = new javax.swing.JMenu();
        jMenuClients = new javax.swing.JMenuItem();
        jMenuItemInvoicesS = new javax.swing.JMenuItem();
        jMenuPurchases = new javax.swing.JMenu();
        jMenuItemSuppliers = new javax.swing.JMenuItem();
        jMenuItemInvoicesP = new javax.swing.JMenuItem();
        jMenuMedDatabase = new javax.swing.JMenu();
        jMenuItemCategories = new javax.swing.JMenuItem();
        jMenuItemMedicamentsCatalog = new javax.swing.JMenuItem();
        jMenuItemPriceList = new javax.swing.JMenuItem();
        jMenuItemStock = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemExportExcel = new javax.swing.JMenuItem();
        jMenuItemExportPdf = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItemPrint = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemSettings = new javax.swing.JMenuItem();
        jMenuItemChangeUser = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        jMenuItemNewInvoice.setText("New Invoice");
        jMenuItemNewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewInvoiceActionPerformed(evt);
            }
        });
        jPopupMenuInvoices.add(jMenuItemNewInvoice);

        jMenuItemEditInvoice.setText("Edit Invoice");
        jMenuItemEditInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditInvoiceActionPerformed(evt);
            }
        });
        jPopupMenuInvoices.add(jMenuItemEditInvoice);

        jMenuItemDeleteInvoice.setText("Delete Invoice");
        jMenuItemDeleteInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteInvoiceActionPerformed(evt);
            }
        });
        jPopupMenuInvoices.add(jMenuItemDeleteInvoice);

        jMenuItemAddFinSubject.setText("New");
        jMenuItemAddFinSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddFinSubjectActionPerformed(evt);
            }
        });
        jPopupMenuFinSubjects.add(jMenuItemAddFinSubject);

        jMenuItemEditFinSubject.setText("Edit");
        jMenuItemEditFinSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditFinSubjectActionPerformed(evt);
            }
        });
        jPopupMenuFinSubjects.add(jMenuItemEditFinSubject);

        jMenuItemDeleteFinSubject.setText("Delete");
        jMenuItemDeleteFinSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteFinSubjectActionPerformed(evt);
            }
        });
        jPopupMenuFinSubjects.add(jMenuItemDeleteFinSubject);

        jMenuItemOrder.setText("Order selected medicaments");
        jMenuItemOrder.setToolTipText("");
        jMenuItemOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOrderActionPerformed(evt);
            }
        });
        jPopupMenuStock.add(jMenuItemOrder);

        jMenuItemSale.setText("Sale selected medicaments");
        jMenuItemSale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaleActionPerformed(evt);
            }
        });
        jPopupMenuStock.add(jMenuItemSale);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButtonNewInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070149_Add file.png"))); // NOI18N
        jButtonNewInvoice.setToolTipText("New Invoice");
        jButtonNewInvoice.setFocusable(false);
        jButtonNewInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewInvoice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonNewInvoice);

        jButtonEditInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423072138_bookmarks-edit.png"))); // NOI18N
        jButtonEditInvoice.setToolTipText("Edit Invoice");
        jButtonEditInvoice.setFocusable(false);
        jButtonEditInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEditInvoice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEditInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonEditInvoice);

        jButtonDeleteInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070622_Cancel File.png"))); // NOI18N
        jButtonDeleteInvoice.setToolTipText("Delete Invoice");
        jButtonDeleteInvoice.setFocusable(false);
        jButtonDeleteInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDeleteInvoice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDeleteInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDeleteInvoice);
        jToolBar1.add(jSeparator1);

        jButtonCatalogueMedicaments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423362498_reportorium.png"))); // NOI18N
        jButtonCatalogueMedicaments.setToolTipText("Medicaments Base");
        jButtonCatalogueMedicaments.setFocusable(false);
        jButtonCatalogueMedicaments.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCatalogueMedicaments.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCatalogueMedicaments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCatalogueMedicamentsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonCatalogueMedicaments);

        jButtonPriceListMedicaments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/invoice_budget-32.png"))); // NOI18N
        jButtonPriceListMedicaments.setToolTipText("Price Lists");
        jButtonPriceListMedicaments.setFocusable(false);
        jButtonPriceListMedicaments.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPriceListMedicaments.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPriceListMedicaments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPriceListMedicamentsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonPriceListMedicaments);

        jButtonStockMedicaments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/Medical_goods_medicine.png"))); // NOI18N
        jButtonStockMedicaments.setToolTipText("Stock");
        jButtonStockMedicaments.setFocusable(false);
        jButtonStockMedicaments.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStockMedicaments.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStockMedicaments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStockMedicamentsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonStockMedicaments);
        jToolBar1.add(jSeparator3);

        jButtonExportExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1423070960_Microsoft Office Excel32.png"))); // NOI18N
        jButtonExportExcel.setToolTipText("Export to Excel");
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
        jButtonExportPDF.setToolTipText("Export to PDF");
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

        jButtonExportReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1424199566_application-vnd.ms-excel.png"))); // NOI18N
        jButtonExportReport.setToolTipText("Report");
        jButtonExportReport.setFocusable(false);
        jButtonExportReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportReportActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonExportReport);

        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/printer.png"))); // NOI18N
        jButtonPrint.setToolTipText("Print document");
        jButtonPrint.setFocusable(false);
        jButtonPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonPrint);
        jToolBar1.add(jSeparator2);

        jButtonChangeUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/Login.png"))); // NOI18N
        jButtonChangeUser.setToolTipText("Change user");
        jButtonChangeUser.setFocusable(false);
        jButtonChangeUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonChangeUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonChangeUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeUserActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonChangeUser);

        jButtonSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1421167898_Tools.png"))); // NOI18N
        jButtonSettings.setToolTipText("Settings");
        jButtonSettings.setFocusable(false);
        jButtonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSettingsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSettings);

        jButtonAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/iucosoft/farmacy/gui/icons/1421167948_Information.png"))); // NOI18N
        jButtonAbout.setFocusable(false);
        jButtonAbout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAbout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAboutActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonAbout);
        jToolBar1.add(jSeparator4);

        jLabelFarmacyBalance.setText("Farmacy Balance:  ");
        jToolBar1.add(jLabelFarmacyBalance);

        jTextFieldFarmacyBalance.setEditable(false);
        jTextFieldFarmacyBalance.setBackground(new java.awt.Color(204, 204, 255));
        jToolBar1.add(jTextFieldFarmacyBalance);

        jSplitPane1.setDividerLocation(250);

        jTabbedFinSubjects.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedFinSubjectsStateChanged(evt);
            }
        });

        jListClients.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListClients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListClientsMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListClientsMouseReleased(evt);
            }
        });
        jListClients.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jListClientsKeyReleased(evt);
            }
        });
        jListClients.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListClientsValueChanged(evt);
            }
        });
        jScrollPaneClients.setViewportView(jListClients);

        jTabbedFinSubjects.addTab("Clients", jScrollPaneClients);

        jListSuppliers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListSuppliers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListSuppliersMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListSuppliersMouseReleased(evt);
            }
        });
        jListSuppliers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListSuppliersValueChanged(evt);
            }
        });
        jListSuppliers.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jListSuppliersKeyReleased(evt);
            }
        });
        jScrollPaneSuppliers.setViewportView(jListSuppliers);

        jTabbedFinSubjects.addTab("Suppliers", jScrollPaneSuppliers);

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
        jTableStock.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTableStock.getTableHeader().setReorderingAllowed(false);
        jTableStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableStockMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableStockMouseReleased(evt);
            }
        });
        jScrollPaneStock.setViewportView(jTableStock);

        jTabbedFinSubjects.addTab("Stock", jScrollPaneStock);

        jSplitPane1.setLeftComponent(jTabbedFinSubjects);

        jScrollPaneTableFarmacy.setBorder(javax.swing.BorderFactory.createTitledBorder("Clients"));
        jScrollPaneTableFarmacy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jScrollPaneTableFarmacyMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jScrollPaneTableFarmacyMouseReleased(evt);
            }
        });

        jTableFarmacy.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Subject", "Date", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableFarmacy.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTableFarmacy.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableFarmacy.getTableHeader().setReorderingAllowed(false);
        jTableFarmacy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableFarmacyMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableFarmacyMouseReleased(evt);
            }
        });
        jTableFarmacy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTableFarmacyKeyReleased(evt);
            }
        });
        jScrollPaneTableFarmacy.setViewportView(jTableFarmacy);
        if (jTableFarmacy.getColumnModel().getColumnCount() > 0) {
            jTableFarmacy.getColumnModel().getColumn(0).setResizable(false);
            jTableFarmacy.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTableFarmacy.getColumnModel().getColumn(1).setResizable(false);
            jTableFarmacy.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTableFarmacy.getColumnModel().getColumn(2).setResizable(false);
            jTableFarmacy.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTableFarmacy.getColumnModel().getColumn(3).setResizable(false);
            jTableFarmacy.getColumnModel().getColumn(3).setPreferredWidth(80);
        }
        jTableFarmacy.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if(lastIndexJTabbedPane<2){
                    selectedRowjTableInvoices = jTableFarmacy.getSelectedRow();
                    if (selectedRowjTableInvoices >= 0) {
                        int id = (int) jTableFarmacy.getValueAt(selectedRowjTableInvoices, 0);
                        selectedInvoice = invoiceDao.findInvoiceById(id);
                    } else {
                        selectedInvoice = null;
                    }
                }else{
                    selectedRowjTableInvoices=jTableFarmacy.getSelectedRow();
                    if (selectedRowjTableInvoices >= 0) {
                        int id = (int) jTableFarmacy.getValueAt(selectedRowjTableInvoices, 1);
                        try{
                            if(jTableFarmacy.getValueAt(selectedRowjTableInvoices, 2)!=null){
                                invoiceDao=new InvoiceDaoImpl(InvoicePurchase.class);
                            }else{
                                invoiceDao=new InvoiceDaoImpl(InvoiceSale.class);
                            }
                            selectedInvoice = invoiceDao.findInvoiceById(id);
                        }catch(Exception ex){

                        }
                    } else {
                        selectedInvoice = null;
                    }
                }
            }
        });

        jSplitPane1.setRightComponent(jScrollPaneTableFarmacy);

        jPanelFarmacy.setBorder(javax.swing.BorderFactory.createTitledBorder("Search panel"));
        jPanelFarmacy.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Show stock where balance between");
        jPanelFarmacy.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 55, -1, -1));

        jLabel2.setText("and");
        jPanelFarmacy.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 55, -1, -1));

        jLabel3.setText("Total of invoices");
        jPanelFarmacy.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 60, -1, -1));

        jLabel4.setText("and");
        jPanelFarmacy.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 25, -1, -1));

        jLabel5.setText("Sum between");
        jPanelFarmacy.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 25, -1, -1));

        jFormattedTextFieldTotalOfInvoices.setEditable(false);
        jFormattedTextFieldTotalOfInvoices.setBackground(new java.awt.Color(204, 204, 255));
        jFormattedTextFieldTotalOfInvoices.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 MDL"))));
        jPanelFarmacy.add(jFormattedTextFieldTotalOfInvoices, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 50, 110, -1));

        jLabel6.setText("Date between");
        jPanelFarmacy.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 25, -1, -1));

        jLabel8.setText("and");
        jPanelFarmacy.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 25, -1, -1));

        jLabel9.setText("and");
        jPanelFarmacy.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 25, -1, -1));

        jFormattedTextFieldBalanceMin.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextFieldBalanceMin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldBalanceMinKeyReleased(evt);
            }
        });
        jPanelFarmacy.add(jFormattedTextFieldBalanceMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 50, -1));

        jFormattedTextFieldBalanceMax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldBalanceMaxKeyReleased(evt);
            }
        });
        jPanelFarmacy.add(jFormattedTextFieldBalanceMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, 50, -1));

        jFormattedTextFieldSum1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldSum1KeyReleased(evt);
            }
        });
        jPanelFarmacy.add(jFormattedTextFieldSum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 20, 50, -1));

        jFormattedTextFieldSum2.setText("1000000000");
        jFormattedTextFieldSum2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldSum2ActionPerformed(evt);
            }
        });
        jFormattedTextFieldSum2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldSum2KeyReleased(evt);
            }
        });
        jPanelFarmacy.add(jFormattedTextFieldSum2, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 20, 70, -1));

        javax.swing.GroupLayout jDesktopPaneLayout = new javax.swing.GroupLayout(jDesktopPane);
        jDesktopPane.setLayout(jDesktopPaneLayout);
        jDesktopPaneLayout.setHorizontalGroup(
            jDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPaneLayout.createSequentialGroup()
                .addGroup(jDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
                    .addComponent(jPanelFarmacy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDesktopPaneLayout.setVerticalGroup(
            jDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPaneLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelFarmacy, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDesktopPane.setLayer(jToolBar1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane.setLayer(jSplitPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane.setLayer(jPanelFarmacy, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jMenuSales.setText("Sales");

        jMenuClients.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuClients.setText("Clients");
        jMenuClients.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuClientsActionPerformed(evt);
            }
        });
        jMenuSales.add(jMenuClients);

        jMenuItemInvoicesS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemInvoicesS.setText("Invoices");
        jMenuItemInvoicesS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInvoicesSActionPerformed(evt);
            }
        });
        jMenuSales.add(jMenuItemInvoicesS);

        jMenuBar1.add(jMenuSales);

        jMenuPurchases.setText("Purchases");

        jMenuItemSuppliers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItemSuppliers.setText("Suppliers");
        jMenuItemSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSuppliersActionPerformed(evt);
            }
        });
        jMenuPurchases.add(jMenuItemSuppliers);

        jMenuItemInvoicesP.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemInvoicesP.setText("Invoices");
        jMenuItemInvoicesP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInvoicesPActionPerformed(evt);
            }
        });
        jMenuPurchases.add(jMenuItemInvoicesP);

        jMenuBar1.add(jMenuPurchases);

        jMenuMedDatabase.setText("Medicaments");

        jMenuItemCategories.setText("Categories of medicaments");
        jMenuItemCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCategoriesActionPerformed(evt);
            }
        });
        jMenuMedDatabase.add(jMenuItemCategories);

        jMenuItemMedicamentsCatalog.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItemMedicamentsCatalog.setText("Catalogue of medicaments");
        jMenuItemMedicamentsCatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMedicamentsCatalogActionPerformed(evt);
            }
        });
        jMenuMedDatabase.add(jMenuItemMedicamentsCatalog);

        jMenuItemPriceList.setText("Pricelist of medicaments");
        jMenuItemPriceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPriceListActionPerformed(evt);
            }
        });
        jMenuMedDatabase.add(jMenuItemPriceList);

        jMenuItemStock.setText("Stock of medicaments");
        jMenuItemStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStockActionPerformed(evt);
            }
        });
        jMenuMedDatabase.add(jMenuItemStock);

        jMenuBar1.add(jMenuMedDatabase);

        jMenu1.setText("Tools");

        jMenuItemExportExcel.setText("Export to Excel");
        jMenuItemExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportExcelActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExportExcel);

        jMenuItemExportPdf.setText("Export to Pdf");
        jMenuItemExportPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportPdfActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExportPdf);

        jMenuItem1.setText("Export to CSV");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItemPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemPrint.setText("Print document");
        jMenuItemPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPrintActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemPrint);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Management");
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        jMenuItemSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSettings.setText("Settings");
        jMenuItemSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSettingsActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemSettings);

        jMenuItemChangeUser.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemChangeUser.setText("Change user");
        jMenuItemChangeUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangeUserActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemChangeUser);

        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemAbout);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void modeRestrictionsApply() {
        if (!firstRun) {
            switch (mode.toString()) {
                case "Administrator":
                    jMenuPurchases.setEnabled(true);
                    jTabbedFinSubjects.setEnabledAt(1, true);
                    jScrollPaneTableFarmacy.setVisible(true);
                    jSplitPane1.setDividerLocation(250);
                    jMenuItemOrder.setEnabled(true);
                    jLabelFarmacyBalance.setVisible(true);
                    jTextFieldFarmacyBalance.setVisible(true);
                    break;
                case "Manager":
                    jMenuPurchases.setEnabled(false);
                    jTabbedFinSubjects.setEnabledAt(1, false);
                    jMenuItemOrder.setEnabled(false);
                    jLabelFarmacyBalance.setVisible(false);
                    jTextFieldFarmacyBalance.setVisible(false);
                    switch (jTabbedFinSubjects.getSelectedIndex()) {
                        case 0:
                            jScrollPaneTableFarmacy.setVisible(true);
                            jSplitPane1.setDividerLocation(250);
                            break;
                        case 1:
                            jTabbedFinSubjects.setSelectedIndex(0);
                            break;
                        case 2:
                            jScrollPaneTableFarmacy.setVisible(false);
                    }
            }
        }
        firstRun = false;

    }


    private void jMenuItemMedicamentsCatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMedicamentsCatalogActionPerformed
        try {
            jFrameMedicament = new JFrameMedicament(0, mode);
            jFrameMedicament.setVisible(true);
            if (lastIndexJTabbedPane == 2) {
                refreshThread(jFrameMedicament);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jMenuItemMedicamentsCatalogActionPerformed

    private void jMenuItemCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCategoriesActionPerformed
        try {
            jFrameCategory = new JFrameCategory(mode);
            jFrameCategory.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(JFrameFarmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItemCategoriesActionPerformed

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed

    }//GEN-LAST:event_jMenu2ActionPerformed

    private void showFormFinSubjects(Class classFinSubj) {
        try {
            if (jFrameFinSubjects != null) {
                jFrameFinSubjects = null;
            }
            jFrameFinSubjects = new JFrameFinSubjects(classFinSubj);
            jFrameFinSubjects.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(JFrameFarmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jMenuClientsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuClientsActionPerformed
        showFormFinSubjects(Client.class);
        boolean needToRefreshTableInvoices = true;
        if (lastIndexJTabbedPane != 0) {
            needToRefreshTableInvoices = false;
        }
        int row = jTableFarmacy.getSelectedRow();
        refreshThread(jFrameFinSubjects, clientListModel, jListClients, row, jTableFarmacy.getRowCount(), selectedInvoice.getIdInvoice(), needToRefreshTableInvoices, true);
    }//GEN-LAST:event_jMenuClientsActionPerformed

    private void refreshThread(final Component component,
            final ListModel model,
            final JList list,
            final int row,
            final int rows,
            final int idInvoice,
            final boolean needToResfreshTable,
            final boolean refreshAllTable) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (component.isVisible()) {
                    try {
                        Thread.sleep(300);
                        LOG.log(Level.INFO, "waiting to close " + component.getClass().getSimpleName());
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
                synchronized (list) {
                    blockedRefreshing = true;
                    ((FinSubjListModel) model).refreshModel();
                    blockedRefreshing = false;
                    list.setSelectedValue(selectedFinSubj, true);
                    if (needToResfreshTable) {
                        // JOptionPane.showMessageDialog(null, "row is " + row + " " + selectedInvoice);
                        selectedInvoice = invoiceDao.findInvoiceById(idInvoice);

                        if (rows == invoicesList.size()) {
                            if (refreshAllTable) {
                                refreshTableInvoices(row, invoicesList);
                            } else {
                                refreshTableInvoices(row, selectedInvoice);
                            }
                        } else if (rows > invoicesList.size()) {
                            refreshTableInvoices(row - 1, invoicesList);
                        } else {
                            Util.selectRowInTable(jTableFarmacy, row);
                            //refreshTableInvoices(selectedInvoice);
                        }
                        fillJPanelFarmacy();
                    }
                }
            }
        }).start();
    }

    private void jTableStockRefresh(boolean isDeleted) {
        int rows = jTableFarmacy.getRowCount();//10
        int rowjTableInvoices = selectedRowjTableInvoices;//3
        int row = jTableStock.getSelectedRow();//2
        fillJPanelFarmacy();
        refreshJTableStock(row);
        if (jTableFarmacy.getRowCount() >= rows && !isDeleted) {//10>=10
            row = rowjTableInvoices;//3
        } else {
            row = 0;
        }
        Util.selectRowInTable(jTableFarmacy, row);//3
    }

    private void refreshThread(final Component component) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (component.isVisible()) {
                    try {
                        Thread.sleep(300);
                        LOG.log(Level.INFO, "waiting to close " + component.getClass().getSimpleName());
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
                synchronized (jTableStock) {
                    jTableStockRefresh(false);
                }
            }
        }).start();
    }


    private void jMenuItemSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuppliersActionPerformed
        showFormFinSubjects(Supplier.class);
        boolean needToRefreshTableInvoices = true;
        if (lastIndexJTabbedPane != 1) {
            needToRefreshTableInvoices = false;
        }
        refreshThread(jFrameFinSubjects, supplierListModel, jListSuppliers, jTableFarmacy.getRowCount(), jTableFarmacy.getRowCount(), selectedInvoice.getIdInvoice(), needToRefreshTableInvoices, true);
    }//GEN-LAST:event_jMenuItemSuppliersActionPerformed

    private void jMenuItemPriceListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPriceListActionPerformed
        try {
            jFrameMedicament = new JFrameMedicament(1, mode);
            jFrameMedicament.setVisible(true);
            if (lastIndexJTabbedPane == 2) {
                refreshThread(jFrameMedicament);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItemPriceListActionPerformed

    private void jMenuItemStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStockActionPerformed
        try {
            jFrameMedicament = new JFrameMedicament(2, mode);
            jFrameMedicament.setVisible(true);
            if (lastIndexJTabbedPane == 2) {
                refreshThread(jFrameMedicament);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItemStockActionPerformed

    private void jMenuItemInvoicesPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInvoicesPActionPerformed
        try {
            jInternalFrameInvoices = new JInternalFrameInvoices(InvoicePurchase.class, isJDatePicker);
            jDesktopPane.add(jInternalFrameInvoices);
            jInternalFrameInvoices.setSelected(true);
            boolean needToRefreshTableInvoices = true;
            if (lastIndexJTabbedPane != 1) {
                needToRefreshTableInvoices = false;
            }
            refreshThread(jInternalFrameInvoices,
                    supplierListModel, jListClients, 0, jTableFarmacy.getRowCount(), selectedInvoice.getIdInvoice(), needToRefreshTableInvoices, true);

        } catch (Exception ex) {
            Logger.getLogger(JFrameFarmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItemInvoicesPActionPerformed

    private void jMenuItemInvoicesSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInvoicesSActionPerformed
        try {
            jInternalFrameInvoices = new JInternalFrameInvoices(InvoiceSale.class, isJDatePicker);
            jDesktopPane.add(jInternalFrameInvoices);
            jInternalFrameInvoices.setSelected(true);
            boolean needToRefreshTableInvoices = true;
            if (lastIndexJTabbedPane != 0) {
                needToRefreshTableInvoices = false;
            }
            refreshThread(jInternalFrameInvoices,
                    clientListModel, jListClients, 0, jTableFarmacy.getRowCount(), selectedInvoice.getIdInvoice(), needToRefreshTableInvoices, true);

        } catch (Exception ex) {
            Logger.getLogger(JFrameFarmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItemInvoicesSActionPerformed

    void refreshInvoiceTableByFinSubjectDateSumma(Integer idFinSubj) {
        if (jFormattedTextFieldSum1.getText().isEmpty()) {
            return;
        }
        if (jFormattedTextFieldSum2.getText().isEmpty()) {
            return;
        }
        if (isJDatePicker) {
            invoicesList = invoiceDao.findInvoicesByIdFinSubjDateSumma(idFinSubj,
                    (Date) jDatePickerMin.getModel().getValue(),
                    (Date) jDatePickerMax.getModel().getValue(),
                    Double.parseDouble(jFormattedTextFieldSum1.getText()),
                    Double.parseDouble(jFormattedTextFieldSum2.getText()));
        } else {
            invoicesList = invoiceDao.findInvoicesByIdFinSubjDateSumma(idFinSubj,
                    Util.parseToDateSql((java.util.Date) jSpinnerDateMin.getModel().getValue()),
                    Util.parseToDateSql((java.util.Date) jSpinnerDateMax.getModel().getValue()),
                    Double.parseDouble(jFormattedTextFieldSum1.getText()),
                    Double.parseDouble(jFormattedTextFieldSum2.getText()));

        }
        refreshTableInvoices(0, invoicesList);
    }

    private void editInvoice(int row) {
        int idInvoice = selectedInvoice.getIdInvoice();
        try {
            jDialogInvoiceDetails = new JDialogInvoiceDetails(this, true, selectedInvoice, isJDatePicker);
            jDialogInvoiceDetails.setVisible(true);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        switch (lastIndexJTabbedPane) {
            case 0:
                refreshThread(jDialogInvoiceDetails,
                        clientListModel, jListClients, row, jTableFarmacy.getRowCount(), idInvoice, true, false);
                break;
            case 1:
                refreshThread(jDialogInvoiceDetails,
                        supplierListModel, jListSuppliers, row, jTableFarmacy.getRowCount(), idInvoice, true, false);
                break;
            case 2:
                refreshThread(jDialogInvoiceDetails);
                break;

        }

    }

    private void refreshTableInvoices() {
        aInvoiceTableModel.refreshModel();
        Util.selectRowInTable(jTableFarmacy, 0);
    }

    private void refreshTableInvoices(int row, List<Invoice> invoicesList) {
        aInvoiceTableModel.refreshModel(invoicesList);
        Util.selectRowInTable(jTableFarmacy, row);
    }

    private void refreshTableInvoices(int row, Invoice invoice) {
        if (invoice == null) {
            return;
        }
        aInvoiceTableModel.refreshModel(row, invoice);
        if (row < 0) {
            Util.selectRowInTable(jTableFarmacy, Math.abs(row) - 1);
        } else {
            Util.selectRowInTable(jTableFarmacy, row);
        }
    }

    private void refreshTableInvoices(Invoice invoice) {
        aInvoiceTableModel.refreshModel(invoice);
        Util.selectRowInTable(jTableFarmacy, jTableFarmacy.getRowCount() - 1);
    }

    private void editInvoiceAndRefreshInformation() {
        int row = selectedRowjTableInvoices;
//        int idInvoice = selectedInvoice.getIdInvoice();
        //JOptionPane.showMessageDialog(this, "row=" + row + " idInvoice=" + idInvoice + " finSubj=" + selectedFinSubj);
        editInvoice(row);
    }


    private void jMenuItemEditInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditInvoiceActionPerformed
        if (selectedInvoice != null) {
            editInvoiceAndRefreshInformation();
        } else {
            JOptionPane.showMessageDialog(this, "please select a invoice to edit");
        }
    }//GEN-LAST:event_jMenuItemEditInvoiceActionPerformed

    private int addEmptyInvoice(FinanceSubject finSubj) {
        int rez = 0;
        try {
            selectedInvoice = (Invoice) invoiceClass.newInstance();
            selectedInvoice.setDateInvoice(new Date(new java.util.Date().getTime()));
            if (finSubj != null) {
                selectedInvoice.setIdFinObj(finSubj.getId());
            }
            rez = invoiceDao.addInvoice(selectedInvoice);
        } catch (InstantiationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return rez;
    }

    private void jMenuItemNewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewInvoiceActionPerformed
        int idInvoice = addEmptyInvoice(selectedFinSubj);
        editInvoice(jTableFarmacy.getRowCount());
//        selectedInvoice = invoiceDao.findInvoiceById(selectedInvoice.getIdInvoice());
//        if (selectedInvoice != null) {
//            refreshTableInvoices(selectedInvoice);
//            fillJPanelFarmacy();
//        }
    }//GEN-LAST:event_jMenuItemNewInvoiceActionPerformed


    private void jMenuItemDeleteInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteInvoiceActionPerformed
        if (selectedInvoice != null) {
            int rez = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete invoice:"
                    + jTableFarmacy.getValueAt(selectedRowjTableInvoices, 1)
                    + ", date:" + selectedInvoice.getDateInvoice()
                    + ", sum=" + selectedInvoice.getTotalInvoice() + " MDL?", "Delete???", JOptionPane.YES_NO_OPTION);
            if (rez == JOptionPane.YES_OPTION) {
                try {
                    invoiceDao.deleteInvoice(selectedInvoice);
                    if (lastIndexJTabbedPane < 2) {
                        refreshTableInvoices(-selectedRowjTableInvoices - 1, selectedInvoice);
                        fillJPanelFarmacy();

                    } else {
                        jTableStockRefresh(true);
                    }
                } catch (ConnectionInterruptedException | StockException | NoSuchMoneyException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "please select a invoice to delete");
        }
    }//GEN-LAST:event_jMenuItemDeleteInvoiceActionPerformed

    private void jScrollPaneTableFarmacyMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPaneTableFarmacyMouseReleased
        if (evt.isPopupTrigger()) {
            if (lastIndexJTabbedPane < 2) {
                jMenuItemNewInvoice.setEnabled(true);
                jMenuItemDeleteInvoice.setEnabled(true);

            } else {
                jMenuItemNewInvoice.setEnabled(false);
                jMenuItemDeleteInvoice.setEnabled(false);
            }
            jPopupMenuInvoices.show(jScrollPaneTableFarmacy, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jScrollPaneTableFarmacyMouseReleased

    private void jScrollPaneTableFarmacyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPaneTableFarmacyMousePressed
        if (evt.isPopupTrigger()) {
            if (lastIndexJTabbedPane < 2) {
                jMenuItemNewInvoice.setEnabled(true);
                //jMenuItemDeleteInvoice.setEnabled(true);

            } else {
                jMenuItemNewInvoice.setEnabled(false);
                //jMenuItemDeleteInvoice.setEnabled(false);
            }
            jPopupMenuInvoices.show(jScrollPaneTableFarmacy, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jScrollPaneTableFarmacyMousePressed

    private void jTableFarmacyMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFarmacyMouseReleased
        if (evt.getButton() == BUTTON3 && lastIndexJTabbedPane < 2) {
            int row = jTableFarmacy.rowAtPoint(evt.getPoint());
            jTableFarmacy.setRowSelectionInterval(row, row);
        }
        if (evt.isPopupTrigger()) {
            if (lastIndexJTabbedPane < 2) {
                jMenuItemNewInvoice.setEnabled(true);
                //jMenuItemDeleteInvoice.setEnabled(true);

            } else {
                jMenuItemNewInvoice.setEnabled(false);
                //jMenuItemDeleteInvoice.setEnabled(false);
            }
            jPopupMenuInvoices.show(jTableFarmacy, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableFarmacyMouseReleased

    private void jTableFarmacyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFarmacyMousePressed
        if (evt.getButton() == BUTTON1 && evt.getClickCount() == 2) {
            editInvoiceAndRefreshInformation();
        }
        if (evt.isPopupTrigger()) {
            if (lastIndexJTabbedPane < 2) {
                jMenuItemNewInvoice.setEnabled(true);
                jMenuItemDeleteInvoice.setEnabled(true);

            } else {
                jMenuItemNewInvoice.setEnabled(false);
                jMenuItemDeleteInvoice.setEnabled(false);

            }
            jPopupMenuInvoices.show(jTableFarmacy, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableFarmacyMousePressed

//    private void setColumnWidthTableInvoices() {
//        jTableInvoices.getColumnModel().getColumn(0).setResizable(false);
//        jTableInvoices.getColumnModel().getColumn(0).setPreferredWidth(20);
//        jTableInvoices.getColumnModel().getColumn(1).setResizable(false);
//        jTableInvoices.getColumnModel().getColumn(1).setPreferredWidth(150);
//        jTableInvoices.getColumnModel().getColumn(2).setResizable(false);
//        jTableInvoices.getColumnModel().getColumn(2).setPreferredWidth(80);
//        jTableInvoices.getColumnModel().getColumn(3).setResizable(false);
//        jTableInvoices.getColumnModel().getColumn(3).setPreferredWidth(80);
//    }
//
    private void jTabbedFinSubjectsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedFinSubjectsStateChanged
        switch (jTabbedFinSubjects.getSelectedIndex()) {
            case 0:
                invoiceClass = InvoiceSale.class;
                jListClients.setSelectedIndex(0);
                lastIndexJTabbedPane = 0;
                break;
            case 1:
                invoiceClass = InvoicePurchase.class;
                jListSuppliers.setSelectedIndex(0);
                lastIndexJTabbedPane = 1;
                break;
            case 2:
                refreshJTableStock(0);
                if (jTableStock.getRowCount() > 0) {
                    jTableStock.setRowSelectionInterval(0, 0);
                }
                lastIndexJTabbedPane = 2;
                jTableFarmacy.setModel(aActStockTableModel);
                //aActStockTableModel.refreshModel(selectedIdMedicament);
                break;
        }
        if (jTabbedFinSubjects.getSelectedIndex() < 2) {
            jScrollPaneTableFarmacy.setBorder(BorderFactory.createTitledBorder(jTabbedFinSubjects.getTitleAt(jTabbedFinSubjects.getSelectedIndex())));
//        }
            try {
                aInvoiceTableModel = new InvoiceTableModel(invoiceClass);
                invoiceDao = new InvoiceDaoImpl(invoiceClass);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            jTableFarmacy.setModel(aInvoiceTableModel);
            Util.setColumnWidthTable(jTableFarmacy, 20, 150, 80, 80);
            selectedIndexFinSubject = null;
            fillJPanelFarmacy();
            refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
            jFormattedTextFieldSum1.setEnabled(true);
            jFormattedTextFieldSum2.setEnabled(true);
            for (int i = 0; i < jDatePickerMin.getComponentCount(); i++) {
                jDatePickerMin.getComponent(i).setEnabled(true);
            }
            for (int i = 0; i < jDatePickerMax.getComponentCount(); i++) {
                jDatePickerMax.getComponent(i).setEnabled(true);
            }
            jFormattedTextFieldTotalOfInvoices.setEnabled(true);
            jFormattedTextFieldBalanceMin.setEnabled(false);
            jFormattedTextFieldBalanceMax.setEnabled(false);
            jButtonNewInvoice.setEnabled(true);
            jButtonDeleteInvoice.setEnabled(true);
        } else {
            jFormattedTextFieldSum1.setEnabled(false);
            jFormattedTextFieldSum2.setEnabled(false);
            jDatePickerMin.getJFormattedTextField().setEnabled(false);
            for (int i = 0; i < jDatePickerMin.getComponentCount(); i++) {
                jDatePickerMin.getComponent(i).setEnabled(false);
            }
            for (int i = 0; i < jDatePickerMax.getComponentCount(); i++) {
                jDatePickerMax.getComponent(i).setEnabled(false);
            }
            jFormattedTextFieldTotalOfInvoices.setEnabled(false);
            jFormattedTextFieldBalanceMin.setEnabled(true);
            jFormattedTextFieldBalanceMax.setEnabled(true);
            jButtonNewInvoice.setEnabled(false);
            jButtonDeleteInvoice.setEnabled(false);
            //jScrollPaneTableInvoices.setBorder(BorderFactory.createTitledBorder("sale and purchase history for " + selectedMedicament.getNameMedicament()));
            Util.setColumnWidthTable(jTableFarmacy, 60, 30, 100, 40, 100, 40, 40);
        }
        modeRestrictionsApply();
    }//GEN-LAST:event_jTabbedFinSubjectsStateChanged

    private void jListFinSubjValueChanged(JList jList) {
        if (blockedRefreshing) {
            return;
        }
        switch (jList.getSelectedIndex()) {
            case -1:
                //jListSuppliers.setSelectedIndex(0);
                break;
            case 0:
                selectedFinSubj = null;
                selectedIndexFinSubject = null;
                break;
            default:
                selectedFinSubj = (FinanceSubject) jList.getSelectedValue();
                selectedIndexFinSubject = selectedFinSubj.getId();
        }
        fillJPanelFarmacy();
        refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
    }

    private void jListSuppliersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListSuppliersValueChanged
        jListFinSubjValueChanged(jListSuppliers);
    }//GEN-LAST:event_jListSuppliersValueChanged

    private void jListSuppliersMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListSuppliersMouseReleased
        if (evt.isPopupTrigger()) {
            jPopupMenuFinSubjects.show(jListSuppliers, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jListSuppliersMouseReleased

    private void jListSuppliersMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListSuppliersMousePressed
        if (evt.getButton() == BUTTON1 && evt.getClickCount() == 2) {
            jMenuItemEditFinSubjectActionPerformed(null);
        }
        if (evt.isPopupTrigger()) {
            jPopupMenuFinSubjects.show(jListSuppliers, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jListSuppliersMousePressed

    private void jListClientsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListClientsValueChanged
        jListFinSubjValueChanged(jListClients);
    }//GEN-LAST:event_jListClientsValueChanged

    private void jListClientsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListClientsMouseReleased
        if (evt.isPopupTrigger()) {
            jPopupMenuFinSubjects.show(jListClients, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jListClientsMouseReleased

    private void jListClientsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListClientsMousePressed
        if (evt.getButton() == BUTTON1 && evt.getClickCount() == 2) {
            jMenuItemEditFinSubjectActionPerformed(null);
        }
        if (evt.isPopupTrigger()) {
            jPopupMenuFinSubjects.show(jListClients, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jListClientsMousePressed

    private void jButtonExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportPDFActionPerformed
        try {
            Object views[] = createExportView();
            ExportReports.exportTableModelDialog(
                    "pdf", (JTable) views[0], (String) views[1], (String) views[2]);
        } catch (NothingToExportException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonExportPDFActionPerformed

    private void jButtonExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportExcelActionPerformed
        try {
            Object views[] = createExportView();
            ExportReports.exportTableModelDialog(
                    "xls", (JTable) views[0], (String) views[1], (String) views[2]);
        } catch (NothingToExportException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonExportExcelActionPerformed

    private void jButtonDeleteInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteInvoiceActionPerformed
        jMenuItemDeleteInvoiceActionPerformed(evt);
    }//GEN-LAST:event_jButtonDeleteInvoiceActionPerformed

    private void jButtonEditInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditInvoiceActionPerformed
        jMenuItemEditInvoiceActionPerformed(evt);
    }//GEN-LAST:event_jButtonEditInvoiceActionPerformed

    private void jButtonNewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewInvoiceActionPerformed
        jMenuItemNewInvoiceActionPerformed(evt);
    }//GEN-LAST:event_jButtonNewInvoiceActionPerformed

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
                Logger.getLogger(JFrameFinSubjects.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JFrameFinSubjects.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            type = "edit";
        }
        try {
            jDialogClientsSaveUpdate = new JDialogFinSubjSaveUpdate(this, true, finSubjectClass, type, finSubj);
        } catch (Exception ex) {
            Logger.getLogger(JFrameFinSubjects.class.getName()).log(Level.SEVERE, null, ex);
        }
        jDialogClientsSaveUpdate.setVisible(true);
    }


    private void jMenuItemAddFinSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddFinSubjectActionPerformed
        switch (jTabbedFinSubjects.getSelectedIndex()) {
            case 0:
                selectedFinSubj = null;
                jDialogFinSubjectsCreateAndShow(jDialogFinSubjSaveUpdate, Client.class, null);
                clientListModel.refreshModel();
                jListClients.setSelectedIndex(jListClients.getModel().getSize() - 1);
                break;
            case 1:
                selectedFinSubj = null;
                jDialogFinSubjectsCreateAndShow(jDialogFinSubjSaveUpdate, Supplier.class, null);
                supplierListModel.refreshModel();
                jListSuppliers.setSelectedIndex(jListSuppliers.getModel().getSize() - 1);
                break;
            case 2:
                break;
        }
    }//GEN-LAST:event_jMenuItemAddFinSubjectActionPerformed

    private void jMenuItemEditFinSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditFinSubjectActionPerformed
        if (selectedIndexFinSubject != null) {
            int row = jTableFarmacy.getSelectedRow();
            switch (jTabbedFinSubjects.getSelectedIndex()) {
                case 0:
                    jDialogFinSubjectsCreateAndShow(jDialogFinSubjSaveUpdate, Client.class, selectedFinSubj);
                    clientListModel.refreshModel(selectedFinSubj, jListClients.getSelectedIndex());
                    //jListClients.setSelectedIndex(selectedIndexFinSubject);
                    break;
                case 1:
                    jDialogFinSubjectsCreateAndShow(jDialogFinSubjSaveUpdate, Supplier.class, selectedFinSubj);
                    supplierListModel.refreshModel(selectedFinSubj, jListSuppliers.getSelectedIndex());
                    //jListSuppliers.setSelectedIndex(selectedIndexFinSubject);
                    break;
                case 2:
                    break;
            }
            refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
            Util.selectRowInTable(jTableFarmacy, row);
        } else {
            String title = jTabbedFinSubjects.getTitleAt(lastIndexJTabbedPane);
            JOptionPane.showMessageDialog(this, "Please select existing " + title.substring(0, title.length() - 1) + " to edit");
        }
    }//GEN-LAST:event_jMenuItemEditFinSubjectActionPerformed

    private void jMenuItemDeleteFinSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteFinSubjectActionPerformed
        if (selectedIndexFinSubject != null) {
            int rez = JOptionPane.showConfirmDialog(this, "Are you shure to remove " + selectedFinSubj, "DELETE???", JOptionPane.YES_NO_OPTION);
            if (rez == JOptionPane.YES_OPTION) {
                try {
                    int index;
                    switch (jTabbedFinSubjects.getSelectedIndex()) {
                        case 0:
                            clientDao.deleteFinSubject(selectedFinSubj);
                            index = jListClients.getSelectedIndex();
                            clientListModel.refreshModel(selectedFinSubj, -index);
                            jListClients.setSelectedIndex(index - 1);
                            break;
                        case 1:
                            supplierDao.deleteFinSubject(selectedFinSubj);
                            index = jListSuppliers.getSelectedIndex();
                            supplierListModel.refreshModel(selectedFinSubj, -index);
                            jListSuppliers.setSelectedIndex(index - 1);
                            break;
                        case 2:
                            break;
                    }
                } catch (DeletingException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            String title = jTabbedFinSubjects.getTitleAt(lastIndexJTabbedPane);
            JOptionPane.showMessageDialog(this, "Please select existing " + title.substring(0, title.length() - 1) + " to delete");
        }
    }//GEN-LAST:event_jMenuItemDeleteFinSubjectActionPerformed

    private void jListClientsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jListClientsKeyReleased
        if (evt.getKeyCode() == 127) {
            jMenuItemDeleteFinSubjectActionPerformed(null);
        }
        if (evt.getKeyCode() == 155) {
            jMenuItemAddFinSubjectActionPerformed(null);
        }
    }//GEN-LAST:event_jListClientsKeyReleased

    private void jListSuppliersKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jListSuppliersKeyReleased
        if (evt.getKeyCode() == 155) {
            jMenuItemAddFinSubjectActionPerformed(null);
        }
        if (evt.getKeyCode() == 127) {
            jMenuItemDeleteFinSubjectActionPerformed(null);
        }
    }//GEN-LAST:event_jListSuppliersKeyReleased

    private void jTableFarmacyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableFarmacyKeyReleased
        if (lastIndexJTabbedPane < 2) {
            if (evt.getKeyCode() == 155) {
                jMenuItemNewInvoiceActionPerformed(null);
            }
            if (evt.getKeyCode() == 127) {
                jMenuItemDeleteInvoiceActionPerformed(null);
            }
        }
    }//GEN-LAST:event_jTableFarmacyKeyReleased

    private void createInvoiceByStock(Class invClass, Class invDetailclas, FinanceSubject finSubject) {
        try {
            invoiceDao = new InvoiceDaoImpl(invClass);
            int idInvoice = addEmptyInvoice(selectedFinSubj);
            int[] rows = jTableStock.getSelectedRows();
            InvoiceDetailDaoIntf invoiceDetailDao;
            InvoiceDetail invDetail;
            PriceDaoIntf priceDao;
            invoiceDetailDao = new InvoiceDetailDaoImpl(invDetailclas);
            priceDao = new PriceDaoImpl();
            int idMedicament;
            double price;
            double total = 0.0;
            for (int row : rows) {
                invDetail = (InvoiceDetail) invDetailclas.newInstance();
                invDetail.setIdInvoice(idInvoice);
                idMedicament = ((Integer) jTableStock.getValueAt(row, 0));
                invDetail.setIdMedicament(idMedicament);
                invDetail.setQuantity(1.0);
                price = Util.getPriceofMedicament(priceDao.findPriceById(idMedicament), selectedInvoice);
                invDetail.setPrice(price);
                invDetail.setTotal(price);
                invoiceDetailDao.addInvoiceDetail(invDetail);
                total += price;
            }
            selectedInvoice.setTotalInvoice(total);
            invoiceDao.updateInvoice(selectedInvoice);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        editInvoice(jTableFarmacy.getRowCount());
//        selectedInvoice = invoiceDao.findInvoiceById(selectedInvoice.getIdInvoice());
//        if (selectedInvoice != null) {
//            refreshTableInvoices(selectedInvoice);
//            fillJPanelFarmacy();
//        }
    }


    private void jMenuItemOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOrderActionPerformed
        FinanceSubject finSubj = null;
        switch (jListSuppliers.getSelectedIndex()) {
            case -1:
                break;
            case 0:
                break;
            default:
                finSubj = (FinanceSubject) jListSuppliers.getSelectedValue();
        }
        if (finSubj == null) {
            JOptionPane.showMessageDialog(this, "Cannot to order.Please select previously some Supplier", "WARNING", JOptionPane.WARNING_MESSAGE);
        } else {
            invoiceClass = InvoicePurchase.class;
            createInvoiceByStock(invoiceClass, InvoiceDetailPurchase.class, finSubj);
        }
    }//GEN-LAST:event_jMenuItemOrderActionPerformed

    private void jTableStockMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableStockMousePressed
        if (evt.isPopupTrigger()) {
            jPopupMenuStock.show(jTableStock, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableStockMousePressed

    private void jTableStockMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableStockMouseReleased
        jTableStockMousePressed(evt);
    }//GEN-LAST:event_jTableStockMouseReleased

    private void jMenuItemSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaleActionPerformed
        FinanceSubject finSubj = null;
        switch (jListClients.getSelectedIndex()) {
            case -1:
                break;
            case 0:
                break;
            default:
                finSubj = (FinanceSubject) jListClients.getSelectedValue();
        }
        if (finSubj == null) {
            JOptionPane.showMessageDialog(this, "Cannot to sale.Please select previously some Client", "WARNING", JOptionPane.WARNING_MESSAGE);
        } else {
            invoiceClass = InvoiceSale.class;
            createInvoiceByStock(invoiceClass, InvoiceDetailSale.class, finSubj);
        }
    }//GEN-LAST:event_jMenuItemSaleActionPerformed

    private void changePickerSpinner() {
        if (isJDatePicker) {
            jSpinnerDateMin.setVisible(false);
            jSpinnerDateMax.setVisible(false);
            jLabel8.setVisible(false);
            jDatePickerMin.setVisible(true);
            jDatePickerMax.setVisible(true);
            jLabel9.setVisible(true);
        } else {
            jDatePickerMin.setVisible(false);
            jDatePickerMax.setVisible(false);
            jLabel9.setVisible(false);
            jSpinnerDateMin.setVisible(true);
            jSpinnerDateMax.setVisible(true);
            jLabel8.setVisible(true);
        }
    }

    private void jButtonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSettingsActionPerformed
        try {
            new JDialogSettings(this, true, mode).setVisible(true);
            Properties props = Config.loadProperties();
            isJDatePicker = Boolean.valueOf(props.getProperty("jdatepicker"));
            changePickerSpinner();
        } catch (Exception ex) {
            Logger.getLogger(JFrameFarmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonSettingsActionPerformed

    private void jButtonAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAboutActionPerformed
        new JDialogAbout(this, true).setVisible(true);
    }//GEN-LAST:event_jButtonAboutActionPerformed

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
        } catch (NothingToExportException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPrintActionPerformed

    private void jButtonChangeUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeUserActionPerformed
        if (JOptionPane.showConfirmDialog(this,
                "This operation will close this session.Are you sure?",
                "LOGOUT",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new JDialogAuthentification(this, true, mode).setVisible(true);
            modeRestrictionsApply();
        }
    }//GEN-LAST:event_jButtonChangeUserActionPerformed

    private void jMenuItemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsActionPerformed
        jButtonSettingsActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemSettingsActionPerformed

    private void jMenuItemChangeUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangeUserActionPerformed
        jButtonChangeUserActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemChangeUserActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        jButtonAboutActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportExcelActionPerformed
        jButtonExportExcelActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemExportExcelActionPerformed

    private void jMenuItemExportPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportPdfActionPerformed
        jButtonExportPDFActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemExportPdfActionPerformed

    private void jMenuItemPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPrintActionPerformed
        jButtonPrintActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemPrintActionPerformed

    private void jButtonExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportCSVActionPerformed
        try {
            Object views[] = createExportView();
            ExportReports.exportTableModelDialog(
                    "csv", (JTable) views[0], null, null);
        } catch (NothingToExportException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonExportCSVActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        jButtonExportCSVActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButtonCatalogueMedicamentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCatalogueMedicamentsActionPerformed
        jMenuItemMedicamentsCatalogActionPerformed(evt);
    }//GEN-LAST:event_jButtonCatalogueMedicamentsActionPerformed

    private void jButtonPriceListMedicamentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPriceListMedicamentsActionPerformed
        jMenuItemPriceListActionPerformed(evt);
    }//GEN-LAST:event_jButtonPriceListMedicamentsActionPerformed

    private void jButtonStockMedicamentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStockMedicamentsActionPerformed
        jMenuItemStockActionPerformed(evt);
    }//GEN-LAST:event_jButtonStockMedicamentsActionPerformed

    private void jFormattedTextFieldBalanceMinKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldBalanceMinKeyReleased
        stockList = stockDao.findByBalanceStockList(
                Double.parseDouble(jFormattedTextFieldBalanceMin.getText()),
                Double.parseDouble(jFormattedTextFieldBalanceMax.getText()));
        aStockTableModel.refreshModel(stockList);
    }//GEN-LAST:event_jFormattedTextFieldBalanceMinKeyReleased

    private void jFormattedTextFieldBalanceMaxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldBalanceMaxKeyReleased
        jFormattedTextFieldBalanceMinKeyReleased(evt);
    }//GEN-LAST:event_jFormattedTextFieldBalanceMaxKeyReleased

    private void jFormattedTextFieldSum1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldSum1KeyReleased
        refreshInvoiceTableByFinSubjectDateSumma(selectedIndexFinSubject);
        jFormattedTextFieldTotalOfInvoices.setValue(getTotalOfInvoices());
    }//GEN-LAST:event_jFormattedTextFieldSum1KeyReleased

    private void jFormattedTextFieldSum2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldSum2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextFieldSum2ActionPerformed

    private void jFormattedTextFieldSum2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldSum2KeyReleased
        jFormattedTextFieldSum1KeyReleased(evt);
    }//GEN-LAST:event_jFormattedTextFieldSum2KeyReleased

    private Report getInvoicesReport() throws NothingToExportException {
        Report report = new Report();
        try {
            report.setFarmacyName(farmDao.getFarmacyName());
            report.setInnerData(jTableFarmacy.getColumnName(1));
            Date[] datePeriod = getPeriod();
            report.setHeader("Period: " + datePeriod[0].toString() + "-" + datePeriod[1].toString()
                    + "Summ between " + jFormattedTextFieldSum1.getText()
                    + " and " + jFormattedTextFieldSum2.getText());
            report.setFooter(jFormattedTextFieldTotalOfInvoices.getText());
            int rows = jTableFarmacy.getRowCount();
            List reportList = new ArrayList();
            for (int i = 0; i < rows; i++) {
                reportList.add(new InvoicesReport(
                        (Integer) jTableFarmacy.getValueAt(i, 0),
                        (String) jTableFarmacy.getValueAt(i, 1),
                        (Date) jTableFarmacy.getValueAt(i, 2),
                        (String) jTableFarmacy.getValueAt(i, 3)));
            }
            report.setData(reportList);
        } catch (NullPointerException npe) {
            LOG.log(Level.SEVERE, null, npe);
            throw new NothingToExportException("Nothing to export");
        }
        return report;
    }

    private Report getStockReport() throws NothingToExportException {
        Report report = new Report();
        try {
            report.setFarmacyName(farmDao.getFarmacyName());
            report.setInnerData("");
            report.setHeader("" + new java.util.Date());
            report.setFooter("Balance between " + jFormattedTextFieldBalanceMin.getText() + " and " + jFormattedTextFieldBalanceMax.getText());
            int rows = jTableStock.getRowCount();
            List reportList = new ArrayList();
            for (int i = 0; i < rows; i++) {
                reportList.add(new StockReport(
                        (Integer) jTableStock.getValueAt(i, 0),
                        (String) jTableStock.getValueAt(i, 1),
                        (Double) jTableStock.getValueAt(i, 2)));
            }
            report.setData(reportList);
        } catch (NullPointerException npe) {
            LOG.log(Level.SEVERE, null, npe);
            throw new NothingToExportException("Nothing to export");
        }
        return report;
    }

    private Report getActReport() throws NothingToExportException {
        Report report = new Report();
        try {
            report.setFarmacyName(farmDao.getFarmacyName());
            report.setInnerData("");
            report.setHeader(selectedMedicament.getNameMedicament());
            report.setFooter("");
            int rows = jTableFarmacy.getRowCount();
            List reportList = new ArrayList();
            for (int i = 0; i < rows; i++) {
                reportList.add(new ActReport(
                        (Date) jTableFarmacy.getValueAt(i, 0),
                        (Integer) jTableFarmacy.getValueAt(i, 1),
                        (String) jTableFarmacy.getValueAt(i, 2),
                        (Double) jTableFarmacy.getValueAt(i, 3),
                        (String) jTableFarmacy.getValueAt(i, 4),
                        (Double) jTableFarmacy.getValueAt(i, 5),
                        (Double) jTableFarmacy.getValueAt(i, 6)));
            }
            report.setData(reportList);
        } catch (NullPointerException npe) {
            LOG.log(Level.SEVERE, null, npe);
            throw new NothingToExportException("Nothing to export");
        }
        return report;
    }

    private void jButtonExportReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportReportActionPerformed
        Report report;
        String nameReport;
        String templateName;
        try {
            if (jTabbedFinSubjects.getSelectedIndex() < 2) {
                report = getInvoicesReport();
                nameReport = "InvoicesReport";
                templateName = "invoicesreport.xls";
            } else {
                if (this.getFocusOwner().equals(jTabbedFinSubjects) || this.getFocusOwner().equals(jTableStock) || this.getFocusOwner().equals(jScrollPaneStock)) {
                    report = getStockReport();
                    nameReport = "StockReport";
                    templateName = "stockreport.xls";
                } else {
                    report = getActReport();
                    nameReport = "ActReport";
                    templateName = "actreport.xls";
                }
            }
            ExportReports.exportJxlsDialog(report, nameReport, templateName);
        } catch (NothingToExportException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERORR", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonExportReportActionPerformed

    private double getTotalOfInvoices() {
        Date date1;
        Date date2;
        if (isJDatePicker) {
            date1 = (Date) jDatePickerMin.getModel().getValue();
            date2 = (Date) jDatePickerMax.getModel().getValue();
        } else {
            date1 = Util.parseToDateSql((java.util.Date) jSpinnerDateMin.getModel().getValue());
            date2 = Util.parseToDateSql((java.util.Date) jSpinnerDateMax.getModel().getValue());
        }
        return invoiceDao.getSumOfInvoices((selectedIndexFinSubject),
                date1,
                date2,
                Double.parseDouble(jFormattedTextFieldSum1.getText()),
                Double.parseDouble(jFormattedTextFieldSum2.getText()));

    }

    private void refreshJTableStock(int row) {
        stockList = stockDao.findByBalanceStockList(
                Double.parseDouble(jFormattedTextFieldBalanceMin.getText()),
                Double.parseDouble(jFormattedTextFieldBalanceMax.getText()));
        aStockTableModel.refreshModel(stockList);
        Util.selectRowInTable(jTableStock, row);
    }

    private void fillJPanelFarmacy() {
        jFormattedTextFieldSum1.setText("" + Util.truncDouble(invoiceDao.getMinSumInvoice(selectedIndexFinSubject)));
        jFormattedTextFieldSum2.setText("" + Util.roundUp(invoiceDao.getMaxSumInvoice(selectedIndexFinSubject)));
        if (isJDatePicker) {
            ((SqlDateModel) jDatePickerMin.getModel()).setValue(invoiceDao.getMinDate(selectedIndexFinSubject));
            ((SqlDateModel) jDatePickerMax.getModel()).setValue(invoiceDao.getMaxDate(selectedIndexFinSubject));
        } else {
            blockedRefreshing = true;
            Date date = invoiceDao.getMinDate(selectedIndexFinSubject);
            if (date != null) {
                jSpinnerDateMin.setValue(date);
            } else {
                jSpinnerDateMin.setValue(new Date(0L));
            }
            date = invoiceDao.getMaxDate(selectedIndexFinSubject);
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
            blockedRefreshing = false;
        }
        jFormattedTextFieldTotalOfInvoices.setValue(getTotalOfInvoices());
        jFormattedTextFieldBalanceMin.setText("" + stockDao.getMinBalance());
        jFormattedTextFieldBalanceMax.setText("" + stockDao.getMaxBalance());
        jTextFieldFarmacyBalance.setText("" + farmDao.getFarmacyBalance() + " MDL  ");
        jTextFieldFarmacyBalance.setSize(jTextFieldFarmacyBalance.getSize());
        jToolBar1.revalidate();
    }

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

    private Object[] createExportView() throws NothingToExportException {
        String header;
        String footer;
        JTable jTable;
        try {
            Date[] date = getPeriod();
            if (jTabbedFinSubjects.getSelectedIndex() < 2) {
                header = "Period of invoices from " + date[0].toString()
                        + " to " + date[1].toString();
                footer = "Total sum of invoices is " + Util.roundTo(getTotalOfInvoices()) + " MDL";
                jTable = jTableFarmacy;
                //         tModel = aInvoiceTableModel;
            } else {
                if (this.getFocusOwner().equals(jTabbedFinSubjects) || this.getFocusOwner().equals(jTableStock) || this.getFocusOwner().equals(jScrollPaneStock)) {
                    header = "Stock on " + new java.util.Date();
                    footer = "Balance between " + jFormattedTextFieldBalanceMin.getText() + " and " + jFormattedTextFieldBalanceMax.getText();
                    jTable = jTableStock;
//                tModel = aStockTableModel;
                } else {
                    header = "Sale and Purchase history for " + selectedMedicament.getNameMedicament();
                    footer = "";
                    jTable = jTableFarmacy;
                    //        tModel = aActStockTableModel;
                }
            }
        } catch (NullPointerException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new NothingToExportException("Nothing to export!!!");
        }

        return new Object[]{jTable, header, footer};
    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbout;
    private javax.swing.JButton jButtonCatalogueMedicaments;
    private javax.swing.JButton jButtonChangeUser;
    private javax.swing.JButton jButtonDeleteInvoice;
    private javax.swing.JButton jButtonEditInvoice;
    private javax.swing.JButton jButtonExportCSV;
    private javax.swing.JButton jButtonExportExcel;
    private javax.swing.JButton jButtonExportPDF;
    private javax.swing.JButton jButtonExportReport;
    private javax.swing.JButton jButtonNewInvoice;
    private javax.swing.JButton jButtonPriceListMedicaments;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonSettings;
    private javax.swing.JButton jButtonStockMedicaments;
    private javax.swing.JDesktopPane jDesktopPane;
    private javax.swing.JFormattedTextField jFormattedTextFieldBalanceMax;
    private javax.swing.JFormattedTextField jFormattedTextFieldBalanceMin;
    private javax.swing.JFormattedTextField jFormattedTextFieldSum1;
    private javax.swing.JFormattedTextField jFormattedTextFieldSum2;
    private javax.swing.JFormattedTextField jFormattedTextFieldTotalOfInvoices;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelFarmacyBalance;
    private javax.swing.JList jListClients;
    private javax.swing.JList jListSuppliers;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuClients;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemAddFinSubject;
    private javax.swing.JMenuItem jMenuItemCategories;
    private javax.swing.JMenuItem jMenuItemChangeUser;
    private javax.swing.JMenuItem jMenuItemDeleteFinSubject;
    private javax.swing.JMenuItem jMenuItemDeleteInvoice;
    private javax.swing.JMenuItem jMenuItemEditFinSubject;
    private javax.swing.JMenuItem jMenuItemEditInvoice;
    private javax.swing.JMenuItem jMenuItemExportExcel;
    private javax.swing.JMenuItem jMenuItemExportPdf;
    private javax.swing.JMenuItem jMenuItemInvoicesP;
    private javax.swing.JMenuItem jMenuItemInvoicesS;
    private javax.swing.JMenuItem jMenuItemMedicamentsCatalog;
    private javax.swing.JMenuItem jMenuItemNewInvoice;
    private javax.swing.JMenuItem jMenuItemOrder;
    private javax.swing.JMenuItem jMenuItemPriceList;
    private javax.swing.JMenuItem jMenuItemPrint;
    private javax.swing.JMenuItem jMenuItemSale;
    private javax.swing.JMenuItem jMenuItemSettings;
    private javax.swing.JMenuItem jMenuItemStock;
    private javax.swing.JMenuItem jMenuItemSuppliers;
    private javax.swing.JMenu jMenuMedDatabase;
    private javax.swing.JMenu jMenuPurchases;
    private javax.swing.JMenu jMenuSales;
    private javax.swing.JPanel jPanelFarmacy;
    private javax.swing.JPopupMenu jPopupMenuFinSubjects;
    private javax.swing.JPopupMenu jPopupMenuInvoices;
    private javax.swing.JPopupMenu jPopupMenuStock;
    private javax.swing.JScrollPane jScrollPaneClients;
    private javax.swing.JScrollPane jScrollPaneStock;
    private javax.swing.JScrollPane jScrollPaneSuppliers;
    private javax.swing.JScrollPane jScrollPaneTableFarmacy;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedFinSubjects;
    private javax.swing.JTable jTableFarmacy;
    private javax.swing.JTable jTableStock;
    private javax.swing.JTextField jTextFieldFarmacyBalance;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    JFrameCategory jFrameCategory;
    JFrameMedicament jFrameMedicament;
    DataSourceFarmacy dataSource;
//    JFrameConnection jFrameConnection;
    JFrameFinSubjects jFrameFinSubjects;
    JInternalFrameInvoices jInternalFrameInvoices;
    FinanceSubjectDaoIntf clientDao;
    FinSubjListModel clientListModel;
    FinanceSubjectDaoIntf supplierDao;
    FinSubjListModel supplierListModel;
    InvoiceTableModel aInvoiceTableModel;
    InvoiceDaoIntf invoiceDao;
    Invoice selectedInvoice;
    int selectedRowjTableInvoices;
    JDialogInvoiceDetails jDialogInvoiceDetails;
    List<Invoice> invoicesList;
    Class invoiceClass;
    FinanceSubject selectedFinSubj;
    JDatePickerImpl jDatePickerMin;
    JDatePickerImpl jDatePickerMax;
    Integer selectedIndexFinSubject = null;
    StockTableModel aStockTableModel;
    StockDaoIntf stockDao;
    List<Stock> stockList;
    JDialogFinSubjSaveUpdate jDialogFinSubjSaveUpdate;
    int lastIndexJTabbedPane;
    boolean blockedRefreshing = false;
    ActStockTableModel aActStockTableModel;
    Integer selectedIdMedicament;
    MedicamentDaoIntf medDao;
    Medicament selectedMedicament;
    boolean isJDatePicker;
    JSpinner jSpinnerDateMin;
    JSpinner jSpinnerDateMax;
    StringBuilder mode;
    boolean firstRun = true;
    FarmacyBalanceDaoIntf farmDao;
}
