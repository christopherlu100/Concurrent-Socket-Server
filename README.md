Concurrent Socket Server

Project Description

This is a Java-based concurrent socket server application designed to demonstrate client-server communication using multi-threading. The server can handle multiple clients simultaneously, showcasing the principles of networking and multi-threaded programming in Java.


Features

Supports multiple client connections concurrently.

Utilizes Java Socket and ServerSocket classes.

Multi-threaded server architecture.

Simple client-server communication with text messages.


How It Works

The Server class listens on a specified port and waits for client connections.

Each client connection is handled by a separate thread, allowing multiple clients to communicate with the server concurrently.

Clients can send messages to the server, which are processed and returned.


How to Run

Compile the Java Files:

javac Server.java Client.java

Run the Server:

java Server

Run Clients: (In separate terminals)

java Client


Expected Output

Server console will show connected clients and their messages.

Client console will display responses from the server.


Future Improvements

Add support for file transfer between client and server.

Implement secure communication using SSL sockets.

Add a GUI for client interaction.


Author

Christopher Lu and Louis Daniel Quijano
