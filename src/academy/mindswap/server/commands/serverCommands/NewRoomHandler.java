package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

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
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {
        String message = clientConnectionHandler.getMessage();
        this.openGames = server.getOpenGames().stream().map(Game::getRoomName).collect(Collectors.toSet());

        if (message.split(" ").length != 2) {
            throw new Exception(ServerMessages.CREATE_WRONG);
        }

        String roomName = message.split(" ")[1];

        if (openGames.contains(roomName)) {
            throw new Exception(ServerMessages.ALREADY_CREATED_ROOM);
        } else {
            clientConnectionHandler.createRoom(roomName);
            clientConnectionHandler.send(ServerMessages.ROOM_CREATED + message.substring(12));
        }
    }
}
