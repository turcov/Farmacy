/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.iucosoft.farmacy.db.Config;
import com.iucosoft.farmacy.gui.JFrameFarmacy;
import com.iucosoft.farmacy.reports.Report;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author Turkov S
 */
public class ExportReports {

    private static boolean openfile;
    private static String program;

    public static File fileChooserEvent(String type, Properties props) {
        JFileChooser jFileChooser = new JFileChooser();
        JCheckBox jCheckBox = new JCheckBox("Open file in system");
        jCheckBox.setSelected(props.getProperty("openfile").equals("true"));
        String prog = "";
        switch (type) {
            case "xls":
                prog = props.getProperty("xlsprogram");
                break;
            case "pdf":
                prog = props.getProperty("pdfprogram");
                break;
            case "csv":
                prog = props.getProperty("csvprogram");
                break;
        }
        JTextField jTextFieldprogram = new JTextField(prog);
        JLabel jLabel = new JLabel("Selected application");
        JPanel jPanel = new JPanel();
        if (!System.getProperties().getProperty("os.name").endsWith("nux")){
           jLabel.setEnabled(false);
           jTextFieldprogram.setEnabled(false);
        }
        jPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
//        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        jPanel.add(jCheckBox, gridBagConstraints);
       
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        jPanel.add(jLabel, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 53;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 140, 0);
        jPanel.add(jTextFieldprogram, gridBagConstraints);

        
        jFileChooser.setAccessory(jPanel);
        jFileChooser.setFileSelectionMode(jFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle("Please choose a file to export");
        jFileChooser.removeChoosableFileFilter(jFileChooser.getFileFilter());
        jFileChooser.setFileFilter(new FileNameExtensionFilter("*." + type, type));
        jFileChooser.showSaveDialog(null);
        File f = jFileChooser.getSelectedFile();
        if (f != null) {
            if (!f.getPath().endsWith("." + type)) {
                f = new File(f.getPath() + "." + type);
            }
            openfile = jCheckBox.isSelected();
            program = jTextFieldprogram.getText();
        }
        return f;
    }

    public static void openXlsInSystem(File f, Properties props) {
        Properties sysProps = System.getProperties();
        try {
            if (sysProps.getProperty("os.name").toLowerCase().startsWith("win")) {
                String[] commands = {"cmd", "/c", "start", "\"DummyTitle\"", f.getAbsolutePath()};
                Runtime.getRuntime().exec(commands);
            } else if (sysProps.getProperty("os.name").endsWith("nux")) {
                String commands = program + " " + f.getAbsolutePath();
                Runtime.getRuntime().exec(commands);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExportReports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void exportTableModelDialog(String type, JTable jTable, String header, String footer) {
        Properties props = Config.loadProperties();
        Properties sysProps = System.getProperties();
        File f = fileChooserEvent(type, props);
        if (f != null) {
            boolean rez = false;
            switch (type) {
                case "xls":
                    rez = excelExportTableModel(f, jTable.getModel(), header, footer);
                    if (openfile) {
                        openXlsInSystem(f, props);
                    }
                    break;

                case "pdf":
                    rez = pdfExportTableModel(f, jTable.getModel(), header, footer);
                     {
                        if (openfile) {
                            try {
                                if (sysProps.getProperty("os.name").toLowerCase().startsWith("win")) {
                                    Process p = Runtime.getRuntime()
                                            .exec("rundll32 url.dll,FileProtocolHandler " + f.getAbsolutePath());
                                } else if (sysProps.getProperty("os.name").endsWith("nux")) {
                                    String commands = program + " " + f.getAbsolutePath();
                                    Runtime.getRuntime().exec(commands);
                                }

                            } catch (IOException ex) {
                                Logger.getLogger(ExportReports.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    break;
                case "csv":
                    rez = csvExportTableModel(f, jTable.getModel(), props.getProperty("csvseparator"));
                     {
                        if (openfile) {
                            try {
                                if (sysProps.getProperty("os.name").toLowerCase().startsWith("win")) {
                                    String[] commands = {"cmd", "/c", "start", "\"DummyTitle\"", f.getAbsolutePath()};
                                    Runtime.getRuntime().exec(commands);
                                } else if (sysProps.getProperty("os.name").endsWith("nux")) {
                                    String commands = program + " " + f.getAbsolutePath();
                                    Runtime.getRuntime().exec(commands);
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(ExportReports.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    break;
            }
            if (rez) {
                JOptionPane.showMessageDialog(null, "File was susscesfully saved");
            } else {
                JOptionPane.showMessageDialog(null, "ERROR", "Error while saving file", JOptionPane.ERROR);
            }
        }
    }

    private static boolean csvExportTableModel(File f, TableModel tableModel, String separator) {

        try {
            FileWriter out = new FileWriter(f);
            String st;
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                out.write('"');
                st = tableModel.getColumnName(i);
                out.write(st.replaceAll("\"", "\"\""));
                if (i != tableModel.getColumnCount() - 1) {
                    out.write("\"");
                    out.write(separator);
                } else {
                    out.write('"');
                }
            }
            out.write('\n');
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    out.write('"');
                    st = ((Object) tableModel.getValueAt(i, j)).toString();
                    out.write(st.replaceAll("\"", "\"\""));
                    if (j != tableModel.getColumnCount() - 1) {
                        out.write("\"");
                        out.write(separator);
                    } else {
                        out.write('"');
                    }

                }
                out.write('\n');
            }

            out.close();
            return true;

        } catch (IOException ex) {
            Logger.getLogger(ExportReports.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean excelExportTableModel(File f, TableModel tableModel, String header, String footer) {
        FileOutputStream fos;
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
            Workbook wb = new HSSFWorkbook();
            Sheet sheet = wb.createSheet();
            Row headerText = sheet.createRow(0);
            Row columnNames = sheet.createRow(1);
            Row footerText = sheet.createRow(tableModel.getRowCount() + 2);
            Cell cell;
            CellStyle csHeader = wb.createCellStyle();
//            csHeader.setWrapText(true);
            csHeader.setBorderBottom(CellStyle.BORDER_THIN);
            csHeader.setBorderLeft(CellStyle.BORDER_THIN);
            csHeader.setBorderRight(CellStyle.BORDER_THIN);
            csHeader.setBorderTop(CellStyle.BORDER_THIN);
            CellStyle csDetails = wb.createCellStyle();
            csDetails.cloneStyleFrom(csHeader);
            Font font = wb.createFont();
            font.setBold(true);
            csHeader.setFont(font);
            cell = headerText.createCell(0);
            cell.setCellValue(header);
            cell.setCellStyle(csHeader);

            for (int i = 1; i < tableModel.getColumnCount(); i++) {
                cell = headerText.createCell(i);
                cell.setCellStyle(csHeader);
            }
            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, tableModel.getColumnCount() - 1);
            sheet.addMergedRegion(cra);

            cell = footerText.createCell(0);
            cell.setCellValue(footer);
            cell.setCellStyle(csHeader);

            for (int i = 1; i < tableModel.getColumnCount(); i++) {
                cell = footerText.createCell(i);
                cell.setCellStyle(csHeader);
            }
            cra = new CellRangeAddress(tableModel.getRowCount() + 2, tableModel.getRowCount() + 2, 0, tableModel.getColumnCount() - 1);
            sheet.addMergedRegion(cra);

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                cell = columnNames.createCell(i);
                cell.setCellValue(tableModel.getColumnName(i));
                cell.setCellStyle(csHeader);
                //sheet.setColumnWidth(i, cell.getStringCellValue().length() * 400);
            }
            Row row;
            Object value;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                row = sheet.createRow(i + 2);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    cell = row.createCell(j);
                    value = tableModel.getValueAt(i, j);
                    if (value == null) {
                        value = "";
                    }
                    cell.setCellValue(value.toString());
                    cell.setCellStyle(csDetails);
                    sheet.autoSizeColumn(j);
                    //if (sheet.getColumnWidth(j) < (cell.getStringCellValue().length() * 400)) {
                    //  sheet.setColumnWidth(j, cell.getStringCellValue().length() * 400);
                    //}
                }
            }
            wb.write(fos);
            fos.close();
            return true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExportReports.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportReports.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean pdfExportTableModel(File f, TableModel tableModel, String header, String footer) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();
            //addMetaData(document);
            addTitle(document, header);
            addEmptyLine(document, new Paragraph(), 2);
            addTable(document, tableModel);
            //addEmptyLine(document,new Paragraph(), 2);
            addTitle(document, footer);
            document.newPage();
            document.close();
            fos.close();
            return true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExportReports.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ExportReports.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportReports.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static void addMetaData(Document document) {
//        document.addTitle("My first PDF");
//        document.addSubject("Using iText");
//        document.addKeywords("Java, PDF, iText");
//        document.addAuthor("Lars Vogel");
//        document.addCreator("Lars Vogel");
    }

    private static void addTitle(Document document, String header) throws DocumentException {
        addEmptyLine(document, new Paragraph(), 1);
        Paragraph preface = new Paragraph();
        preface.add(header);
        preface.setAlignment(Element.ALIGN_CENTER);
        document.add(preface);
    }

    private static void addEmptyLine(Document document, Paragraph paragraph, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
        document.add(paragraph);
    }

    private static void addTable(Document document, TableModel tableModel) throws DocumentException {
        PdfPTable table = new PdfPTable(tableModel.getColumnCount());
        for (int i = 0; i < table.getNumberOfColumns(); i++) {
            table.addCell(tableModel.getColumnName(i));
        }
        table.setHeaderRows(1);
        Object value;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < table.getNumberOfColumns(); j++) {
                value = tableModel.getValueAt(i, j);
                if (value == null) {
                    value = "";
                }
                table.addCell(value.toString());
            }
        }
        document.add(table);
    }

    public static void exportJxlsDialog(Report report, String nameReport, String templateName) {
        Properties props = Config.loadProperties();
        File f = fileChooserEvent("xls", props);
        if (f != null) {
            List reportList = new ArrayList();
            reportList.add(report);
            Map beans = new HashMap();
            beans.put(nameReport, reportList);
            XLSTransformer transformer = new XLSTransformer();
            try {
                transformer.transformXLS(templateName, beans, f.getAbsolutePath());
                if (openfile) {
                    openXlsInSystem(f, props);
                }
            } catch (ParsePropertyException ex) {
                Logger.getLogger(JFrameFarmacy.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JFrameFarmacy.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(JFrameFarmacy.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
