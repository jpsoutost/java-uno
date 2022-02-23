package academy.mindswap.server;

import academy.mindswap.client.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final String roomName;
    private LinkedList<Card> deck;
    private LinkedList<Card> playedCards;
    private List <Server.ClientConnectionHandler> players;
    private List <PlayerDeck> playersDecks;
    private boolean isThereAWinner;
    int indexOfPlayerTurn;
    private Card lastCardPlayed;


    public Game(String roomName) {
        this.roomName = roomName;
        createDeck();
        this.players = new ArrayList<>();
    }

    private void createDeck(){
        this.deck = new LinkedList<>();
        this.playersDecks = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.YELLOW,i));
        }
        for (int i=10; i < 13; i++){
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.YELLOW,i));
            deck.add(new Card(CardColors.YELLOW,i));
        }
        Collections.shuffle(this.deck);
    }

    public void addClient(Server.ClientConnectionHandler clientConnectionHandler){
        this.players.add(clientConnectionHandler);
    }

    public void start() throws IOException {
        setPlayersDecks();
        this.lastCardPlayed = getFirstCard();
        boolean playerPlayedAlreadyOneCard = false;
        boolean canFinishTurn=false;
        boolean canPlayAgain = true; // This variable prevents the draw action to not work properly.
        int playersToSkip = 0; //to use increment when skip cards are played
        int cardsToDraw = 0; //to use when plus2 cards are played;


        while (!isThereAWinner) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String play = in.readLine();

            if(deck.isEmpty()){
                Collections.shuffle(this.playedCards);
                this.deck = this.playedCards;
                this.playedCards.clear();
            }

            if (play.equals("/list")) {
                System.out.println(indexOfPlayerTurn + " - " + playersDecks.get(indexOfPlayerTurn));
                continue;
            }

            if (play.equals("/finishTurn")) {
                if (canFinishTurn) {
                    System.out.println("End of Turn");
                    indexOfPlayerTurn+=playersToSkip;
                    indexOfPlayerTurn++;
                    if (indexOfPlayerTurn > players.size() - 1) {
                        indexOfPlayerTurn -= players.size() - 1;
                    }

                    playerPlayedAlreadyOneCard = false;
                    canPlayAgain = true;
                    canFinishTurn=false;
                    playersToSkip=0;
                    continue;
                } else {
                    System.out.println("You have to play or draw a card first.");
                    continue;
                }
            }
            if (play.equals("/draw")) {
                if (!playerPlayedAlreadyOneCard) {
                    Card newCard = deck.poll();
                    playersDecks.get(indexOfPlayerTurn).getPlayerDeck().add(newCard);
                    System.out.println("You draw a " + newCard);
                    canFinishTurn=true;
                    continue;
                } else {
                    System.out.println("You can't draw more than one card each turn nor if you have already played a card.");
                    continue;
                }

            }
            if (!play.matches("[0-" + (playersDecks.get(indexOfPlayerTurn).getPlayerDeck().size() - 1) + "]")) {
                System.out.println("Play not legal.");
                continue;
            }



            if (canPlayAgain) {

                Card chosenCard = playersDecks.get(indexOfPlayerTurn).getPlayerDeck().get(Integer.parseInt(play));


                if (chosenCard.getNumber() == lastCardPlayed.getNumber()) {
                    if (chosenCard.getNumber()==10){
                        playersToSkip++;
                    }else if (chosenCard.getNumber()==11) {
                        cardsToDraw++;
                        cardsToDraw++;
                    }else if (chosenCard.getNumber()==12){
                        int indexToReverse = playersDecks.size()-1;
                        PlayerDeck[] temp = new PlayerDeck[playersDecks.size()];
                        for (PlayerDeck pd:playersDecks) {
                            temp[indexToReverse] = pd;
                            indexToReverse--;
                        }
                        playersDecks = Arrays.stream(temp).collect(Collectors.toList());
                    }

                    System.out.println(playersDecks.get(indexOfPlayerTurn).getPlayerDeck().remove(Integer.parseInt(play)));
                    this.playedCards.add(lastCardPlayed);
                    lastCardPlayed = chosenCard;
                    playerPlayedAlreadyOneCard = true;
                    canFinishTurn = true;
                    continue;
                }
                if (chosenCard.getColor() == lastCardPlayed.getColor() || !playerPlayedAlreadyOneCard) {
                    if (chosenCard.getNumber()==10){
                        playersToSkip++;
                    }else if (chosenCard.getNumber()==12){
                        int indexToReverse = playersDecks.size()-1;
                        PlayerDeck[] temp = new PlayerDeck[playersDecks.size()];
                        for (PlayerDeck pd:playersDecks) {
                            temp[indexToReverse] = pd;
                            indexToReverse--;
                        }
                        playersDecks = Arrays.stream(temp).collect(Collectors.toList());
                    }
                    if (cardsToDraw != 0){
                        for (int i = 0; i < cardsToDraw; i++) {
                            Card newCard = deck.poll();
                            playersDecks.get(indexOfPlayerTurn).getPlayerDeck().add(newCard);
                            System.out.println("You draw a " + newCard);
                        }
                    }
                    System.out.println(playersDecks.get(indexOfPlayerTurn).getPlayerDeck().remove(Integer.parseInt(play)));
                    this.playedCards.add(lastCardPlayed);
                    lastCardPlayed = chosenCard;
                    playerPlayedAlreadyOneCard = true;
                    canPlayAgain=false;
                    canFinishTurn = true;
                    continue;
                }else {
                    System.out.println("Play not allowed");
                    continue;
                }
            }

            checkIfWinner();
        }
    }

    private void setPlayersDecks(){
        this.playersDecks = new ArrayList<>();
        this.players.stream().map(player -> player.getName()).forEach(player -> playersDecks.add(new PlayerDeck(player)));
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

    public String getRoomName() {
        return roomName;
    }
}
