package academy.mindswap.server.commands.gameCommands;

import academy.mindswap.server.Card;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.commands.gameCommands.GameCommandHandler;
import academy.mindswap.server.messages.GameMessages;

public class PlayHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        String play = game.getPlay();
        boolean valid = false;

        if (game.canPlayACard()) {

            Card chosenCard = game.getPlayerToPlay().getDeck().get(Integer.parseInt(play));

            if (game.hasCardsToDraw(chosenCard)) {
                game.goFishingCards();
                return;
            }

            if (game.isAPlus4Card(chosenCard) && game.canPlayAPlus4Card()) {
                game.dealWithPlus4Cards(chosenCard);
                return;
            }

            if (chosenCard.getColor() == game.getLastCardPlayed().getColor() && game.isFirstCardOfTurn()) {
                game.setCanPlayAgain(false);
                valid = true;
            }

            if (chosenCard.getNumber() == game.getLastCardPlayed().getNumber()) {
                game.setCanPlayAgain(true);
                valid = true;
            }

            if (valid) {
                game.dealWithSpecialCards(chosenCard);
                game.cardChangesInDecks(chosenCard);
                game.updateBooleans();
            } else {
                game.getPlayerToPlay().send(GameMessages.NOT_ALLOWED);
            }

        } else {
            game.getPlayerToPlay().send(GameMessages.NOT_ALLOWED);
        }
    }
}
