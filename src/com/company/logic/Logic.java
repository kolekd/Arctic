package com.company.logic;

import com.company.model.*;
import com.company.util.RandomDecision;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.company.logic.Constants.*;

public class Logic {

    // Kind of ez - val=10, freq=20, wgen=24, eachTickTileGoDownBy=TILE_SIZE / 4

    // Nice & smooth - init_delay=50, min_delay=15, val=10, freq=20, wgen=48, eachTickTileGoDownBy=TILE_SIZE / 8

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

    public boolean projectilesWillBeLaunched;
    private boolean movingWallOrPowerUp;

    public List<WallLine> wallLineList;
    public List<Projectile> projectileList;

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

        projectilesWillBeLaunched = false;
        movingWallOrPowerUp = false;

        wallLineList = new ArrayList<>();
        projectileList = new ArrayList<>();

        TOTAL_TICK_COUNT = 0;
        TICK_COUNT = 0;
        SCORE_COUNT = 0;

        gameJustLaunched = false;
        gameRunning = true;
        timer = new Timer(INITIAL_DELAY, listener);
        timer.start();
    }

    private void generateWall() {
        List<Wall> wallPlacerList = new ArrayList<>();
        WallLine wallLine = new WallLine(wallPlacerList);

        int solidCount = 0;
        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if(RandomDecision.get()) {
                wallPlacerList.add(new Wall(true, (i * TILE_SIZE)));
                solidCount++;
            } else if(solidCount < MAX_TILES_IN_A_ROW - 1) {
                wallPlacerList.add(new Wall(false, 0));
            }
        }

        wallLineList.add(wallLine);
    }

    private void generateMovingWall() {
        List<Wall> wallPlacerList = new ArrayList<>();
        MovingWallLine movingWallLine = new MovingWallLine(wallPlacerList);

        int randomPosX = RandomDecision.randomNumberInRange(0, MAX_TILES_IN_A_ROW - 1);
        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if(i == randomPosX) {
                wallPlacerList.add(new MovingWall(true, i * TILE_SIZE));
            } else {
                wallPlacerList.add(new Wall(false, i * TILE_SIZE));
            }
        }

        wallLineList.add(movingWallLine);
    }

    private void generatePowerUp() {
        List<Wall> wallPlacerList = new ArrayList<>();
        WallLine wallLine = new WallLine(wallPlacerList);

        PowerUp.Builder powerUpBuilder = new PowerUp.Builder(true,
                RandomDecision.randomNumberInRange(0, MAX_TILES_IN_A_ROW - 1) * TILE_SIZE);

        if (RandomDecision.get()) {
            powerUpBuilder.makeBreaker();
        } else {
            powerUpBuilder.makeShooter();
        }

        wallPlacerList.add(powerUpBuilder.build());

        wallLineList.add(wallLine);
    }

    //  This happens every tick.
    public void tickAction() {
        TICK_COUNT++;
        TOTAL_TICK_COUNT++;
        SCORE_COUNT++;

        if(wallLineList.isEmpty()) {
            checkProjectiles(null);
        }

        //  Goes through each individual row of walls ("wall lines").
        Iterator<WallLine> wallLineIterator = wallLineList.iterator();
        while (wallLineIterator.hasNext()) {
            WallLine currentWallLine = wallLineIterator.next();

            checkProjectiles(currentWallLine);

            /*  Checks whether the player is in the hitbox of a wall or a power-up.
                    - Wall - player unbuffed -> ends the game, player buffed -> removes the wall
                    - Power-up -> buffs the player.     */
            for (int i = 0; i < currentWallLine.getWalls().size(); i++) {
                if(!(currentWallLine.getWalls().get(0) instanceof PowerUp)) {

                    //  These next 2 bunches of code handle showing gained score from destroying walls.
                    if(currentWallLine.getWalls().get(i).getJustDestroyed().length() > 0) {
                        currentWallLine.getWalls().get(i).setScoreDisplayCounter(
                                currentWallLine.getWalls().get(i).getScoreDisplayCounter() + 1);
                    }

                    if(currentWallLine.getWalls().get(i).getScoreDisplayCounter() > 10) {
                        currentWallLine.getWalls().get(i).setJustDestroyed("");
                    }

                    boolean stopTheGame = false;
                    if(!(currentWallLine instanceof MovingWallLine)) {
                        if(playerPosY == currentWallLine.getPosY() + TILE_SIZE &&
                                currentWallLine.getWalls().get(playerPosX / TILE_SIZE).isPlaced()) {
                            if(playerBuff.equals("breaker")) {
                                currentWallLine.getWalls().get(playerPosX / TILE_SIZE).setPlaced(false);
                                currentWallLine.getWalls().get(playerPosX / TILE_SIZE).setJustDestroyed("breaker");
                                SCORE_COUNT += 1000;
                                playerBuff = "";
                            } else {
                                stopTheGame = true;
                            }
                        }
                    } else if (((MovingWallLine) currentWallLine).retrieveMovingWall().isPlaced() &&
                               playerPosY <= currentWallLine.getPosY() + TILE_SIZE &&
                               playerPosY >= currentWallLine.getPosY() - TILE_SIZE &&
                               playerPosX <= ((MovingWallLine) currentWallLine).retrieveMovingWall().getPosX() + TILE_SIZE &&
                               playerPosX >= ((MovingWallLine) currentWallLine).retrieveMovingWall().getPosX() - TILE_SIZE) {
                        if(playerBuff.equals("breaker")) {
                            ((MovingWallLine) currentWallLine).retrieveMovingWall().setPlaced(false);
                            ((MovingWallLine) currentWallLine).retrieveMovingWall().setJustDestroyed("breaker");
                            ((MovingWall)((MovingWallLine) currentWallLine).retrieveMovingWall()).setMoving(false);
                            SCORE_COUNT += 1000;
                        } else {
                            stopTheGame = true;
                        }
                    }

                    if(stopTheGame) {
                        timer.stop();
                        gameRunning = false;
                    }

                //  Here it knows that the current tile is a buff.
                } else if(currentWallLine.getWalls().get(0) instanceof PowerUp &&
                          playerPosY == currentWallLine.getPosY() + TILE_SIZE  &&
                          currentWallLine.getWalls().get(0).getPosX() == playerPosX) {

                    currentWallLine.getWalls().get(0).setPlaced(false);
                    playerBuff = ((currentWallLine.getWalls().get(0)).toString());
                    wallLineIterator.remove();
                }
            }

            //  Removes non-visible walls and moves visible ones.
            if (currentWallLine.getPosY() > BOARD_HEIGHT) {
                wallLineIterator.remove();
            } else {

                //  Moves the horizontally moving tiles.
                if (currentWallLine instanceof MovingWallLine) {
                    for (Wall wall : currentWallLine.getWalls()) {
                        if(wall instanceof MovingWall) {
                            if (((MovingWall) wall).isMovingRight()) {
                                wall.setPosX(wall.getPosX() + STEP_DISTANCE);
                            } else {
                                wall.setPosX(wall.getPosX() - STEP_DISTANCE);
                            }

                            ((MovingWall) wall).bounceIfAtBorder();
                        }
                    }
                }

                currentWallLine.setPosY(currentWallLine.getPosY() + STEP_DISTANCE);
            }
        }

        //  Launches projectiles if they're supposed to be launched.
        if(projectilesWillBeLaunched) {
            if(playerBuff.equals("shooter")) {
                playerBuff = "";
            }
            if(!isOutOfBounds(playerPosX - TILE_SIZE)) {
                projectileList.add(new Projectile(playerPosX - TILE_SIZE, playerPosY - TILE_SIZE, true));
            }
            if(!isOutOfBounds(playerPosX + TILE_SIZE)) {
                projectileList.add(new Projectile(playerPosX + TILE_SIZE, playerPosY - TILE_SIZE, true));
            }
            projectilesWillBeLaunched = false;
        }

        //  Generates a wall or power-up.
        if(TOTAL_TICK_COUNT % WALL_GENERATION_FREQUENCY == 0) {
            if(GENERATED_WALLS_COUNT % ANOMALLY_GENERATION_FREQUENCY == 0){
                if(movingWallOrPowerUp) {
                    generatePowerUp();
                    movingWallOrPowerUp = false;
                } else {
                    generateMovingWall();
                    movingWallOrPowerUp = true;
                }
                GENERATED_WALLS_COUNT++;
            } else {
                generateWall();
                GENERATED_WALLS_COUNT++;
            }
        }

        /*  Shortens the time between each tick, resets TICK_COUNT and
                increases the value the TICK_COUNT has to reach to run these methods.   */
        if(TICK_COUNT > SPEED_INCREASE_FREQUENCY && timer.getDelay() > MIN_DELAY) {
            timer.setDelay(timer.getDelay() - SPEED_INCREASE_VALUE);
            SPEED_INCREASE_FREQUENCY += INITIAL_SPEED_INCREASE_FREQUENCY;
            TICK_COUNT = 0;
        }

        /*  At a certain point the value by which the time between each tick is shortened
                gets gradually decremented to prevent extreme game speed increase.  */
        if(TOTAL_TICK_COUNT > POINT_OF_DECREMENTING_SI_VALUE &&
           TOTAL_TICK_COUNT % SI_VALUE_DECREASE_FREQUENCY == 0 && SPEED_INCREASE_VALUE > 1) {
            SPEED_INCREASE_VALUE--;
        }
        System.out.println(debugReport());
    }

    // Checks if any projectile hit any wall. If so, removes them. If not, moves them further upwards.
    private void checkProjectiles(WallLine currentWallLine) {
        Iterator<Projectile> projectileIterator = projectileList.iterator();
        while (projectileIterator.hasNext()) {
            Projectile currentProjectile = projectileIterator.next();
            int convertedPosX = currentProjectile.getPosX() / TILE_SIZE;

            if(currentWallLine != null) {
                if(!(currentWallLine.getWalls().get(0) instanceof PowerUp) && currentProjectile.isLaunched()) {
                    if(!(currentWallLine instanceof MovingWallLine)) {
                        if (currentWallLine.getWalls().get(convertedPosX).isPlaced() &&
                            currentWallLine.getPosY() <= currentProjectile.getPosY() + TILE_SIZE &&
                            currentWallLine.getPosY() >= currentProjectile.getPosY() - TILE_SIZE) {

                            currentWallLine.getWalls().get(convertedPosX).setPlaced(false);
                            currentWallLine.getWalls().get(convertedPosX).setJustDestroyed("shooter");
                            SCORE_COUNT += 200;
                            projectileIterator.remove();
                        }
                    } else if (((MovingWallLine) currentWallLine).retrieveMovingWall().isPlaced() &&
                               currentProjectile.getPosY() <= currentWallLine.getPosY() + TILE_SIZE &&
                               currentProjectile.getPosY() >= currentWallLine.getPosY() - TILE_SIZE &&
                               currentProjectile.getPosX() <= ((MovingWallLine) currentWallLine).retrieveMovingWall().getPosX() + TILE_SIZE &&
                               currentProjectile.getPosX() >= ((MovingWallLine) currentWallLine).retrieveMovingWall().getPosX() - TILE_SIZE) {

                            ((MovingWallLine) currentWallLine).retrieveMovingWall().setPlaced(false);
                            ((MovingWallLine) currentWallLine).retrieveMovingWall().setJustDestroyed("shooter");
                            ((MovingWall)((MovingWallLine) currentWallLine).retrieveMovingWall()).setMoving(false);
                            SCORE_COUNT += 200;
                            projectileIterator.remove();
                    }
                }
            }

            if (currentProjectile.getPosY() < -TILE_SIZE) {
                projectileIterator.remove();
            } else {
                currentProjectile.setPosY(currentProjectile.getPosY() - STEP_DISTANCE);
            }
        }
    }

    //  Checks whether there is no walls at the provided coordinates. Also handles player picking up the buff.
    public boolean noWallThere(int coords) {
        for (WallLine wallLine : wallLineList) {
            for (Wall wall : wallLine.getWalls()) {
                if (!(wallLine instanceof MovingWallLine)) {
                    if (coords == wall.getPosX() &&
                            playerPosY < wallLine.getPosY() + TILE_SIZE &&
                            playerPosY > wallLine.getPosY() - TILE_SIZE) {
                        if(wall.isPowerUp() && wall.isPlaced()) {
                            wall.setPlaced(false);
                            playerBuff = wall.toString();
                            return true;
                        }
                        return false;
                    }
                }
//                else if(coords >= ((MovingWallLine) wallLine).retrieveMovingWall().getPosX() &&
//                          coords <= ((MovingWallLine) wallLine).retrieveMovingWall().getPosX() + TILE_SIZE &&
//                          playerPosY < wallLine.getPosY() + TILE_SIZE && playerPosY > wallLine.getPosY() - TILE_SIZE) {
//                    return false;
//                }
            }
        }

        return true;
    }

    private boolean isOutOfBounds(int posX) {
        return posX < 0 || posX > BOARD_WIDTH - TILE_SIZE;
    }

    private String debugReport() {
        return "Score:  " + SCORE_COUNT + "  Delay: " + timer.getDelay() + "  SI Frequency: " +
                SPEED_INCREASE_FREQUENCY + "  SI Value: " + SPEED_INCREASE_VALUE + "  Walls generated: " +
                GENERATED_WALLS_COUNT;
    }
}
