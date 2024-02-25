package cipfpbatoi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerChat {
    private static final int PORT = 6789;
    private static Map<String, PrintWriter> clientesConectados = new HashMap<>();
    private static Map<String, Integer> ultimoMensajeDeCadaPersona = new HashMap<>();
    private static List<String> historialDeChat = new ArrayList<>();

    private static ExecutorService executor;

    public static void main(String[] args) {
        int numCores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(numCores);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String message = "El servidor se ha cerrado";
            System.out.println(message);
            enviarMensajeClientes(message);
            executor.shutdown();
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor a la escucha siuuu.");
            while (true) {
                Socket socket = serverSocket.accept();
                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                executor.submit(new ClientWorker(socket, outToClient));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
            System.out.println("Servidor apagado.");
        }
    }

    public static void removeClient(String username) {
        clientesConectados.remove(username);
        String message = username + " se ha desconectado.";
        System.out.println(message);
        enviarMensajeClientes(message);
    }

    public static void addClient(PrintWriter writer, String username) {
        clientesConectados.put(username, writer);
        if (!ultimoMensajeDeCadaPersona.containsKey(username)) {
            ultimoMensajeDeCadaPersona.put(username, historialDeChat.size());
        }
        int startIndex = ultimoMensajeDeCadaPersona.get(username);
        for (int i = startIndex; i < historialDeChat.size(); i++) {
            writer.println(historialDeChat.get(i));
        }
        String message = username + " se ha conectado.";
        System.out.println(message);
        enviarMensajeClientes(message);
    }

    public static void enviarMensajeClientes(String message) {
        historialDeChat.add(message);
        for (String username : clientesConectados.keySet()) {
            clientesConectados.get(username).println(message);
            ultimoMensajeDeCadaPersona.put(username, historialDeChat.size());
        }
    }
}