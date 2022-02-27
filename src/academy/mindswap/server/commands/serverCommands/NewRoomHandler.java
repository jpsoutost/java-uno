package academy.mindswap.server.commands.serverCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;
import academy.mindswap.server.messages.ServerMessages;

import java.util.Set;
import java.util.stream.Collectors;

public class NewRoomHandler implements CommandHandler {
    Set<String> openGames;

        @Override
        public void execute(Server server, Server.ClientConnectionHandler clientConnectionHandler) {
            String message = clientConnectionHandler.getMessage();
            this.openGames = server.getOpenGames().stream().map(Game::getRoomName).collect(Collectors.toSet());

        if (message.split(" ").length != 2) {
            clientConnectionHandler.send(ServerMessages.JOIN_WRONG);
            return;
        }

        String roomName = message.split(" ")[1];

        if (openGames.contains(roomName)) {
            clientConnectionHandler.send(ServerMessages.ALREADY_CREATED_ROOM);
        } else {
            clientConnectionHandler.createRoom(roomName);
            clientConnectionHandler.send(ServerMessages.ROOM_CREATED + message.substring(12));
        }
    }
}
