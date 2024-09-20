import java.io.*;
import java.nio.file.*;

public class FileHandler {
    private static final String DATA_FILE = "weather_data.dat";
    private static final String TEMP_FILE = "weather_data.tmp";

    public synchronized void saveData(String data) throws IOException {
        // Write data to temporary file first
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE))) {
            writer.write(data);
        }

        // Once written, rename the temporary file to the actual data file
        Files.move(Paths.get(TEMP_FILE), Paths.get(DATA_FILE), StandardCopyOption.REPLACE_EXISTING);
    }

    public synchronized String loadData() throws IOException {
        File dataFile = new File(DATA_FILE);
        if (dataFile.exists()) {
            return new String(Files.readAllBytes(dataFile.toPath()));
        } else {
            return "";
        }
    }

    public synchronized void recoverFromCrash() throws IOException {
        File tempFile = new File(TEMP_FILE);
        if (tempFile.exists()) {
            System.out.println("Recovery needed. Applying temporary data.");
            Files.move(tempFile.toPath(), Paths.get(DATA_FILE), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}