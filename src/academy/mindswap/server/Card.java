package academy.mindswap.server;

public class Card {
    private CardColors color;
    private int number;

    public Card(CardColors color, int number) {
        this.color = color;
        this.number = number;
    }

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", number=" + number +
                '}';
    }
}
