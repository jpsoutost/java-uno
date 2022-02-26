package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

public class QuitGameHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {

        if (game.isLastPlayer()) {
            game.changeLastPlayer();
        }

        clientConnectionHandler.quitGame();
        clientConnectionHandler.send(GameMessages.WELCOME);
    }
}
