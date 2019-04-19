package com.company.model.newmodel;

public class Projectile extends Tile {

    boolean isLaunched;

    public Projectile(int posX, int posY, boolean isPlaced, boolean isLaunched) {
        super(posX, posY, isPlaced);
        this.isLaunched = isLaunched;
    }

    public boolean isLaunched() {
        return isLaunched;
    }

    public void setLaunched(boolean launched) {
        isLaunched = launched;
    }

}
