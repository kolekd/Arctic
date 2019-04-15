package com.company.logic;

import com.company.model.Wall;
import com.company.model.WallLine;
import com.company.util.RandomDecision;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.company.model.Constants.*;

public class Logic {

    // Kind of ez - val=10, freq=20, wgen=24, eachTickTileGoDownBy=TILE_SIZE / 4

    // Nice & smooth - init_delay=50, min_delay=15, val=10, freq=20, wgen=48, eachTickTileGoDownBy=TILE_SIZE / 8

    private static final int MIN_DELAY = 7;
    private static final int SI_VALUE_DECREASE_FREQUENCY =  80;
    private static final int POINT_OF_DECREMENTING_SI_VALUE = 60;

    private static int SPEED_INCREASE_VALUE;
    private static int SPEED_INCREASE_FREQUENCY;
    private static int WALL_GENERATION_FREQUENCY;

    public boolean gameRunning;
    public boolean gameJustLaunched;

    public int SCORE_COUNT;
    private int TICK_COUNT;

    public int player_posX;
    public int player_posY;

    public List<WallLine> listOfWallLists;

    private Timer timer;

    public Logic() {
        this.gameJustLaunched = true;
    }

    public void initGame(ActionListener listener) {
        SPEED_INCREASE_VALUE = INITIAL_SPEED_INCREASE_VALUE;
        SPEED_INCREASE_FREQUENCY = INITIAL_SPEED_INCREASE_FREQUENCY;
        WALL_GENERATION_FREQUENCY = INITIAL_WALL_GENERATION_FREQUENCY;

        player_posX = (BOARD_WIDTH / 2) - (TILE_SIZE / 2);
        player_posY = BOARD_HEIGHT - (TILE_SIZE * 4);

        listOfWallLists = new ArrayList<>();

        SCORE_COUNT = 0;
        TICK_COUNT = 0;

        gameJustLaunched = false;
        gameRunning = true;
        timer = new Timer(INITIAL_DELAY, listener);
        timer.start();
    }

    private void generateWall() {

        List<Wall> wallPlacerList = new ArrayList<>();
        WallLine wallLine = new WallLine(wallPlacerList, 0);

        for (int i = 0; i < MAX_TILES_IN_A_ROW; i++) {
            if(RandomDecision.get()) {
                wallPlacerList.add(new Wall(true, (i * TILE_SIZE)));
            } else {
                wallPlacerList.add(new Wall(false, 0));
            }
        }

        listOfWallLists.add(wallLine);
    }

    public void tickAction() {
        System.out.println("Score:  " + SCORE_COUNT + "  Delay: " + timer.getDelay() + "  SI Frequency: " +
                SPEED_INCREASE_FREQUENCY + "  SI Value: " + SPEED_INCREASE_VALUE);
        TICK_COUNT++;
        SCORE_COUNT++;

        Iterator<WallLine> iterator = listOfWallLists.iterator();
        while (iterator.hasNext()) {
            WallLine currentWallLine = iterator.next();

            for (int i = 0; i < currentWallLine.getWalls().size(); i++) {
                if(player_posY == currentWallLine.getPosY() + TILE_SIZE &&
                        currentWallLine.getWalls().get(player_posX / TILE_SIZE).isPlaced()) {

                    timer.stop();
                    gameRunning = false;
                    break;
                }
            }

            if (currentWallLine.getPosY() + TILE_SIZE > BOARD_HEIGHT) {
                iterator.remove();
            } else {
                currentWallLine.setPosY(currentWallLine.getPosY() + TILE_SIZE/8);
            }
        }

        if(SCORE_COUNT % WALL_GENERATION_FREQUENCY == 0) {
            generateWall();
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

        //REPAINT HERE
    }

    public boolean noWallThere(int coords) {
        for(WallLine wallLine : listOfWallLists) {
            for(Wall wall : wallLine.getWalls()) {
                if (coords == wall.getPosX() &&
                        player_posY < wallLine.getPosY() + TILE_SIZE &&
                        player_posY > wallLine.getPosY() - TILE_SIZE){
                    return false;
                }
            }
        }

        return true;
    }

    public String debugReport() {
        return "Score:  " + SCORE_COUNT + "  Delay: " + timer.getDelay() + "  SI Frequency: " +
                SPEED_INCREASE_FREQUENCY + "  SI Value: " + SPEED_INCREASE_VALUE;
    }
}
