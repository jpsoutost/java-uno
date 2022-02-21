package academy.mindswap.server;

import academy.mindswap.client.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        List<Client> playersList = new ArrayList<Client>(
                Arrays.asList(client1, client2, client3));
        try {
            game.start(playersList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
