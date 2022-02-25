package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.commands.CommandHandler;
import academy.mindswap.server.messages.GameMessages;

public class FinishHandler implements GameCommandHandler {

    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        if (!game.getHasToChooseAColor()) {
            if (game.isCanFinishTurn()) {
                clientConnectionHandler.send(GameMessages.END_TURN);
                game.getServer().roomBroadcast(game, clientConnectionHandler.getName(), GameMessages.END_TURN);
                game.setIndexOfPlayerTurn(game.getIndexOfPlayerTurn() + game.getPlayersToSkip());
                game.setIndexOfPlayerTurn(game.getIndexOfPlayerTurn() + 1);

                if (game.getIndexOfPlayerTurn() > game.getPlayers().size() - 1) {
                    game.setIndexOfPlayerTurn(game.getIndexOfPlayerTurn() - game.getPlayers().size());
                }

                game.setPlayerPlayedAlreadyOneCard(false);
                game.setCanPlayAgain(true);
                game.setCanFinishTurn(false);
                game.setPlayersToSkip(0);

            } else {
                clientConnectionHandler.send(GameMessages.NOT_PLAYED);
            }
        } else {
            clientConnectionHandler.send(GameMessages.CHOOSE_COLOR);
        }
    }
}
