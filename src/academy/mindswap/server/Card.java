package academy.mindswap.server;

import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the cards of the game.
 */
public class Card {
    private CardColors color;
    private final int number;
    private final String symbol;

    /**
     * Method that create a card.
     * @param color Define the card color.
     * @param number Define the card number.
     */
    public Card(CardColors color, int number) {
        this.color = color;
        this.number = number;
        if(number == 10){
            this.symbol = "S ";
        }else if(number == 11){
            this.symbol = "+2";
        }else if(number == 12){
            this.symbol = "R ";
        }else if(number == 13){
            this.symbol = "+4";
        }else{
            this.symbol= number + " ";
        }
    }

    /**
     * @return the color of the card, by a ENUM class.
     */
    public CardColors getColor() {
        return color;
    }

    /**
     * @return The number of the card.
     */
    public int getNumber() {
        return number;
    }

    public void setColor(CardColors color) {
        this.color = color;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        String cardColor = color.getConsoleColors();
        String card = GameMessages.CARD_ON_TABLE1
                + "\n|    " + symbol + "   |\n"
                + GameMessages.CARD_ON_TABLE2;

        if (number ==13){
            cardColor = ConsoleColors.PURPLE;
        }

        return cardColor + card;
    }
}
