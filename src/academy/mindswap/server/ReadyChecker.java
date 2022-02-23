package academy.mindswap.server;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ReadyChecker implements Runnable{
    private List<Game> openGames;
    private List<Game> closedGames;
    private ExecutorService service;

    public ReadyChecker(List<Game> openGames, List<Game> closedGames, ExecutorService service) {
        this.openGames = openGames;
        this.closedGames = closedGames;
        this.service = service;
    }

    @Override
    public void run() {
        while(true) {
            boolean someoneIsNotReady = false;
            synchronized (this) {
                for (Game game : openGames) {
                    for (Server.ClientConnectionHandler player : game.getPlayers()) {
                        if (!player.isReady()) {
                            someoneIsNotReady = true;
                            break;
                        }
                    }
                    if (!someoneIsNotReady) {
                        openGames.remove(game);
                        closedGames.add(game);
                        game.getPlayers().get(0).messageChanged=false;
                        service.submit(game);
                    }
                    someoneIsNotReady = false;
                }
            }
        }
    }
}
