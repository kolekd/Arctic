package com.company.logic;

import com.company.model.PowerUp;
import com.company.model.Tile;
import com.company.model.wall.MovingWall;
import com.company.model.wall.Wall;
import com.company.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

import static com.company.logic.Constants.MAX_TILES_IN_A_ROW;
import static com.company.logic.Constants.TILE_SIZE;

public class TileManager extends ArrayList<List<Tile>> {

    void generateWall() {
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

    void generateMovingWall() {
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

    void generatePowerUp() {
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
