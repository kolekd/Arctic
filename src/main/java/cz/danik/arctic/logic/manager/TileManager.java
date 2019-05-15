package cz.danik.arctic.logic.manager;

import cz.danik.arctic.model.PowerUp;
import cz.danik.arctic.model.Tile;
import cz.danik.arctic.model.wall.MovingWall;
import cz.danik.arctic.model.wall.Wall;
import cz.danik.arctic.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

import static cz.danik.arctic.values.Constants.*;
import static cz.danik.arctic.values.Constants.STEP_DISTANCE;

public class TileManager extends ArrayList<List<Tile>> {

    //  Moves all the walls downwards and also moves moving walls horizontally.
    public void moveTiles(List<Tile> tileList) {
        int currentPosY = tileList.get(0).getPosY();
        for(Tile tile : tileList) {
            if(tile instanceof MovingWall) {
                if(((MovingWall) tile).isMovingRight()) {
                    tile.setPosX(tile.getPosX() + STEP_DISTANCE);
                } else {
                    tile.setPosX(tile.getPosX() - STEP_DISTANCE);
                }

                ((MovingWall) tile).bounceIfAtBorder();
            }

            tile.setPosY(currentPosY + STEP_DISTANCE);
        }
    }

    public void generateWall() {
        List<Tile> tileList = new ArrayList<>();

        int solidCount = 0;
        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if (RandomUtil.get()) {
                tileList.add(new Wall((i * TILE_SIZE), -TILE_SIZE, true));
                solidCount++;
            } else if (solidCount < MAX_TILES_IN_A_ROW - 1) {
                tileList.add(new Wall((i * TILE_SIZE), -TILE_SIZE, false));
            }
        }

        add(tileList);
    }

    public void generateMovingWall() {
        List<Tile> tileList = new ArrayList<>();

        int randomPosX = RandomUtil.randomNumberInRange(0, MAX_TILES_IN_A_ROW - 1);
        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if (i == randomPosX) {
                tileList.add(new MovingWall(i * TILE_SIZE, -TILE_SIZE, RandomUtil.get()));
            } else {
                tileList.add(new Wall((i * TILE_SIZE), -TILE_SIZE, false));
            }
        }

        add(tileList);
    }

    public void generatePowerUp() {
        List<Tile> tileList = new ArrayList<>();

        int randomPosX = RandomUtil.randomNumberInRange(0, MAX_TILES_IN_A_ROW - 1);
        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if (i == randomPosX) {
                if (RandomUtil.get()) {
                    tileList.add(new PowerUp(i * TILE_SIZE, -TILE_SIZE, "breaker"));
                } else {
                    tileList.add(new PowerUp(i * TILE_SIZE, -TILE_SIZE, "shooter"));
                }

            } else {
                tileList.add(new Wall(i * TILE_SIZE, -TILE_SIZE, false));
            }
        }

        add(tileList);
    }

}
