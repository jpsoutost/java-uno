package academy.mindswap.server;

/**
 * A class that represents the cards of the game.
 * There is normal cards and special cards.
 * A normal card has a number from 1 to 9 and a color (blue, red, green or yellow).
 * The special cards are: 10 - skip cards; 11- plus2cards; 12 - invert card; 13 - plus4card.
 */
public class Card {
    private CardColors color;
    private int number;

    /**
     * Method that create a card.
     * @param color Define the card color.
     * @param number Define the card number.
     */
    public Card(CardColors color, int number) {
        this.color = color;
        this.number = number;
    }


    //GETTERS AND SETTERS

    public CardColors getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }

    public void setColor(CardColors color) {
        this.color = color;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    //OVERRIDE METHODS

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", number=" + number +
                '}';
    }


}
