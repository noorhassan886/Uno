package common;

public abstract class UnoCard {

    protected String color;

    public UnoCard(String startingColor) {
        this.color = startingColor;
    }

    public String getColor() {
        return color;
    }

    public static UnoCard fromString(String card) {
//        TODO: do this
//        if() {
//            RegularCard
//        }
//        if() {
//            ColoredSpecialcard
//        }
//        if() {
//            WildCard
//        }
        return null;
    }

}

