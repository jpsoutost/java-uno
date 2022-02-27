package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

public class UnoCommandHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        if (game.getPlayerToPlay().getDeck().size() == 1) {
            game.saidUNO(true);
            clientConnectionHandler.send(GameMessages.UNO_UNO);
            game.getServer().roomBroadcast(game, clientConnectionHandler.getName(),
                    GameMessages.UNO_UNO);
        }
    }
}
