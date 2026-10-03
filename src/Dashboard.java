import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Dashboard extends JFrame {

    private boolean isSidebarCollapsed = false;
    private JPanel sidebar;
    private JButton toggleSidebarBtn;
    private JPanel cardsPanelRef;
    private CardLayout cardLayout;
    private JPanel mainpanel;

    Dashboard() {
        setTitle("Student Database Management System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 800));
        sidebar.setBackground(new Color(15, 45, 90));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JButton dashboardBtn = new JButton("Dashboard");
        JButton studentsBtn = new JButton("Students");
        JButton addstudentsBtn = new JButton("Add Students");
        JButton coursesBtn = new JButton("Courses");
        JButton libraryBtn = new JButton("Library");
        JButton marksBtn = new JButton("Marks");
        JButton feesBtn = new JButton("Fees");

        JButton[] buttons = {dashboardBtn, studentsBtn, addstudentsBtn,
                coursesBtn, libraryBtn, marksBtn, feesBtn};

        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setBackground(new Color(25, 65, 125));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
        }
        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);

        // Top bar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setPreferredSize(new Dimension(1400, 70));
        topbar.setBackground(new Color(10, 35, 80));

        toggleSidebarBtn = new JButton("=");
        toggleSidebarBtn.setFont(new Font("Arial", Font.BOLD, 24));
        toggleSidebarBtn.setFocusPainted(false);
        toggleSidebarBtn.setBackground(new Color(10, 35, 80));
        toggleSidebarBtn.setForeground(Color.WHITE);
        toggleSidebarBtn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        toggleSidebarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleSidebarBtn.addActionListener(e -> toggleSidebar());

        JLabel title = new JLabel("Student Database Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        topbar.add(toggleSidebarBtn, BorderLayout.WEST);
        topbar.add(title, BorderLayout.CENTER);
        add(topbar, BorderLayout.NORTH);

        // Main panel
        cardLayout = new CardLayout();
        mainpanel = new JPanel(cardLayout);
        mainpanel.setBackground(new Color(240, 242, 245));

        // Dashboard content
        JPanel dashboardcontent = new JPanel(new BorderLayout());
        dashboardcontent.setBackground(new Color(240, 242, 245));
        JLabel dashboardtitle = new JLabel("Dashboard");
        dashboardtitle.setFont(new Font("Arial", Font.BOLD, 50));
        dashboardtitle.setBorder(BorderFactory.createEmptyBorder(100, 30, 20, 20));
        dashboardcontent.add(dashboardtitle, BorderLayout.NORTH);

        cardsPanelRef = new JPanel(new GridLayout(1, 4, 20, 20));
        cardsPanelRef.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        cardsPanelRef.setBackground(new Color(240, 242, 245));
        cardsPanelRef.setPreferredSize(new Dimension(1100, 250));
        cardsPanelRef.add(createCard("Total Students", "0", new Color(66, 135, 245)));
        cardsPanelRef.add(createCard("Courses", "0", new Color(46, 204, 113)));
        cardsPanelRef.add(createCard("Subjects", "0", new Color(241, 196, 15)));
        cardsPanelRef.add(createCard("Fees Collected", "₹0", new Color(231, 76, 60)));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(240, 242, 245));
        centerWrapper.add(cardsPanelRef);
        dashboardcontent.add(centerWrapper, BorderLayout.CENTER);
        mainpanel.add(dashboardcontent, "Dashboard");

        // Create pages
        StudentProfile studentProfile = new StudentProfile(cardLayout, mainpanel);
        StudentsPage studentsPage = new StudentsPage(cardLayout, mainpanel, studentProfile);
        AddStudentPage addStudentsPage = new AddStudentPage();
        CoursesPage coursesPage = new CoursesPage(cardLayout, mainpanel);
        LibraryPage libraryPage = new LibraryPage(cardLayout, mainpanel);
        MarksPage marksPage = new MarksPage(cardLayout, mainpanel);
        FeesPage feesPage = new FeesPage(cardLayout, mainpanel);

        mainpanel.add(studentsPage, "Students");
        mainpanel.add(addStudentsPage, "Add Student");
        mainpanel.add(studentProfile, "Student Profile");
        mainpanel.add(coursesPage, "Courses");
        mainpanel.add(libraryPage, "Library");
        mainpanel.add(marksPage, "Marks");
        mainpanel.add(feesPage, "Fees");

        add(mainpanel, BorderLayout.CENTER);
        setVisible(true);

        // Sidebar actions
        dashboardBtn.addActionListener(e -> {
            cardLayout.show(mainpanel, "Dashboard");
            updateDashboardCounts();
        });
        studentsBtn.addActionListener(e -> cardLayout.show(mainpanel, "Students"));
        addstudentsBtn.addActionListener(e -> cardLayout.show(mainpanel, "Add Student"));
        libraryBtn.addActionListener(e -> cardLayout.show(mainpanel, "Library"));
        coursesBtn.addActionListener(e -> cardLayout.show(mainpanel, "Courses"));
        feesBtn.addActionListener(e -> cardLayout.show(mainpanel, "Fees"));
        marksBtn.addActionListener(e -> cardLayout.show(mainpanel, "Marks"));

        updateDashboardCounts();
    }

    private void updateDashboardCounts() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
            if (rs.next()) {
                ((JLabel) ((JPanel) cardsPanelRef.getComponent(0)).getComponent(1))
                        .setText(String.valueOf(rs.getInt(1)));
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM courses");
            if (rs.next()) {
                ((JLabel) ((JPanel) cardsPanelRef.getComponent(1)).getComponent(1))
                        .setText(String.valueOf(rs.getInt(1)));
            }
            rs = stmt.executeQuery("SELECT COUNT(DISTINCT subject) FROM marks");
            if (rs.next()) {
                ((JLabel) ((JPanel) cardsPanelRef.getComponent(2)).getComponent(1))
                        .setText(String.valueOf(rs.getInt(1)));
            }
            rs = stmt.executeQuery("SELECT COALESCE(SUM(amount),0) FROM fees_payments WHERE MONTH(payment_date)=MONTH(CURDATE()) AND YEAR(payment_date)=YEAR(CURDATE())");
            if (rs.next()) {
                ((JLabel) ((JPanel) cardsPanelRef.getComponent(3)).getComponent(1))
                        .setText("₹ " + rs.getDouble(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void toggleSidebar() {
        if (isSidebarCollapsed) {
            sidebar.setPreferredSize(new Dimension(250, 800));
            toggleSidebarBtn.setText("X");
            restoreButtonTexts();
        } else {
            sidebar.setPreferredSize(new Dimension(0, 800));
            toggleSidebarBtn.setText("=");
            hideButtonTexts();
        }
        isSidebarCollapsed = !isSidebarCollapsed;
        sidebar.revalidate();
        sidebar.repaint();
    }

    private void hideButtonTexts() {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setToolTipText(btn.getText());
                btn.setText("");
            }
        }
    }

    private void restoreButtonTexts() {
        String[] originalTexts = {"Dashboard", "Students", "Add Students",
                "Courses", "Library", "Marks", "Fees"};
        int idx = 0;
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setText(originalTexts[idx++]);
                btn.setToolTipText(null);
            }
        }
    }

    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Arial", Font.BOLD, 18));
        cardTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        JLabel cardValue = new JLabel(value);
        cardValue.setFont(new Font("Arial", Font.BOLD, 30));
        cardValue.setForeground(color);
        cardValue.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 10));
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(cardValue, BorderLayout.CENTER);
        return card;
    }

    public static void main(String[] args) {
        new Dashboard();
    }
}