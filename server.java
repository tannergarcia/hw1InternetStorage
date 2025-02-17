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
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {
            writer.println("Hello!");
            String text;
            while ((text = reader.readLine()) != null) {
                System.out.println("Received: " + text);
                if (!text.matches("[A-Za-z ]+")) {
                    writer.println("Invalid input: only letters are allowed. Please try again.");
                    System.out.println("Received non alphabet input");
                    continue;
                }
                if (text.equalsIgnoreCase("bye")) {
                    writer.println("disconnected");
                    break;
                }
                String capitalizedText = text.toUpperCase();
                writer.println(capitalizedText);
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}