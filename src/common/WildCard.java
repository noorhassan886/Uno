package common;

public class WildCard extends SpecialCard {

    public WildCard(String type) {
        super(null, type);
    }

    public void setColor(String newColor) {this.color = newColor;}

    @Override
    public String toString() {

        String s = "Wild";
        if(this.type.equals("+4"))
            s+= " Draw 4";
        return s;
    }
}
