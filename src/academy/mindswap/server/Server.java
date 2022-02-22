package academy.mindswap.server;

import academy.mindswap.server.commands.Command;
import academy.mindswap.server.messages.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Server {

    private ServerSocket serverSocket;
    private ExecutorService service;
    private List<ClientConnectionHandler> clients;
    private List<ClientConnectionHandler> clientsOnGeneral;
    private List<Game> openGames;
    private List<Game> closedGames;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        service = Executors.newCachedThreadPool();
        openGames = new ArrayList<Game>();
        closedGames = new ArrayList<Game>();

        while (true) {
            acceptConnection();
        }
    }

    private void acceptConnection() throws IOException {
        ClientConnectionHandler clientConnectionHandler =
                new ClientConnectionHandler(serverSocket.accept());

        service.submit(clientConnectionHandler);
    }

    private void addClient(ClientConnectionHandler clientConnectionHandler) {
        clients.add(clientConnectionHandler);
        clientsOnGeneral.add(clientConnectionHandler);
        clientConnectionHandler.send(Messages.WELCOME);
        broadcast(clientConnectionHandler.getName(), Messages.PLAYER_ENTERED_LOBBY);
    }

    public void broadcast(String name, String message) {
        clientsOnGeneral.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public void roomBroadcast(Game game, String name, String message) {
        game.getClients().stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }


    public String listClients() {
        StringBuffer buffer = new StringBuffer();
        return clients.stream().map(c -> c.getName()).collect(Collectors.joining(" ,"));
    }

    public String listOpenRooms() {
        StringBuffer buffer = new StringBuffer();
        openGames.forEach(game -> {
            buffer.append(game.getName() + " ");
            buffer.append(game.getClients().size + "/" + 5 + " ");
            buffer.append(game.getCliets().toString() + "\n");
        });

        return buffer.toString();
    }

    public void removeClient(ClientConnectionHandler clientConnectionHandler) {
        clients.remove(clientConnectionHandler);
    }

    public Optional<ClientConnectionHandler> getClientByName(String name) {
        return clientsOnGeneral.stream()
                .filter(clientConnectionHandler -> clientConnectionHandler.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Game> getOpenGames() {
        return openGames;
    }

    public List<ClientConnectionHandler> getClientsOnGeneral() {
        return clientsOnGeneral;
    }

    public class ClientConnectionHandler implements Runnable {

        private final String name;
        private final Socket clientSocket;
        private BufferedWriter out;
        private String message;
        private BufferedReader in;
        private Game game;
        private boolean isReady;


        public ClientConnectionHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.name = generateName();
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public String generateName() throws IOException {
            send("Choose username:");
            String username = in.readLine();
            if (clients.stream().map(c -> c.name).collect(Collectors.toList()).contains(username)){
                send("Username Aalready in use.");
                username = generateName();
            }
            return username;
        }

        @Override
        public void run() {
            addClient(this);
            try {

                while (!clientSocket.isClosed()) {
                    message = in.readLine();

                    if (isCommand(message)) {
                        dealWithCommand(message);
                        continue;
                    }

                    if (message.equals("")) {
                        return;
                    }

                    broadcast(name, message);
                }
            } catch (IOException e) {
                System.err.println(Messages.PLAYER_ERROR + e.getMessage());
            } finally {
                removeClient(this);
            }
        }

        private boolean isCommand(String message) {
            return message.startsWith("/");
        }

        private void dealWithCommand(String message) throws IOException {
            String description = message.split(" ")[0];
            Command command = Command.getCommandFromDescription(description);

            if (command == null) {
                out.write(Messages.NO_SUCH_COMMAND);
                out.newLine();
                out.flush();
                return;
            }

            command.getHandler().execute(Server.this, this);
        }

        public void send(String message) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                removeClient(this);
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }

        public void setGame(Game game) {
            this.game = game;
        }

        public boolean isReady() {
            return isReady;
        }

        public void setReady(boolean ready) {
            isReady = ready;
        }
    }

}
