package examSystem;

import java.io.*;

public class FileHandler {

    // ================= BINARY FILE SAVE =================
    public static void saveResultToFile(String studentName, String subject,
                                         int score, int total) {

        // Binary file (.dat) — append fix
        try {
            File file = new File("results.dat");
            FileOutputStream fos = new FileOutputStream(file, true);

            // 
            ObjectOutputStream oos;
            if (file.exists() && file.length() > 0) {
                oos = new AppendingObjectOutputStream(fos);
            } else {
                oos = new ObjectOutputStream(fos);
            }

            String data = studentName + "|" + subject + "|" + score + "|" + total;
            oos.writeObject(data);
            oos.close();
            System.out.println("Saved in Binary File: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("Binary save error: " + e);
        }

        // Text file (.txt)
        try {
            FileWriter fw = new FileWriter("results.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Student: " + studentName
                + " | Subject: " + subject
                + " | Score: " + score + "/" + total);
            bw.newLine();
            bw.close();
            System.out.println("Saved in Text file.");
        } catch (Exception e) {
            System.out.println("Text save error: " + e);
        }
    }

    // ================= BINARY FILE READ =================
    public static void readResultsFromFile() {
        try {
            File file = new File("results.dat");
            if (!file.exists()) {
                System.out.println("File doesn't exist.");
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file));
            while (true) {
                try {
                    String data = (String) ois.readObject();
                    System.out.println("Record: " + data);
                } catch (EOFException eof) {
                    break;
                }
            }
            ois.close();
        } catch (Exception e) {
            System.out.println("Binary read error: " + e);
        }
    }

    // ================= APPEND HELPER CLASS =================
    static class AppendingObjectOutputStream extends ObjectOutputStream {
        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }
        @Override
        protected void writeStreamHeader() throws IOException {
            reset(); 
        }
    }
}