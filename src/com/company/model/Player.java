package com.company.model;

public class Player extends Tile {

    private String buff;
    private boolean launchProjectiles;

    public Player(int posX, int posY) {
        super(posX, posY);
        this.isPlaced = true;
        this.buff= "";
        this.launchProjectiles = false;
    }

    public String getBuff() {
        return buff;
    }

    public void setBuff(String buff) {
        this.buff = buff;
    }

    public boolean willLaunchProjectiles() {
        return launchProjectiles;
    }

    public void setLaunchProjectiles(boolean launchProjectiles) {
        this.launchProjectiles = launchProjectiles;
    }
}
