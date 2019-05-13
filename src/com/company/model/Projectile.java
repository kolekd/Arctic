package com.company.model;

import static com.company.Constants.TILE_SIZE;

public class Projectile extends Tile {

    private boolean isLaunched;

    public Projectile(int posX, int posY) {
        super(posX, posY);
        this.isPlaced = true;
        this.isLaunched = true;
    }

    @Override
    public boolean overlapsWith(Tile tile) {
        return tile.isPlaced() && this.isPlaced &&
                posY >= tile.getPosY() - (TILE_SIZE + 4) && posX > tile.getPosX() - TILE_SIZE &&
                posY <= tile.getPosY() + TILE_SIZE && posX < tile.getPosX() + TILE_SIZE;
    }

    public boolean isLaunched() {
        return isLaunched;
    }

    public void setLaunched(boolean launched) {
        isLaunched = launched;
    }

}
