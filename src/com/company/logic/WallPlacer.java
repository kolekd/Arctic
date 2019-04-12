package com.company.logic;

public class WallPlacer {

    boolean willBePlaced;

    public WallPlacer(boolean willBePlaced) {
        this.willBePlaced = willBePlaced;
    }

    public boolean isWillBePlaced() {
        return willBePlaced;
    }

    public void setWillBePlaced(boolean willBePlaced) {
        this.willBePlaced = willBePlaced;
    }
}
