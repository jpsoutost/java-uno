package academy.mindswap.server;

import academy.mindswap.server.commands.serverCommands.Command;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.server.messages.ServerMessages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A class that represents the Server.
 */
public class Server {

    private ServerSocket serverSocket;
    private ExecutorService service;
    private List<ClientConnectionHandler> clients;
    private List<ClientConnectionHandler> clientsOnGeneral;
    private List<Game> openGames;
    private List<Game> closedGames;

    /**
     * A constructor method that creates the server.
     */
    public Server() {
    }

    /**
     * A method that initializes the server.
     */
    public void start(int port) throws IOException, InterruptedException {
        clients = new ArrayList<>();
        clientsOnGeneral = new ArrayList<>();
        openGames = new ArrayList<>();
        closedGames = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        service = Executors.newCachedThreadPool();

        service.submit(new ReadyChecker(this));

        while (true) {
            acceptConnection();
        }
    }

    /**
     * Method that accepts connection to a client.
     * @throws IOException Throws IO Exception.
     */
    private void acceptConnection() throws IOException {
        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler(serverSocket.accept());
        service.submit(clientConnectionHandler);
    }

    /**
     * Method that add a player.
     * The client is added into two Array lists (server and lobby).
     * A message is sent to the client when he enters in lobby.
     * @param clientConnectionHandler The client.
     */
    private void addClient(ClientConnectionHandler clientConnectionHandler) {
        clients.add(clientConnectionHandler);
        clientsOnGeneral.add(clientConnectionHandler);
        clientConnectionHandler.send(ServerMessages.WELCOME);
        broadcast(clientConnectionHandler.getName(), ServerMessages.PLAYER_ENTERED_LOBBY);
    }

