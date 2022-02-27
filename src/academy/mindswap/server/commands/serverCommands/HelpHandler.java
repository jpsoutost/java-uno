package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

public class HelpHandler implements CommandHandler {

    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        clientConnectionHandler.send(ServerMessages.COMMANDS_LIST);
    }
}
