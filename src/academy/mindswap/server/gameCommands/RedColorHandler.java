package academy.mindswap.server.gameCommands;

import academy.mindswap.server.CardColors;
import academy.mindswap.server.Colors;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

import java.awt.*;

public class RedColorHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        game.getLastCardPlayed().setColor(Colors.RED);
        clientConnectionHandler.send("Color changed to red.");
        game.getServer().roomBroadcast(game,game.getPlayerToPlay().getName(),"Color changed to red.");
    }
}
