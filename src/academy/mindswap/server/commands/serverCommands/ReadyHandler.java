package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

/**
 * A class that represents the command to turn a player ready to the game, that implements CommandHandler.
 */
public class ReadyHandler implements CommandHandler{

    /**
     * An override method that executes the command to a player.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {
        Game game = clientConnectionHandler.getGame();

        if (game == null) {
            throw new Exception(ServerMessages.PLAYER_OUT);

        }
        if (game.getPlayers().size() == 1) {
            throw new Exception(ServerMessages.PLAYER_ALONE);
        }

        boolean ready = !clientConnectionHandler.isReady();
        clientConnectionHandler.setReady(ready);

        if (ready) {
            clientConnectionHandler.send(ServerMessages.PLAYER_READY);
            server.roomBroadcast(game, clientConnectionHandler.getName(), ServerMessages.I_AM_READY);
        } else {
            clientConnectionHandler.send(ServerMessages.PLAYER_NOT_READY);
            server.roomBroadcast(game, clientConnectionHandler.getName(), ServerMessages.I_AM_NOT_READY);
        }
    }
}
