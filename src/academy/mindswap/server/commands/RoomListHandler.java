package academy.mindswap.server.commands;

import academy.mindswap.server.Server;

/**
 * A class that represents the command to turn a player ready to the game, that implements CommandHandler.
 */
public class RoomListHandler implements CommandHandler {

    /**
     * An override method that executes the command to a player.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(server.listOpenRooms());
    }
}
