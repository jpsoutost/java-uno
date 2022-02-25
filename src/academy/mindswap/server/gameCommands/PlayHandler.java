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


            if (chosenCard.getNumber() != game.getLastCardPlayed().getNumber()) {
                game.goFishingCards();
                return;
            }


            if(chosenCard.getNumber() == 13){
                game.setCardsToDraw(game.getCardsToDraw()+4);
                game.getPlayerToPlay().getDeck().remove(chosenCard);
                game.getPlayerToPlay().send(chosenCard.toString());
                game.getServer().roomBroadcast(game,game.getPlayerToPlay().getName(),chosenCard.toString());
                game.getPlayedCards().add(game.getLastCardPlayed());
                game.setLastCardPlayed(chosenCard);
                game.getPlayerToPlay().send(GameMessages.CHOOSE_COLOR);
                game.setPlayerPlayedAlreadyOneCard(true);
                game.setCanFinishTurn(true);
                game.setHasToChooseAColor(true);
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

                game.getPlayerToPlay().getDeck().remove(chosenCard);
                game.getPlayerToPlay().send(chosenCard.toString());
                game.getServer().roomBroadcast(game,game.getPlayerToPlay().getName(),chosenCard.toString());
                game.getPlayedCards().add(game.getLastCardPlayed());
                game.setLastCardPlayed(chosenCard);
                game.setPlayerPlayedAlreadyOneCard(true);
                game.setCanFinishTurn(true);
            }else if (chosenCard.getColor() == game.getLastCardPlayed().getColor() && !game.isPlayerPlayedAlreadyOneCard()) {
                if (chosenCard.getNumber()==10){
                    game.setPlayersToSkip(game.getPlayersToSkip()+1);
                }else if (chosenCard.getNumber()==12){
                    game.dealWithReverse();
                }else if (chosenCard.getNumber()==11) {
                    game.setCardsToDraw(game.getCardsToDraw()+2);
                }
                game.getPlayerToPlay().getDeck().remove(chosenCard);
                game.getPlayerToPlay().send(chosenCard.toString());
                game.getServer().roomBroadcast(game,game.getPlayerToPlay().getName(),chosenCard.toString());
                game.getPlayedCards().add(game.getLastCardPlayed());
                game.setLastCardPlayed(chosenCard);
                game.setPlayerPlayedAlreadyOneCard(true);
                game.setCanPlayAgain(false);
                game.setCanFinishTurn(true);
            }else{
                game.getPlayerToPlay().send(GameMessages.NOT_ALLOWED);
            }
        }else{
            game.getPlayerToPlay().send(GameMessages.NOT_ALLOWED);
        }
    }
}
