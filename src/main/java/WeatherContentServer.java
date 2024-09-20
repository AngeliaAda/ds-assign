import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class WeatherContentServer {
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

            // Parse the input file and convert it to JSON format
            JSONObject jsonData = parseInputFile(inputFile);

            // Check if "id" is present
            if (!jsonData.has("id")) {
                System.out.println("Error: Entry must contain an 'id' field.");
                return;
            }

            // Maintain Lamport Clock: Increment before sending the request
            lamportClock.increment();

            // Connect to the Aggregation Server using a socket
            Socket socket = new Socket(serverName, port);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Send the JSON data
            out.println(jsonData.toString());

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            System.out.println("Response from server: " + response);

            // Close the socket
            socket.close();

        } catch (Exception e) {
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