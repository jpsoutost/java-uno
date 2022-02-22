package academy.mindswap.server.commands;

import academy.mindswap.server.Server;

public class ReadyHandler implements CommandHandler{
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {

        boolean ready = clientConnectionHandler.isReady() ? false:true;
        clientConnectionHandler.setReady(ready);
    }
}
