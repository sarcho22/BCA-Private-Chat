package day7_bca_chat_with_serialization;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    public static final int PORT = 54323;
    private static final ArrayList<ClientConnectionData> clientArrayList = new ArrayList<>();
    //Make this a thread-safe collection 
    private static final List<ClientConnectionData> clientList = Collections.synchronizedList(clientArrayList);

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(100);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started.");
            System.out.println("Local IP: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Local Port: " + serverSocket.getLocalPort());

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.printf("Connected to %s:%d on local port %d\n", socket.getInetAddress(),
                            socket.getPort(), socket.getLocalPort());

                    // handle client business in another thread
                    pool.execute(new ChatServerSocketListener(socket, clientList));
                } 
                
                // prevent exceptions from causing server from exiting.
                catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
    }
}
