package academy.mindswap.server;


import academy.mindswap.server.gameCommands.GameCommand;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.server.messages.Messages;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that represents the game.
 */
public class Game implements Runnable {

    //GAME ATTRIBUTES
    private final String roomName;
    private LinkedList<Card> deck;
    private LinkedList<Card> playedCards;
    private List<Server.ClientConnectionHandler> players;
    private Server server;

    //GAME LOGIC CONTROL BOOLEANS
    private boolean gameIsRunning;
    private boolean isThereAWinner;
    private boolean playedAtLeastOneCard;
    private boolean canFinishTurn;
    private boolean canPlayAgain;
    private boolean hasToChooseAColor;
    private boolean drewACard;

    //STORAGE OF ROUND DATA
    private int indexOfPlayerTurn;
    private Card lastCardPlayed;
    private String play;
    private Server.ClientConnectionHandler playerToPlay;


    //ACCUMULATORS FOR SPECIAL CARDS
    private int cardsToDraw;
    private int playersToSkip;


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
            deck.add(new Card(CardColors.BLUE,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.GREEN,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.RED,i));
            deck.add(new Card(CardColors.YELLOW,i));
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

        gameIsRunning=true;
        welcomeGuests();
        setPlayersDecks();
        lastCardPlayed = getFirstCard();
        resetBooleansAndAccumulators();


        while (!isThereAWinner) {
            this.playerToPlay = players.get(indexOfPlayerTurn);


            if(players.size()<=1){
                playerToPlay.send("You can't play alone.");
                break;
            }

            if(deck.isEmpty()){
                replaceDeck();
            }

            playerToPlay.send(playerToPlay.showDeck());
            playerToPlay.setGameCommandChanged(false);
            play = waitForPlay();

            if(isServerCommand(play)){
                playerToPlay.send("You can't use a server command inside a game.");
            }

            if (isChat(play)){
                continue;
            }

            play();
            checkIfWinner();
        }

