package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.commands.CommandHandler;

public class FinishHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
            if (game.canFinishTurn()) {
                clientConnectionHandler.send("End of Turn");
                game.getServer().roomBroadcast(game, clientConnectionHandler.getName(),"End of Turn");
                game.setIndexOfPlayerTurn(game.getIndexOfPlayerTurn() + game.getPlayersToSkip());
                game.setIndexOfPlayerTurn(game.getIndexOfPlayerTurn()+1);

                if (game.getIndexOfPlayerTurn() > game.getPlayers().size() - 1) {
                    game.setIndexOfPlayerTurn(game.getIndexOfPlayerTurn()-game.getPlayers().size());
                }

                game.setPlayerPlayedAlreadyOneCard(false);
                game.setCanPlayAgain(true);
                game.setCanFinishTurn(false);
                game.setPlayersToSkip(0);

            } else {
                clientConnectionHandler.send("You have to play or draw a card first.");
            }
    }

}
