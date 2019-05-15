package cz.danik.arctic.model.wall;

import cz.danik.arctic.values.Constants;

public class MovingWall extends Wall {

    private boolean moving;
    private boolean movingRight;

    public MovingWall(int posX, int posY, boolean movingRight) {
        super(posX, posY);
        this.isPlaced = true;
        this.moving = true;
        this.movingRight = movingRight;
    }

    public void bounceIfAtBorder() {
        if(posX <= 0) {
            movingRight = true;
        } else if (posX >= Constants.BOARD_WIDTH - Constants.TILE_SIZE) {
            movingRight = false;
        }
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }
}
