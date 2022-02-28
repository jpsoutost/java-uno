package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.commands.gameCommands.GameCommand;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.server.messages.ServerMessages;


/**
 * A class that represents the command to disconnect, that implements CommandHandler.
 */
public class QuitHandler implements CommandHandler {

    /**
     * An override method that executes the command to disconnect.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {
        Game game=clientConnectionHandler.getGame();

        server.broadcast(clientConnectionHandler.getName(), GameMessages.PLAYER_DISCONNECTED);
        if(game.gameIsRunning()) {
            game.someoneWentDown(clientConnectionHandler);
            clientConnectionHandler.setGameCommandChanged(true);
        }

        server.removeClient(clientConnectionHandler);
        clientConnectionHandler.close();
    }
}