        finishGame();
        gameIsRunning = false;
    }

    private void welcomeGuests(){
        Server.ClientConnectionHandler player = players.get(0);
        player.send(GameMessages.UNO);
        server.roomBroadcast(this, player.getName(), GameMessages.UNO);
    }

    private void play(){
        GameCommand gameCommand = GameCommand.getGameCommandFromDescription(play);

        if (play.matches("[0-" + (playerToPlay.getDeck().size() - 1) + "]")) {
            gameCommand = GameCommand.getGameCommandFromDescription("play");
        }

        if (gameCommand == null) {
            gameCommand = GameCommand.getGameCommandFromDescription("NotLegal");
        }

        gameCommand.getCommandHandler().execute(this, playerToPlay);
    }

    public void changeColor(CardColors cardColors){
        lastCardPlayed.setColor(cardColors);
        playerToPlay.send(GameMessages.COLOR_CHANGED + cardColors.getDescription());
        server.roomBroadcast(this, playerToPlay.getName(), GameMessages.COLOR_CHANGED + cardColors
                .getDescription());
        hasToChooseAColor = false;
    }

    private void replaceDeck(){
        Collections.shuffle(this.playedCards);
        this.deck = this.playedCards;
        this.playedCards.clear();
    }

    public void resetBooleansAndAccumulators(){
        playedAtLeastOneCard = false;
        canFinishTurn=false;
        canPlayAgain = true;
        playersToSkip = 0;
        hasToChooseAColor = false;
        drewACard=false;
    }

    public void finishGame(){
        players.forEach(player -> {
            server.getClientsOnGeneral().add(player);
            player.setGame(null);
            player.setReady(false);
            player.getDeck().clear();
            player.send(Messages.WELCOME);
        });
    }

    public boolean hasCardsToDraw(Card card){
        return card.getNumber() != lastCardPlayed.getNumber() && cardsToDraw!=0 && isFirstCardOfTurn() ;
    }

    public boolean hasCardsToDraw(){
        return cardsToDraw!=0 && isFirstCardOfTurn();
    }

    public void dealWithPlus4Cards(Card card){
        cardsToDraw+=4;
        cardChangesInDecks(card);
        playerToPlay.send(GameMessages.CHOOSE_COLOR);
        updateBooleans();
        hasToChooseAColor = true;
    }

    public void cardChangesInDecks(Card card){
        playerToPlay.getDeck().remove(card);
        playerToPlay.send(card.toString());
        server.roomBroadcast(this,playerToPlay.getName(),card.toString());
        this.playedCards.add(lastCardPlayed);
        lastCardPlayed=card;
    }

    public void updateBooleans(){
        playedAtLeastOneCard = true;
        canFinishTurn = true;
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

    private boolean isChat(String play){
        return play.startsWith("-");
    }

    private boolean isServerCommand(String play){
        return play.startsWith("/");
    }

    private String waitForPlay (){
        while(!playerToPlay.isGameCommandChanged()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return playerToPlay.getMessage();
    }



    /**
     * Method that set the players deck into an Array List.
     */
    private void setPlayersDecks(){
        this.players.stream().map(Server.ClientConnectionHandler::getDeck).forEach(playerDeck -> {
            for (int i = 0; i < 7; i++) {
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
            playerToPlay.send(GameMessages.THE_WINNER);
            server.roomBroadcast(this,playerToPlay.getName(),playerToPlay.getName() + GameMessages.THE_WINNER); //here
        }
    }

    public void goFishingCards(){
        if (cardsToDraw != 0 && !playedAtLeastOneCard){
            for (int i = 0; i < cardsToDraw; i++) {
                drawCard();
                drewACard=false;
            }
            cardsToDraw = 0;
            canFinishTurn = false;
        }
    }

    public void drawCard(){
        Card newCard = deck.poll();
        playerToPlay.getDeck().add(newCard);
        playerToPlay.send(GameMessages.YOU_DRAW + newCard);
        server.roomBroadcast(this,playerToPlay.getName(),playerToPlay.getName() + GameMessages.PLAYER_DRAW);
        drewACard=true;
    }

    public void setNextPlayerToPlay(){
        indexOfPlayerTurn+=playersToSkip;
        indexOfPlayerTurn++;
        if(indexOfPlayerTurn > players.size()-1){
            indexOfPlayerTurn-=players.size();
        }
    }

    public boolean canPlayACard(){
        return canPlayAgain && !hasToChooseAColor;
    }

    public boolean isASkipCard(Card card){
        return card.getNumber() == 10;
    }

    public boolean isAPlus2Card(Card card){
        return card.getNumber() == 11;
    }

    public boolean isAReverseCard(Card card){
        return card.getNumber() == 12;
    }

    public boolean isAPlus4Card(Card card){
        return card.getNumber() == 13;
    }

    public void dealWithSpecialCards(Card card){
        if (isASkipCard(card)){
            playersToSkip++;
        }else if (isAReverseCard(card)){
            dealWithReverse();
        }else if (isAPlus2Card(card)) {
            cardsToDraw+=2;
        }
    }

    public boolean canDrawACard(){
        return !playedAtLeastOneCard && !drewACard;
    }

    public boolean canPlayAPlus4Card() {
        return lastCardPlayed.getNumber() == 13 || isFirstCardOfTurn();
    }

    public boolean isLastPlayer(){
        return (players.indexOf(playerToPlay)) == players.size()-1;
    }

    public void changeLastPlayer(){
        indexOfPlayerTurn=0;
    }

    //GETTERS

    public String getRoomName() {
        return roomName;
    }

    public List<Server.ClientConnectionHandler> getPlayers() {
        return players;
    }

    public Card getLastCardPlayed() {
        return lastCardPlayed;
    }

    public Server getServer() {
        return server;
    }

    public Boolean getHasToChooseAColor() {
        return hasToChooseAColor;
    }

    public boolean isFirstCardOfTurn() {
        return !playedAtLeastOneCard;
    }

    public boolean canFinishTurn() {
        return canFinishTurn;
    }

    public String getPlay() {
        return play;
    }

    public Server.ClientConnectionHandler getPlayerToPlay() {
        return playerToPlay;
    }

    public boolean gameIsRunning() {
        return gameIsRunning;
    }

    //SETTERS

    public void setServer(Server server) {
        this.server = server;
    }

    public void setCanFinishTurn(boolean canFinishTurn) {
        this.canFinishTurn = canFinishTurn;
    }

    public void setCanPlayAgain(boolean canPlayAgain) {
        this.canPlayAgain = canPlayAgain;
    }

}
