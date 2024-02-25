package cipfpbatoi;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceiveMessage implements Runnable{
    private BufferedReader reader;

    public ReceiveMessage(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        // Lee los mensajes del servidor mientras este esté disponible e impreme los mensjes del servidor
        try {
            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
