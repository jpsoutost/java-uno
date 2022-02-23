package academy.mindswap.server;


import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that represents the game.
 */
public class Game implements Runnable {
    private final String roomName;
    private LinkedList<Card> deck;
    private LinkedList<Card> playedCards;
    private List <Server.ClientConnectionHandler> players;
    private boolean isThereAWinner;
    int indexOfPlayerTurn;
    private Card lastCardPlayed;
    Server server;

    /**
     * Game initialized by creating a new card deck.
     */
    public Game(String roomName, Server server) {
        this.roomName = roomName;
        this.server = server;
        createDeck();
        this.players = new ArrayList<>();
    }

    /**
     * Method that create a deck of cards into a linked list.
     */
    private void createDeck(){
        this.deck = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.YELLOW,i));
        }
      /*  for (int i=10; i < 13; i++){
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.YELLOW,i));
            deck.add(new Card(CardColors.YELLOW,i));
        }*/
        Collections.shuffle(this.deck);
    }

    public void addClient(Server.ClientConnectionHandler clientConnectionHandler){
        this.players.add(clientConnectionHandler);
    }

    /**
     * Method that starts the game and verify the winner.
     * The game is initialized by setting the deck of the players.
     * While there isn't a winner, the game is running by verifying the player's turn and by validating the draw played.
     */
    @Override
    public void run() {

        setPlayersDecks();
        this.lastCardPlayed = getFirstCard();
        boolean playerPlayedAlreadyOneCard = false;
        boolean canFinishTurn=false;
        boolean canPlayAgain = true; // This variable prevents the draw action to not work properly.
        int playersToSkip = 0; //to use increment when skip cards are played
        int cardsToDraw = 0; //to use when plus2 cards are played;


        while (!isThereAWinner) {
            Server.ClientConnectionHandler playerToPlay = players.get(indexOfPlayerTurn);

            if(deck.isEmpty()){
                Collections.shuffle(this.playedCards);
                this.deck = this.playedCards;
                this.playedCards.clear();
            }


            playerToPlay.send(playerToPlay.getName() + " - " + playerToPlay.getDeck());

            while(!playerToPlay.messageChanged){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            playerToPlay.messageChanged = false;
            String play = playerToPlay.getMessage();



            if (play.equals("/finishTurn")) {
                if (canFinishTurn) {
                    playerToPlay.send("End of Turn");
                    server.roomBroadcast(this,playerToPlay.getName(),"End of Turn");
                    indexOfPlayerTurn+=playersToSkip;
                    indexOfPlayerTurn++;
                    if (indexOfPlayerTurn > players.size() - 1) {
                        indexOfPlayerTurn -= players.size();
                    }

                    playerPlayedAlreadyOneCard = false;
                    canPlayAgain = true;
                    canFinishTurn=false;
                    playersToSkip=0;
                    continue;
                } else {
                    playerToPlay.send("You have to play or draw a card first.");
                    continue;
                }
            }

            if (play.equals("/draw")) {
                if (!playerPlayedAlreadyOneCard) {
                    Card newCard = deck.poll();
                    playerToPlay.getDeck().add(newCard);
                    playerToPlay.send("You draw a " + newCard);
                    server.roomBroadcast(this,playerToPlay.getName(),playerToPlay.getName() + " draw a card.");

                    canFinishTurn=true;
                    continue;
                } else {
                    playerToPlay.send("You can't draw more than one card each turn nor if you have already played a card.");
                    continue;
                }

            }

            if (!play.matches("[0-" + (playerToPlay.getDeck().size() - 1) + "]")) {
                playerToPlay.send("Play not legal.");
                continue;
            }

            if(play.equals("/blue") && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.BLUE);
                continue;
            }
            if(play.equals("/yellow") && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.YELLOW);
                continue;
            }
            if(play.equals("/green") && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.GREEN);
                continue;
            }
            if(play.equals("/red") && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.RED);
                continue;
            }


            if (canPlayAgain) {

                Card chosenCard = playerToPlay.getDeck().get(Integer.parseInt(play));

                if (cardsToDraw != 0){
                    if (chosenCard.getNumber() != lastCardPlayed.getNumber()) {
                        for (int i = 0; i < cardsToDraw; i++) {
                            Card newCard = deck.poll();
                            playerToPlay.getDeck().add(newCard);
                            System.out.println("You draw a " + newCard);
                            cardsToDraw = 0;
                            canFinishTurn = false;
                        }
                    }
                }

                if(chosenCard.getNumber() == 13){
                    cardsToDraw=+4;
                    playerToPlay.getDeck().remove(chosenCard);
                    playerToPlay.send(chosenCard.toString());
                    server.roomBroadcast(this,playerToPlay.getName(),chosenCard.toString());
                    this.playedCards.add(lastCardPlayed);
                    lastCardPlayed=chosenCard;
                    playerToPlay.send("Choose color:");
                    playerPlayedAlreadyOneCard = true;
                    canFinishTurn = true;
                    continue;
                }


                if (chosenCard.getNumber() == lastCardPlayed.getNumber()) {
                    if (chosenCard.getNumber()==10){
                        playersToSkip++;
                    }else if (chosenCard.getNumber()==11) {
                        cardsToDraw++;
                        cardsToDraw++;
                    }else if (chosenCard.getNumber()==12){
                        int indexToReverse = players.size()-1;
                        Server.ClientConnectionHandler[] temp = new Server.ClientConnectionHandler[players.size()];
                        for (Server.ClientConnectionHandler p:players) {
                            temp[indexToReverse] = p;
                            indexToReverse--;
                        }
                        players = Arrays.stream(temp).collect(Collectors.toList());
                    }

                    playerToPlay.getDeck().remove(chosenCard);
                    playerToPlay.send(chosenCard.toString());
                    server.roomBroadcast(this,playerToPlay.getName(),chosenCard.toString());
                   // this.playedCards.add(lastCardPlayed);
                    lastCardPlayed = chosenCard;
                    playerPlayedAlreadyOneCard = true;
                    canFinishTurn = true;
                }else if (chosenCard.getColor() == lastCardPlayed.getColor() && !playerPlayedAlreadyOneCard) {
                    if (chosenCard.getNumber()==10){
                        playersToSkip++;
                    }else if (chosenCard.getNumber()==12){
                        int indexToReverse = players.size()-1;
                        Server.ClientConnectionHandler[] temp = new Server.ClientConnectionHandler[players.size()];
                        for (Server.ClientConnectionHandler p:players) {
                            temp[indexToReverse] = p;
                            indexToReverse--;
                        }
                        players = Arrays.stream(temp).collect(Collectors.toList());
                    }else if (chosenCard.getNumber()==11) {
                        cardsToDraw++;
                        cardsToDraw++;
                    }

                    playerToPlay.getDeck().remove(chosenCard);
                    playerToPlay.send(chosenCard.toString());
                    server.roomBroadcast(this,playerToPlay.getName(),chosenCard.toString());
                    //this.playedCards.add(lastCardPlayed);
                    lastCardPlayed = chosenCard;
                    playerPlayedAlreadyOneCard = true;
                    canPlayAgain=false;
                    canFinishTurn = true;
                }else {
                    playerToPlay.send("Play not allowed");
                }
            }else{
                playerToPlay.send("Play not allowed");
            }

            checkIfWinner();
        }
    }

    /**
     * Method that set the players deck into an Array List.
     */
    private void setPlayersDecks(){
        this.players.stream().map(player -> player.getDeck()).forEach(playerDeck -> {
            for (int i = 0; i < 3; i++) {
              playerDeck.add(this.deck.poll());
          }
      });
    }

    /**
     * @return The first card of a player.
     */
    private Card getFirstCard(){
        Card card=this.deck.poll();
        Server.ClientConnectionHandler playerToPlay = players.get(0);
        playerToPlay.send(card.toString());
        server.roomBroadcast(this,playerToPlay.getName(),card.toString());
        return card;
    }

    /**
     * Check if a player is a winner, and if it's true validate de boolean parameter.
     * If it's true, prints the winner player.
     */
    private void checkIfWinner(){
        Server.ClientConnectionHandler playerToPlay = players.get(indexOfPlayerTurn);
        if(playerToPlay.getDeck().size()==0){
            isThereAWinner=true;
            playerToPlay.send("You finished your deck and you are the winner.");
            server.roomBroadcast(this,playerToPlay.getName(),playerToPlay.getName() + " finished his deck and is the winner.");
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public List<Server.ClientConnectionHandler> getPlayers() {
        return players;
    }
}
