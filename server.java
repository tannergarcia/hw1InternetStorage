import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java server <port number>");
            return;
        }

        int port = Integer.parseInt(args[0]); // Port number to listen on

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // Server is always listening for new connections
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new ClientHandler(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        // Initialize structures to read client input and write server output
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {
            writer.println("Hello!");
            String text;

            // Loop to always check the input message from client
            while ((text = reader.readLine()) != null) {
                System.out.println("Received: " + text);

                // Alpha characters and spaces are allowed
                if (!text.matches("[A-Za-z ]+")) {
                    writer.println("Invalid input: only letters are allowed. Please try again.");
                    System.out.println("Received non alphabet input");
                    continue;
                }

                // Disconnect if "bye" was sent by client
                if (text.equalsIgnoreCase("bye")) {
                    writer.println("disconnected");
                    break;
                }

                // Send back capitalized input if valid
                String capitalizedText = text.toUpperCase();
                writer.println(capitalizedText);
            }


        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();

        // Always closes the connection
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}