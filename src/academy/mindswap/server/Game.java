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
    private Card lastCardPlayed;


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
        this.lastCardPlayed = getFirstCard();
        boolean canFinishTurn = false;


        while (!isThereAWinner){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String play = in.readLine();

            if(play.equals("/list")){
                System.out.println(indexOfPlayerTurn + " - " + playersDecks.get(indexOfPlayerTurn));
                continue;
            }

            if(play.equals("/finishTurn")){
                if (canFinishTurn) {
                    System.out.println("End of Turn");
                    if (indexOfPlayerTurn == players.size() - 1) {
                        indexOfPlayerTurn = 0;
                    } else {
                        indexOfPlayerTurn++;
                    }

                    canFinishTurn=false;
                    continue;
                }else{
                    System.out.println("You have to play or draw a card first.");
                    continue;
                }
            }
            if(play.equals("/draw")){
                if(!canFinishTurn){
                    Card newCard = deck.poll();
                    playersDecks.get(indexOfPlayerTurn).getPlayerDeck().add(newCard);
                    System.out.println("You draw a " + newCard);
                    canFinishTurn = true;
                    continue;
                }else{
                    System.out.println("You can't draw more than one card each turn.");
                    continue;
                }

            }
            if(!play.matches("[0-" + (playersDecks.get(indexOfPlayerTurn).getPlayerDeck().size() -1) + "]")){
                System.out.println("Play not legal.");
                continue;
            }

            Card chosenCard = playersDecks.get(indexOfPlayerTurn).getPlayerDeck().get(Integer.parseInt(play));
            if(chosenCard.getNumber() == lastCardPlayed.getNumber() || chosenCard.getColor() == lastCardPlayed.getColor()){
                System.out.println(playersDecks.get(indexOfPlayerTurn).getPlayerDeck().remove(Integer.parseInt(play)));
                canFinishTurn = true;
                if (chosenCard.getNumber() == lastCardPlayed.getNumber()) {
                    canFinishTurn = true;
                    this.lastCardPlayed = chosenCard;
                }
                lastCardPlayed = chosenCard;
                // gruardar numa variavel numero daquela carta
                // quando o deck size = 0
            } else {
                System.out.println("Play not allowed");
                continue;
            }
            checkIfWinner();
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

    private Card getFirstCard(){
        Card card=this.deck.poll();
        System.out.println(card);
        return card;
    }

    private void checkIfWinner(){
        if(playersDecks.get(indexOfPlayerTurn).getPlayerDeck().size()==0){
            isThereAWinner=true;
            System.out.println(indexOfPlayerTurn + "finished his deck and is the winner.");
        }
    }
}
