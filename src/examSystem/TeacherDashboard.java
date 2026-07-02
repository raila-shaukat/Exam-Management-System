package examSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;

public class TeacherDashboard {

    public static void show(String teacherName) {

        JFrame frame = new JFrame("Teacher Dashboard - " + teacherName);
        frame.setSize(500, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 250, 245));

        JLabel lblWelcome = new JLabel("Welcome, " + teacherName + " (Teacher)");
        lblWelcome.setBounds(100, 20, 320, 30);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 15));
        lblWelcome.setForeground(new Color(20, 100, 20));
        panel.add(lblWelcome);

        JButton btnCreate = new JButton("Create Exam");
        btnCreate.setBounds(150, 80, 200, 50);
        btnCreate.setBackground(new Color(30, 100, 200));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        panel.add(btnCreate);

        JButton btnAddQ = new JButton("Add Questions");
        btnAddQ.setBounds(150, 160, 200, 50);
        btnAddQ.setBackground(new Color(20, 140, 60));
        btnAddQ.setForeground(Color.WHITE);
        btnAddQ.setFocusPainted(false);
        panel.add(btnAddQ);

        JButton btnResults = new JButton("View Results");
        btnResults.setBounds(150, 240, 200, 50);
        btnResults.setBackground(new Color(130, 30, 150));
        btnResults.setForeground(Color.WHITE);
        btnResults.setFocusPainted(false);
        panel.add(btnResults);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(150, 320, 200, 50);
        btnLogout.setBackground(new Color(200, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        panel.add(btnLogout);

        frame.add(panel);
        frame.setVisible(true);

        // =====================
        // CREATE EXAM
        // =====================
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JDialog dialog = new JDialog(frame, "Create Exam", true);
                dialog.setSize(430, 360);
                dialog.setLayout(null);
                dialog.setLocationRelativeTo(frame);

                JLabel l1 = new JLabel("Title:");
                l1.setBounds(40, 25, 100, 25);
                dialog.add(l1);
                JTextField txtTitle = new JTextField();
                txtTitle.setBounds(150, 25, 220, 25);
                dialog.add(txtTitle);

                JLabel l2 = new JLabel("Subject:");
                l2.setBounds(40, 65, 100, 25);
                dialog.add(l2);
                JTextField txtSubject = new JTextField();
                txtSubject.setBounds(150, 65, 220, 25);
                dialog.add(txtSubject);

                JLabel l3 = new JLabel("Duration (mins):");
                l3.setBounds(40, 105, 130, 25);
                dialog.add(l3);
                JTextField txtDuration = new JTextField();
                txtDuration.setBounds(180, 105, 190, 25);
                dialog.add(txtDuration);

                JLabel l4 = new JLabel("Total Marks:");
                l4.setBounds(40, 145, 120, 25);
                dialog.add(l4);
                JTextField txtMarks = new JTextField();
                txtMarks.setBounds(180, 145, 190, 25);
                dialog.add(txtMarks);

                JLabel l5 = new JLabel("Exam Type:");
                l5.setBounds(40, 185, 120, 25);
                dialog.add(l5);
                String[] types = {"MCQ", "Short Questions", "Mixed"};
                JComboBox<String> cmbType = new JComboBox<>(types);
                cmbType.setBounds(180, 185, 190, 25);
                dialog.add(cmbType);

                JLabel lblMsg = new JLabel("");
                lblMsg.setBounds(40, 220, 350, 25);
                lblMsg.setForeground(Color.RED);
                dialog.add(lblMsg);

                JButton btnSave = new JButton("Save Exam");
                btnSave.setBounds(140, 258, 150, 35);
                btnSave.setBackground(new Color(30, 100, 200));
                btnSave.setForeground(Color.WHITE);
                btnSave.setFocusPainted(false);
                dialog.add(btnSave);

                btnSave.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String title   = txtTitle.getText().trim();
                        String subject = txtSubject.getText().trim();
                        String durStr  = txtDuration.getText().trim();
                        String marksStr = txtMarks.getText().trim();
                        String type    = cmbType.getSelectedItem().toString();

                        if (title.isEmpty() || subject.isEmpty()
                                || durStr.isEmpty() || marksStr.isEmpty()) {
                            lblMsg.setText("Fill all the fields!");
                            return;
                        }

                        try {
                            int duration = Integer.parseInt(durStr);
                            int marks    = Integer.parseInt(marksStr);

                            Connection con = DBConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO exams (title, subject, duration, total_marks, exam_type, created_by) " +
                                "VALUES (?,?,?,?,?,?)");
                            ps.setString(1, title);
                            ps.setString(2, subject);
                            ps.setInt(3, duration);
                            ps.setInt(4, marks);
                            ps.setString(5, type);
                            ps.setString(6, teacherName);
                            ps.executeUpdate();
                            con.close();

                            JOptionPane.showMessageDialog(dialog, "Exam saved!");
                            dialog.dispose();

                        } catch (NumberFormatException ex) {
                            lblMsg.setText("Duration and Marks should be number!");
                        } catch (Exception ex) {
                            lblMsg.setText("DB Error: " + ex.getMessage());
                        }
                    }
                });

                dialog.setVisible(true);
            }
        });

        // =====================
        // ADD QUESTIONS
        // =====================
        btnAddQ.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JDialog dialog = new JDialog(frame, "Add Question", true);
                dialog.setSize(490, 480);
                dialog.setLayout(null);
                dialog.setLocationRelativeTo(frame);

                JLabel l0 = new JLabel("Select Exam:");
                l0.setBounds(30, 20, 110, 25);
                dialog.add(l0);

                JComboBox<String> cmbExam = new JComboBox<>();
                cmbExam.setBounds(150, 20, 290, 25);
                dialog.add(cmbExam);

                HashMap<String, Integer> examMap = new HashMap<>();
                try {
                    Connection con = DBConnection.getConnection();
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(
                        "SELECT exam_id, title FROM exams WHERE created_by='" + teacherName + "'");
                    while (rs.next()) {
                        String label = rs.getInt("exam_id") + " - " + rs.getString("title");
                        cmbExam.addItem(label);
                        examMap.put(label, rs.getInt("exam_id"));
                    }
                    con.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Exams didn't load yet: " + ex.getMessage());
                    return;
                }

                if (cmbExam.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(frame, "First Create Exam!");
                    dialog.dispose();
                    return;
                }

                JLabel l1 = new JLabel("Question:");
                l1.setBounds(30, 65, 100, 25);
                dialog.add(l1);
                JTextField txtQ = new JTextField();
                txtQ.setBounds(130, 65, 320, 25);
                dialog.add(txtQ);

                JLabel lA = new JLabel("Option 1:");
                lA.setBounds(30, 105, 90, 25);
                dialog.add(lA);
                JTextField txtA = new JTextField();
                txtA.setBounds(130, 105, 320, 25);
                dialog.add(txtA);

                JLabel lB = new JLabel("Option 2:");
                lB.setBounds(30, 145, 90, 25);
                dialog.add(lB);
                JTextField txtB = new JTextField();
                txtB.setBounds(130, 145, 320, 25);
                dialog.add(txtB);

                JLabel lC = new JLabel("Option 3:");
                lC.setBounds(30, 185, 90, 25);
                dialog.add(lC);
                JTextField txtC = new JTextField();
                txtC.setBounds(130, 185, 320, 25);
                dialog.add(txtC);

                JLabel lD = new JLabel("Option 4:");
                lD.setBounds(30, 225, 90, 25);
                dialog.add(lD);
                JTextField txtD = new JTextField();
                txtD.setBounds(130, 225, 320, 25);
                dialog.add(txtD);

                JLabel l6 = new JLabel("Correct (1-4):");
                l6.setBounds(30, 265, 110, 25);
                dialog.add(l6);
                JTextField txtCorrect = new JTextField();
                txtCorrect.setBounds(150, 265, 80, 25);
                dialog.add(txtCorrect);

                JLabel lblMsg = new JLabel("");
                lblMsg.setBounds(30, 298, 400, 25);
                lblMsg.setForeground(Color.RED);
                dialog.add(lblMsg);

                JButton btnSave = new JButton("Save Question");
                btnSave.setBounds(160, 335, 160, 35);
                btnSave.setBackground(new Color(20, 140, 60));
                btnSave.setForeground(Color.WHITE);
                btnSave.setFocusPainted(false);
                dialog.add(btnSave);

                btnSave.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String qText   = txtQ.getText().trim();
                        String opt1    = txtA.getText().trim();
                        String opt2    = txtB.getText().trim();
                        String opt3    = txtC.getText().trim();
                        String opt4    = txtD.getText().trim();
                        String corrStr = txtCorrect.getText().trim();

                        if (qText.isEmpty() || opt1.isEmpty() || opt2.isEmpty()
                                || opt3.isEmpty() || opt4.isEmpty() || corrStr.isEmpty()) {
                            lblMsg.setText("Fill all the fields!");
                            return;
                        }

                        try {
                            int correct = Integer.parseInt(corrStr);
                            if (correct < 1 || correct > 4) {
                                lblMsg.setText("Correct Option should be between 1 and 4!");
                                return;
                            }

                            // Convert int into A/B/C/D
                            String correctLetter = "";
                            if (correct == 1) correctLetter = "A";
                            else if (correct == 2) correctLetter = "B";
                            else if (correct == 3) correctLetter = "C";
                            else if (correct == 4) correctLetter = "D";

                            String selected = cmbExam.getSelectedItem().toString();
                            int examId = examMap.get(selected);

                            Connection con = DBConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO questions (exam_id, question, optionA, optionB, optionC, optionD, correctAnswer) " +
                                "VALUES (?,?,?,?,?,?,?)");
                            ps.setInt(1, examId);
                            ps.setString(2, qText);
                            ps.setString(3, opt1);
                            ps.setString(4, opt2);
                            ps.setString(5, opt3);
                            ps.setString(6, opt4);
                            ps.setString(7, correctLetter);
                            ps.executeUpdate();
                            con.close();

                            JOptionPane.showMessageDialog(dialog, "Question saved!");
                            txtQ.setText("");
                            txtA.setText(""); txtB.setText("");
                            txtC.setText(""); txtD.setText("");
                            txtCorrect.setText("");
                            lblMsg.setText("");

                        } catch (NumberFormatException ex) {
                            lblMsg.setText("Correct option should be number!");
                        } catch (Exception ex) {
                            lblMsg.setText("DB Error: " + ex.getMessage());
                        }
                    }
                });

                dialog.setVisible(true);
            }
        });

        // =====================
        // VIEW RESULTS
        // =====================
        btnResults.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JDialog dialog = new JDialog(frame, "Student Results", true);
                dialog.setSize(600, 380);
                dialog.setLayout(new BorderLayout());
                dialog.setLocationRelativeTo(frame);

                String[] columns = {"Student", "Subject", "Marks", "Percentage", "Date"};
                javax.swing.table.DefaultTableModel model =
                    new javax.swing.table.DefaultTableModel(columns, 0);

                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                        "SELECT r.student_name, e.subject, r.marks, r.percentage, r.attempt_date " +
                        "FROM results r " +
                        "JOIN exams e ON r.exam_id = e.exam_id " +
                        "WHERE e.created_by = ? ORDER BY r.attempt_date DESC");
                    ps.setString(1, teacherName);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("student_name"),
                            rs.getString("subject"),
                            rs.getInt("marks"),
                            String.format("%.2f%%", rs.getDouble("percentage")),
                            rs.getString("attempt_date")
                        });
                    }
                    con.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Results didn't load yet: " + ex.getMessage());
                    return;
                }

                JTable table = new JTable(model);
                table.setFont(new Font("Arial", Font.PLAIN, 13));
                table.setRowHeight(28);
                table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
                table.setEnabled(false);
                dialog.add(new JScrollPane(table), BorderLayout.CENTER);

                JLabel lblCount = new JLabel("  Total results: " + model.getRowCount());
                lblCount.setFont(new Font("Arial", Font.PLAIN, 12));
                dialog.add(lblCount, BorderLayout.SOUTH);

                dialog.setVisible(true);
            }
        });

        // =====================
        // LOGOUT
        // =====================
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginSignupWindow();
            }
        });
    }
}