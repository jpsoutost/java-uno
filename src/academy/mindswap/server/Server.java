package academy.mindswap.server;



import academy.mindswap.server.commands.Command;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.server.messages.Messages;

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
     * A constructor method that initializes Server.
     */
    public Server() {
    }


    public void start(int port) throws IOException, InterruptedException {
        clients = new ArrayList<>();                 //Array list of clients on server
        clientsOnGeneral = new ArrayList<>();        //Array list of clients on General (in the lobby)
        openGames = new ArrayList<>();               //Array list of open games
        closedGames = new ArrayList<>();             //Array list of closed games
        serverSocket = new ServerSocket(port);
        service = Executors.newCachedThreadPool();

        service.submit(new ReadyChecker(this));

        while (true) {
            acceptConnection();
        }
    }

    /**
     * Method that accepts connection to a client. The client is a player.
     * @throws IOException Throws IO Exception.
     */
    private void acceptConnection() throws IOException {
        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler(serverSocket.accept());
        service.submit(clientConnectionHandler);
    }

    /**
     * Method that add a player.
     * The player is added into two Array lists (server and lobby).
     * A message is send to the player when he enters in lobby.
     * @param clientConnectionHandler The player.
     */
    private void addClient(ClientConnectionHandler clientConnectionHandler) {
        clients.add(clientConnectionHandler);
        clientsOnGeneral.add(clientConnectionHandler);
        clientConnectionHandler.send(Messages.WELCOME);
        broadcast(clientConnectionHandler.getName(), Messages.PLAYER_ENTERED_LOBBY);
    }

    /**
     * Method that send a message to all players in the lobby.
     * @param name Name of the player.
     * @param message Message.
     */
    public void broadcast(String name, String message) {
        clientsOnGeneral.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    /**
     * Method that send a message to all players in the game.
     * @param game The game.
     * @param name Name of the player.
     * @param message Message.
     */
    public void roomBroadcast(Game game, String name, String message) {
        game.getPlayers().stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + "("+game.getRoomName()+"): " + message));
    }

    /**
     * Method that list the name of the players.
     * @return The list of the players.
     */
    public String listClients() {
        return "[ " + clients.stream().map(ClientConnectionHandler::getName)
                .collect(Collectors.joining(" ,"))
                + " ]";
    }

    /**
     * Method that list of the open rooms for each game.
     * @return The list of open rooms.
     */
    public String listOpenRooms() {
        if(openGames.isEmpty()){
            return "There is no open rooms";
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
     * Method that removes a player.
     * @param clientConnectionHandler The player to remove.
     */
    public void removeClient(ClientConnectionHandler clientConnectionHandler) {
        clients.remove(clientConnectionHandler);
    }

    /**
     * A class thaT represents the client connection handler that implements Runnable.
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
            send("Choose username:");
            String username = in.readLine();
            if (clients.stream().map(c -> c.name).toList().contains(username)){
                send("Username Already in use.");
                username = generateName();
            }
            return username;
        }

        /**
         * Method that overrides run that verify and execute commands into and during the game.
         */
        @Override
        public void run() {
            try {
                this.name = generateName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            addClient(this);

            try {
                while (!clientSocket.isClosed()){
                    message = in.readLine();

                    if (isCommand(message)) {
                        if (gameIsRunning()) {
                            send("You can't use server commands while playing.");
                        } else {
                            dealWithCommand(message);
                        }
                        continue;
                    }else if (isRoomChat(message, game)) {

                        if (message.substring(1).equals("")) {
                            continue;
                        }

                        roomBroadcast(game, name, message.substring(1));
                        continue;
                    }else if (message.equals("")) {
                        continue;
                    }

                    if (this.game == null) {
                    broadcast(name, message);
                    continue;
                    }

                    gameCommandChanged = true;
                }
            } catch (IOException e) {
                System.err.println(Messages.PLAYER_ERROR + e.getMessage());
            } finally {
                removeClient(this);
            }
        }

        /**
         * Method that verify if the message is a command game.
         * If message is not null, the command is executed.
         * @param message The message.
         * @throws IOException Throws IOException.
         */
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

        /**
         * Method to send messages.
         * @param message The message.
         */
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

        @Override
        public String toString() {
            return "{" + name + ", isReady=" + isReady +
                    "}";
        }

        /**
         * Method that create a room.
         * When the player create a room, he enters that room, and the list of open games is actualized.
         * @param roomName The room name.
         */
        public void createRoom(String roomName){
            Game game = new Game(roomName, Server.this);
            enteringRoom(game);
            openGames.add(game);
        }

        /**
         * Method to entering room.
         * Defines the game, adds player to the game and removes the player of the general list.
         * @param game The game.
         */
        public void enteringRoom(Game game){
            this.game = game;
            game.addClient(this);
            clientsOnGeneral.remove(this);
        }

        /**
         * Method to quit game.
         * Removes the player of the game list and adds to the general list.
         * Send an informative message.
         */
        public void quitGame(){
            game.getPlayers().remove(this);
            clientsOnGeneral.add(this);
            roomBroadcast(game, name, Messages.PLAYER_QUIT_ROOM);
            this.game = null;
        }

        // METHODS TO PRINT THE CARDS

        private String deck1(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                deckGraphicConstructor += card.getColor().getConsoleColors() + GameMessages.CARD1 + "  ";
            }
            return deckGraphicConstructor;
        }

        private String deck2(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                deckGraphicConstructor += card.getColor().getConsoleColors() + GameMessages.CARD2 + "  ";
            }
            return deckGraphicConstructor;
        }

        private String deck3(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                deckGraphicConstructor += card.getColor().getConsoleColors() + "|  " + card.getNumber() + "  |  ";
            }
            return deckGraphicConstructor;
        }

        private String deck4(){
            String deckGraphicConstructor="";
            for (Card card:deck) {
                deckGraphicConstructor += card.getColor().getConsoleColors() + GameMessages.CARD3 + "  ";
            }
            return deckGraphicConstructor;
        }

        public String showDeck(){
            return  "\n" + deck1() + "\n" + deck2() + "\n" + deck3()
                    + "\n" + deck2() + "\n" + deck4() + "\n";
        }


        //BOOLEAN METHODS

        private boolean isCommand(String message) {
            return message.startsWith("/");
        }

        private boolean isRoomChat(String message,Game game) {
            if (game != null) {
                return message.startsWith("-");
            }else{
                return false;
            }
        }

        private boolean gameIsRunning(){
            if (game == null) return false;
            return game.gameIsRunning();
        }

        public boolean isReady() {
            return isReady;
        }

        public boolean isGameCommandChanged() {
            return gameCommandChanged;
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


        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }


        public List<Card> getDeck() {
            return deck;
        }

        public Game getGame() {
            return game;
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
