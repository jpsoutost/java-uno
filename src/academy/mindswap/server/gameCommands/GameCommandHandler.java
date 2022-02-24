package academy.mindswap.server.gameCommands;

import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public interface GameCommandHandler {
    void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler);
}
