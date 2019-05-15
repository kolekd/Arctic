package cz.danik.arctic.model;

public class PowerUp extends Tile {

    private String name;

    public PowerUp(int posX, int posY, String name) {
        super(posX, posY);
        this.isPlaced = true;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
