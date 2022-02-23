package academy.mindswap.server;

/**
 * A class that represents the cards of the game.
 */
public class Card {
    private final CardColors color;
    private final int number;

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

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", number=" + number +
                '}';
    }


}
