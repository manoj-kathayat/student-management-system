import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MarksPage extends JPanel {

    private JComboBox<String> studentCombo;
    private JComboBox<String> semesterCombo;
    private JTable marksTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel, percentageLabel, gradeLabel;
    private int currentStudentId;

    public MarksPage(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        JLabel heading = new JLabel("Marks Management");
        heading.setFont(new Font("Arial", Font.BOLD, 42));
        topPanel.add(heading, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 242, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel selectPanel = new JPanel(new GridBagLayout());
        selectPanel.setBackground(Color.WHITE);
        selectPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectPanel.add(studentLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        studentCombo = new JComboBox<>();
        studentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        selectPanel.add(studentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel semLabel = new JLabel("Semester:");
        semLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectPanel.add(semLabel, gbc);
        gbc.gridx = 1;
        String[] semesters = {"Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"};
        semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        selectPanel.add(semesterCombo, gbc);

        centerPanel.add(selectPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel marksToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        marksToolbar.setBackground(new Color(240, 242, 245));
        JButton addRowBtn = new JButton("+ Add Subject Row");
        JButton removeRowBtn = new JButton("- Remove Selected Row");
        addRowBtn.setFont(new Font("Arial", Font.BOLD, 14));
        removeRowBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        marksToolbar.add(addRowBtn);
        marksToolbar.add(removeRowBtn);
        centerPanel.add(marksToolbar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        String[] columns = {"Subject", "Internal (30)", "External (70)", "Total (100)", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3 && column != 4;
            }
        };
        marksTable = new JTable(tableModel);
        marksTable.setRowHeight(40);
        marksTable.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        marksTable.getTableHeader().setBackground(new Color(15, 45, 90));
        marksTable.getTableHeader().setForeground(Color.WHITE);
        marksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        marksTable.setShowGrid(true);
        marksTable.setGridColor(new Color(220, 220, 220));

        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 1 || e.getColumn() == 2) {
                updateTotalsAndGrade();
            }
        });

        JScrollPane tableScroll = new JScrollPane(marksTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setPreferredSize(new Dimension(1000, 260));
        centerPanel.add(tableScroll);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        totalLabel = new JLabel("Total Marks: 0 / 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        percentageLabel = new JLabel("Percentage: 0.00%");
        percentageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gradeLabel = new JLabel("Grade: F");
        gradeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        summaryPanel.add(totalLabel);
        summaryPanel.add(percentageLabel);
        summaryPanel.add(gradeLabel);
        centerPanel.add(summaryPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("Save Marks");
        saveBtn.setBackground(new Color(15, 45, 90));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 18));
        saveBtn.setPreferredSize(new Dimension(160, 45));
        saveBtn.addActionListener(e -> saveMarks());
        buttonPanel.add(saveBtn);
        centerPanel.add(buttonPanel);

        addRowBtn.addActionListener(e -> {
            tableModel.addRow(new Object[]{"New Subject", 0, 0, 0, "F"});
        });
        removeRowBtn.addActionListener(e -> {
            int sel = marksTable.getSelectedRow();
            if (sel >= 0) {
                tableModel.removeRow(sel);
                updateTotalsAndGrade();
            }
        });

        loadStudentCombo();
        studentCombo.addActionListener(e -> {
            if (studentCombo.getSelectedItem() != null) {
                String selected = (String) studentCombo.getSelectedItem();
                currentStudentId = Integer.parseInt(selected.split("\\(")[1].replace(")", ""));
                loadMarks();
            }
        });
        semesterCombo.addActionListener(e -> loadMarks());

        if (studentCombo.getItemCount() > 0) {
            studentCombo.setSelectedIndex(-1);
            studentCombo.setSelectedIndex(0);
        }

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadStudentCombo();
                if (studentCombo.getItemCount() > 0) {
                    studentCombo.setSelectedIndex(-1);
                    studentCombo.setSelectedIndex(0);
                }
            }
        });
    }

    private void loadStudentCombo() {
        studentCombo.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, full_name FROM students ORDER BY full_name")) {
            while (rs.next()) {
                studentCombo.addItem(rs.getString("full_name") + " (" + rs.getInt("id") + ")");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load students: " + ex.getMessage());
        }
    }

    private void loadMarks() {
        if (currentStudentId == 0) return;
        tableModel.setRowCount(0);
        String sql = "SELECT subject, internal_marks, external_marks FROM marks WHERE student_id = ? AND semester = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentStudentId);
            pstmt.setString(2, (String) semesterCombo.getSelectedItem());
            ResultSet rs = pstmt.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int i = rs.getInt("internal_marks");
                int e = rs.getInt("external_marks");
                tableModel.addRow(new Object[]{
                        rs.getString("subject"), i, e, i + e, calculateGrade(i + e)
                });
            }
            if (!hasData) {
                loadSubjectTemplates();
            }
            updateTotalsAndGrade();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSubjectTemplates() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT subject FROM marks ORDER BY subject")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getString("subject"), 0, 0, 0, "F"});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateTotalsAndGrade() {
        int totalMarks = 0;
        int maxMarks = tableModel.getRowCount() * 100;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int internal = 0, external = 0;
            try { internal = Integer.parseInt(tableModel.getValueAt(i, 1).toString()); } catch (Exception ignored) {}
            try { external = Integer.parseInt(tableModel.getValueAt(i, 2).toString()); } catch (Exception ignored) {}
            internal = Math.min(30, Math.max(0, internal));
            external = Math.min(70, Math.max(0, external));
            int total = internal + external;
            tableModel.setValueAt(total, i, 3);
            totalMarks += total;
            tableModel.setValueAt(calculateGrade(total), i, 4);
        }
        double percentage = maxMarks == 0 ? 0 : (totalMarks * 100.0) / maxMarks;
        totalLabel.setText("Total Marks: " + totalMarks + " / " + maxMarks);
        percentageLabel.setText(String.format("Percentage: %.2f%%", percentage));
        gradeLabel.setText("Grade: " + calculateGrade((int) percentage));
    }

    private String calculateGrade(int marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B+";
        if (marks >= 60) return "B";
        if (marks >= 50) return "C";
        return "F";
    }

    private void saveMarks() {
        if (currentStudentId == 0) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }
        String semester = (String) semesterCombo.getSelectedItem();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                int internal = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                int external = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                internal = Math.min(30, Math.max(0, internal));
                external = Math.min(70, Math.max(0, external));
                tableModel.setValueAt(internal, i, 1);
                tableModel.setValueAt(external, i, 2);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid marks at row " + (i + 1));
                return;
            }
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM marks WHERE student_id = ? AND semester = ?")) {
                del.setInt(1, currentStudentId);
                del.setString(2, semester);
                del.executeUpdate();
            }

            String insertSql = "INSERT INTO marks (student_id, semester, subject, internal_marks, external_marks) VALUES (?,?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String subject = tableModel.getValueAt(i, 0).toString().trim();
                    if (subject.isEmpty()) continue;
                    int internal = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                    int external = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                    pstmt.setInt(1, currentStudentId);
                    pstmt.setString(2, semester);
                    pstmt.setString(3, subject);
                    pstmt.setInt(4, internal);
                    pstmt.setInt(5, external);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            JOptionPane.showMessageDialog(this, "Marks saved successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }
}