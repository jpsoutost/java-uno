package academy.mindswap.server;

import academy.mindswap.server.messages.GameMessages;

/**
 * A class that represents the cards of the game.
 */
public class Card {
    //private CardColors color;
    private String color;
    private int number;//10-skip Cards, 11-plus2Cards, 12-invertCards
    private Game n;

    /**
     * Method that create a card.
     * @param color Define the card color.
     * @param number Define the card number.
     */
    public Card(String color, int number) {
        this.color = color;
        this.number = number;
    }

    /**
     * @return the color of the card, by a ENUM class.
     */
    public String getColor() {
        return color;
    }

    /**
     * @return The number of the card.
     */
    public int getNumber() {
        return number;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return color + GameMessages.CARD1 + "     " + number + "\n" + GameMessages.CARD2;
    }
    /*public String toString() {
        return color2 +
                "  ___________  \n" +
                " |+" + number + "   +       \n" +
                " |+          |         \n" +
                " |   +   +   |         \n" +
                " |     +     |         \n" +
                " |   +   +   |         \n" +
                " |         + |         \n" +
                " |  +    +  "+ number + "\n" +
                "  ``````´´´´´        ";
    }*/
}
