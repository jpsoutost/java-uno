package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Server;

public class PlayerListHandler implements CommandHandler {

    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(server.listClients());
    }
}
