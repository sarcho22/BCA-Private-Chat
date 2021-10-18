package day7_bca_chat_with_serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ChatServerSocketListener  implements Runnable {
    private Socket socket;

    private ClientConnectionData client;
    private List<ClientConnectionData> clientList;

    public ChatServerSocketListener(Socket socket, List<ClientConnectionData> clientList) {
        this.socket = socket;
        this.clientList = clientList;
    }

    private void setup() throws Exception {
        ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
        String name = socket.getInetAddress().getHostName();

        client = new ClientConnectionData(socket, socketIn, socketOut, name);
        clientList.add(client);

        System.out.println("added client " + name);

    }

    private void processChatMessage(MessageCtoS_Chat m) {
        System.out.println("Chat received from " + client.getUserName() + " - broadcasting");
        broadcast(new MessageStoC_Chat(client.getUserName(), m.msg), client);
    }

    /**
     * Broadcasts a message to all clients connected to the server.
     */
    public void broadcast(Message m, ClientConnectionData skipClient) {
        try {
            System.out.println("broadcasting: " + m);
            for (ClientConnectionData c : clientList){
                // if c equals skipClient, then c.
                // or if c hasn't set a userName yet (still joining the server)
                if ((c != skipClient) && (c.getUserName()!= null)){
                    c.getOut().writeObject(m);
                }
            }
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }        
    }

    @Override
    public void run() {
        try {
            setup();
            ObjectInputStream in = client.getInput();

            MessageCtoS_Join joinMessage = (MessageCtoS_Join)in.readObject();
            client.setUserName(joinMessage.userName);
            broadcast(new MessageStoC_Welcome(joinMessage.userName), client);
            
            while (true) {
                Message msg = (Message) in.readObject();
                if (msg instanceof MessageCtoS_Quit) {
                    break;
                }
                else if (msg instanceof MessageCtoS_Chat) {
                    processChatMessage((MessageCtoS_Chat) msg);
                }
                else {
                    System.out.println("Unhandled message type: " + msg.getClass());
                }
            }
        } catch (Exception ex) {
            if (ex instanceof SocketException) {
                System.out.println("Caught socket ex for " + 
                    client.getName());
            } else {
                System.out.println(ex);
                ex.printStackTrace();
            }
        } finally {
            //Remove client from clientList
            clientList.remove(client); 

            // Notify everyone that the user left.
            broadcast(new MessageStoC_Exit(client.getUserName()), client);

            try {
                client.getSocket().close();
            } catch (IOException ex) {}
        }
    }
        
}
