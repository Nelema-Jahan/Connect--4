import java.awt.Color;

public class Player {
    private String name;
    private Color color;
    private int id;

    public Player(String name, Color color, int id) {
        this.name = name;
        this.color = color;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }
}
