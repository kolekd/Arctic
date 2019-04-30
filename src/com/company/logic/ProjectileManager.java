package com.company.logic;

import com.company.model.Projectile;
import com.company.model.Tile;
import com.company.model.wall.MovingWall;
import com.company.model.wall.Wall;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.company.logic.Constants.*;
import static com.company.logic.Constants.STEP_DISTANCE;

public class ProjectileManager extends ArrayList<Tile> {

    private boolean launchProjectiles;

    // Checks if any projectile hit any wall. If so, removes them both. If not, moves the projectile further upwards.
    void checkProjectiles(int SCORE_COUNT, List<List<Tile>> tileManager) {
        Iterator<Tile> projectileIterator = this.iterator();
        while (projectileIterator.hasNext()) {
            Tile currentProjectile = projectileIterator.next();

            Iterator<List<Tile>> tileIterator = tileManager.iterator();
            outerloop:
            while(tileIterator.hasNext()) {
                List<Tile> currentTileLayer = tileIterator.next();

                for (Tile tile : currentTileLayer) {
                    if (currentProjectile.overlapsWith(tile) && tile instanceof Wall) {
                        tile.setPlaced(false);
                        ((Wall) tile).setJustDestroyedBy("shooter");

                        if(tile instanceof MovingWall) {
                            ((MovingWall) tile).setMoving(false);
                        }

                        SCORE_COUNT += SHOOTER_SCORE_VALUE;
                        currentProjectile.setPlaced(false);
                        break outerloop;
                    }
                }
            }

            if(currentProjectile.getPosY() < -TILE_SIZE || !currentProjectile.isPlaced()) {
                projectileIterator.remove();
            } else {
                currentProjectile.setPosY(currentProjectile.getPosY() - STEP_DISTANCE);
            }
        }
    }

    //  Launches projectiles if they're supposed to be launched.
    //  TODO: Simplify params when Player.class is implemented.
    void launchIfNeeded(int playerPosX, int playerPosY, String playerBuff) {
        if (launchProjectiles) {
            if (playerBuff.equals("shooter")) {
                playerBuff = "";
            }
            if (!isOutOfBounds(playerPosX - TILE_SIZE)) {
                add(new Projectile(playerPosX - TILE_SIZE, playerPosY - TILE_SIZE));
            }
            if (!isOutOfBounds(playerPosX + TILE_SIZE)) {
                add(new Projectile(playerPosX + TILE_SIZE, playerPosY - TILE_SIZE));
            }

            launchProjectiles = false;
        }
    }

    private boolean isOutOfBounds(int posX) {
        return posX < 0 || posX > BOARD_WIDTH - TILE_SIZE;
    }

    public void setLaunchProjectiles(boolean launchProjectiles) {
        this.launchProjectiles = launchProjectiles;
    }
}
