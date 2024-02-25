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
    // Este primer map alamacena la inforamcion del cliente, su nombre y el write por el cual envia mensajes.
    private static Map<String, PrintWriter> clientesConectados = new HashMap<>();
    // Este segundo map guarda el nombre de usuario y el índice del historial del chat para saber cual es el ultimo mensjae que ese usuario ha visto.
    private static Map<String, Integer> ultimoMensajeDeCadaPersona = new HashMap<>();
    // Este List guarda todos los mensajes.
    private static List<String> historialDeChat = new ArrayList<>();

    private static ExecutorService executor;

    public static void main(String[] args) {
        // Primero con Runtime.getRuntime().availableProcessors() obtenemos el número de procesadores y creamos un hilo para cada uno de ellos
        int numCores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(numCores);
        // Creamos un hook de cierre, se ejecuta cuando el servidor se cierra para mandar a los clientes un mensaje de que el servidor se ha cerrado y cierra el executor.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String message = "El servidor se ha cerrado";
            System.out.println(message);
            enviarMensajeClientes(message);
            executor.shutdown();
        }));

        // Primero se abre un socket en el puerto 6789, se espera a la conexiones de los clientes, se acepta las conexiones y se crea un socket para comunicarse con el cliente.
        // Se crea un PrintWriter para mandar mensajes al cliente y se envia el y se crea un ClientWorker para manejar la comunicación entre el servidor y el cliente.
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
    // Método para eliminar un cliente y mandar un mensaje a los clientes de cuál se ha desconectado.
    public static void removeClient(String username) {
        clientesConectados.remove(username);
        String message = username + " se ha desconectado.";
        System.out.println(message);
        enviarMensajeClientes(message);
    }
    // Este método se encarga de añadir a la lista el nuevo cliente y mandarle los mensajes que se han enviado durante su ausencia.
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
    // Este metodo envia los mensajes a los clientes.
    public static void enviarMensajeClientes(String message) {
        historialDeChat.add(message);
        for (String username : clientesConectados.keySet()) {
            clientesConectados.get(username).println(message);
            ultimoMensajeDeCadaPersona.put(username, historialDeChat.size());
        }
    }
}