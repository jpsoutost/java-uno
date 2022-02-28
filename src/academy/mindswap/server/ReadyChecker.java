package academy.mindswap.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that checks if all players in each game are ready to play.
 * When all the player in a game are ready, that game starts.
 */
public class ReadyChecker implements Runnable {
    private final Server server;
    boolean someoneIsNotReady;
    private final ExecutorService service;


    public ReadyChecker(Server server) {
        this.server = server;
        service = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        while (true) {

            List<Game> og = new ArrayList<>(server.getOpenGames());

            if (!og.isEmpty()) {
                for (Game game : og) {
                    someoneIsNotReady = false;
                    for (Server.ClientConnectionHandler player : game.getPlayers()) {
                        if (!player.isReady()) {
                            someoneIsNotReady = true;
                            break;
                        }
                    }
                    if (!someoneIsNotReady && game.getPlayers().size() > 1) {
                        server.getClosedGames().add(game);
                        server.getOpenGames().remove(game);
                        service.submit(game);
                        someoneIsNotReady = true;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
