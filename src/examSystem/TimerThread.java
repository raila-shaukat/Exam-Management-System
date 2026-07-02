package examSystem;

import javax.swing.*;

public class TimerThread extends Thread {

    JLabel label;
    int time;
    boolean examSubmitted = false;
    JFrame frame;

    public TimerThread(JLabel label, int time, JFrame frame) {
        this.label = label;
        this.time = time;
        this.frame = frame;
    }

    public void stopTimer() {
        examSubmitted = true;
    }

    public void run() {
        try {
            while (time >= 0 && !examSubmitted) {
                label.setText("Time Left: " + time + " sec");
                Thread.sleep(1000);
                time--;
            }
            if (!examSubmitted) {
                JOptionPane.showMessageDialog(frame, "Time Over!");
                frame.dispose();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}