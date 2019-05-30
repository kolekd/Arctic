package cz.danik.arctic.logic;


import cz.danik.arctic.logic.manager.ProjectileManager;
import cz.danik.arctic.logic.manager.TileManager;
import cz.danik.arctic.model.scoreboard.Score;
import cz.danik.arctic.model.tile.Player;
import cz.danik.arctic.model.tile.PowerUp;
import cz.danik.arctic.model.tile.Tile;
import cz.danik.arctic.model.tile.wall.MovingWall;
import cz.danik.arctic.model.tile.wall.Wall;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import static cz.danik.arctic.logic.GameFlow.*;
import static cz.danik.arctic.values.Globals.*;
import static cz.danik.arctic.values.Constants.*;

public class Logic {

    public Player player;

    public TileManager tileManager;
    public ProjectileManager projectileManager;

    public Logic() {}

    public void initGame(ActionListener listener) {
        SPEED_INCREASE_VALUE = INITIAL_SPEED_INCREASE_VALUE;
        SPEED_INCREASE_FREQUENCY = INITIAL_SPEED_INCREASE_FREQUENCY;
        GENERATED_WALLS_COUNT = 0;

        player = new Player(INITIAL_PLAYER_POS_X, INITIAL_PLAYER_POS_Y);

        tileManager = new TileManager();
        projectileManager = new ProjectileManager();

        TOTAL_TICK_COUNT = 0;
        TICK_COUNT = 0;
        SECONDARY_TICK_COUNT = 0;

        SCORE_COUNT = 0;

        PRIMARY_TIMER = new Timer(INITIAL_DELAY_PRIMARY, listener);
        SECONDARY_TIMER = new Timer(INITIAL_DELAY_SECONDARY, listener);

        GameFlow.startTimers();
    }

    //  This happens every tick.
    public void tickAction() {
        TICK_COUNT++;
        TOTAL_TICK_COUNT++;
        SCORE_COUNT++;

        //  Goes through each individual row of walls.
        Iterator<List<Tile>> wallLineIterator = tileManager.iterator();
        while (wallLineIterator.hasNext()) {
            List<Tile> tileList = wallLineIterator.next();
            Tile sampleTile = tileList.get(0);

            /*  Checks whether the player is in the hitbox of a wall or a power-up.
                 - Wall - player unbuffed -> ends the game, player buffed -> removes the wall
                 - Power-up -> buffs the player.  */
            for (Tile tile : tileList) {

                boolean stopTheGame = false;
                if (tile.isPlaced() &&
                        player.getPosY() >= tile.getPosY() - TILE_SIZE &&
                        player.getPosY() <= tile.getPosY() + TILE_SIZE &&
                        player.getPosX() > tile.getPosX() - TILE_SIZE &&
                        player.getPosX() < tile.getPosX() + TILE_SIZE) {
                    if (tile instanceof Wall) {
                        if (player.getBuff().equals(BREAKER)) {
                            tile.setPlaced(false);
                            ((Wall) tile).setJustDestroyedBy(BREAKER);
                            if (tile instanceof MovingWall) {
                                ((MovingWall) tile).setMoving(false);
                            }
                            SCORE_COUNT += BREAKER_SCORE_VALUE;
                            player.setBuff("");
                        } else {
                            stopTheGame = true;
                        }
                    } else if (tile instanceof PowerUp) {
                        player.setBuff(((PowerUp) tile).getName());
                        tile.setPlaced(false);
                        wallLineIterator.remove();
                    }
                }

                if (stopTheGame) {
                    GameFlow.stopTimers();
                    CURRENT_WINDOW = GAME_OVER_WINDOW;
                }
            }

            //  Removes non-visible walls and moves visible ones.
            if (sampleTile.getPosY() > BOARD_HEIGHT) {
                wallLineIterator.remove();
            } else {
                tileManager.moveTiles(tileList);
            }
        }

        if(player.willLaunchProjectiles()) {
            projectileManager.launch(player.getPosX(), player.getPosY());
            player.setLaunchProjectiles(false);
            player.setBuff("");
        }

        /*  Generates wall. Based on the value of ANOMALY_GENERATION_FREQUENCY generates
            a moving wall or a power-up instead.  */
        if (TOTAL_TICK_COUNT % WALL_GENERATION_FREQUENCY == 0) {
            if (GENERATED_WALLS_COUNT % ANOMALY_GENERATION_FREQUENCY == 0) {
                tileManager.generateAnomaly();
            } else {
                tileManager.generateWall();
            }

            GENERATED_WALLS_COUNT++;
        }

        if(!projectileManager.isEmpty()) {
            projectileManager.checkProjectiles(tileManager);
        }

        manageSpeeds();

        System.out.println(debugReport());
    }

    public void tickActionSecondary() {
        SECONDARY_TICK_COUNT++;
        projectileManager.move();

        for (List<Tile> tileList : tileManager) {
            for (Tile tile : tileList) {
                //  These next 2 bunches of code handle showing gained score from destroying walls.
                if (tile instanceof Wall) {
                    if (((Wall) tile).getJustDestroyedBy().length() > 0) {
                        ((Wall) tile).setScoreDisplayCounter(((Wall) tile).getScoreDisplayCounter() + 1);
                    }

                    if (((Wall) tile).getScoreDisplayCounter() > ON_BREAK_SCORE_DISPLAY_TIME) {
                        ((Wall) tile).setJustDestroyedBy("");
                    }
                }

            }
        }
    }

    //  Checks whether there is no walls at the provided coordinates. Also handles player picking up the buff.
    public boolean noWallThere(int coords) {
        for (List<Tile> wallList : tileManager) {
            for (Tile wall : wallList) {
                if (coords == wall.getPosX() && wall.isPlaced() && !(wall instanceof PowerUp) &&
                     player.getPosY() <= wall.getPosY() + TILE_SIZE &&
                     player.getPosY() >= wall.getPosY() - TILE_SIZE) {

                    return false;
                }
            }
        }

        return true;
    }

    private String debugReport() {
        return    "Score: " + SCORE_COUNT +
                "  Total TickCount: " + TOTAL_TICK_COUNT +
                "  TickCount: " + TICK_COUNT +
                "  Delay: " + PRIMARY_TIMER.getDelay() +
                "  SI Frequency: " + SPEED_INCREASE_FREQUENCY +
                "  SI Value: " + SPEED_INCREASE_VALUE +
                "  Walls generated: " + GENERATED_WALLS_COUNT +
                "  Buff: " + player.getBuff();
    }
}
