package com.company.logic;

import com.company.model.Projectile;
import com.company.model.Tile;

import java.util.ArrayList;

import static com.company.logic.Constants.BOARD_WIDTH;
import static com.company.logic.Constants.TILE_SIZE;

public class ProjectileManager extends ArrayList<Tile> {

    private boolean launchProjectiles;

    String launchIfNeeded(int playerPosX, int playerPosY, String playerBuff) {
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

        return playerBuff;
    }

    private boolean isOutOfBounds(int posX) {
        return posX < 0 || posX > BOARD_WIDTH - TILE_SIZE;
    }

    public boolean isLaunchProjectiles() {
        return launchProjectiles;
    }

    public void setLaunchProjectiles(boolean launchProjectiles) {
        this.launchProjectiles = launchProjectiles;
    }
}
