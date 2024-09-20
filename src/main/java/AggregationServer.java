import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

public class AggregationServer {
    private static final int DEFAULT_PORT = 4567;
    private LamportClock lamportClock = new LamportClock();

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
                if (receivedData != null) {
                    JSONObject jsonData = new JSONObject(receivedData);

                    // Handle PUT request
                    if (jsonData.has("id")) {
                        System.out.println("Received PUT data: " + jsonData.toString());
                        // Update Lamport Clock and return acknowledgment
                        out.println("200 OK");  // Send success response back to the client
                    } else {
                        out.println("400 Bad Request");  // Missing 'id' field
                    }
                }
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}