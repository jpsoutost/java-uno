package academy.mindswap.server;


import academy.mindswap.server.messages.GameMessages;

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
    private int indexOfPlayerTurn;
    private Card lastCardPlayed;
    private int cardsToDraw;
    private Server server;
    private Boolean hasToChooseAColor;

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
        this.playedCards = new LinkedList<>();

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
        for (int i = 0; i < 4; i++) {
            deck.add(new Card(CardColors.BLUE,13));
        }
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



        while (!isThereAWinner) {
            Server.ClientConnectionHandler playerToPlay = players.get(indexOfPlayerTurn);

            if(deck.isEmpty()){
                Collections.shuffle(this.playedCards);
                this.deck = this.playedCards;
                this.playedCards.clear();
            }


            playerToPlay.send(playerToPlay.getName() + " - " + playerToPlay.getDeck());

            while(!playerToPlay.isGameCommandChanged()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            playerToPlay.setGameCommandChanged(false);
            String play = playerToPlay.getMessage();
            if (play.startsWith("-") || play.startsWith("/")){
                continue;
            }



            if (play.equals("finishTurn") && !hasToChooseAColor) {
                if (canFinishTurn) {
                    playerToPlay.send(GameMessages.END_TURN);
                    server.roomBroadcast(this,playerToPlay.getName(),GameMessages.END_TURN);
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
                    playerToPlay.send(GameMessages.NOT_PLAYED); // here
                    continue;
                }
            }

            if (play.equals("draw") && !hasToChooseAColor) {
                if (!playerPlayedAlreadyOneCard) {
                    Card newCard = deck.poll();
                    playerToPlay.getDeck().add(newCard);
                    playerToPlay.send(GameMessages.YOU_DRAW + newCard);
                    server.roomBroadcast(this,playerToPlay.getName(),playerToPlay.getName() + GameMessages.PLAYER_DRAW);

                    canFinishTurn=true;
                    continue;
                } else {
                    playerToPlay.send(GameMessages.JUST_ONE_CARD); // here
                    continue;
                }

            }

            if(play.equals(CardColors.BLUE.getDescription()) && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.BLUE);
                playerToPlay.send(GameMessages.COLOR_CHANGED + CardColors.BLUE.getDescription()); //HERE
                server.roomBroadcast(this,playerToPlay.getName(),GameMessages.COLOR_CHANGED + CardColors.BLUE.getDescription());
                hasToChooseAColor=false;
                continue;
            }
            if(play.equals(CardColors.YELLOW.getDescription()) && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.YELLOW);
                playerToPlay.send(GameMessages.COLOR_CHANGED + CardColors.YELLOW.getDescription()); //HERE
                server.roomBroadcast(this,playerToPlay.getName(),GameMessages.COLOR_CHANGED + CardColors.YELLOW.getDescription());
                hasToChooseAColor=false;
                continue;
            }
            if(play.equals(CardColors.GREEN.getDescription()) && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.GREEN);
                playerToPlay.send(GameMessages.COLOR_CHANGED + CardColors.GREEN.getDescription());//HERE
                server.roomBroadcast(this,playerToPlay.getName(),GameMessages.COLOR_CHANGED + CardColors.GREEN.getDescription());
                hasToChooseAColor=false;
                continue;
            }
            if(play.equals(CardColors.RED.getDescription()) && lastCardPlayed.getNumber()==13){
                lastCardPlayed.setColor(CardColors.RED);
                playerToPlay.send(GameMessages.COLOR_CHANGED + CardColors.RED.getDescription()); //HERE
                server.roomBroadcast(this,playerToPlay.getName(),GameMessages.COLOR_CHANGED + CardColors.RED.getDescription());
                hasToChooseAColor=false;
                continue;
            }

            if (!play.matches("[0-" + (playerToPlay.getDeck().size() - 1) + "]")) {
                playerToPlay.send(GameMessages.NOT_LEGAL);
                hasToChooseAColor=false;
                continue;
            }


            if (canPlayAgain && !hasToChooseAColor) {

                Card chosenCard = playerToPlay.getDeck().get(Integer.parseInt(play));

                if (cardsToDraw != 0 && !playerPlayedAlreadyOneCard){
                    if (chosenCard.getNumber() != lastCardPlayed.getNumber()) {
                        for (int i = 0; i < cardsToDraw; i++) {
                            Card newCard = deck.poll();
                            playerToPlay.getDeck().add(newCard);
                            playerToPlay.send(GameMessages.YOU_DRAW + newCard);
                            server.roomBroadcast(this,playerToPlay.getName(),playerToPlay.getName() + GameMessages.PLAYER_DRAW);
                        }
                        cardsToDraw = 0;
                        canFinishTurn = false;
                        continue;
                    }
                }

                if(chosenCard.getNumber() == 13){
                    cardsToDraw+=4;
                    playerToPlay.getDeck().remove(chosenCard);
                    playerToPlay.send(chosenCard.toString());
                    server.roomBroadcast(this,playerToPlay.getName(),chosenCard.toString());
                    this.playedCards.add(lastCardPlayed);
                    lastCardPlayed=chosenCard;
                    playerToPlay.send(GameMessages.CHOOSE_COLOR);
                    playerPlayedAlreadyOneCard = true;
                    canFinishTurn = true;
                    continue;
                }


                if (chosenCard.getNumber() == lastCardPlayed.getNumber()) {
                    if (chosenCard.getNumber()==10){
                        playersToSkip++;
                    }else if (chosenCard.getNumber()==11) {
                        cardsToDraw+=2;
                    }else if (chosenCard.getNumber()==12){
                        dealWithReverse();
                    }

                    playerToPlay.getDeck().remove(chosenCard);
                    playerToPlay.send(chosenCard.toString());
                    server.roomBroadcast(this,playerToPlay.getName(),chosenCard.toString());
                    this.playedCards.add(lastCardPlayed);
                    lastCardPlayed = chosenCard;
                    playerPlayedAlreadyOneCard = true;
                    canFinishTurn = true;
                }else if (chosenCard.getColor() == lastCardPlayed.getColor() && !playerPlayedAlreadyOneCard) {
                    if (chosenCard.getNumber()==10){
                        playersToSkip++;
                    }else if (chosenCard.getNumber()==12){
                        dealWithReverse();
                    }else if (chosenCard.getNumber()==11) {
                        cardsToDraw+=2;
                    }
                    playerToPlay.getDeck().remove(chosenCard);
                    playerToPlay.send(chosenCard.toString());
                    server.roomBroadcast(this,playerToPlay.getName(),chosenCard.toString());
                    this.playedCards.add(lastCardPlayed);
                    lastCardPlayed = chosenCard;
                    playerPlayedAlreadyOneCard = true;
                    canPlayAgain=false;
                    canFinishTurn = true;
                }else {
                    playerToPlay.send(GameMessages.NOT_ALLOWED); //here
                }
            }else{
                playerToPlay.send(GameMessages.NOT_ALLOWED); //here
            }

            checkIfWinner();
        }
    }

    private void dealWithReverse(){
        Server.ClientConnectionHandler p = players.get(indexOfPlayerTurn);
        invertPlayers();

        Optional<Server.ClientConnectionHandler> playerPlaying = players.stream().filter(player -> player==p).findFirst();
        indexOfPlayerTurn = players.indexOf(playerPlaying.get());
    }

    private void invertPlayers(){
        int indexToReverse = players.size()-1;
        Server.ClientConnectionHandler[] temp = new Server.ClientConnectionHandler[players.size()];
        for (Server.ClientConnectionHandler p:players) {
            temp[indexToReverse] = p;
            indexToReverse--;
        }
        players = Arrays.stream(temp).collect(Collectors.toList());
    }



    /**
     * Method that set the players deck into an Array List.
     */
    private void setPlayersDecks(){
        this.players.stream().map(player -> player.getDeck()).forEach(playerDeck -> {
            for (int i = 0; i < 5; i++) {
              playerDeck.add(this.deck.poll());
          }
      });
    }

    /**
     * @return The first card of a player.
     */
    private Card getFirstCard(){
        Card card=this.deck.poll();
        while (card.getNumber()>9){
            this.playedCards.add(card);
            card = deck.poll();
        }
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
            playerToPlay.send(GameMessages.THE_WINNER); //here
            server.roomBroadcast(this,playerToPlay.getName(),playerToPlay.getName() + GameMessages.THE_WINNER); //here
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public List<Server.ClientConnectionHandler> getPlayers() {
        return players;
    }
}
