package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

/**
 * A class that represents the command to one player leave the room, that implements CommandHandler.
 */
public class QuitRoomHandler implements CommandHandler {

    /**
     * An override method that executes the command to a player leave/quit the room.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {
        Game game = clientConnectionHandler.getGame();

        if (game == null){
            throw new Exception(ServerMessages.PLAYER_NOT_IN_ROOM);
        }

        clientConnectionHandler.quitGame();
        clientConnectionHandler.send(ServerMessages.REDIRECTED_LOBBY);

        if (game.getPlayers().isEmpty()) {
            server.getOpenGames().remove(game);
            return;
        }

        for (Server.ClientConnectionHandler player : game.getPlayers()) {
            player.setReady(false);
        }
    }
}
