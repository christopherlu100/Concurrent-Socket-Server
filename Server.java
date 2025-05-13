import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {

    private static final int PORT = 2200;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Concurrent Server listening on port " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                new ClientHandler(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            System.out.println("Received request: " + request);

            if (request == null || request.isEmpty()) {
                System.out.println("Error: Empty or null request received");
                out.println("Error: Invalid request");
            } else {
                String response = processRequest(request);
                out.println(response);
            }

        } catch (IOException e) {
            System.out.println("Client handler error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Date & Time: " + dateFormat.format(new Date());
    }

    private String getUptime() {
        return executeCommand("uptime -p");
    }

    private String getMemoryUsage() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win")
                ? executeCommand("systeminfo | findstr \"Available Physical Memory\"")
                : executeCommand("free -h");
    }

    private String getNetstat() {
        return executeCommand("netstat -an");
    }

    private String getCurrentUsers() {
        return executeCommand("who");
    }

    private String getRunningProcesses() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? executeCommand("tasklist") : executeCommand("ps aux");
    }

    private String executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString().trim();
        } catch (IOException e) {
            return "Error: Unable to execute command";
        }
    }
}