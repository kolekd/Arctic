package com.company.logic;

public class Constants {
    public static final boolean DEBUG_MODE = false;

    public static final int TILE_SIZE = 32;

    public static final int BOARD_HEIGHT = TILE_SIZE * 20;
    public static final int BOARD_WIDTH = TILE_SIZE * 13;
    public static final int MAX_TILES_IN_A_ROW = BOARD_WIDTH/TILE_SIZE;     //  <- Number of maximum walls per wall line.

    public static final int STEP_DISTANCE = TILE_SIZE / 8;                         /*  <- Distance by which the walls and
                                                                                     projectiles move each tick. */
    static final int INITIAL_SPEED_INCREASE_FREQUENCY = 20;                 //  <- How often does the speed increase.
    static final int INITIAL_SPEED_INCREASE_VALUE = 6;                      //  <- By how much does the speed increase.
    static final int INITIAL_DELAY = 50;                                    //  <- Starting speed.

    static final int MIN_DELAY = 3;                                         //  <- Maximum speed.
    static final int SI_VALUE_DECREASE_FREQUENCY =  80;                     /*  <- How often does the
                                                                                   'speed decrease value' decrease. */
    static final int POINT_OF_DECREMENTING_SI_VALUE = 60;                   /*  <- When does the
                                                                                   'speed decrease value' start decreasing. */
    static final int WALL_GENERATION_FREQUENCY = 85;                        //  <- How often do the walls spawn.
    static final int ANOMALY_GENERATION_FREQUENCY = 10;                     //  <- Per how many wall spawns.

    public static final int BREAKER_SCORE_VALUE = 1000;                     /*  <- Score points earned when breaking
                                                                                    a wall using the Breaker power-up. */
    public static final int SHOOTER_SCORE_VALUE = 500;                      /*  <- Score points earned when breaking
                                                                                    a wall using the Shooter power-up. */
    public static final String BREAKER = "breaker";
    public static final String SHOOTER = "shooter";

    public static final String GAME_TITLE = "Arctic";
    public static final String RESET_BUTTON = "R";
    public static final String START_BUTTON = "space";
}
