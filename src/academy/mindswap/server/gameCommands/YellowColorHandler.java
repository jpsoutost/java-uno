package academy.mindswap.server.gameCommands;

import academy.mindswap.server.CardColors;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class YellowColorHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        game.getLastCardPlayed().setColor(CardColors.YELLOW);
        clientConnectionHandler.send("Color changed to yellow.");
        game.getServer().roomBroadcast(game,game.getPlayerToPlay().getName(),"Color changed to yellow.");
    }
}
