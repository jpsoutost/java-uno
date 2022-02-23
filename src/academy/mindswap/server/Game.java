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

/**
 * A class that represents the game.
 */
public class Game {
    private LinkedList<Card> deck;
    private List <Client> players;
    private List <PlayerDeck> playersDecks;
    private boolean isThereAWinner;
    int indexOfPlayerTurn;
    private Card lastCardPlayed;

    /**
     * Game initialized by creating a new card deck.
     */
    public Game() {
        createDeck();
    }

    /**
     * Method that create a deck of cards into a linked list.
     */
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

    /**
     * Method that starts the game and verify the winner.
     * The game is initialized by setting the deck of the players.
     * While isn't there a winner, the game is running by verifying the player's turn and by validating the draw played.
     * @param playersList The list of the players that is going to play the game.
     * @throws IOException To consider the exceptions produced by failed or interrupted I/O operations.
     */
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

            Card choosenCard = playersDecks.get(indexOfPlayerTurn).getPlayerDeck().get(Integer.parseInt(play));
            if(choosenCard.getColor() == lastCardPlayed.getColor() || choosenCard.getNumber() == lastCardPlayed.getNumber()){
                System.out.println(playersDecks.get(indexOfPlayerTurn).getPlayerDeck().remove(Integer.parseInt(play)));
                lastCardPlayed = choosenCard;
                canFinishTurn = true;
            }else{
                System.out.println("Play not allowed");
                continue;
            }
            checkIfWinner();
        }
    }

    /**
     * Method that set the players deck into an Array List.
     */
    private void setPlayersDecks(){
        this.playersDecks = new ArrayList<>();
        this.players.stream().forEach(player -> playersDecks.add(new PlayerDeck(player)));
        playersDecks.forEach(playersDecks -> {
          for (int i = 0; i < 3; i++) {
              playersDecks.getPlayerDeck().add(this.deck.poll());
          }
      });
    }

    /**
     * @return The first card of a player.
     */
    private Card getFirstCard(){
        Card card=this.deck.poll();
        System.out.println(card);
        return card;
    }

    /**
     * Check if a player is a winner, and if it's true validate de boolean parameter.
     * If it's true, prints the winner player.
     */
    private void checkIfWinner(){
        if(playersDecks.get(indexOfPlayerTurn).getPlayerDeck().size()==0){
            isThereAWinner=true;
            System.out.println(indexOfPlayerTurn + "finished his deck and is the winner.");
        }
    }
}
