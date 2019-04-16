package com.company.logic;

import com.company.model.PowerUp;
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

    public List<WallLine> listOfWallLines;

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

        listOfWallLines = new ArrayList<>();

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

        listOfWallLines.add(wallLine);
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

        listOfWallLines.add(wallLine);
    }

    //  This happens every tick.
    public void tickAction() {
        TICK_COUNT++;
        SCORE_COUNT++;

        Iterator<WallLine> iterator = listOfWallLines.iterator();
        while (iterator.hasNext()) {
            WallLine currentWallLine = iterator.next();

            //  Checks whether the player is in the hitbox of a wall or a power-up.
            //   Wall (player) -> ends the game, Power-up -> buffs the player.
            for (int i = 0; i < currentWallLine.getWalls().size(); i++) {
                if(!currentWallLine.getWalls().get(0).isPowerUp()) {
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
                } else {
                    if(playerPosY == currentWallLine.getPosY() + TILE_SIZE &&
                            currentWallLine.getWalls().get(0).getPosX() == playerPosX) {

                        currentWallLine.getWalls().get(0).setPlaced(false);
                        playerBuff = ((currentWallLine.getWalls().get(0)).toString());
                        iterator.remove();
                        return;
                    }
                }
            }

            if (currentWallLine.getPosY() > BOARD_HEIGHT) {
                iterator.remove();
            } else {
                currentWallLine.setPosY(currentWallLine.getPosY() + TILE_SIZE/8);
            }
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

    //  Checks whether there is no walls at the provided coordinates. Ignores buffs.
    public boolean noWallThere(int coords) {
        for(WallLine wallLine : listOfWallLines) {
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
