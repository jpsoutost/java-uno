package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.ReadyChecker;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.CommandsMessages;

import java.util.Optional;

public class JoinRoomHandler implements CommandHandler {
    @Override
    public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
        String message = clientConnectionHandler.getMessage();

        if (message.split(" ").length != 2) {
            clientConnectionHandler.send(CommandsMessages.JOIN_WRONG);
            return;
        }

        String roomName = message.split(" ")[1];

        Optional<Game> game = server.getOpenGames().stream().filter(room -> room.getRoomName().equals(roomName))
                .findFirst();

        if (game.isEmpty()) {
            clientConnectionHandler.send(CommandsMessages.SUCH_ROOM);
            return;
        }

        if (game.get().getPlayers().size() >= 10) {
            clientConnectionHandler.send(CommandsMessages.ROOM_FULL);
        }

        clientConnectionHandler.enteringRoom(game.get());
        clientConnectionHandler.send(CommandsMessages.JOINED_ROOM + message.substring(10));
        server.roomBroadcast(clientConnectionHandler.getGame(), clientConnectionHandler.getName(),
                CommandsMessages.ENTERED);
    }
}
