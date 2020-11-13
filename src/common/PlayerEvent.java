package common;

public class PlayerEvent {

    private int id;
    private String action;
    private  String payload;


    public PlayerEvent(int id, String action, String payload) {
        this.id = id;
        this.action = action;
        this.payload = payload;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getPayload() {
        return payload;
    }
}
