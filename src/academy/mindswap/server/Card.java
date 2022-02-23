package academy.mindswap.server;

public class Card {
    private final CardColors color;
    private final int number;//10-skip Cards, 11-plus2Cards, 12-invertCards

    public Card(CardColors color, int number) {
        this.color = color;
        this.number = number;
    }

    public CardColors getColor() {
        return color;
    }

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
