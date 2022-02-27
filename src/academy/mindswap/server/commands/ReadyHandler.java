package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.CommandsMessages;

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
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        Game game = clientConnectionHandler.getGame();

        if (game == null) {
            clientConnectionHandler.send(CommandsMessages.PLAYER_OUT);
            return;
        }
        if (game.getPlayers().size() == 1) {
            clientConnectionHandler.send(CommandsMessages.PLAYER_ALONE);
            return;
        }

        boolean ready = !clientConnectionHandler.isReady();
        clientConnectionHandler.setReady(ready);

        if (ready) {
            clientConnectionHandler.send(CommandsMessages.READY);
            server.roomBroadcast(game, clientConnectionHandler.getName(), CommandsMessages.DO_IT);
        } else {
            clientConnectionHandler.send(CommandsMessages.NOTREADY);
            server.roomBroadcast(game, clientConnectionHandler.getName(), CommandsMessages.UNREADY);
        }
    }
}
