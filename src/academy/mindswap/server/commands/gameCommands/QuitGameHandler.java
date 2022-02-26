package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.commands.gameCommands.GameCommandHandler;
import academy.mindswap.server.messages.Messages;

public class QuitGameHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {

        if(game.isLastPlayer()){
            game.changeLastPlayer();
        }

        clientConnectionHandler.quitGame();
        clientConnectionHandler.send(Messages.WELCOME);


    }
}
