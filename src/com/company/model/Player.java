package com.company.model;

public class Player extends Tile {

    private String buff;

    public Player(int posX, int posY) {
        super(posX, posY);
        this.isPlaced = true;
        this.buff= "";
    }

    public String getBuff() {
        return buff;
    }

    public void setBuff(String buff) {
        this.buff = buff;
    }
}
