package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Server;

public class RoomListHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(server.listOpenRooms());
    }
}
