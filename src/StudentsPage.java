import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StudentsPage extends JPanel {

    private JTable studentTable;
    private DefaultTableModel model;
    private StudentProfile studentProfile;

    public StudentsPage(CardLayout cardLayout, JPanel mainPanel, StudentProfile profile) {
        this.studentProfile = profile;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        JLabel heading = new JLabel("Students Management");
        heading.setFont(new Font("Arial", Font.BOLD, 42));
        topPanel.add(heading, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 242, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JPanel actionBar = new JPanel(new GridBagLayout());
        actionBar.setBackground(new Color(240, 242, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        searchField.setForeground(Color.GRAY);
        searchField.setText("Search Student...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search Student...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search Student...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        actionBar.add(searchField, gbc);

        JButton searchBtn = new JButton("Search");
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchBtn.setFocusPainted(false);
        searchBtn.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.weightx = 0;
        actionBar.add(searchBtn, gbc);

        JButton addBtn = new JButton("Add Student");
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 18));
        addBtn.setFocusPainted(false);
        addBtn.setBackground(new Color(15, 45, 90));
        gbc.gridx = 2;
        actionBar.add(addBtn, gbc);
        addBtn.addActionListener(e -> cardLayout.show(mainPanel, "Add Student"));

        centerPanel.add(actionBar, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Course", "Phone", "Email", "Status", "Actions"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(model);
        studentTable.setRowHeight(45);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        studentTable.setFocusable(false);
        studentTable.getTableHeader().setBackground(new Color(15, 45, 90));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        studentTable.getTableHeader().setPreferredSize(new Dimension(100, 45));
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));
        studentTable.setSelectionBackground(new Color(220, 235, 252));

        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return c;
            }
        });

        studentTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 16));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    label.setBackground(new Color(220, 235, 252));
                    label.setOpaque(true);
                } else {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                    label.setOpaque(true);
                }
                label.setForeground(new Color(52, 152, 219));
                return label;
            }
        });

        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = studentTable.rowAtPoint(e.getPoint());
                int col = studentTable.columnAtPoint(e.getPoint());
                if (col == 6 && row >= 0) {
                    int x = e.getX();
                    int cellX = studentTable.getCellRect(row, col, true).x;
                    int studentId = (int) studentTable.getValueAt(row, 0);
                    if (x < cellX + 50) {
                        studentProfile.setStudentId(studentId);
                        cardLayout.show(mainPanel, "Student Profile");
                    } else if (x < cellX + 100) {
                        editStudent(row);
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(null,
                                "Delete " + studentTable.getValueAt(row, 1) + "?",
                                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            deleteStudent(studentId);
                            loadStudents(null);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadStudents(null);

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty() || query.equals("Search Student...")) {
                loadStudents(null);
            } else {
                loadStudents(query);
            }
        });

        // Refresh when page becomes visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadStudents(null);
            }
        });
    }

    private void loadStudents(String search) {
        model.setRowCount(0);
        String sql;
        if (search == null) {
            sql = "SELECT id, full_name, course, phone, email, status FROM students";
        } else {
            sql = "SELECT id, full_name, course, phone, email, status FROM students WHERE full_name LIKE ? OR id LIKE ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (search != null) {
                String like = "%" + search + "%";
                pstmt.setString(1, like);
                pstmt.setString(2, like);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("course"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("status"),
                    "View | Edit | Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteStudent(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadStudents(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void editStudent(int row) {
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        String newName = JOptionPane.showInputDialog(this, "Edit Name:", name);
        if (newName != null && !newName.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE students SET full_name=? WHERE id=?")) {
                pstmt.setString(1, newName);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
                loadStudents(null);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
            }
        }
    }
}