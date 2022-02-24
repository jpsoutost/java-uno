package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class ReadyHandler implements CommandHandler{
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {

        boolean ready = clientConnectionHandler.isReady() ? false:true;
        clientConnectionHandler.setReady(ready);
        Game game = clientConnectionHandler.getGame();

        if(ready) {
            clientConnectionHandler.send("You are ready.");
            server.roomBroadcast(game, clientConnectionHandler.getName(), "I am ready.");
        }else{
            clientConnectionHandler.send("You are not ready.");
            server.roomBroadcast(game, clientConnectionHandler.getName(),  "I am not ready.");
        }
    }
}
