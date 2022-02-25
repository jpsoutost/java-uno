package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Card;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

public class PlayHandler implements GameCommandHandler{
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        String play = game.getPlay();

        if (game.isCanPlayAgain() && !game.getHasToChooseAColor()) {

            Card chosenCard = game.getPlayerToPlay().getDeck().get(Integer.parseInt(play));

            if (game.hasCardsToDraw(chosenCard)) {
                game.goFishingCards();
                return;
            }


            if(game.isAPlus4Card(chosenCard)){
                game.dealWithPlus4Cards(chosenCard);
                return;
            }


            if (chosenCard.getNumber() == game.getLastCardPlayed().getNumber()) {
                if (chosenCard.getNumber()==10){
                    game.setPlayersToSkip(game.getPlayersToSkip()+1);
                }else if (chosenCard.getNumber()==11) {
                    game.setCardsToDraw(game.getCardsToDraw() +2);
                }else if (chosenCard.getNumber()==12){
                    game.dealWithReverse();
                }

                game.cardChangesInDecks(chosenCard);
                game. updateBooleans();

            }else if (chosenCard.getColor() == game.getLastCardPlayed().getColor() && !game.isPlayerPlayedAlreadyOneCard()) {

                if (chosenCard.getNumber()==10){
                    game.setPlayersToSkip(game.getPlayersToSkip()+1);
                }else if (chosenCard.getNumber()==12){
                    game.dealWithReverse();
                }else if (chosenCard.getNumber()==11) {
                    game.setCardsToDraw(game.getCardsToDraw()+2);
                }

                game.cardChangesInDecks(chosenCard);
                game.updateBooleans();
                game.setCanPlayAgain(false);

            }else{
                game.getPlayerToPlay().send(GameMessages.NOT_ALLOWED);
            }
        }else{
            game.getPlayerToPlay().send(GameMessages.NOT_ALLOWED);
        }
    }
}
