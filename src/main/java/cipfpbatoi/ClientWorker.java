package cipfpbatoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWorker implements Runnable {
    private Socket connectionSocket;
    private PrintWriter outToClient;
    private BufferedReader inFromClient;
    private String username;

    public ClientWorker(Socket connectionSocket, PrintWriter outToClient) {
        this.connectionSocket = connectionSocket;
        this.outToClient = outToClient;
    }

    @Override
    public void run() {
        try {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            username = inFromClient.readLine();
            ServerChat.addClient(outToClient, username);
            String clientMessage;
            while ((clientMessage = inFromClient.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("bye")) {
                    System.out.println("Desconectando...");
                    ServerChat.removeClient(username);
                    break;
                }
                ServerChat.enviarMensajeClientes(username + ": " + clientMessage);
            }

            if (inFromClient != null) inFromClient.close();
            if (outToClient != null) outToClient.close();
            if (connectionSocket != null) connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}