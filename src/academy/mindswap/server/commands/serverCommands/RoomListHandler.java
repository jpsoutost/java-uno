package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Server;

/**
 * A class that represents the command to see the open rooms list, that implement CommandHandler interface.
 */
public class RoomListHandler implements CommandHandler {

    /**
     * An override method that executes the command to see the open rooms list.
     * @param server The server.
     * @param clientConnectionHandler The player.
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(server.listOpenRooms());
    }
}
