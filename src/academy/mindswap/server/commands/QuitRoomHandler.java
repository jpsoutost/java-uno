package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.Messages;

public class QuitRoomHandler implements CommandHandler {

    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        String message = clientConnectionHandler.getMessage();
        String room = clientConnectionHandler.getGame().getRoomName();
        Game game = clientConnectionHandler.getGame();

        clientConnectionHandler.setGame(null);
        game.getPlayers().remove(clientConnectionHandler);
        server.getClientsOnGeneral().add(clientConnectionHandler);
        server.roomBroadcast(game, clientConnectionHandler.getName(), Messages.PLAYER_QUIT_ROOM);
        server.broadcast(clientConnectionHandler.getName(), " Entered in the lobby.");
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
