package common;

public abstract class UnoCard {

    protected String color;

    public UnoCard(String startingColor) {
        this.color = startingColor;
    }

    public String getColor() {
        return color;
    }

}

