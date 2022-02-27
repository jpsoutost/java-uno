package academy.mindswap.server;



import academy.mindswap.server.commands.serverCommands.Command;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.server.messages.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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
    }

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

    private void acceptConnection() throws IOException {
        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler(serverSocket.accept());
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
        game.getPlayers().stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + "("+game.getRoomName()+"): " + message));
    }

    public String listClients() {
        return "[ " + clients.stream().map(ClientConnectionHandler::getName)
                .collect(Collectors.joining(" ,"))
                + " ]";
    }

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

    public void removeClient(ClientConnectionHandler clientConnectionHandler) {
        clients.remove(clientConnectionHandler);
    }

    public List<Game> getOpenGames() {
        return openGames;
    }

    public List<ClientConnectionHandler> getClientsOnGeneral() {
        return clientsOnGeneral;
    }

    public List<Game> getClosedGames() {
        return closedGames;
    }

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

        public String generateName() throws IOException {
            send("Choose username:");
            String username = in.readLine();
            if (clients.stream().map(c -> c.name).toList().contains(username)){
                send("Username Already in use.");
                username = generateName();
            }
            return username;
        }

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
                        if(commandIsQuit(message)){
                            dealWithCommand(message);
                        }else if (gameIsRunning()) {
                            send("You can't use server commands while playing.");
                        }else{
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

        private boolean commandIsQuit(String message){
            return message.equals("/quit");
        }

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

        private boolean gameIsRunning(){
           if (game == null) return false;
           return game.gameIsRunning();
        }

        public void close() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

        public String showDeck(){
            return  deck1() + "\n" + deck2() + "\n" + deck3()
                    + "\n" + deck2() + "\n" + deck4();
        }

        @Override
        public String toString() {
            return "{" + name + ", isReady=" + isReady +
                    "}";
        }

        public void enteringRoom(Game game){
            this.game = game;
            game.addClient(this);
            clientsOnGeneral.remove(this);
        }

        public void createRoom(String roomName){
            Game game = new Game(roomName, Server.this);
            enteringRoom(game);
            openGames.add(game);
        }

        public void quitGame(){
            game.getPlayers().remove(this);
            clientsOnGeneral.add(this);
            roomBroadcast(game, name, Messages.PLAYER_QUIT_ROOM);
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
