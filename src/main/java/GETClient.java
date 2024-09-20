import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class GETClient {
    private static LamportClock lamportClock = new LamportClock();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: GETClient <server-address:port>");
            return;
        }

        try {
            String serverAddress = args[0];
            String[] addressParts = serverAddress.split(":");
            String serverName = addressParts[0];
            int port = Integer.parseInt(addressParts[1]);

            // Maintain Lamport Clock: Increment before sending request
            lamportClock.increment();

            // Connect to the server
            Socket socket = new Socket(serverName, port);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Send a GET request (in this case just a request for data)
            out.println("GET /weather.json");

            // Read the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String receivedData = in.readLine();
            if (receivedData != null) {
                JSONObject jsonData = new JSONObject(receivedData);
                jsonData.keys().forEachRemaining(key -> {
                    System.out.println(key + ": " + jsonData.get(key));
                });
            }

            // Close the socket
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}