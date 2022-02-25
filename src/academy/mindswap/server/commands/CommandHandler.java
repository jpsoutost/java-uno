package academy.mindswap.server.commands;

import academy.mindswap.server.Server;

/**
 * An interface class to Command Handler.
 */
public interface CommandHandler {
    void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler);
}
