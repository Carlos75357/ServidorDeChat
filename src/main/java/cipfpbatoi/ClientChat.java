package cipfpbatoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientChat {
    public static void main(String[] args) throws IOException {
        // Esto dice cual es la ip del servidor y cual es el puerto por el cual el server escucha
        String serverHostname = "localhost";
        int port = 6789;

        try (Socket clientSocket = new Socket(serverHostname, port);
             PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            // Aqui simplemente pide el nombre de usuario y lo envia al servidor
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese su nombre de usuario: ");
            String username = scanner.nextLine();
            outToServer.println(username);

            // Aqui recibe la respuesta del servidor
            String serverResponse = inFromServer.readLine();
            System.out.println(serverResponse);
            // no va
            if (serverResponse.equals("El servidor se ha cerrado")) {
                System.exit(0);
            }

            // Aqui creamos un hilo para recibir los mensjaes desde el servidor
            ReceiveMessage hiloParaRecibirMensajes = new ReceiveMessage(inFromServer);
            Thread receiveThread = new Thread(hiloParaRecibirMensajes);

            receiveThread.start();

            String message;
            while (!(message = scanner.nextLine()).equalsIgnoreCase("bye")) {
                outToServer.println(message);
            }
            // Esta parte "supuestamente" le envia al servudir un mensaje indicando que se desconecta y cierra el flujo de entrada.
            outToServer.println("bye");
            System.in.close();
        }
    }
}