package common;

public abstract class SpecialCard extends UnoCard {

    String type;

    public SpecialCard(String color, String startingType) {
        super(color);
        this.type = startingType;
    }

    @Override
    public String getInfo() {
        return this.type;
    }

}
