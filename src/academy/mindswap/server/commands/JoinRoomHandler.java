package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.Messages;

import java.util.Optional;

public class JoinRoomHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        String message = clientConnectionHandler.getMessage();

        if (message.split(" ").length < 2) {
            clientConnectionHandler.send("Wrong way to join a Room.");
            return;
        }

        String roomName = message.split(" ")[1];

        Optional<Game> game = server.getOpenGames().stream().filter(room -> room.getRoomName().equals(roomName)).findFirst();

        if (game.isEmpty()) {
            clientConnectionHandler.send("No such room.");
            return;
        }

        game.get().addClient(clientConnectionHandler);
        clientConnectionHandler.setGame(game.get());
        server.getClientsOnGeneral().remove(clientConnectionHandler);
        clientConnectionHandler.send("You joined room " + message);
        server.roomBroadcast(game.get(),clientConnectionHandler.getName(),clientConnectionHandler.getName() + " entered the room.");
    }
}
