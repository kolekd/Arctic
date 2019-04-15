package com.company.logic;

public class Constants {
    public static final int TILE_SIZE = 32;

    public static final int BOARD_HEIGHT = TILE_SIZE * 20;
    public static final int BOARD_WIDTH = TILE_SIZE * 13;
    public static final int MAX_TILES_IN_A_ROW = BOARD_WIDTH/TILE_SIZE;

    static final int INITIAL_SPEED_INCREASE_FREQUENCY = 20;
    static final int INITIAL_SPEED_INCREASE_VALUE = 6;
    static final int INITIAL_WALL_GENERATION_FREQUENCY = 90;
    static final int INITIAL_DELAY = 50;
    static final int POWER_UP_GENERATION_FREQUENCY = 10;             //  <- Per how many walls.
}
