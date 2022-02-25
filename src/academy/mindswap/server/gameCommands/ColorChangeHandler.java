package academy.mindswap.server.gameCommands;

import academy.mindswap.server.CardColors;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class ColorChangeHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        String play = game.getPlay();

        if(game.getHasToChooseAColor()) {
            switch (play) {
                case "b": {
                    game.getLastCardPlayed().setColor(CardColors.BLUE);
                    clientConnectionHandler.send("Color changed to blue.");
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), "Color changed to blue.");
                    game.setHasToChooseAColor(false);
                    break;
                }
                case "y": {
                    game.getLastCardPlayed().setColor(CardColors.YELLOW);
                    clientConnectionHandler.send("Color changed to yellow.");
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), "Color changed to yellow.");
                    game.setHasToChooseAColor(false);
                    break;
                }
                case "g": {
                    game.getLastCardPlayed().setColor(CardColors.GREEN);
                    clientConnectionHandler.send("Color changed to green.");
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), "Color changed to green.");
                    game.setHasToChooseAColor(false);
                    break;
                }
                case "r": {
                    game.getLastCardPlayed().setColor(CardColors.RED);
                    clientConnectionHandler.send("Color changed to red.");
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), "Color changed to red.");
                    game.setHasToChooseAColor(false);
                    break;
                }
            }
        }else{
            clientConnectionHandler.send("You can't change the color.");
        }

    }
}
