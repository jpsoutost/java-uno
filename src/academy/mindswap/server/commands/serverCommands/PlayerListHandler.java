package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Server;

/**
 * A class that represents the command to shows/print the list of the players.
 */
public class PlayerListHandler implements CommandHandler {

    /**
     * An override method that executes the command to server to list players.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(server.listClients());
    }
}
