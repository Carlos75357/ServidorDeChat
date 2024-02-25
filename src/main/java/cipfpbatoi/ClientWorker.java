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
            // Se crea un bufferedReader para leer los mensajes del cliente, se lee el nombre de usuario y se agrega el cliente a la lista de conectados.
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            username = inFromClient.readLine();
            ServerChat.addClient(outToClient, username);
            String clientMessage;
            while ((clientMessage = inFromClient.readLine()) != null) {
                // Lee los mensajes del cliente y si este en algún momento escribe bye, el cliente se desconecta de la lista de conectados.
                if (clientMessage.equalsIgnoreCase("bye")) {
                    System.out.println("Desconectando...");
                    ServerChat.removeClient(username);
                    break;
                }
                // Esta linea es la que envia el mensaje a todos los clientes.
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