package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

public class QuitRoomHandler implements CommandHandler {

    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        Game game = clientConnectionHandler.getGame();

        if (game == null){
            clientConnectionHandler.send(ServerMessages.PLAYER_NOT_IN_ROOM);
            return;
        }

        clientConnectionHandler.quitGame();
        clientConnectionHandler.send(ServerMessages.REDIRECTED_LOBBY);

        if (game.getPlayers().isEmpty()) {
            server.getOpenGames().remove(game);
            return;
        }

        for (Server.ClientConnectionHandler player : game.getPlayers()) {
            player.setReady(false);
        }
    }
}
