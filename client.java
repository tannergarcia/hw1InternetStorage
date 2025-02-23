import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

public class client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java client <    server> <port>");
            return;
        }
        String serverName = args[0]; // Server address
        int port = Integer.parseInt(args[1]); // Server port
        // Vector to store round trip times
        Vector<Double> roundTripTimes = new Vector<>();

        try {
            // Establish connection to the server
            Socket clientSocket = new Socket(serverName, port);
            System.out.println("Connected to server at " + serverName + " on port " + port);

            // Create input and output streams
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Collect user input
            String hellomsg = inFromServer.readLine();
            System.out.println("Received from server " + hellomsg);

            Scanner scanner = new Scanner(System.in);
            try {
                while (true) {
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();

                    // get start time
                    long startTime = System.currentTimeMillis();

                    // send msg
                    outToServer.writeBytes(message + '\n');

                    // read response
                    String response = inFromServer.readLine();

                    // record end time
                    long endTime = System.currentTimeMillis();

                    // calculate RTT
                    long roundTripTime = endTime - startTime;

                    if (response.equalsIgnoreCase("disconnected")) {
                        System.out.println("exit");
                        break;
                    }
                    System.out.println("Response from server: " + response);
                    System.out.println("Round trip time: " + roundTripTime + " ms");
                    roundTripTimes.add((double) roundTripTime);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                clientSocket.close();
                scanner.close();

                if (!roundTripTimes.isEmpty()) {
                    // print all RTTs for testing
                    System.out.println("RTTs: " + roundTripTimes);

                    // get min, max, avg
                    double min = -1;
                    double max = 0;
                    double total = 0;
                    for (int i = 0; i < roundTripTimes.size(); i++) {
                        total += roundTripTimes.get(i);
                        if (roundTripTimes.get(i) < min || min == -1) {
                            min = roundTripTimes.get(i);
                        }
                        if (roundTripTimes.get(i) > max) {
                            max = roundTripTimes.get(i);
                        }
                    }

                    double mean = total / roundTripTimes.size();

                    // calculate variance
                    double variance = 0;
                    for (int i = 0; i < roundTripTimes.size(); i++) {
                        variance += Math.pow(roundTripTimes.get(i) - mean, 2);
                    }
                    variance /= roundTripTimes.size();

                    // standard deviation is just sqrt of variance
                    double standardDeviation = Math.sqrt(variance);


                    System.out.println("Minimum RTT: " + min + " ms");
                    System.out.println("Maximum RTT: " + max + " ms");
                    System.out.println("Mean RTT: " + mean + " ms");
                    System.out.println("Variance of RTT: " + variance + " ms");
                    System.out.println("Standard Deviation of RTT: " + standardDeviation + " ms");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}