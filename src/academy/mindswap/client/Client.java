package academy.mindswap.client;

import java.io.*;
import java.net.Socket;

/**
 * A class that represents the player.
 */
public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.start("localhost", 8080);
        } catch (IOException e) {
            System.out.println("Connection closed...");
        }

    }

    /**
     * Method that start the connection.
     * @param host Host.
     * @param port Port.
     * @throws IOException
     */
    private void start(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        new Thread(new KeyboardHandler(out, socket)).start();
        String line;
        while (( line = in.readLine()) != null) {
            System.out.println(line);
        }
        socket.close();
    }

    /**
     * A class that represents the key bord handler.
     */
    private class KeyboardHandler implements Runnable {
        private final BufferedWriter out;
        private final Socket socket;
        private final BufferedReader in;

        public KeyboardHandler(BufferedWriter out, Socket socket) {
            this.out = out;
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {

            while (!socket.isClosed()) {
                try {
                    String line = in.readLine();

                    out.write(line);
                    out.newLine();
                    out.flush();

                    if (line.equals("/quit")) {
                        socket.close();
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println("Something went wrong with the server. Connection closing...");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
