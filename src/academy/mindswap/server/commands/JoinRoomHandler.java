package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.ReadyChecker;
import academy.mindswap.server.Server;

import java.util.Optional;

public class JoinRoomHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        String message = clientConnectionHandler.getMessage();

        if (message.split(" ").length != 2) {
            clientConnectionHandler.send("Wrong way to join a Room.");
            return;
        }

        String roomName = message.split(" ")[1];

        Optional<Game> game = server.getOpenGames().stream().filter(room -> room.getRoomName().equals(roomName)).findFirst();

        if (game.isEmpty()) {
            clientConnectionHandler.send("No such room.");
            return;
        }

        clientConnectionHandler.enteringRoom(game.get());
        clientConnectionHandler.send("You joined room " + message.substring(10));
        server.roomBroadcast(clientConnectionHandler.getGame(),clientConnectionHandler.getName()," entered the room.");
    }
}
