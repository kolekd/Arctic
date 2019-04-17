package com.company.logic;

import com.company.model.PowerUp;
import com.company.model.Projectile;
import com.company.model.Wall;
import com.company.model.WallLine;
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
    private int WALL_GENERATION_FREQUENCY;
    private int GENERATED_WALLS_COUNT;

    public int SCORE_COUNT;
    private int TICK_COUNT;

    public boolean gameRunning;
    public boolean gameJustLaunched;

    public int playerPosX;
    public int playerPosY;
    public String playerBuff;

    public boolean projectileWillBeLaunched;

    public List<WallLine> wallLineList;
    public List<Projectile> projectileList;

    public Timer timer;

    public Logic() {
        this.gameJustLaunched = true;
    }

    public void initGame(ActionListener listener) {
        SPEED_INCREASE_VALUE = INITIAL_SPEED_INCREASE_VALUE;
        SPEED_INCREASE_FREQUENCY = INITIAL_SPEED_INCREASE_FREQUENCY;
        WALL_GENERATION_FREQUENCY = INITIAL_WALL_GENERATION_FREQUENCY;
        GENERATED_WALLS_COUNT = 0;

        playerPosX = (BOARD_WIDTH / 2) - (TILE_SIZE / 2);
        playerPosY = BOARD_HEIGHT - (TILE_SIZE * 4);
        playerBuff = "";

        projectileWillBeLaunched = false;

        wallLineList = new ArrayList<>();
        projectileList = new ArrayList<>();

        SCORE_COUNT = 0;
        TICK_COUNT = 0;

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
        SCORE_COUNT++;

        //  Goes through each individual row of walls ("wall lines").
        Iterator<WallLine> wallLineIterator = wallLineList.iterator();
        while (wallLineIterator.hasNext()) {
            WallLine currentWallLine = wallLineIterator.next();

            /*  Checks whether the player or projectile is in the hitbox of a wall or a power-up.
                    - Wall - player unbuffed -> ends the game, player buffed -> removes the wall
                    - Power-up -> buffs the player.       */
            for (int i = 0; i < currentWallLine.getWalls().size(); i++) {
                if(!currentWallLine.getWalls().get(0).isPowerUp()) {

                    // Checks if any projectile hit any wall. If not, moves them forward.
                    Iterator<Projectile> projectileIterator = projectileList.iterator();
                    while (projectileIterator.hasNext()) {
                        Projectile currentProjectile = projectileIterator.next();
                        int convertedPosX = currentProjectile.getPosX() / TILE_SIZE;

                        if(currentProjectile.isLaunched() &&
                           currentWallLine.getWalls().get(convertedPosX).isPlaced() &&
                           currentWallLine.getPosY() <= currentProjectile.getPosY() - TILE_SIZE + 4 &&
                           currentWallLine.getPosY() >= currentProjectile.getPosY() - TILE_SIZE - 4) {
                            currentWallLine.getWalls().get(convertedPosX).setPlaced(false);
                            projectileIterator.remove();
                        }
                    }

                    if(playerPosY == currentWallLine.getPosY() + TILE_SIZE &&
                       currentWallLine.getWalls().get(playerPosX / TILE_SIZE).isPlaced()) {

                        if(playerBuff.equals("breaker")) {
                            currentWallLine.getWalls().get(playerPosX / TILE_SIZE).setPlaced(false);
                            playerBuff = "";
                        } else {
                            timer.stop();
                            gameRunning = false;
                            break;
                        }
                    }

                //  Here it knows that the current tile is a buff.
                } else if(currentWallLine.getWalls().get(0) instanceof PowerUp &&
                          playerPosY == currentWallLine.getPosY() + TILE_SIZE  &&
                          currentWallLine.getWalls().get(0).getPosX() == playerPosX) {

                    currentWallLine.getWalls().get(0).setPlaced(false);
                    playerBuff = ((currentWallLine.getWalls().get(0)).toString());
                    wallLineIterator.remove();
                    return;
                }
            }

            //  Removes non-visible walls and moves visible ones.
            if (currentWallLine.getPosY() > BOARD_HEIGHT) {
                wallLineIterator.remove();
            } else {
                currentWallLine.setPosY(currentWallLine.getPosY() + STEP_DISTANCE);
            }
        }

        //  Removes non-visible projectiles and moves visible ones.
        Iterator<Projectile> projectileIterator = projectileList.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            if(projectile.getPosY() < -TILE_SIZE || !projectile.isLaunched()) {
                projectileIterator.remove();
            } else {
                projectile.setPosY(projectile.getPosY() - STEP_DISTANCE);
            }
        }

        //  Launches a projectile if one's supposed to be launched.
        if(projectileWillBeLaunched) {
            playerBuff = "";
            projectileList.add(new Projectile(playerPosX, playerPosY - TILE_SIZE, true));
            projectileWillBeLaunched = false;
        }

        if(SCORE_COUNT % WALL_GENERATION_FREQUENCY == 0) {
            if(GENERATED_WALLS_COUNT % POWER_UP_GENERATION_FREQUENCY == 0){
                generatePowerUp();
                GENERATED_WALLS_COUNT++;
            } else {
                generateWall();
                GENERATED_WALLS_COUNT++;
            }
        }

        if(TICK_COUNT > SPEED_INCREASE_FREQUENCY && timer.getDelay() > MIN_DELAY) {
            timer.setDelay(timer.getDelay() - SPEED_INCREASE_VALUE);
            SPEED_INCREASE_FREQUENCY += INITIAL_SPEED_INCREASE_FREQUENCY;
            TICK_COUNT = 0;
        }

        if(SCORE_COUNT % SI_VALUE_DECREASE_FREQUENCY == 0 &&
                SCORE_COUNT > POINT_OF_DECREMENTING_SI_VALUE && SPEED_INCREASE_VALUE > 1) {
            SPEED_INCREASE_VALUE--;
        }
    }

    //  Checks whether there is no walls at the provided coordinates.
    public boolean noWallThere(int coords) {
        for(WallLine wallLine : wallLineList) {
            for(Wall wall : wallLine.getWalls()) {
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

        return true;
    }

    public String debugReport() {
        return "Score:  " + SCORE_COUNT + "  Delay: " + timer.getDelay() + "  SI Frequency: " +
                SPEED_INCREASE_FREQUENCY + "  SI Value: " + SPEED_INCREASE_VALUE + "  Walls generated: " +
                GENERATED_WALLS_COUNT;
    }
}
