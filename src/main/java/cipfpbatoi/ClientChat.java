package cipfpbatoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientChat {
    public static void main(String[] args) throws IOException {
        String serverHostname = "localhost";
        int port = 6789;

        try (Socket clientSocket = new Socket(serverHostname, port);
             PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese su nombre de usuario: ");
            String username = scanner.nextLine();
            outToServer.println(username);

            String serverResponse = inFromServer.readLine();
            System.out.println(serverResponse);
            // no va
            if (serverResponse.equals("El servidor se ha cerrado")) {
                System.exit(0);
            }

            ReceiveMessage receiveMessageRunnable = new ReceiveMessage(inFromServer);
            Thread receiveThread = new Thread(receiveMessageRunnable);

            receiveThread.start();

            String message;
            while (!(message = scanner.nextLine()).equalsIgnoreCase("bye")) {
                outToServer.println(message);
            }
            outToServer.println("bye");
            System.in.close();
        }
    }
}