    /**
     * Method that sends a message to all clients in the lobby.
     * @param name Name of the player.
     * @param message Message.
     */
    public void broadcast(String name, String message) {
        clientsOnGeneral.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(ConsoleColors.WHITE + name + ": " + message));
    }

    /**
     * Method that send a message to all clients in the room of the sender.
     * @param game The game.
     * @param name Name of the client.
     * @param message Message.
     */
    public void roomBroadcast(Game game, String name, String message) {
        game.getPlayers().stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send( ConsoleColors.WHITE + name + "(" +
                        game.getRoomName() + "): " + message));
    }

    /**
     * Method that list the name of all the clients connected to the server.
     * @return The list of clients.
     */
    public String listClients() {
        return "[ " + clients.stream().map(ClientConnectionHandler::getName)
                .collect(Collectors.joining(" ,"))
                + " ]";
    }

    /**
     * Method that list all the open rooms in the server.
     * @return The list of open rooms.
     */
    public String listOpenRooms() {
        if (openGames.isEmpty()) {
            return ServerMessages.NO_OPEN_ROOMS;
        }
        StringBuffer buffer = new StringBuffer();
        openGames.forEach(game -> {
            buffer.append(game.getRoomName()).append(" ");
            buffer.append(game.getPlayers().size()).append("/").append(10).append(" ");
            buffer.append(game.getPlayers().toString()).append("\n");
        });

        return buffer.toString();
    }

    /**
     * Method that removes a client.
     * @param clientConnectionHandler The client to remove.
     */
    public void removeClient(ClientConnectionHandler clientConnectionHandler) {
        clients.remove(clientConnectionHandler);
    }

    //GETTERS

    public List<Game> getOpenGames() {
        return openGames;
    }

    public List<ClientConnectionHandler> getClientsOnGeneral() {
        return clientsOnGeneral;
    }

    public List<Game> getClosedGames() {
        return closedGames;
    }


    /**
     * A class that represents the client in the server.
     */
    public class ClientConnectionHandler implements Runnable {

        private String name;
        private final Socket clientSocket;
        private final BufferedWriter out;
        private final BufferedReader in;
        private String message;
        private Game game;
        private List<Card> deck;
        private boolean isReady;
        private boolean gameCommandChanged;

        /**
         * Constructor method for the client connection handler.
         * @param clientSocket The socket to communicate with the relative client.
         * @throws IOException Throws IOException.
         */
        public ClientConnectionHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.deck = new ArrayList<>();
        }

        /**
         * Method that generate the name of the player.
         * @return The username of the player.
         * @throws IOException Throws IOException.
         */
        public String generateName() throws IOException {
            send(ServerMessages.CHOOSE_USERNAME);
            String username = in.readLine();
            if (clients.stream().map(c -> c.name).toList().contains(username)) {
                send(ServerMessages.USERNAME_INVALID);
                username = generateName();
            }
            return username;
        }

        /**
         * Method that overrides run that reads client input and sends server output to client.
         */
        @Override
        public void run() {
            try {
                this.name = generateName();
            } catch (IOException e) {
            }
            addClient(this);

            try {
                while (!clientSocket.isClosed()) {
                    message = in.readLine();

                    if (isCommand(message)) {
                        if (isQuitCommand(message)) {
                            dealWithCommand(message);
                            continue;
                        }

                        if (gameIsRunning()) {
                            send(ServerMessages.WHILE_PLAYING_COMMAND);
                        } else {
                            dealWithCommand(message);
                        }
                        continue;
                    } else if (isRoomChat(message, game)) {

                        if (message.substring(1).equals("")) {
                            continue;
                        }

                        roomBroadcast(game, name, ConsoleColors.CYAN + message.substring(1));
                        continue;
                    } else if (message.equals("")) {
                        continue;
                    }

                    if (this.game == null) {
                        broadcast(name,ConsoleColors.CYAN + message);
                        continue;
                    }

                    gameCommandChanged = true;
                }
            } catch (IOException e) {
                System.err.println(ServerMessages.PLAYER_ERROR + e.getMessage());
            } finally {
                removeClient(this);
            }
        }

        /**
         * Method that verify if the message is a server command.
         * @param message The message.
         */
        private boolean isCommand(String message) {
            return message.startsWith("/");
        }

        /**
         * Method that verify if the message is a quit command.
         * @param message The message.
         */
        private boolean isQuitCommand(String message){
            return message.equals("/quit");
        }

        /**
         * Method that verify if the message is belongs to room chat.
         * @param message The message.
         * @param game The game room.
         */
        private boolean isRoomChat(String message, Game game) {
            if (game != null) {
                return message.startsWith("-");
            } else {
                return false;
            }
        }

        /**
         * Method that gets the server command and executes it.
         * If message is not null, the command is executed.
         * @param message The message.
         */
        private void dealWithCommand(String message) {
            String description = message.split(" ")[0];
            Command command = Command.getCommandFromDescription(description);

            if (command == null) {
                send(ServerMessages.NO_SUCH_COMMAND);
                return;
            }

            try {
                command.getHandler().execute(Server.this, this);
            } catch (Exception e) {
                send(e.getMessage());
            }
        }

        /**
         * Method to send messages to the client.
         * @param message The message.
         */
        public void send(String message) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                removeClient(this);
            }
        }

        /**
         * Method to checks if the game is running.
         */
        private boolean gameIsRunning() {
            if (game == null) return false;
            return game.gameIsRunning();
        }

        /**
         * Method to close the communication socket.
         */
        public void close() {
            try {
                clientSocket.close();
            } catch (IOException e) {
            }
        }

        /**
         * Method to create graphical view of players deck.
         * @return String deckGraphicConstructor part of the graphic view of players deck.
         */
        private String deck1(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                if (card.getNumber()==13){
                    deckGraphicConstructor += ConsoleColors.PURPLE + GameMessages.CARD1;
                }else{
                    deckGraphicConstructor += card.getColor().getConsoleColors()
                            + GameMessages.CARD1;
                }
            }
            return deckGraphicConstructor;
        }

        /**
         * Method to create graphical view of players deck.
         * @return String deckGraphicConstructor part of the graphic view of players deck.
         */
        private String deck2(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                if (card.getNumber()==13){
                    deckGraphicConstructor += ConsoleColors.PURPLE + GameMessages.CARD2;
                }else{
                    deckGraphicConstructor += card.getColor().getConsoleColors() + GameMessages.CARD2;
                }
            }
            return deckGraphicConstructor;
        }

        /**
         * Method to create graphical view of players deck.
         * @return String deckGraphicConstructor part of the graphic view of players deck.
         */
        private String deck3(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                if (card.getNumber()==13){
                    deckGraphicConstructor += ConsoleColors.PURPLE + "|   " + card.getSymbol() + "  |";
                }else{
                    deckGraphicConstructor += card.getColor().getConsoleColors() + "|   " + card.getSymbol() + "  |";
                }
            }
            return deckGraphicConstructor;
        }

        /**
         * Method to create graphical view of players deck.
         * @return String deckGraphicConstructor part of the graphic view of players deck.
         */
        private String deck4(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                if (card.getNumber()==13){
                    deckGraphicConstructor += ConsoleColors.PURPLE + GameMessages.CARD3;
                }else{
                    deckGraphicConstructor += card.getColor().getConsoleColors() + GameMessages.CARD3;
                }
            }
            return deckGraphicConstructor;
        }

        /**
         * Method to create graphical view of players deck.
         * @return String graphic view of players deck.
         */
        public String showDeck(){
            return  deck1() + "\n" + deck2() + "\n" + deck3()
                    + "\n" + deck2() + "\n" + deck4();
        }

        @Override
        public String toString() {
            return "{" + name + ", isReady=" + isReady +
                    "}";
        }

        /**
         * Method to entering room/game.
         * Defines the game, adds player to the game and removes the player of the general list.
         * @param game The game.
         */
        public void enteringRoom(Game game) {
            this.game = game;
            game.addClient(this);
            clientsOnGeneral.remove(this);
        }

        /**
         * Method that create a room.
         * When the player create a room, he enters that room, and the list of open games is actualized.
         * @param roomName The room name.
         */
        public void createRoom(String roomName) {
            Game game = new Game(roomName, Server.this);
            enteringRoom(game);
            openGames.add(game);
        }

        /**
         * Method to quit game.
         * Removes the player of the game list and adds to the general list.
         * Send an informative message.
         */
        public void quitGame(){
            game.getPlayers().remove(this);
            clientsOnGeneral.add(this);
            isReady = false;
            deck.clear();
            roomBroadcast(game, name, GameMessages.PLAYER_QUIT_ROOM);
            this.game = null;
        }

        //GETTERS

        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }

        public boolean isReady() {
            return isReady;
        }

        public List<Card> getDeck() {
            return deck;
        }

        public Game getGame() {
            return game;
        }

        public boolean isGameCommandChanged() {
            return gameCommandChanged;
        }

        //SETTERS

        public void setGame(Game game) {
            this.game = game;
        }

        public void setReady(boolean ready) {
            isReady = ready;
        }

        public void setGameCommandChanged(boolean gameCommandChanged) {
            this.gameCommandChanged = gameCommandChanged;
        }

    }

}
