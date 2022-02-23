package academy.mindswap.server;

import academy.mindswap.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that represents the deck of cards of a player.
 */
public class PlayerDeck {
    private List<Card> playerDeck;
    private Client client;

    /**
     * A player deck is initialized with an empty array list.
     * @param client The player who will receive the deck.
     */
    public PlayerDeck(Client client) {
        this.client = client;
        this.playerDeck = new ArrayList<>();
    }

    /**
     * @return A deck of cards to a player.
     */
    public List<Card> getPlayerDeck() {
        return playerDeck;
    }

    /**
     * @return A client.
     */
    public Client getClient() {
        return client;
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

