package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.Messages;

/**
 * A class that represents the command to one player leave the room, that implements CommandHandler.
 */
public class QuitRoomHandler implements CommandHandler {

    /**
     * An override method that executes the command to a player leave/quit the room.
     * @param server
     * @param clientConnectionHandler
     */
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        Game game = clientConnectionHandler.getGame();

        clientConnectionHandler.quitGame();

        if (game.getPlayers().isEmpty()){
            server.getOpenGames().remove(game);
            return;
        }

        for (Server.ClientConnectionHandler player: game.getPlayers()) {
            player.setReady(false);
            player.send("Someone left the room and you became unready.");
        }

    }


}
