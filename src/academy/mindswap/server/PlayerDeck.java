package academy.mindswap.server;

import academy.mindswap.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDeck {
    private List<Card> playerDeck;
    private Client client;

    public PlayerDeck(Client client) {
        this.client = client;
        this.playerDeck = new ArrayList<>();
    }

    public List<Card> getPlayerDeck() {
        return playerDeck;
    }

    @Override
    public String toString() {
        String string="";
        for (Card card:playerDeck) {
            string += "[" + card + "]";
        }
        return string;
    }
}

