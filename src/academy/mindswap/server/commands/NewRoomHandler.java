package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.Messages;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NewRoomHandler implements CommandHandler {
    Set<String> openGames;

        @Override
        public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
            String message = clientConnectionHandler.getMessage();
            this.openGames = server.getOpenGames().stream().map( game -> game.getRoomName()).collect(Collectors.toSet());

            if (message.split(" ").length < 2) {
                clientConnectionHandler.send("Wrong way to create a Room.");
                return;
            }

            String roomName = message.split(" ")[1];

            if (openGames.contains(roomName)) {
                clientConnectionHandler.send("Room with that name already created.");
            } else {
                Game game = new Game(roomName, server);
                game.addClient(clientConnectionHandler);
                clientConnectionHandler.setGame(game);
                server.getOpenGames().add(game);
                clientConnectionHandler.send("You created room " + message);
                server.getClientsOnGeneral().remove(clientConnectionHandler);
            }
        }
}