package net.csv.editor.view;

import com.opencsv.CSVParser;
import net.csv.editor.tools.ComponentTools;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;


public class PrincipalFrm extends JFrame {

    private JPanel locationPanel;
    private JTextField locationTextField;
    private JButton openFileButton;
    private JPanel editorPanel;
    private JTable csvGrid;
    private DefaultTableModel defaultTableModel;
    private JPanel actionsPanel;
    private JButton addRowButton;
    private JButton addColumnButton;
    private JButton deleteSelectedRowButton;
    private JButton deleteSelectedColumnButton;
    private JButton saveCSVButton;
    private JScrollPane jScrollPaneCsvGrid;

    public PrincipalFrm() {
        super("CSV Editor");
        init();
    }

    public void init() {
        ComponentTools.setGrayColor(this.getContentPane());
        this.locationPanel = new JPanel( new BorderLayout() );
        this.locationTextField = new JTextField(40);
        ComponentTools.setGrayColor(locationTextField);
        this.openFileButton = new JButton("Open");
        ComponentTools.setGrayColor(openFileButton);
        this.editorPanel = new JPanel(new BorderLayout());
        ComponentTools.setGrayColor(locationPanel);
        this.csvGrid = new JTable();
        ComponentTools.setGrayColor(csvGrid);
        this.defaultTableModel = new DefaultTableModel();
        this.actionsPanel = new JPanel();
        this.addRowButton = new JButton("Add Row");
        this.addColumnButton = new JButton("Add Column");
        this.deleteSelectedRowButton = new JButton("Delete selected row");
        this.deleteSelectedColumnButton = new JButton("Delete selected column");
        this.saveCSVButton = new JButton("Save csv");
        this.csvGrid.setModel(this.defaultTableModel);
        this.csvGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.locationTextField.setEditable(false);
        this.getContentPane().add(this.locationPanel, BorderLayout.NORTH);
        this.locationPanel.add(this.locationTextField ,BorderLayout.CENTER );
        this.locationPanel.add(this.openFileButton , BorderLayout.EAST);
        this.openFileButton.addActionListener(action -> this.openFileAction(action));
        this.addColumnButton.addActionListener(action -> this.addColumnAction(action));
        this.getContentPane().add(this.editorPanel);
        jScrollPaneCsvGrid = new JScrollPane(csvGrid);
        this.editorPanel.add(jScrollPaneCsvGrid, BorderLayout.CENTER);
        ComponentTools.setGrayColor(editorPanel,jScrollPaneCsvGrid.getViewport());
        this.getContentPane().add(actionsPanel, BorderLayout.SOUTH);
        this.actionsPanel.add(addRowButton);
        this.actionsPanel.add(addColumnButton);
        this.actionsPanel.add(deleteSelectedRowButton);
        this.actionsPanel.add(deleteSelectedColumnButton);
        this.actionsPanel.add(saveCSVButton);
        ComponentTools.setGrayColor(addRowButton,addColumnButton,deleteSelectedColumnButton,deleteSelectedRowButton,saveCSVButton,actionsPanel);
        this.saveCSVButton.addActionListener(event -> this.saveCSV(event));
        this.addRowButton.addActionListener(event -> addRow(event));
        this.deleteSelectedRowButton.addActionListener(event -> this.deleteSelectedRowAction(event));
        this.deleteSelectedColumnButton.addActionListener(event -> this.deleteSelectedColumnAction(event));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void openFileAction(ActionEvent action) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new CsvFileFilter());
        if (fileChooser.showOpenDialog(this) != JFileChooser.CANCEL_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                String path = selectedFile.toString();
                this.locationTextField.setText(path);
                try {
                    ArrayList<String[]> lines = parseCSV(selectedFile);
                    populateTable(lines);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void addColumnAction(ActionEvent event) {
        String columnName = JOptionPane.showInputDialog("Insert Column Name");
        if (columnName != null && !columnName.isEmpty()) {
            this.defaultTableModel.addColumn(columnName);
        }
    }

    private void deleteSelectedRowAction(ActionEvent event) {
        int selectedRow = this.csvGrid.getSelectedRow();
        if (selectedRow != -1) {
            this.defaultTableModel.removeRow(selectedRow);
        }
    }

    private void deleteSelectedColumnAction(ActionEvent event) {
        int selectedColumn = this.csvGrid.getSelectedColumn();
        if (selectedColumn != -1) {
            Object[] columnIdentifiers = getColumnIdentifiers(  selectedColumn );
            this.defaultTableModel.setColumnIdentifiers( columnIdentifiers );
            // TableColumn tableColumn = csvGrid.getColumnModel().getColumn(selectedColumn);
            // this.csvGrid.getColumnModel().removeColumn(tableColumn);
        }
    }

    private Object[] getColumnIdentifiers(int selectedColumn) {
        int columnCount = this.defaultTableModel.getColumnCount();
        ArrayList<String> columnNames = new ArrayList<>();
        for( int i = 0;i<columnCount;i++ ){
            String columnName = this.defaultTableModel.getColumnName(i);
            columnNames.add(columnName);
        }
        columnNames.remove(selectedColumn);
        Object[] identifiers = new Object[columnNames.size()];
        for( int i=0;i<identifiers.length;i++ ){
            identifiers[i]=columnNames.get(i);
        }
        return identifiers;
    }

    private void addRow(ActionEvent event) {
        int columnCount = this.defaultTableModel.getColumnCount();
        Object[] row = new Object[columnCount];
        this.defaultTableModel.addRow(row);
    }


    private void saveCSV(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) != JFileChooser.CANCEL_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String extension = selectedFile.toString();
            if (!extension.endsWith(".csv")) {
                extension = extension + ".csv";
                TableColumnModel columnModel = csvGrid.getColumnModel();
                ArrayList<String> columns = new ArrayList<>();
                Enumeration<TableColumn> cols = columnModel.getColumns();
                while (cols.hasMoreElements()) {
                    TableColumn tableColumn = cols.nextElement();
                    columns.add(tableColumn.getHeaderValue().toString());
                }
                int rowCount = this.defaultTableModel.getRowCount();
                ArrayList<String[]> rows = new ArrayList<>();
                for (int i = 0; i < rowCount; i++) {
                    String[] rowData = getRowData(i);
                    rows.add(rowData);
                }
                writeCSVData(columns, rows, extension);
            }
        }
    }

