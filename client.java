import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        String serverName = "localhost"; // Server address
        int port = 6789; // Server port

        try {
            // Establish connection to the server
            Socket clientSocket = new Socket(serverName, port);
            System.out.println("Connected to server at " + serverName + " on port " + port);

            // Create input and output streams
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Collect user input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter message: ");
            String message = scanner.nextLine();

            // Send message to server
            outToServer.writeBytes(message + '\n');

            // Read response from server
            String response = inFromServer.readLine();
            System.out.println("Response from server: " + response);

            // Close the connection
            clientSocket.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}