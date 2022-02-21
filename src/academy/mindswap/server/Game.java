package academy.mindswap.server;

import academy.mindswap.client.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Game {
    private LinkedList<Card> deck;
    private List <Client> players;
    private List <PlayerDeck> playersDecks;
    private boolean isThereAWinner;
    int indexOfPlayerTurn;


    public Game() {
        createDeck();
    }

    private void createDeck(){
        this.deck = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.YELLOW,i));
        }
        Collections.shuffle(this.deck);
    }

    public void start(List<Client> playersList) throws IOException {
        this.players = playersList;
        setPlayersDecks();
        getFirstCard();

        while (!isThereAWinner){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String play = in.readLine();

            if(play.equals("/list")){
                System.out.println(playersDecks.get(indexOfPlayerTurn));
                continue;
            }
            System.out.println(playersDecks.get(indexOfPlayerTurn).getPlayerDeck().remove(Integer.parseInt(play)));

            if(play.equals("/finishTurn")){
                System.out.println("End of Turn");
                indexOfPlayerTurn = indexOfPlayerTurn == players.size()-1 ? 0:indexOfPlayerTurn++;
            }
        }
    }

    private void setPlayersDecks(){
        this.playersDecks = new ArrayList<>();
        this.players.stream().forEach(player -> playersDecks.add(new PlayerDeck(player)));
        playersDecks.forEach(playersDecks -> {
          for (int i = 0; i < 3; i++) {
              playersDecks.getPlayerDeck().add(this.deck.poll());
          }
      });
    }

    private void getFirstCard(){
        System.out.println(this.deck.poll());
    }
}