    private void writeCSVData(ArrayList<String> columns, ArrayList<String[]> rows, String extension) {
        StringBuilder csvData = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            if (i != 0) {
                csvData.append(",");
            }
            csvData.append(columns.get(i));
        }
        csvData.append("\r\n");
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (i != 0) {
                    csvData.append(",");
                }
                csvData.append(row[i]);
            }
            csvData.append("\r\n");
        }
        byte[] data = csvData.toString().getBytes();
        OutputStream os = null;
        try {
            os = new FileOutputStream(extension);
            os.write(data);
            os.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String[] getRowData(int index) {
        int columnCount = this.csvGrid.getColumnCount();
        String[] columnData = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            Object cellData = this.csvGrid.getValueAt(index, i);
            columnData[i] = cellData.toString();
        }
        return columnData;
    }

    private void populateTable(ArrayList<String[]> lines) {
        Iterator<String[]> iterator = lines.iterator();
        boolean first = true;
        while (iterator.hasNext()) {
            String[] line = iterator.next();
            if (first) {
                for (String col : line) {
                    this.defaultTableModel.addColumn(col);
                }
                first = false;
            } else {
                Object[] row = new Object[line.length];
                System.arraycopy(line, 0, row, 0, line.length);
                this.defaultTableModel.addRow(row);
            }
        }

    }

    private ArrayList<String[]> parseCSV(File selectedFile) {
        RandomAccessFile raf = null;
        CSVParser parser = new CSVParser();
        ArrayList<String[]> csvData = new ArrayList<>();
        try {
            raf = new RandomAccessFile(selectedFile, "r");
            do {
                String line = raf.readLine();
                String[] values = parser.parseLine(line);
                csvData.add(values);
            } while (raf.getFilePointer() < raf.length());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (Exception ex) {
            }
        }
        return csvData;
    }


    private class CsvFileFilter extends FileFilter {

        @Override
        public boolean accept(File file) {
            if (file != null) {
                return file.isDirectory() || file.toString().endsWith(".csv");
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return "csv files";
        }
    }

}
