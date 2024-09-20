import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

public class WeatherContentServer {
    // Lamport Clock for time synchronization
    private static LamportClock lamportClock = new LamportClock();

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: WeatherContentServer <server-address:port> <input-file>");
            return;
        }

        try {
            String serverAddress = args[0];
            String inputFile = args[1];

            // Parse server address and port
            String[] addressParts = serverAddress.split(":");
            String serverName = addressParts[0];
            int port = Integer.parseInt(addressParts[1]);

            while (true) {
                // Parse the input file and convert it to JSON format
                try {
                    JSONObject jsonData = parseInputFile(inputFile);

                    // Ensure that the "id" field is present
                    if (!jsonData.has("id")) {
                        System.out.println("Error: Entry must contain an 'id' field.");
                        return;
                    }

                    // Add Lamport clock to the JSON data
                    lamportClock.increment();  // Increment clock before sending
                    jsonData.put("lamport_clock", lamportClock.getClock());  // Add clock value to the JSON data

                    // Connect to the AggregationServer and send the data
                    try (Socket socket = new Socket(serverName, port);
                         PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        out.println(jsonData.toString());

                        // Read the response from the server
                        String response = in.readLine();
                        if (response != null) {
                            JSONObject jsonResponse = new JSONObject(response);
                            int receivedClock = jsonResponse.getInt("lamport_clock");  // Read Lamport clock from server response

                            // Update Lamport clock based on server's clock
                            lamportClock.update(receivedClock);

                            System.out.println("Response from server: " + jsonResponse.toString());
                        }
                    } catch (UnknownHostException e) {
                        System.err.println("Unknown host: " + serverName);
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println("I/O error: " + e.getMessage());
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    System.err.println("Error parsing input file: " + e.getMessage());
                    e.printStackTrace();
                }

                // Sleep for 30 seconds before sending the next data
                Thread.sleep(30000);  // Adjust this interval as needed
            }

        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Parse the input file and convert it to a JSON object
    private static JSONObject parseInputFile(String inputFile) throws Exception {
        JSONObject jsonObject = new JSONObject();
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":", 2); // Split on first colon
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                jsonObject.put(key, value);
            }
        }
        reader.close();
        return jsonObject;
    }
}