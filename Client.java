import java.io.*;
import java.net.*;
import java.util.Scanner;

class ThreadHandler extends Thread {
    private String address;
    private int port;
    private String input;
    private int counter;
    private long time;

    public ThreadHandler(String address, int port, String input, int counter) {
        this.address = address;
        this.port = port;
        this.input = input;
        this.counter = counter;
    }

    public long getTime() {
        return time;
    }

    public void run() {
        try {
            long starttime = System.nanoTime();

            Socket socket = new Socket(address, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(input);
            String line;
            //String output = in.readLine();



            //System.out.println(output);
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            long endtime = System.nanoTime();
            time = (endtime - starttime)/1000000;
            System.out.println("Turn-around time for client request number " + counter + ": " + (endtime - starttime) / 1000000 + "ms\n");

            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

public class Client {

    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Network Address:");
        String address = sc.nextLine();

        System.out.println("Enter Port:");
        int port = sc.nextInt();
        sc.nextLine();

        while (true) {

            System.out.println("Please Enter Number Corresponding to Desired Command:");
            System.out.println("1 - Date and Time");
            System.out.println("2 - Uptime");
            System.out.println("3 - Memory Use");
            System.out.println("4 - Netstat");
            System.out.println("5 - Current Users");
            System.out.println("6 - Running Process");
            System.out.println("7 - Exit Program");

            String input = sc.nextLine();

            if (input.equals("7")) {
                System.out.println("Exiting Program");
                System.exit(0);
                break;
            }

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
            sc.nextLine();

            ThreadHandler[] threads = new ThreadHandler[requests];



            for (int i = 0; i < requests; i++) {
                threads[i] = new ThreadHandler(address, port, input, i + 1);
                threads[i].start();
            }

            long totalTime = 0;

            for (int i = 0; i < requests; i++) {
                try {
                    threads[i].join();
                    totalTime += threads[i].getTime();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

            }



            System.out.println("Total turn-around time for client requests: " + totalTime +"ms");
            System.out.println("Average turn-around time for client requsets: " + (totalTime/requests) +"ms\n");

        }
        sc.close();
    }
}

