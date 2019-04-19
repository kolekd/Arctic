package com.company.model.newmodel;

public class PowerUp extends Tile {

    private String name;

    public PowerUp(int posX, int posY, boolean isPlaced, String name) {
        super(posX, posY, isPlaced);
        this.name = name;
    }

    private PowerUp() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
