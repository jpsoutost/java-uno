package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class ReadyHandler implements CommandHandler{
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        Game game = clientConnectionHandler.getGame();

        if (game == null){
            clientConnectionHandler.send("You can't be ready if you are not in a room.");
            return;
        }
        if (game.getPlayers().size()==1){
            clientConnectionHandler.send("You can't play alone.");
            return;
        }

        boolean ready = !clientConnectionHandler.isReady();
        clientConnectionHandler.setReady(ready);

        if(ready) {
            clientConnectionHandler.send("You are ready.");
            server.roomBroadcast(game, clientConnectionHandler.getName(), "I am ready.");
        }else{
            clientConnectionHandler.send("You are not ready.");
            server.roomBroadcast(game, clientConnectionHandler.getName(),  "I am not ready.");
        }
    }
}
