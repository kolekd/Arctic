package com.company.model;

public class Projectile {

    int posY;
    int posX;
    boolean isLaunched;

    public Projectile(int posX, int posY, boolean isLaunched) {
        this.posX = posX;
        this.posY = posY;
        this.isLaunched = isLaunched;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public boolean isLaunched() {
        return isLaunched;
    }

    public void setLaunched(boolean launched) {
        isLaunched = launched;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}
