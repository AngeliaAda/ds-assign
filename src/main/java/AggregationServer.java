import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

public class AggregationServer {
    private static final int DEFAULT_PORT = 4567;
    private static LamportClock lamportClock = new LamportClock();  // Aggregation server's local clock
    private static JSONObject storedData = new JSONObject();  // Store the latest weather data


    public static void main(String[] args) {
        try {
            int port = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Aggregation Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming connections
                new Thread(new ClientHandler(clientSocket)).start(); // Handle each client connection in a new thread
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                String receivedData = in.readLine();
                
                // Assume receivedData is a JSON string (can be PUT or GET)
               
                // Check if it's a GET request
                if (receivedData != null && receivedData.startsWith("GET")) {
                    // Handle GET request: Return the stored weather data
                    System.out.println("Received GET request");
                    lamportClock.increment();  // Increment the clock before sending
                    storedData.put("lamport_clock", lamportClock.getClock());  // Attach the clock to the response
                    out.println(storedData.toString());  // Send the stored data back to the client
                } else {
                    // Handle PUT request: Parse the JSON data
                    System.out.println("Received PUT data: " + receivedData);
                    JSONObject jsonData = new JSONObject(receivedData);
                    int receivedClock = jsonData.getInt("lamport_clock");  // Extract Lamport clock from message

                    // Update the local Lamport clock
                    lamportClock.update(receivedClock);

                    // Store the new data
                    storedData = jsonData;

                    // Respond with 200 OK
                    lamportClock.increment();  // Increment before sending the response
                    JSONObject response = new JSONObject();
                    response.put("status", "200 OK");
                    response.put("lamport_clock", lamportClock.getClock());
                    out.println(response.toString());
                }

                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}