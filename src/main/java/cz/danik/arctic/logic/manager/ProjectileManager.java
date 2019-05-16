package cz.danik.arctic.logic.manager;

import cz.danik.arctic.model.Projectile;
import cz.danik.arctic.model.Tile;
import cz.danik.arctic.model.wall.MovingWall;
import cz.danik.arctic.model.wall.Wall;
import cz.danik.arctic.values.Globals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static cz.danik.arctic.values.Constants.*;

public class ProjectileManager extends ArrayList<Tile> {

    // Checks if any projectile hit any wall. If so, removes them both. If not, moves the projectile further upwards.
    public void checkProjectiles(List<List<Tile>> tileManager) {
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

                        Globals.SCORE_COUNT += SHOOTER_SCORE_VALUE;
                        currentProjectile.setPlaced(false);
                        break outerloop;
                    }
                }
            }
        }
    }

    public void move() {
        Iterator<Tile> projectileIterator = this.iterator();
        while (projectileIterator.hasNext()) {
            Tile currentProjectile = projectileIterator.next();

            if(currentProjectile.getPosY() < -TILE_SIZE || !currentProjectile.isPlaced()) {
                projectileIterator.remove();
            } else {
                currentProjectile.setPosY(currentProjectile.getPosY() - STEP_DISTANCE);
            }
        }
    }

    //  Launches projectiles if they're supposed to be launched.
    public void launch(int posX, int posY) {
        if (!isOutOfBounds(posX - TILE_SIZE)) {
            add(new Projectile(posX - TILE_SIZE, posY - TILE_SIZE));
        }
        if (!isOutOfBounds(posX + TILE_SIZE)) {
            add(new Projectile(posX + TILE_SIZE, posY - TILE_SIZE));
        }
    }

    private boolean isOutOfBounds(int posX) {
        return posX < 0 || posX > BOARD_WIDTH - TILE_SIZE;
    }

}
