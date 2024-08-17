import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchfileConvert extends JFrame {
    private JButton selectFilesButton, startButton, cancelButton;
    private JComboBox<String> conversionTypeComboBox;
    private JProgressBar overallProgressBar;
    private JTable fileProgressTable;
    private JTextArea statusTextArea;
    private List<File> selectedFiles;
    private ExecutorService executorService;
    private List<FileConverter> activeConverters;
    private JLabel activeThreadsLabel;
    private AtomicInteger activeThreadsCount;

    // Define colors
    private static final Color LIGHT_RED = new Color(255, 204, 204); // Light red background
    private static final Color RED_CONVERT_SECTION = new Color(255, 102, 102); // Red for the conversion section

    public BatchfileConvert() {
        setTitle("Batch File Converter");
        setSize(1000, 600);  // Enlarged the frame size for a larger sidebar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        activeConverters = new CopyOnWriteArrayList<>();
        activeThreadsCount = new AtomicInteger(0);
    }

    private void initComponents() {
        // Setting light red background for the main content pane
        getContentPane().setBackground(LIGHT_RED);
        setLayout(new BorderLayout());

        // Enlarged the sidebar panel
        JPanel sidebarPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        sidebarPanel.setPreferredSize(new Dimension(300, 0));  // Set a larger preferred size for the sidebar
        sidebarPanel.setBackground(RED_CONVERT_SECTION);  // Set background to red for the conversion section

        // Create buttons and combo box with white font color
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);  // Increase font size

        selectFilesButton = new JButton("Select Files");
        selectFilesButton.setBackground(Color.DARK_GRAY);
        selectFilesButton.setForeground(Color.WHITE);
        selectFilesButton.setFont(buttonFont);

        conversionTypeComboBox = new JComboBox<>(new String[]{"PDF to Docx", "Image Resize"});
        conversionTypeComboBox.setBackground(Color.DARK_GRAY);
        conversionTypeComboBox.setForeground(Color.WHITE);
        conversionTypeComboBox.setFont(buttonFont);

        startButton = new JButton("Start Conversion");
        startButton.setBackground(Color.DARK_GRAY);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(buttonFont);

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.DARK_GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(buttonFont);

        // Add buttons and combo box to sidebar
        sidebarPanel.add(selectFilesButton);
        sidebarPanel.add(new JLabel("Conversion Type:"));
        sidebarPanel.add(conversionTypeComboBox);
        sidebarPanel.add(startButton);
        sidebarPanel.add(cancelButton);

        // Set label font size and color
        activeThreadsLabel = new JLabel("Active Threads: 0");
        activeThreadsLabel.setForeground(Color.WHITE);
        activeThreadsLabel.setFont(buttonFont);
        sidebarPanel.add(activeThreadsLabel);

        // Adjust progress bar background and foreground
        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setBackground(Color.BLACK);
        overallProgressBar.setForeground(Color.GREEN);

        // Status text area with white font and light red background
        statusTextArea = new JTextArea(5, 40);
        statusTextArea.setEditable(false);
        statusTextArea.setBackground(LIGHT_RED);
        statusTextArea.setForeground(Color.BLACK);
        statusTextArea.setFont(buttonFont);
        JScrollPane statusScrollPane = new JScrollPane(statusTextArea);

        // Table to track individual file progress with light red background and white font
        String[] columnNames = {"File Name", "Progress", "Status"};
        Object[][] data = {};
        fileProgressTable = new JTable(new ProgressTableModel(data, columnNames)) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(LIGHT_RED);  // Set table background color to light red
                c.setForeground(Color.BLACK);  // Set font color to black
                c.setFont(buttonFont);  // Set larger font size
                return c;
            }
        };
        fileProgressTable.setBackground(LIGHT_RED);
        fileProgressTable.setForeground(Color.BLACK);
        JScrollPane tableScrollPane = new JScrollPane(fileProgressTable);

        // Adjusted the layout: Sidebar on the left, table on the center, and status area below
        add(sidebarPanel, BorderLayout.WEST);
        add(tableScrollPane, BorderLayout.CENTER);
        add(statusScrollPane, BorderLayout.SOUTH);
        add(overallProgressBar, BorderLayout.NORTH);

        selectFilesButton.addActionListener(e -> selectFiles());
        startButton.addActionListener(e -> startConversion());
        cancelButton.addActionListener(e -> cancelConversion());
    }

    private void selectFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFiles = List.of(fileChooser.getSelectedFiles());
            statusTextArea.append("Selected " + selectedFiles.size() + " files.\n");

            // Clear the table before adding new files
            ProgressTableModel model = (ProgressTableModel) fileProgressTable.getModel();
            model.setRowCount(0);

            // Populate table with file information
            for (File file : selectedFiles) {
                model.addRow(new Object[]{file.getName(), 0, "Waiting"});
            }
        }
    }

    private void startConversion() {
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select files first.");
            return;
        }

        String conversionType = (String) conversionTypeComboBox.getSelectedItem();
        overallProgressBar.setValue(0);
        statusTextArea.setText("");

        for (int i = 0; i < selectedFiles.size(); i++) {
            File file = selectedFiles.get(i);
            FileConverter converter = new FileConverter(file, conversionType, i);
            activeConverters.add(converter);
            activeThreadsCount.incrementAndGet();
            executorService.submit(converter); // Submit task to thread pool
        }

        updateActiveThreads();
    }

    private void cancelConversion() {
        for (FileConverter converter : activeConverters) {
            converter.cancel(true);
        }
        activeConverters.clear();
        statusTextArea.append("Conversion cancelled.\n");
        updateActiveThreads();
    }

    private void updateActiveThreads() {
        activeThreadsLabel.setText("Active Threads: " + activeThreadsCount.get());
    }

    private class FileConverter implements Runnable {
        private final File file;
        private final String conversionType;
        private final int rowIndex;
        private volatile boolean isCancelled = false;

        public FileConverter(File file, String conversionType, int rowIndex) {
            this.file = file;
            this.conversionType = conversionType;
            this.rowIndex = rowIndex;
        }

        @Override
        public void run() {
            try {
                SwingUtilities.invokeLater(() -> updateTableStatus(rowIndex, "In Progress"));

                for (int i = 0; i <= 100 && !isCancelled; i += 10) {
                    Thread.sleep(500); // Simulate conversion work
                    final int progress = i;
                    SwingUtilities.invokeLater(() -> updateTableProgress(rowIndex, progress));
                }

                if (!isCancelled) {
                    SwingUtilities.invokeLater(() -> updateTableStatus(rowIndex, "Completed"));
                }
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> updateTableStatus(rowIndex, "Cancelled"));
            } finally {
                activeConverters.remove(this);
                activeThreadsCount.decrementAndGet();
                SwingUtilities.invokeLater(() -> updateOverallProgress());
                SwingUtilities.invokeLater(() -> updateActiveThreads());
            }
        }

        public void cancel(boolean mayInterruptIfRunning) {
            isCancelled = true;
        }
    }

    private void updateTableProgress(int rowIndex, int progress) {
        ProgressTableModel model = (ProgressTableModel) fileProgressTable.getModel();
        model.setValueAt(progress, rowIndex, 1);
    }

    private void updateTableStatus(int rowIndex, String status) {
        ProgressTableModel model = (ProgressTableModel) fileProgressTable.getModel();
        model.setValueAt(status, rowIndex, 2);
    }

    private void updateOverallProgress() {
        int totalProgress = 0;
        for (int i = 0; i < fileProgressTable.getRowCount(); i++) {
            totalProgress += (int) fileProgressTable.getValueAt(i, 1);
        }
        int overallProgress = totalProgress / Math.max(1, fileProgressTable.getRowCount());
        overallProgressBar.setValue(overallProgress);

        if (activeConverters.isEmpty()) {
            JOptionPane.showMessageDialog(BatchfileConvert.this, "All conversions completed!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BatchfileConvert().setVisible(true));
    }

    // Custom TableModel for file progress
    private static class ProgressTableModel extends DefaultTableModel {
        public ProgressTableModel(Object[][] data, String[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Make table cells non-editable
        }
    }
}