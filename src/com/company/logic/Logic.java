package com.company.logic;

import com.company.model.*;
import com.company.model.newmodel.Wall;
import com.company.model.newmodel.MovingWall;
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

    public List<List<Wall>> listOfWallLists;
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

        listOfWallLists = new ArrayList<>();
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
        List<Wall> wallList = new ArrayList<>();

        int solidCount = 0;
        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if(RandomDecision.get()) {
                wallList.add(new Wall((i * TILE_SIZE), -TILE_SIZE, true));
                solidCount++;
            } else if(solidCount < MAX_TILES_IN_A_ROW - 1) {
                wallList.add(new Wall((i * TILE_SIZE), -TILE_SIZE, false));
            }
        }

        listOfWallLists.add(wallList);
    }

//    private void generateMovingWall() {
//        List<Wall> wallList = new ArrayList<>();
//
//        int randomPosX = RandomDecision.randomNumberInRange(0, MAX_TILES_IN_A_ROW - 1);
//        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
//            if(i == randomPosX) {
//                wallList.add(new MovingWall(true, i * TILE_SIZE));
//            } else {
//                wallList.add(new Wall(false, i * TILE_SIZE));
//            }
//        }
//
//        listOfWallLists.add(wallList);
//    }

//    private void generatePowerUp() {
//        List<Wall> listOfWalls = new ArrayList<>();
//        WallLine wallLine = new WallLine(listOfWalls);
//
//        PowerUp.Builder powerUpBuilder = new PowerUp.Builder(true,
//                RandomDecision.randomNumberInRange(0, MAX_TILES_IN_A_ROW - 1) * TILE_SIZE);
//
//        if (RandomDecision.get()) {
//            powerUpBuilder.makeBreaker();
//        } else {
//            powerUpBuilder.makeShooter();
//        }
//
//        listOfWalls.add(powerUpBuilder.build());
//
//        listOfWallLists.add(wallLine);
//    }

    //  This happens every tick.
    public void tickAction() {
        TICK_COUNT++;
        TOTAL_TICK_COUNT++;
        SCORE_COUNT++;

//        if(listOfWallLists.isEmpty()) {
//            checkProjectiles(null);
//        }

        //  Goes through each individual row of walls ("wall lines").
        Iterator<List<Wall>> wallLineIterator = listOfWallLists.iterator();
        while (wallLineIterator.hasNext()) {
            List<Wall> listOfWalls = wallLineIterator.next();
            Wall wallAtPlayerPosX = listOfWalls.get(playerPosX / TILE_SIZE);
//            checkProjectiles(listOfWalls);

            /*  Checks whether the player is in the hitbox of a wall or a power-up.
                    - Wall - player unbuffed -> ends the game, player buffed -> removes the wall
                    - Power-up -> buffs the player.     */
            for (int i = 0; i < listOfWalls.size(); i++) {
//                if(!(listOfWalls.get(0) instanceof PowerUp)) {
                if(gameRunning) {

                    //  These next 2 bunches of code handle showing gained score from destroying walls.
                    if(listOfWalls.get(i).getJustDestroyedBy().length() > 0) {
                        listOfWalls.get(i).setScoreDisplayCounter(listOfWalls.get(i).getScoreDisplayCounter() + 1);
                    }

                    if(listOfWalls.get(i).getScoreDisplayCounter() > 10) {
                        listOfWalls.get(i).setJustDestroyedBy("");
                    }

                    boolean stopTheGame = false;
                    if(!containsMovingWall(listOfWalls) &&
                       wallAtPlayerPosX.isPlaced() &&
                       playerPosY >= wallAtPlayerPosX.getPosY() - TILE_SIZE &&
                       playerPosY <= wallAtPlayerPosX.getPosY() + TILE_SIZE
                        ||
                       retrieveMovingWall(listOfWalls).isPlaced() &&
                       playerPosY >= wallAtPlayerPosX.getPosY() - TILE_SIZE &&
                       playerPosY <= wallAtPlayerPosX.getPosY() + TILE_SIZE &&
                       playerPosX <= retrieveMovingWall(listOfWalls).getPosX() + TILE_SIZE &&
                       playerPosX >= retrieveMovingWall(listOfWalls).getPosX() - TILE_SIZE) {
                            if(playerBuff.equals("breaker")) {
                                wallAtPlayerPosX.setPlaced(false);
                                wallAtPlayerPosX.setJustDestroyedBy("breaker");
//                                retrieveMovingWall(listOfWalls).setMoving(false);
                                SCORE_COUNT += 1000;
                                playerBuff = "";
                            } else {
                                stopTheGame = true;
                            }
                        }

                    if(stopTheGame) {
                        timer.stop();
                        gameRunning = false;
                    }

                    //  Here it knows that the current tile is a buff.
//                    } else if(listOfWalls.get(0) instanceof PowerUp &&
//                            playerPosY == listOfWalls.getPosY() + TILE_SIZE  &&
//                            listOfWalls.get(0).getPosX() == playerPosX) {
//
//                        listOfWalls.get(0).setPlaced(false);
//                        playerBuff = ((listOfWalls.get(0)).toString());
//                        wallLineIterator.remove();
//                    }

                    }

            }

            //  Removes non-visible walls and moves visible ones.
            if (listOfWalls.get(0).getPosY() > BOARD_HEIGHT) {
                wallLineIterator.remove();
            } else {

                //  Moves the horizontally moving tiles.
                if (listOfWalls instanceof MovingWallLine) {
                    for (Wall wall : listOfWalls) {
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
                //  TODO: LEFT OFF HERE
                moveWalls(listOfWalls);
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
                if(!(currentWallLine.get(0) instanceof PowerUp) && currentProjectile.isLaunched()) {
                    if(!(currentWallLine instanceof MovingWallLine)) {
                        if (currentWallLine.get(convertedPosX).isPlaced() &&
                            currentWallLine.getPosY() <= currentProjectile.getPosY() + TILE_SIZE &&
                            currentWallLine.getPosY() >= currentProjectile.getPosY() - TILE_SIZE) {

                            currentWallLine.get(convertedPosX).setPlaced(false);
                            currentWallLine.get(convertedPosX).setJustDestroyed("shooter");
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
        for (WallLine wallLine : listOfWallLists) {
            for (Wall wall : wallLine) {
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
            }
        }

        return true;
    }

    private boolean containsMovingWall(List<Wall> wallList) {
        for(Wall wall : wallList) {
            if (wall instanceof MovingWall) {
                return true;
            }
        }

        return false;
    }

    private MovingWall retrieveMovingWall(List<Wall> wallList) {
        for(Wall wall : wallList) {
            if (wall instanceof MovingWall) {
                return (MovingWall) wall;
            }
        }

        return null;
    }

    private void moveWalls(List<Wall> wallList) {
        int currentPosY = wallList.get(0).getPosY();
        for(Wall wall : wallList) {
            wall.setPosY(currentPosY + STEP_DISTANCE);
        }
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
