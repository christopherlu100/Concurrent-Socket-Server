import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {

    // Define port number for server
    private static final int PORT = 2200;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Concurrent Server listening on port " + PORT + "...");

            // Continuously listen for incoming client connections
            while (true) {
                // Accept client connections and start a new client handler thread
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                new ClientHandler(clientSocket).start();    // Create a new thread for each client
            }

        } catch (IOException e) {
            // Handle any IOExceptions that may occur
            e.printStackTrace();
        }
    }
}

// ClientHandler class extends Thread to handle individual client requests
class ClientHandler extends Thread {
    private Socket clientSocket;    // Socket for client communication

    // Constructor to initialize the client socket
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (
                // Setting up input and output streams for client communications
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Read client request
            String request = in.readLine();
            System.out.println("Received request: " + request);

            // Validate the request and send appropriate response
            if (request == null || request.isEmpty()) {
                System.out.println("Error: Empty or null request received");
                out.println("Error: Invalid request");
            } else {
                String response = processRequest(request);
                out.println(response); // Send response to client
            }

        } catch (IOException e) {
            System.out.println("Client handler error: " + e.getMessage());
        } finally {
            // Ensure the client connection is closed properly
            try {
                clientSocket.close();
                System.out.println("Client connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to process client requests based on the command received
    private String processRequest(String request) {
        switch (request) {
            case "1": return getDateTime();
            case "2": return getUptime();
            case "3": return getMemoryUsage();
            case "4": return getNetstat();
            case "5": return getCurrentUsers();
            case "6": return getRunningProcesses();
            default: return "Invalid command";
        }
    }

    // Method for current date and time
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Date & Time: " + dateFormat.format(new Date());
    }

    // Method for system uptime
    private String getUptime() {
        return executeCommand("uptime -p");
    }

    // Method for system memory usage
    private String getMemoryUsage() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win")
                ? executeCommand("systeminfo | findstr \"Available Physical Memory\"")
                : executeCommand("free -h");
    }

    // Method for network statistics
    private String getNetstat() {
        return executeCommand("netstat -an");
    }

    // Method for current logged in users
    private String getCurrentUsers() {
        return executeCommand("who");
    }

    // Method for running processes
    private String getRunningProcesses() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? executeCommand("tasklist") : executeCommand("ps aux");
    }

    // Method to execute a system command and return its output
    private String executeCommand(String command) {
        try {
            // Execute the command using the system shell
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            // Read the output and append the result
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            return output.toString().trim();    // Return the final output
            
        } catch (IOException e) {
            return "Error: Unable to execute command";
        }
    }
}
