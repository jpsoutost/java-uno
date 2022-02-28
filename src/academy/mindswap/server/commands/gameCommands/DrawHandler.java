package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the command to draw a card from the deck.
 */
public class DrawHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) throws Exception {

        if (game.hasCardsToDraw()) {
            game.goFishingCards();
            return;
        }

        if (game.canDrawACard()) {
            game.drawCard();
            game.setCanFinishTurn(true);
        } else if (game.getHasToChooseAColor()) {
            throw new Exception(GameMessages.CHOOSE_COLOR);
        } else {
            throw new Exception(GameMessages.JUST_ONE_CARD);
        }
    }
}
