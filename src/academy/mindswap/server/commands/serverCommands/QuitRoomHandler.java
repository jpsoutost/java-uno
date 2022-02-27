package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.GameMessages;

public class QuitRoomHandler implements CommandHandler {

    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        Game game = clientConnectionHandler.getGame();

        clientConnectionHandler.quitGame();
        clientConnectionHandler.send(GameMessages.WELCOME);

        if (game.getPlayers().isEmpty()) {
            server.getOpenGames().remove(game);
            return;
        }

        for (Server.ClientConnectionHandler player : game.getPlayers()) {
            player.setReady(false);
        }
    }
}
