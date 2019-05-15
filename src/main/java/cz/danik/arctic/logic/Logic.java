package cz.danik.arctic.logic;


import cz.danik.arctic.logic.manager.ProjectileManager;
import cz.danik.arctic.logic.manager.TileManager;
import cz.danik.arctic.model.Player;
import cz.danik.arctic.model.PowerUp;
import cz.danik.arctic.model.Tile;
import cz.danik.arctic.model.wall.MovingWall;
import cz.danik.arctic.model.wall.Wall;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import static cz.danik.arctic.logic.GameFlow.*;
import static cz.danik.arctic.values.Globals.*;
import static cz.danik.arctic.values.Constants.*;

public class Logic {

    public static boolean gameRunning;
    public boolean gameJustLaunched;

    private boolean movingWallOrPowerUp;

    public Player player;

    public TileManager tileManager;
    public ProjectileManager projectileManager;

    public Logic() {
        this.gameJustLaunched = true;
    }

    public void initGame(ActionListener listener) {
        SPEED_INCREASE_VALUE = INITIAL_SPEED_INCREASE_VALUE;
        SPEED_INCREASE_FREQUENCY = INITIAL_SPEED_INCREASE_FREQUENCY;
        GENERATED_WALLS_COUNT = 0;

        player = new Player(INITIAL_PLAYER_POS_X, INITIAL_PLAYER_POS_Y);

        movingWallOrPowerUp = false;

        tileManager = new TileManager();
        projectileManager = new ProjectileManager();

        TOTAL_TICK_COUNT = 0;
        TICK_COUNT = 0;
        SCORE_COUNT = 0;

        gameJustLaunched = false;
        gameRunning = true;

        GameFlow.timer = new Timer(INITIAL_DELAY, listener);
        GameFlow.timer.start();
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

                //  These next 2 bunches of code handle showing gained score from destroying walls.
                if (tile instanceof Wall) {
                    if (((Wall) tile).getJustDestroyedBy().length() > 0) {
                        ((Wall) tile).setScoreDisplayCounter(((Wall) tile).getScoreDisplayCounter() + 1);
                    }

                    if (((Wall) tile).getScoreDisplayCounter() > 10) {
                        ((Wall) tile).setJustDestroyedBy("");
                    }
                }

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
                    GameFlow.timer.stop();
                    gameRunning = false;
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
                if (movingWallOrPowerUp) {
                    tileManager.generatePowerUp();
                    movingWallOrPowerUp = false;
                } else {
                    tileManager.generateMovingWall();
                    movingWallOrPowerUp = true;
                }
                GENERATED_WALLS_COUNT++;
            } else {
                tileManager.generateWall();
                GENERATED_WALLS_COUNT++;
            }
        }

        if(!projectileManager.isEmpty()) {
            projectileManager.checkProjectiles(tileManager);
        }

        GameFlow.manageSpeeds();

        System.out.println(debugReport());
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
        return "Score: " + SCORE_COUNT + "  Delay: " + GameFlow.timer.getDelay() + "  SI Frequency: " +
                SPEED_INCREASE_FREQUENCY + "  SI Value: " + SPEED_INCREASE_VALUE + "  Walls generated: " +
                GENERATED_WALLS_COUNT + "  Buff: " + player.getBuff();
    }
}
