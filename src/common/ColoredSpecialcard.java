package common;

public class ColoredSpecialcard extends SpecialCard {

    public ColoredSpecialcard(String startingColor, String type) {
        super(startingColor, type);

    }

    @Override
    public String toString() {
        String s = "";
        s += this.color;
        s += " ";
        s += this.type;
        return s;
    }
}
