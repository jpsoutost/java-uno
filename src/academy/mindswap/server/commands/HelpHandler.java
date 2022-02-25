package academy.mindswap.server.commands;

import academy.mindswap.server.Server;
import academy.mindswap.server.messages.Messages;

/**
 * A class that represents the command help, that implements CommandHandler.
 */
public class HelpHandler implements CommandHandler {
    /**
     * An override method that executes the command help handler.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(Messages.COMMANDS_LIST);
    }
}
