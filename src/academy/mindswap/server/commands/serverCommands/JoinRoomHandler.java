package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

import java.util.Optional;

/**
 * A class that represents the command to join room, that implements CommandHandler.
 */
public class JoinRoomHandler implements CommandHandler {

    /**
     * An override method that executes the command to join into the room.
     * The player only can join an existing room.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {
        String message = clientConnectionHandler.getMessage();

        if (message.split(" ").length != 2) {
            throw new Exception(ServerMessages.JOIN_WRONG);
        }

        String roomName = message.split(" ")[1];

        Optional<Game> game = server.getOpenGames().stream().filter(room -> room.getRoomName().equals(roomName))
                .findFirst();

        if (game.isEmpty()) {
            throw new Exception(ServerMessages.NO_SUCH_ROOM);
        }

        if (game.get().getPlayers().size() >= 10) {
            throw new Exception(ServerMessages.ROOM_FULL);
        }

        clientConnectionHandler.enteringRoom(game.get());
        clientConnectionHandler.send(ServerMessages.JOINED_ROOM + message.substring(10));
        server.roomBroadcast(clientConnectionHandler.getGame(), clientConnectionHandler.getName(),
                ServerMessages.PLAYER_ENTERED_ROOM);
    }
}
