package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.server.messages.ServerMessages;

/**
 * A class that represents the command to see the game commands list.
 */
public class HelpGameHandler implements GameCommandHandler{

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(GameMessages.HELP);
    }
}
