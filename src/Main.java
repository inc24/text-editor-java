import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    private static File currentFile = null;
    private static String currentFontFamily = "Arial";
    private static Integer currentFontStyle = Font.PLAIN;
    private static Integer currentFontSize = 16;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Text Editor");
        frame.setSize(1200, 1000);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font(currentFontFamily, currentFontStyle, currentFontSize));
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();

        // ===== FILE MENU =====
        JMenu menuFile = new JMenu("File");
        JMenuItem openFileItem = new JMenuItem("Open");
        JMenuItem saveFileItem = new JMenuItem("Save");
        JMenuItem saveAsFileItem = new JMenuItem("Save As");
        JMenuItem exitFileItem = new JMenuItem("Exit");
        menuFile.add(openFileItem);
        menuFile.add(saveFileItem);
        menuFile.add(saveAsFileItem);
        menuFile.addSeparator();
        menuFile.add(exitFileItem);

        // Open
        openFileItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                    textArea.read(reader, null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error opening file: " + ex.getMessage());
                }
            }
        });

        // Save As
        saveAsFileItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".txt");
                }
                currentFile = selectedFile;
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                    textArea.write(writer);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
                }
            }
        });

        // Save
        saveFileItem.addActionListener(e -> {
            if (currentFile != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                    textArea.write(writer);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
                }
            } else {
                saveAsFileItem.doClick();
            }
        });

        // Exit
        exitFileItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // ===== EDIT MENU =====
        JMenu menuEdit = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        menuEdit.add(cutItem);
        menuEdit.add(copyItem);
        menuEdit.add(pasteItem);

        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());

        // Popup Menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cutPopup = new JMenuItem("Cut");
        JMenuItem copyPopup = new JMenuItem("Copy");
        JMenuItem pastePopup = new JMenuItem("Paste");

        cutPopup.addActionListener(e -> textArea.cut());
        copyPopup.addActionListener(e -> textArea.copy());
        pastePopup.addActionListener(e -> textArea.paste());

        popupMenu.add(cutPopup);
        popupMenu.add(copyPopup);
        popupMenu.add(pastePopup);

        textArea.setComponentPopupMenu(popupMenu);

        // ===== FORMAT MENU =====
        JMenu menuFormat = new JMenu("Format");

        // Font Family
        JMenu menuFontFamily = new JMenu("Font Family");

        Map<String, String> fontMap = new LinkedHashMap<>();
        fontMap.put("Times New Roman", "Times New Roman");
        fontMap.put("Calibri", "Calibri");
        fontMap.put("Arial", "Arial");

       for (Map.Entry<String, String> entry : fontMap.entrySet()) {
           JMenuItem fontItem = new JMenuItem(entry.getKey());
           fontItem.addActionListener(e -> {
               currentFontFamily = entry.getValue();
               textArea.setFont(new Font(
                       currentFontFamily,
                       currentFontStyle,
                       currentFontSize));
           });
           menuFontFamily.add(fontItem);
       }
       menuFormat.add(menuFontFamily);

        // Font Style
        JMenu menuFontStyle = new JMenu("Font Style");

        Map<String, Integer> styleMap = new LinkedHashMap<>();
        styleMap.put("Regular", Font.PLAIN);
        styleMap.put("Bold", Font.BOLD);
        styleMap.put("Italic", Font.ITALIC);
        styleMap.put("Bold Italic", Font.BOLD | Font.ITALIC);

        for (Map.Entry<String, Integer> entry : styleMap.entrySet()) {
            JMenuItem styleItem = new JMenuItem(entry.getKey());
            styleItem.addActionListener(e -> {
                currentFontStyle = entry.getValue();
                textArea.setFont(new Font(
                        currentFontFamily,
                        currentFontStyle,
                        currentFontSize));
            });
            menuFontStyle.add(styleItem);
        }
        menuFormat.add(menuFontStyle);


        // Font Size
        JMenu menuFontSize = new JMenu("Font Size");
        JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 72, 1));
        fontSizeSpinner.setPreferredSize(new Dimension(60, 25));
        fontSizeSpinner.addChangeListener(e -> {
            currentFontSize = (int) fontSizeSpinner.getValue();
            textArea.setFont(new Font(
                    currentFontFamily,
                    currentFontStyle,
                    currentFontSize));
        });
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        spinnerPanel.add(fontSizeSpinner);
        menuFontSize.add(spinnerPanel);
        menuFormat.add(menuFontSize);

        // Font Color
        JMenu menuFontColor = new JMenu("Font Color");

        Map<String, Color> colorMap = new LinkedHashMap<>();
        colorMap.put("Black", Color.BLACK);
        colorMap.put("Red", Color.RED);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("Blue", Color.BLUE);

        for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
            JMenuItem colorItem = new JMenuItem(entry.getKey());
            colorItem.addActionListener(e ->
                    textArea.setForeground(
                            entry.getValue()
                    ));
            menuFontColor.add(colorItem);
        }
        menuFormat.add(menuFontColor);

        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuFormat);
        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }
}
