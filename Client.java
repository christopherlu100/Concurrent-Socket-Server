import java.io.*;
import java.net.*;
import java.util.Scanner;

// Threadhandler class extends Thread to handle multiple client requests concurrently
class ThreadHandler extends Thread {
    private String address;    // Server address
    private int port;          // Server port number
    private String input;      // Command input from client
    private int counter;       // Client request counter
    private long time;         // Time taken for the request

    // COntrustor for initializing thread parameters
    public ThreadHandler(String address, int port, String input, int counter) {
        this.address = address;
        this.port = port;
        this.input = input;
        this.counter = counter;
    }

    // Getter to retrieve request time
    public long getTime() {
        return time;
    }

    // Define thread behavior
    public void run() {
        try {
            long starttime = System.nanoTime(); // Start tracking grequest time

            // Establish a socket connection to the server
            Socket socket = new Socket(address, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the user command to the server
            out.println(input);
            String line;

            // Read the server response and print it
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            // Calc the time taken for requests
            long endtime = System.nanoTime();
            time = (endtime - starttime)/1000000;
            System.out.println("Turn-around time for client request number " + counter + ": " + (endtime - starttime) / 1000000 + "ms\n");


            // Close socket connection
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

public class Client {

    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        // User input for network address and port
        System.out.println("Enter Network Address:");
        String address = sc.nextLine();

        System.out.println("Enter Port:");
        int port = sc.nextInt();
        sc.nextLine();

        // Infintite loop to send client requests
        while (true) {
            System.out.println("Please Enter Number Corresponding to Desired Command:");
            System.out.println("1 - Date and Time");
            System.out.println("2 - Uptime");
            System.out.println("3 - Memory Use");
            System.out.println("4 - Netstat");
            System.out.println("5 - Current Users");
            System.out.println("6 - Running Process");
            System.out.println("7 - Exit Program");

            // User command input
            String input = sc.nextLine();

            // Exit if user selects 7
            if (input.equals("7")) {
                System.out.println("Exiting Program");
                System.exit(0);
                break;
            }

            // User input for number of concurrent requesets
            System.out.println("Please Enter Number of Request");
            System.out.println("Options: 1, 5, 10, 15, 20, 25, 100");
            int requests = sc.nextInt();
            switch (requests) {
                case 1:
                case 5:
                case 10:
                case 15:
                case 20:
                case 25:
                case 100:
                    break;
                default:
                    System.out.println("Invalid Number of Request");
                    continue;
            }
            sc.nextLine(); // Clears buffer

            // Creating an array of threds based on number of requests
            ThreadHandler[] threads = new ThreadHandler[requests];

            // Initializing and starting each client requests thread
            for (int i = 0; i < requests; i++) {
                threads[i] = new ThreadHandler(address, port, input, i + 1);
                threads[i].start();
            }

            long totalTime = 0;

            // Wait for each thread to complete and calculate total time
            for (int i = 0; i < requests; i++) {
                try {
                    threads[i].join();
                    totalTime += threads[i].getTime();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

            }

            // Display total and average turn around time for all requests
            System.out.println("Total turn-around time for client requests: " + totalTime +"ms");
            System.out.println("Average turn-around time for client requsets: " + (totalTime/requests) +"ms\n");

        }
        
        sc.close(); // Close scanner
    }
}

