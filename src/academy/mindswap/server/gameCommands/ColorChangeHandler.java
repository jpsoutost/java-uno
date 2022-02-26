package academy.mindswap.server.gameCommands;

import academy.mindswap.server.CardColors;
import academy.mindswap.server.Game;
import academy.mindswap.server.Server;

public class ColorChangeHandler implements GameCommandHandler {
    @Override
    public void execute(Game game, Server.ClientConnectionHandler clientConnectionHandler) {
        String play = game.getPlay();

        if(game.getHasToChooseAColor()) {
            switch (play) {
                case "b" -> game.changeColor(CardColors.BLUE);
                case "y" -> game.changeColor(CardColors.YELLOW);
                case "g" -> game.changeColor(CardColors.GREEN);
                case "r" -> game.changeColor(CardColors.RED);
            }
        }else{
            clientConnectionHandler.send("You can't change the color.");
        }

    }
}
