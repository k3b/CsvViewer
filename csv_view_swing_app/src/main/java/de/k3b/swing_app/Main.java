// src/main/java/example/
// UI: Main.java
package de.k3b.swing_app;

import javax.swing.*;
import javax.swing.table.TableRowSorter;

import de.k3b.csvviewer.lib.data.model.TableModelApi;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUI);
    }

    private static void createAndShowUI() {
        // Create API implementation
        TableModelApi api = new DemoTableModel();

        // Wrap into Swing TableModel
        JTableAdapter model = new JTableAdapter(api);

        JTable table = new JTable(model);
        model.setTable(table);

        // ✅ Enable sorting
        TableRowSorter<JTableAdapter> sorter = new TableRowSorter<>(model);

        // disable sorting for column 0
        // sorter.setSortable(0, false);

        // Custom comparator
        // sorter.setComparator(2, (a, b) -> ((Integer) a).compareTo((Integer) b));

        // sort first column initially
        // sorter.toggleSortOrder(0);
        table.setRowSorter(sorter);

        // Optional: better UI
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        JFrame frame = new JFrame("TableModelApi Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
