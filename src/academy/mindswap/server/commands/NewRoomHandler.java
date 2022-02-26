package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class that represents the command to create a new room, that implements CommandHandler.
 */
public class NewRoomHandler implements CommandHandler {
    Set<String> openGames;

    /**
     * An override method that executes the command new room. The room can be open by any player.
     * The players only can open rooms with different names.
     * The name of the room can only have one word.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
        @Override
        public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
            String message = clientConnectionHandler.getMessage();
            this.openGames = server.getOpenGames().stream().map( game -> game.getRoomName()).collect(Collectors.toSet());

            if (message.split(" ").length != 2) {
                clientConnectionHandler.send("Wrong way to create a Room.");
                return;
            }

            String roomName = message.split(" ")[1];

            if (openGames.contains(roomName)) {
                clientConnectionHandler.send("Room with that name already created.");
            } else {
                clientConnectionHandler.createRoom(roomName);
                clientConnectionHandler.send("You created room " + message.substring(12));
            }
        }
}
