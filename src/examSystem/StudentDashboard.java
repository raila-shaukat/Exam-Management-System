package examSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;

public class StudentDashboard {

    public static void show(String studentName) {

        JFrame frame = new JFrame("Student Dashboard - " + studentName);
        frame.setSize(500, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 245, 255));

        JLabel lblWelcome = new JLabel("Welcome, " + studentName + " (Student)");
        lblWelcome.setBounds(110, 20, 300, 30);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 15));
        lblWelcome.setForeground(new Color(50, 50, 180));
        panel.add(lblWelcome);

        JButton btnViewExams = new JButton("View Assigned Exams");
        btnViewExams.setBounds(150, 80, 200, 50);
        btnViewExams.setBackground(new Color(30, 100, 200));
        btnViewExams.setForeground(Color.WHITE);
        btnViewExams.setFocusPainted(false);
        panel.add(btnViewExams);

        JButton btnAttempt = new JButton("Attempt Exam");
        btnAttempt.setBounds(150, 160, 200, 50);
        btnAttempt.setBackground(new Color(20, 150, 50));
        btnAttempt.setForeground(Color.WHITE);
        btnAttempt.setFocusPainted(false);
        panel.add(btnAttempt);

        JButton btnMyResults = new JButton("View My Results");
        btnMyResults.setBounds(150, 240, 200, 50);
        btnMyResults.setBackground(new Color(130, 30, 150));
        btnMyResults.setForeground(Color.WHITE);
        btnMyResults.setFocusPainted(false);
        panel.add(btnMyResults);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(150, 320, 200, 50);
        btnLogout.setBackground(new Color(200, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        panel.add(btnLogout);

        frame.add(panel);
        frame.setVisible(true);

        // =====================
        // VIEW ASSIGNED EXAMS
        // =====================
        btnViewExams.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JDialog dialog = new JDialog(frame, "Available Exams", true);
                dialog.setSize(520, 350);
                dialog.setLayout(new BorderLayout());
                dialog.setLocationRelativeTo(frame);

                String[] columns = {"ID", "Title", "Subject", "Duration", "Total Marks", "Type"};
                javax.swing.table.DefaultTableModel model =
                    new javax.swing.table.DefaultTableModel(columns, 0);

                try {
                    Connection con = DBConnection.getConnection();
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(
                        "SELECT exam_id, title, subject, duration, total_marks, exam_type FROM exams");
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("exam_id"),
                            rs.getString("title"),
                            rs.getString("subject"),
                            rs.getInt("duration") + " mins",
                            rs.getInt("total_marks"),
                            rs.getString("exam_type")
                        });
                    }
                    con.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Exams didn't load yet: " + ex.getMessage());
                    return;
                }

                JTable table = new JTable(model);
                table.setFont(new Font("Arial", Font.PLAIN, 13));
                table.setRowHeight(28);
                table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
                table.setEnabled(false);
                dialog.add(new JScrollPane(table), BorderLayout.CENTER);

                JLabel lbl = new JLabel("  Total exams: " + model.getRowCount());
                lbl.setFont(new Font("Arial", Font.PLAIN, 12));
                dialog.add(lbl, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        });

        // =====================
        // ATTEMPT EXAM
        // =====================
        btnAttempt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JDialog selectDialog = new JDialog(frame, "Select Exam", true);
                selectDialog.setSize(400, 180);
                selectDialog.setLayout(null);
                selectDialog.setLocationRelativeTo(frame);

                JLabel lbl = new JLabel("Select Exam:");
                lbl.setBounds(30, 25, 150, 25);
                selectDialog.add(lbl);

                JComboBox<String> cmbExam = new JComboBox<>();
                cmbExam.setBounds(30, 60, 330, 28);
                selectDialog.add(cmbExam);

                HashMap<String, Integer> examMap  = new HashMap<>();
                HashMap<String, Integer> timerMap = new HashMap<>();

                try {
                    Connection con = DBConnection.getConnection();
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(
                        "SELECT exam_id, title, subject, duration FROM exams");
                    while (rs.next()) {
                        String label = rs.getInt("exam_id") + " - "
                            + rs.getString("title")
                            + " (" + rs.getInt("duration") + " mins)";
                        cmbExam.addItem(label);
                        examMap.put(label, rs.getInt("exam_id"));
                        timerMap.put(label, rs.getInt("duration") * 60);
                    }
                    con.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                    return;
                }

                if (cmbExam.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(frame, "There is no Exam Available!");
                    selectDialog.dispose();
                    return;
                }

                JButton btnStart = new JButton("Start Exam");
                btnStart.setBounds(130, 110, 130, 35);
                btnStart.setBackground(new Color(20, 150, 50));
                btnStart.setForeground(Color.WHITE);
                btnStart.setFocusPainted(false);
                selectDialog.add(btnStart);

                btnStart.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String selected    = cmbExam.getSelectedItem().toString();
                        int examId         = examMap.get(selected);
                        int timerSeconds   = timerMap.get(selected);
                        String subjectName = selected.split(" - ")[1].split(" \\(")[0];
                        selectDialog.dispose();
                        startExam(frame, studentName, examId, subjectName, timerSeconds);
                    }
                });

                selectDialog.setVisible(true);
            }
        });

        // =====================
        // VIEW MY RESULTS
        // =====================
        btnMyResults.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JDialog dialog = new JDialog(frame, "My Results", true);
                dialog.setSize(520, 350);
                dialog.setLayout(new BorderLayout());
                dialog.setLocationRelativeTo(frame);

                String[] columns = {"Subject", "Marks", "Percentage", "Date"};
                javax.swing.table.DefaultTableModel model =
                    new javax.swing.table.DefaultTableModel(columns, 0);

                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                        "SELECT e.subject, r.marks, r.percentage, r.attempt_date " +
                        "FROM results r " +
                        "JOIN exams e ON r.exam_id = e.exam_id " +
                        "WHERE r.student_name = ? ORDER BY r.attempt_date DESC");
                    ps.setString(1, studentName);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        model.addRow(new Object[]{
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

                JLabel lbl = new JLabel("  Total attempts: " + model.getRowCount());
                lbl.setFont(new Font("Arial", Font.PLAIN, 12));
                dialog.add(lbl, BorderLayout.SOUTH);
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

    // =====================
    // START EXAM
    // =====================
    private static void startExam(JFrame parentFrame, String studentName,
                                   int examId, String subjectName, int timerSeconds) {

        java.util.ArrayList<String[]> questions = new java.util.ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM questions WHERE exam_id = ?");
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] q = {
                    rs.getString("question"),
                    rs.getString("option1"),
                    rs.getString("option2"),
                    rs.getString("option3"),
                    rs.getString("option4"),
                    rs.getString("correctAnswer"),
                    String.valueOf(rs.getInt("qid"))
                };
                questions.add(q);
            }
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                "Questions didn't load yet: " + ex.getMessage());
            return;
        }

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                "There are no questions in the exam!");
            return;
        }

        JFrame examFrame = new JFrame("Exam - " + subjectName);
        examFrame.setSize(630, 490);
        examFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        examFrame.setLocationRelativeTo(null);
        examFrame.setLayout(null);
        examFrame.getContentPane().setBackground(Color.WHITE);

        int[] currentIndex    = {0};
        int[] selectedAnswers = new int[questions.size()];
        java.util.Arrays.fill(selectedAnswers, -1);

        JLabel lblQNum = new JLabel("Question 1 of " + questions.size());
        lblQNum.setBounds(20, 15, 250, 25);
        lblQNum.setFont(new Font("Arial", Font.BOLD, 13));
        examFrame.add(lblQNum);

        JLabel lblTimer = new JLabel("Time Left: " + timerSeconds + " sec");
        lblTimer.setBounds(430, 15, 180, 25);
        lblTimer.setFont(new Font("Arial", Font.BOLD, 14));
        lblTimer.setForeground(new Color(200, 50, 50));
        examFrame.add(lblTimer);

        JSeparator sep = new JSeparator();
        sep.setBounds(10, 45, 600, 2);
        examFrame.add(sep);

        JLabel lblQuestion = new JLabel();
        lblQuestion.setBounds(20, 55, 590, 60);
        lblQuestion.setFont(new Font("Arial", Font.PLAIN, 13));
        examFrame.add(lblQuestion);

        JRadioButton opt1 = new JRadioButton();
        JRadioButton opt2 = new JRadioButton();
        JRadioButton opt3 = new JRadioButton();
        JRadioButton opt4 = new JRadioButton();

        ButtonGroup bg = new ButtonGroup();
        bg.add(opt1); bg.add(opt2); bg.add(opt3); bg.add(opt4);

        opt1.setBounds(60, 130, 500, 30); opt1.setBackground(Color.WHITE);
        opt2.setBounds(60, 175, 500, 30); opt2.setBackground(Color.WHITE);
        opt3.setBounds(60, 220, 500, 30); opt3.setBackground(Color.WHITE);
        opt4.setBounds(60, 265, 500, 30); opt4.setBackground(Color.WHITE);

        opt1.setFont(new Font("Arial", Font.PLAIN, 13));
        opt2.setFont(new Font("Arial", Font.PLAIN, 13));
        opt3.setFont(new Font("Arial", Font.PLAIN, 13));
        opt4.setFont(new Font("Arial", Font.PLAIN, 13));

        examFrame.add(opt1); examFrame.add(opt2);
        examFrame.add(opt3); examFrame.add(opt4);

        JSeparator sep2 = new JSeparator();
        sep2.setBounds(10, 320, 600, 2);
        examFrame.add(sep2);

        JButton btnPrev = new JButton("< Previous");
        btnPrev.setBounds(50, 345, 130, 38);
        btnPrev.setEnabled(false);
        examFrame.add(btnPrev);

        JButton btnNext = new JButton("Next >");
        btnNext.setBounds(240, 345, 130, 38);
        btnNext.setBackground(new Color(30, 100, 200));
        btnNext.setForeground(Color.WHITE);
        btnNext.setFocusPainted(false);
        examFrame.add(btnNext);

        JButton btnSubmit = new JButton("Submit Exam");
        btnSubmit.setBounds(430, 345, 155, 38);
        btnSubmit.setBackground(new Color(20, 140, 60));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        examFrame.add(btnSubmit);

        examFrame.setVisible(true);

        // TimerThread start 
        TimerThread timerThread = new TimerThread(lblTimer, timerSeconds, examFrame);
        timerThread.start();

        // Question load 
        Runnable loadQuestion = new Runnable() {
            public void run() {
                int i = currentIndex[0];
                String[] q = questions.get(i);
                lblQNum.setText("Question " + (i + 1) + " of " + questions.size());
                lblQuestion.setText("<html>" + q[0] + "</html>");
                opt1.setText("A)  " + q[1]);
                opt2.setText("B)  " + q[2]);
                opt3.setText("C)  " + q[3]);
                opt4.setText("D)  " + q[4]);
                bg.clearSelection();

                if (selectedAnswers[i] == 1)      opt1.setSelected(true);
                else if (selectedAnswers[i] == 2) opt2.setSelected(true);
                else if (selectedAnswers[i] == 3) opt3.setSelected(true);
                else if (selectedAnswers[i] == 4) opt4.setSelected(true);

                btnPrev.setEnabled(i > 0);
                btnNext.setEnabled(i < questions.size() - 1);
            }
        };
        loadQuestion.run();

        // Answer save
        ActionListener saveAnswer = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = currentIndex[0];
                if (opt1.isSelected())      selectedAnswers[i] = 1;
                else if (opt2.isSelected()) selectedAnswers[i] = 2;
                else if (opt3.isSelected()) selectedAnswers[i] = 3;
                else if (opt4.isSelected()) selectedAnswers[i] = 4;
            }
        };
        opt1.addActionListener(saveAnswer);
        opt2.addActionListener(saveAnswer);
        opt3.addActionListener(saveAnswer);
        opt4.addActionListener(saveAnswer);

        // Next button
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = currentIndex[0];
                if (opt1.isSelected())      selectedAnswers[i] = 1;
                else if (opt2.isSelected()) selectedAnswers[i] = 2;
                else if (opt3.isSelected()) selectedAnswers[i] = 3;
                else if (opt4.isSelected()) selectedAnswers[i] = 4;
                currentIndex[0]++;
                loadQuestion.run();
            }
        });

        // Previous button
        btnPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = currentIndex[0];
                if (opt1.isSelected())      selectedAnswers[i] = 1;
                else if (opt2.isSelected()) selectedAnswers[i] = 2;
                else if (opt3.isSelected()) selectedAnswers[i] = 3;
                else if (opt4.isSelected()) selectedAnswers[i] = 4;
                currentIndex[0]--;
                loadQuestion.run();
            }
        });

        // Submit button
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(examFrame,
                    "Do you want to submit your exam?",
                    "Confirm Submit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    timerThread.stopTimer();
                    submitExam(examFrame, studentName, examId,
                        subjectName, questions, selectedAnswers);
                }
            }
        });
    }

    // =====================
    // SUBMIT + RESULT WINDOW
    // =====================
    private static void submitExam(JFrame examFrame, String studentName,
            int examId, String subjectName,
            java.util.ArrayList<String[]> questions, int[] selectedAnswers) {

        // Score calculate 
        int score = 0;
        int total = questions.size();
        for (int i = 0; i < total; i++) {
            String correctLetter = questions.get(i)[5];
            String studentAnswer = "";
            if (selectedAnswers[i] == 1)      studentAnswer = "A";
            else if (selectedAnswers[i] == 2) studentAnswer = "B";
            else if (selectedAnswers[i] == 3) studentAnswer = "C";
            else if (selectedAnswers[i] == 4) studentAnswer = "D";
            if (studentAnswer.equals(correctLetter)) score++;
        }

        double percentage = ((double) score / total) * 100;

        String grade;
        if      (percentage >= 90) grade = "Excellent! Pass";
        else if (percentage >= 70) grade = "Good! Pass";
        else if (percentage >= 50) grade = "Average! Pass";
        else                       grade = "Fail";

        // Save in DB
        try {
            Connection con = DBConnection.getConnection();
            String date = new java.text.SimpleDateFormat("dd-MM-yyyy")
                .format(new java.util.Date());
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO results (student_name, exam_id, marks, percentage, attempt_date) " +
                "VALUES (?,?,?,?,?)");
            ps.setString(1, studentName);
            ps.setInt(2, examId);
            ps.setInt(3, score);
            ps.setDouble(4, percentage);
            ps.setString(5, date);
            ps.executeUpdate();
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(examFrame,
                "Result didn't save : " + ex.getMessage());
        }

        // Save in file
        FileHandler.saveResultToFile(studentName, subjectName, score, total);

        examFrame.dispose();

        // =====================
        // RESULT WINDOW
        // =====================
        JFrame resultFrame = new JFrame("Exam Result");
        resultFrame.setSize(420, 360);
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.setLayout(null);
        resultFrame.getContentPane().setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("EXAM RESULT", SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, 420, 35);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        resultFrame.add(lblTitle);

        JSeparator line1 = new JSeparator();
        line1.setBounds(30, 62, 360, 2);
        resultFrame.add(line1);

        JLabel l1 = new JLabel("Student Name:");
        l1.setBounds(50, 80, 150, 25);
        l1.setFont(new Font("Arial", Font.PLAIN, 13));
        resultFrame.add(l1);
        JLabel v1 = new JLabel(studentName);
        v1.setBounds(220, 80, 170, 25);
        v1.setFont(new Font("Arial", Font.BOLD, 13));
        v1.setForeground(new Color(30, 80, 200));
        resultFrame.add(v1);

        JLabel l2 = new JLabel("Exam:");
        l2.setBounds(50, 115, 150, 25);
        l2.setFont(new Font("Arial", Font.PLAIN, 13));
        resultFrame.add(l2);
        JLabel v2 = new JLabel(subjectName);
        v2.setBounds(220, 115, 170, 25);
        v2.setFont(new Font("Arial", Font.BOLD, 13));
        resultFrame.add(v2);

        JLabel l3 = new JLabel("Marks Obtained:");
        l3.setBounds(50, 150, 150, 25);
        l3.setFont(new Font("Arial", Font.PLAIN, 13));
        resultFrame.add(l3);
        JLabel v3 = new JLabel(score + " / " + total);
        v3.setBounds(220, 150, 170, 25);
        v3.setFont(new Font("Arial", Font.BOLD, 13));
        resultFrame.add(v3);

        JLabel l4 = new JLabel("Percentage:");
        l4.setBounds(50, 185, 150, 25);
        l4.setFont(new Font("Arial", Font.PLAIN, 13));
        resultFrame.add(l4);
        JLabel v4 = new JLabel(String.format("%.2f%%", percentage));
        v4.setBounds(220, 185, 170, 25);
        v4.setFont(new Font("Arial", Font.BOLD, 13));
        resultFrame.add(v4);

        JLabel v5 = new JLabel(grade, SwingConstants.CENTER);
        v5.setBounds(60, 222, 300, 28);
        v5.setFont(new Font("Arial", Font.BOLD, 14));
        v5.setForeground(percentage >= 50 ? new Color(20, 140, 60) : Color.RED);
        resultFrame.add(v5);

        JSeparator line2 = new JSeparator();
        line2.setBounds(30, 260, 360, 2);
        resultFrame.add(line2);

        JButton btnClose = new JButton("Close");
        btnClose.setBounds(155, 278, 110, 35);
        btnClose.setBackground(new Color(100, 100, 200));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        resultFrame.add(btnClose);

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resultFrame.dispose();
            }
        });

        resultFrame.setVisible(true);
    }
}