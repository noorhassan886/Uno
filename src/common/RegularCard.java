package common;

public class RegularCard extends UnoCard {

    private int number;

    public RegularCard(String startingColor, int startingNumber) {
        super(startingColor);
        this.number = startingNumber;
    }
    @Override
    public String toString() {
        String s = "";
        s += this.color;
        s += " ";
        s += this.number;
        return s;
    }

}
