package academy.mindswap.server.gameCommands;

import academy.mindswap.server.CardColors;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.CommandsMessages;
import academy.mindswap.server.messages.GameMessages;

public class ColorChangeHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        String play = game.getPlay();

        if (game.getHasToChooseAColor()) {
            switch (play) {
                case "b": {
                    game.getLastCardPlayed().setColor(CardColors.BLUE);
                    clientConnectionHandler.send(CardColors.BLUE.getDescription());
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), CardColors.BLUE
                            .getDescription());
                    game.setHasToChooseAColor(false);
                    break;
                }
                case "y": {
                    game.getLastCardPlayed().setColor(CardColors.YELLOW);
                    clientConnectionHandler.send(CardColors.YELLOW.getDescription());
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), CardColors.YELLOW
                            .getDescription());
                    game.setHasToChooseAColor(false);
                    break;
                }
                case "g": {
                    game.getLastCardPlayed().setColor(CardColors.GREEN);
                    clientConnectionHandler.send(CardColors.GREEN.getDescription());
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), CardColors.GREEN
                            .getDescription());
                    game.setHasToChooseAColor(false);
                    break;
                }
                case "r": {
                    game.getLastCardPlayed().setColor(CardColors.RED);
                    clientConnectionHandler.send(CardColors.RED.getDescription());
                    game.getServer().roomBroadcast(game, game.getPlayerToPlay().getName(), CardColors.RED
                            .getDescription());
                    game.setHasToChooseAColor(false);
                    break;
                }
            }
        } else {
            clientConnectionHandler.send(GameMessages.CANT_CHANGE);
        }
    }
}
