package cz.danik.arctic.model.tile.wall;

import cz.danik.arctic.model.tile.Tile;

public class Wall extends Tile {

    private String justDestroyedBy;
    private int scoreDisplayCounter;

    public Wall(int posX, int posY, boolean isPlaced) {
        super(posX, posY, isPlaced);
        this.justDestroyedBy = "";
        this.scoreDisplayCounter = 0;
    }

    Wall(int posX, int posY) {
        super(posX, posY);
        this.justDestroyedBy = "";
        this.scoreDisplayCounter = 0;
    }

    public void setJustDestroyedBy(String justDestroyedBy) {
        this.justDestroyedBy = justDestroyedBy;
    }

    public String getJustDestroyedBy() {
        return justDestroyedBy;
    }

    public int getScoreDisplayCounter() {
        return scoreDisplayCounter;
    }

    public void setScoreDisplayCounter(int scoreDisplayCounter) {
        this.scoreDisplayCounter = scoreDisplayCounter;
    }
}
