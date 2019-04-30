package com.company.logic;


import com.company.model.PowerUp;
import com.company.model.Tile;
import com.company.model.wall.MovingWall;
import com.company.model.wall.Wall;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import static com.company.logic.Constants.*;

public class Logic {

    private int SPEED_INCREASE_VALUE;
    private int SPEED_INCREASE_FREQUENCY;
    private int GENERATED_WALLS_COUNT;

    private int TOTAL_TICK_COUNT;
    private int TICK_COUNT;
    public int SCORE_COUNT;

    public boolean gameRunning;
    public boolean gameJustLaunched;

    public int playerPosX;
    public int playerPosY;
    public String playerBuff;

    private boolean movingWallOrPowerUp;

    public TileManager tileManager;
    public ProjectileManager projectileManager;

    public Timer timer;

    public Logic() {
        this.gameJustLaunched = true;
    }

    public void initGame(ActionListener listener) {
        SPEED_INCREASE_VALUE = INITIAL_SPEED_INCREASE_VALUE;
        SPEED_INCREASE_FREQUENCY = INITIAL_SPEED_INCREASE_FREQUENCY;
        GENERATED_WALLS_COUNT = 0;

        playerPosX = (BOARD_WIDTH / 2) - (TILE_SIZE / 2);
        playerPosY = BOARD_HEIGHT - (TILE_SIZE * 4);
        playerBuff = "";

        movingWallOrPowerUp = false;

        tileManager = new TileManager();
        projectileManager = new ProjectileManager();

        TOTAL_TICK_COUNT = 0;
        TICK_COUNT = 0;
        SCORE_COUNT = 0;

        gameJustLaunched = false;
        gameRunning = true;

        timer = new Timer(INITIAL_DELAY, listener);
        timer.start();
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
            Tile tileAtPlayerPosX = tileList.get(playerPosX / TILE_SIZE);

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
                        playerPosY >= tile.getPosY() - TILE_SIZE &&
                        playerPosY <= tile.getPosY() + TILE_SIZE &&
                        playerPosX > tile.getPosX() - TILE_SIZE &&
                        playerPosX < tile.getPosX() + TILE_SIZE) {
                    if (tile instanceof Wall) {
                        if (playerBuff.equals("breaker")) {
                            tile.setPlaced(false);
                            ((Wall) tile).setJustDestroyedBy("breaker");
                            if (tile instanceof MovingWall) {
                                ((MovingWall) tile).setMoving(false);
                            }
                            SCORE_COUNT += BREAKER_SCORE_VALUE;
                            playerBuff = "";
                        } else {
                            stopTheGame = true;
                        }
                    } else if (tile instanceof PowerUp) {
                        playerBuff = ((PowerUp) tile).getName();
                        tile.setPlaced(false);
                        wallLineIterator.remove();
                    }
                }

                if (stopTheGame) {
                    timer.stop();
                    gameRunning = false;
                }
            }

            //  Removes non-visible walls and moves visible ones.
            if (tileAtPlayerPosX.getPosY() > BOARD_HEIGHT) {
                wallLineIterator.remove();
            } else {
                moveTiles(tileList);
            }
        }

        projectileManager.launchIfNeeded(playerPosX, playerPosY, playerBuff);

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
            projectileManager.checkProjectiles(SCORE_COUNT, tileManager);
        }

        /*  Shortens the time between each tick, resets TICK_COUNT and
                increases the value the TICK_COUNT has to reach to run these methods.   */
        if (TICK_COUNT > SPEED_INCREASE_FREQUENCY && timer.getDelay() > MIN_DELAY) {
            timer.setDelay(timer.getDelay() - SPEED_INCREASE_VALUE);
            SPEED_INCREASE_FREQUENCY += INITIAL_SPEED_INCREASE_FREQUENCY;
            TICK_COUNT = 0;
        }

        /*  At a certain point the value by which the time between each tick is shortened
                gets gradually decremented to prevent extreme game speed increase.  */
        if (TOTAL_TICK_COUNT > POINT_OF_DECREMENTING_SI_VALUE &&
                TOTAL_TICK_COUNT % SI_VALUE_DECREASE_FREQUENCY == 0 && SPEED_INCREASE_VALUE > 1) {
            SPEED_INCREASE_VALUE--;
        }
        System.out.println(debugReport());
    }

    //  Checks whether there is no walls at the provided coordinates. Also handles player picking up the buff.
    public boolean noWallThere(int coords) {
        for (List<Tile> wallList : tileManager) {
            for (Tile wall : wallList) {
                if (coords == wall.getPosX() && wall.isPlaced() &&
                     playerPosY <= wall.getPosY() + TILE_SIZE &&
                     playerPosY >= wall.getPosY() - TILE_SIZE) {

                    if(wall instanceof PowerUp && wall.isPlaced()) {
                        wall.setPlaced(false);
                        playerBuff = wall.toString();
                        return true;
                    }

                    return false;
                }
            }
        }

        return true;
    }

    //  Moves all the walls downwards and also moves moving walls horizontally.
    private void moveTiles(List<Tile> tileList) {
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

    private String debugReport() {
        return "Score:  " + SCORE_COUNT + "  Delay: " + timer.getDelay() + "  SI Frequency: " +
                SPEED_INCREASE_FREQUENCY + "  SI Value: " + SPEED_INCREASE_VALUE + "  Walls generated: " +
                GENERATED_WALLS_COUNT;
    }
}
