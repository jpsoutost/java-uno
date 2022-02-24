package academy.mindswap.server.commands;

import academy.mindswap.server.Server;

/**
 * A class that executes the
 */
public interface CommandHandler {
    void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler);
}
