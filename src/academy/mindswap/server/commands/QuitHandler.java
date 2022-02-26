package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.gameCommands.GameCommand;
import academy.mindswap.server.messages.Messages;

/**
 * A class that represents the command to quit game, that implements CommandHandler.
 */
public class QuitHandler implements CommandHandler {

    /**
     * An override method that executes the command to leave/quit the game.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        Game game=clientConnectionHandler.getGame();

        if(game.gameIsRunning()){
            GameCommand command = GameCommand.getGameCommandFromDescription("q");
            command.getCommandHandler().execute(game, clientConnectionHandler);
        }

        server.removeClient(clientConnectionHandler);
        server.broadcast(clientConnectionHandler.getName(), clientConnectionHandler.getName() + Messages.PLAYER_DISCONNECTED);
        clientConnectionHandler.close();
    }
}
