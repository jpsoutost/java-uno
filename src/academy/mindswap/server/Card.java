package academy.mindswap.server;

import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the cards of the game.
 */
public class Card {
    private CardColors color;
    private int number;//10-skip Cards, 11-plus2Cards, 12-invertCards
    private Game n;

    /**
     * Method that create a card.
     * @param color Define the card color.
     * @param number Define the card number.
     */
    public Card(CardColors color, int number) {
        this.color = color;
        this.number = number;
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

    @Override
    public String toString() {
        return color.getConsoleColors() + GameMessages.CARD_ON_TABLE1 + "\n|    " + number + "    |\n" + GameMessages.CARD_ON_TABLE2;
    }
}
