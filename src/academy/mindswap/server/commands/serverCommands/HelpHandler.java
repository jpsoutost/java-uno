package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

/**
 * A class that represents the command to list all the server commands, that implements CommandHandler.
 */
public class HelpHandler implements CommandHandler {
    /**
     * An override method that executes the command help handler.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(ServerMessages.COMMANDS_LIST);
    }
}
