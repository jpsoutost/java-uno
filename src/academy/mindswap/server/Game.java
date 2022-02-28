package academy.mindswap.server;


import academy.mindswap.server.commands.gameCommands.GameCommand;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.server.messages.ServerMessages;


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
    private boolean saidUNO;
    private boolean someoneWentDown;

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
     * @param roomName The name of the room/game.
     * @param server The server in which this game occurs.
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

    /**
     * Method that add a new player to this game.
     * @param clientConnectionHandler The player.
     */
    public void addClient(Server.ClientConnectionHandler clientConnectionHandler){
        this.players.add(clientConnectionHandler);
    }

    /**
     * Method that start the game, run the game and verify the winner.
     * The game is initialized by setting the deck of the players.
     * While there isn't a winner, the game is running by verifying who is the player to play
     * and by validating the play.
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


            if(checkNumberOfPlayers()){
                break;
            }

            if(deck.isEmpty()){
                replaceDeck();
            }

            playerToPlay.send(playerToPlay.showDeck());
            playerToPlay.setGameCommandChanged(false);
            play = waitForPlay();

            if(someoneWentDown){
                break;
            }

            if(isServerCommand(play)){
                playerToPlay.send(ServerMessages.WHILE_PLAYING_COMMAND);
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

    /**
     * Method that send welcome message to the players.
     */
    private void welcomeGuests(){
        Server.ClientConnectionHandler player = players.get(0);
        player.send(GameMessages.UNO);
        server.roomBroadcast(this, player.getName(), GameMessages.UNO);
    }

    /**
     * Method checks if there are enough players to play each round.
     */
    private boolean checkNumberOfPlayers(){
        if(players.size()<=1){
            playerToPlay.send(ServerMessages.PLAYER_ALONE);
            return true;
        }
        return false;
    }

    /**
     * Method that verify the player's play and executes the GameCommand Class.
     */
    private void play() {
        GameCommand gameCommand = GameCommand.getGameCommandFromDescription(play);

        if (playIsACardFromDeck(play)) {
            gameCommand = GameCommand.getGameCommandFromDescription(GameMessages.PLAY);
        }

        if (gameCommand == null) {
            gameCommand = GameCommand.getGameCommandFromDescription(GameMessages.NOTLEGAL);
        }

        try {
            gameCommand.getCommandHandler().execute(this, playerToPlay);
        } catch (Exception e) {
            playerToPlay.send(e.getMessage());
        }
    }

    /**
     * Method that change the card color.
     * This can occur when a player plays a special card.
     * @param cardColors The color of the last card played.
     */
    public void changeColor(CardColors cardColors){
        lastCardPlayed.setColor(cardColors);
        playerToPlay.send(cardColors.getConsoleColors() + GameMessages.COLOR_CHANGED + cardColors.getDescription());
        server.roomBroadcast(this, playerToPlay.getName(), cardColors.getConsoleColors() +
                GameMessages.COLOR_CHANGED + cardColors.getDescription());
        hasToChooseAColor = false;
    }

    /**
     * Method that renews the deck of cards in the game, using all the cards that were played before.
     * After creating a new deck, the list of cards that have been played is cleared.
     */
    private void replaceDeck(){
        Collections.shuffle(this.playedCards);
        this.deck = this.playedCards;
        this.playedCards.clear();
    }

    /**
     * Method that resets, whenever necessary, booleans and accumulators that are used during the game.
     */
    public void resetBooleansAndAccumulators(){
        playedAtLeastOneCard = false;
        canFinishTurn=false;
        canPlayAgain = true;
        playersToSkip = 0;
        hasToChooseAColor = false;
        drewACard=false;
    }

    /**
     * Method that add the player(s) to the list of the players in the lobby and send message, when the game finish.
     */
    public void finishGame(){
        players.forEach(player -> {
            server.getClientsOnGeneral().add(player);
            player.setGame(null);
            player.setReady(false);
            player.getDeck().clear();
            player.send(GameMessages.END_OF_GAME);
            player.send(ServerMessages.REDIRECTED_LOBBY);
            server.broadcast(player.getName(), ServerMessages.PLAYER_ENTERED_LOBBY);
        });
    }

    /**
     * Method that implements the rules of the special card Plus4.
     * The plus 4 card allows the player to change the color of the card in play.
     * The next player have to draw 4 cards into the table deck.
     * @param card The card.
     */
    public void dealWithPlus4Cards(Card card){
        cardsToDraw+=4;
        cardChangesInDecks(card);
        playerToPlay.send(GameMessages.CHOOSE_COLOR);
        updateBooleans();
        hasToChooseAColor = true;
    }



    /**
     * Method that remove the card on table after a turn, and add to the list of played cards.
     * The last card played becomes the playing card.
     * @param card The card on table.
     */
    public void cardChangesInDecks(Card card){
        playerToPlay.getDeck().remove(card);
        playerToPlay.send(card.toString());
        server.roomBroadcast(this,playerToPlay.getName(),card.toString());
        this.playedCards.add(lastCardPlayed);
        lastCardPlayed=card;
    }

    /**
     * Method that allows to update boolean methods during the game.
     */
    public void updateBooleans(){
        playedAtLeastOneCard = true;
        canFinishTurn = true;
    }

    /**
     * Method that implements the rules of the special card Reverse.
     * The Reverse card allows to reverse the order of playing in the next turn.
     */
    private void dealWithReverse(){
        Server.ClientConnectionHandler p = players.get(indexOfPlayerTurn);
        invertPlayers();

        Optional<Server.ClientConnectionHandler> playerPlaying = players.stream().filter(player -> player==p).findFirst();
        indexOfPlayerTurn = players.indexOf(playerPlaying.get());
    }

    /**
     * Method that invert the players list order.
     */
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
     * Method that waits for the input of a client.
     * @return play/input.
     * @throws InterruptedException
     */
    private String waitForPlay (){
        while(!playerToPlay.isGameCommandChanged()){
            if (someoneWentDown){
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        return playerToPlay.getMessage();
    }

    /**
     * Method that set each player deck into an Array List.
     */
    private void setPlayersDecks(){
        this.players.stream().map(Server.ClientConnectionHandler::getDeck).forEach(playerDeck -> {
            for (int i = 0; i < 7; i++) {
                playerDeck.add(this.deck.poll());
            }
        });
    }

    /**
     * Method that return the firs card of the game.
     * @return The first card of the game.
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
            playerToPlay.send(GameMessages.YOU_THE_WINNER);
            server.roomBroadcast(this,playerToPlay.getName(), GameMessages.PLAYER_THE_WINNER);
        }
    }

    /**
     * Method to draw cards if there are cards to draw from special cards.
     * The number of cards to draw, variates with the circumstance, but it's defined by the counter "cardsToDraw".
     */
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

    /**
     * Method to draw a card from the deck.
     * The head card of the list is removed and added to the deck of the player.
     * A message with the card is printed on console.
     */
    public void drawCard(){
        Card newCard = deck.poll();
        playerToPlay.getDeck().add(newCard);
        playerToPlay.send(GameMessages.YOU_DRAW + newCard);
        server.roomBroadcast(this,playerToPlay.getName(), GameMessages.PLAYER_DRAW);
        drewACard=true;
    }

    /**
     * Method that set the next player to play.
     */
    public void setNextPlayerToPlay(){
        indexOfPlayerTurn+=playersToSkip;
        indexOfPlayerTurn++;
        if(indexOfPlayerTurn > players.size()-1){
            indexOfPlayerTurn-=players.size();
        }

        Server.ClientConnectionHandler player = players.get(indexOfPlayerTurn);
        player.send(GameMessages.TIME_TO_PLAY);
        server.roomBroadcast(this, player.getName(), GameMessages.ISPLAYING);
    }

    /**
     * Method to deal with special cards.
     * @param card The card.
     */
    public void dealWithSpecialCards(Card card){
        if (isASkipCard(card)){
            playersToSkip++;
        }else if (isAReverseCard(card)){
            dealWithReverse();
        }else if (isAPlus2Card(card)) {
            cardsToDraw+=2;
        }
    }

    /**
     * Method to check if some player left the server.
     */
    public void someoneWentDown(Server.ClientConnectionHandler player) {
        this.someoneWentDown = true;
        server.roomBroadcast(this,player.getName(), GameMessages.PLAYER_DISCONNECTED);
    }

    /**
     * Method to change the turn of the player to play, by reset the indexOfPlayerTurn.
     */
    public void changeLastPlayer(){
        indexOfPlayerTurn=0;
    }


    //BOOLEAN METHODS

    /**
     * Boolean method that validates if the play is a card in players deck.
     * @param play play.
     * @return True if the play is a number between 0 and decks size.
     */
    public boolean playIsACardFromDeck(String play) {
        try {
            int card = Integer.parseInt(play);
            return card>=0 && card < playerToPlay.getDeck().size();
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Boolean method that validates if the player has cards to draw, checking the car that player intends to play.
     * @param card The card that player intends to play.
     * @return True if the card hasn't the same number as the card on the table,
     * if is the first card to play in that turn, and there are cards to draw.
     */
    public boolean hasCardsToDraw(Card card){
        return card.getNumber() != lastCardPlayed.getNumber() && cardsToDraw!=0 && isFirstCardOfTurn() ;
    }

    /**
     * Boolean method that validates if the player has cards to draw.
     * @return True if the counter of cardsToDraw is superior to zero and if it's the first card of turn to this player.
     */
    public boolean hasCardsToDraw(){
        return cardsToDraw!=0 && isFirstCardOfTurn();
    }

    /**
     * Method that validates with true the text written by the players in the lobby.
     * It's possible send messages to the players, like a chat.
     * @param play The text.
     * @return True if the fist word starts with that character.
     */
    private boolean isChat(String play){
        return play.startsWith("-");
    }

    /**
     * Method that validates the text written by the players is a server command.
     * @param play The text.
     * @return True if the fist word starts with that character.
     */
    private boolean isServerCommand(String play){
        return play.startsWith("/");
    }

    /**
     * Boolean method that validates if a player can play a card.
     * @return True if the player can play again and if he doesn't have to choose a color.
     */
    public boolean canPlayACard(){
        return canPlayAgain && !hasToChooseAColor;
    }

    /**
     * Boolean method that validates the special Skip card .
     * @param card The card.
     * @return True if the card is the 10 number.
     */
    public boolean isASkipCard(Card card){
        return card.getNumber() == 10;
    }

    /**
     * Boolean method that validates the special Plus 2 card.
     * @param card The card.
     * @return True if the card is the 11 number.
     */
    public boolean isAPlus2Card(Card card){
        return card.getNumber() == 11;
    }

    /**
     * Boolean method that validates the special Reverse card.
     * @param card The card.
     * @return True if the card is the 12 number.
     */
    public boolean isAReverseCard(Card card){
        return card.getNumber() == 12;
    }

    /**
     * Boolean method that validates the special Plus 4 card.
     * @param card The card.
     * @return True if the card is the 13 number.
     */
    public boolean isAPlus4Card(Card card){
        return card.getNumber() == 13;
    }

    /**
     * Boolean method that validates if a player can draw a card.
     * @return True if the player don't played a card and didn't draw a card in the round.
     */
    public boolean canDrawACard(){
        return !playedAtLeastOneCard && !drewACard;
    }

    /**
     * Boolean method that validates if a player can play a Plus 4 card.
     * @return True if the card played number is 13 or it's your first card to play in that turn.
     */
    public boolean canPlayAPlus4Card() {
        return lastCardPlayed.getNumber() == 13 || isFirstCardOfTurn();
    }

    /**
     * Boolean method that validates if is the last player.
     * @return True if the player to play it is the only player.
     */
    public boolean isLastPlayer(){
        return (players.indexOf(playerToPlay)) == players.size()-1;
    }

    /**
     * Boolean method that validates if is the first card of the turn.
     * @return True if the player don't have played at least one card.
     */
    public boolean isFirstCardOfTurn() {
        return !playedAtLeastOneCard;
    }

    /**
     * Boolean method that validates if the player can finish is turn.
     * @return True if the player can finish turn.
     */
    public boolean canFinishTurn() {
        return canFinishTurn;
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

    public String getPlay() {
        return play;
    }

    public Server.ClientConnectionHandler getPlayerToPlay() {
        return playerToPlay;
    }

    public boolean gameIsRunning() {
        return gameIsRunning;
    }

    public boolean getSaidUNO() {
        return saidUNO;
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

    public void saidUNO(boolean saidUno) {
        this.saidUNO = saidUno;
    }


}
