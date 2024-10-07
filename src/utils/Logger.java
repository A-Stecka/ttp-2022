package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger {

////LADOWANIE DO PLIKU//////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadIntoFile(String filename, String[] data) {
        File csv = new File(filename);
        try {
            if (csv.createNewFile()){
                try (PrintWriter pw = new PrintWriter(csv)) {
                    for (String line : data)
                        pw.println(line);
                }
            } else {
                try (FileWriter pw = new FileWriter(csv, true)) {
                    for (String line : data)
                        pw.append(line);
                    pw.append("\n");
                    pw.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nDID YOU CLOSE THE FILE??? YEA I DIDN'T THINK SO.\n\n\n\n");
        }
    }

    public void loadIntoFile(String filename, double[] bests, double[] worsts, double[] avgs) {
        int noOfGenerations = bests.length;
        String[] data = new String[noOfGenerations];
        for (int i = 0; i < noOfGenerations; i++)
            data[i] = i + ";" + bests[i] + ";" + worsts[i] + ";" + avgs[i] + ";";
        loadIntoFile(filename, data);
    }

    public void loadIntoFile(String filename, double[] currents, double[] bests, double[] worsts, double[] avgs) {
        int noOfIterations = bests.length;
        String[] data = new String[noOfIterations];
        for (int i = 0; i < noOfIterations; i++)
            data[i] = i + ";" + currents[i] + ";" + bests[i] + ";" + worsts[i] + ";" + avgs[i] + ";";
        loadIntoFile(filename, data);
    }

    public void loadIntoFile(String filename, String params, double best, double worst, double avg, double std, double time) {
        File csv = new File(filename);
        try {
            if (csv.createNewFile()) {
                try (PrintWriter pw = new PrintWriter(csv)) {
                    pw.println(best + ";" + worst + ";" + avg + ";" + std + ";" + time + ";" + params + ";");
                }
            } else {
                try (FileWriter pw = new FileWriter(csv, true)) {
                    pw.append(String.valueOf(best));
                    pw.append(";");
                    pw.append(String.valueOf(worst));
                    pw.append(";");
                    pw.append(String.valueOf(avg));
                    pw.append(";");
                    pw.append(String.valueOf(std));
                    pw.append(";");
                    pw.append(String.valueOf(time));
                    pw.append(";");
                    pw.append(params);
                    pw.append(";");
                    pw.append("\n");
                    pw.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nDID YOU CLOSE THE FILE??? YEA I DIDN'T THINK SO.\n\n\n\n");
        }
    }

}
