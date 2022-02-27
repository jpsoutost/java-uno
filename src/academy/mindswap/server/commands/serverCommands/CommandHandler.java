package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Server;

/**
 * An interface class to Command Handler.
 */
public interface CommandHandler {
    void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) throws Exception;
}
