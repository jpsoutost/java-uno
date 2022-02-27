package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

public class DrawHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {

        if (game.hasCardsToDraw()) {
            game.goFishingCards();
            return;
        }

        if (game.canDrawACard()) {
            game.drawCard();
            game.setCanFinishTurn(true);
        } else if (game.getHasToChooseAColor()) {
            clientConnectionHandler.send(GameMessages.CHOOSE_COLOR);
        } else {
            clientConnectionHandler.send(GameMessages.JUST_ONE_CARD);
        }
    }
}
