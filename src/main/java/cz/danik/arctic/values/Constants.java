package cz.danik.arctic.values;

public class Constants {
    public static final boolean DEBUG_MODE_DEFAULT_VALUE = false;

    public static final int TILE_SIZE = 32;

    public static final int BOARD_HEIGHT = TILE_SIZE * 20;
    public static final int BOARD_WIDTH = TILE_SIZE * 13;
    public static final int MAX_TILES_IN_A_ROW = BOARD_WIDTH/TILE_SIZE;     //  <- Number of maximum walls per wall line.

    public static final int INITIAL_PLAYER_POS_X = (BOARD_WIDTH / 2) - (TILE_SIZE / 2);
    public static final int INITIAL_PLAYER_POS_Y = BOARD_HEIGHT - (TILE_SIZE * 4);

    public static final int STEP_DISTANCE = TILE_SIZE / 8;                  /*  <- Distance by which the walls and
                                                                                     projectiles move each tick. */
    public static final int INITIAL_SPEED_INCREASE_FREQUENCY = 1;                 //  <- How often does the speed increase.
    public static final int INITIAL_SPEED_INCREASE_VALUE = 5;                      //  <- By how much does the speed increase.
    public static final int INITIAL_DELAY_PRIMARY = 40;                            //  <- Primary timer starting speed.
    public static final int INITIAL_DELAY_SECONDARY = 12;                          //  <- Secondary timer starting speed.

    public static final int MIN_DELAY = 3;                                         //  <- Maximum speed.
    public static final int SI_VALUE_DECREASE_FREQUENCY =  80;                     /*  <- How often does the
                                                                                   'speed decrease value' decrease. */
    public static final int POINT_OF_DECREMENTING_SI_VALUE = 60;                   /*  <- When does the
                                                                                   'speed decrease value' start decreasing. */
    public static final int WALL_GENERATION_FREQUENCY = 85;                        //  <- How often do the walls spawn.
    public static final int ANOMALY_GENERATION_FREQUENCY = 10;                     //  <- Per how many wall spawns.
    public static final boolean INITIAL_WALL_OR_POWER_UP = false;


    public static final int ON_BREAK_SCORE_DISPLAY_TIME = 45;
    public static final int BREAKER_SCORE_VALUE = 1000;                     /*  <- Score points earned when breaking
                                                                                    a wall using the Breaker power-up. */
    public static final int SHOOTER_SCORE_VALUE = 500;                      /*  <- Score points earned when breaking
                                                                                    a wall using the Shooter power-up. */
    public static final String BREAKER = "breaker";
    public static final String SHOOTER = "shooter";

    // Menu
    public static final String RESET_KEY_TEXT = "R";
    public static final String START_KEY_TEXT = "space";
    public static final String GO_TO_MENU_KEY_TEXT = "Q";
    public static final String RESUME_KEY_TEXT = "esc";

    public static final String GAME_TITLE_TEXT = "Arctic";

    public static final String SINGLE_PLAYER_TEXT = "Singleplayer";
    public static final String MULTI_PLAYER_TEXT = "Multiplayer";
    public static final String DEBUG_MODE_TEXT = "Debug mode: ";

    public static final String START_GAME_TEXT = "Start the game";
    public static final String ADD_PLAYER_TEXT = "Add player.";
    public static final String REMOVE_PLAYER_TEXT = "Remove player.";
    public static final String PAUSED_TEXT = "Paused";
    public static final String GO_BACK = "<- Go back <-";

    public static final String MAIN_MENU_WINDOW = "menu";
    public static final String MULTIPLAYER_MENU_WINDOW = "multimenu";
    public static final String GAME_WINDOW = "game";
    public static final String GAME_OVER_WINDOW = "over";
    public static final String GAME_PAUSED_WINDOW = "paused";
    public static final String SCOREBOARD_WINDOW = "scoreboard";

    private static final int MENU_TEXT_POSITIONER = (BOARD_HEIGHT / 2) + (TILE_SIZE / 2);
    public static final int LINE_1_TEXT_POSITION = MENU_TEXT_POSITIONER;
    public static final int LINE_2_TEXT_POSITION = MENU_TEXT_POSITIONER + TILE_SIZE;
    public static final int LINE_3_TEXT_POSITION = MENU_TEXT_POSITIONER + (TILE_SIZE * 2);
    public static final int LINE_4_TEXT_POSITION = MENU_TEXT_POSITIONER + (TILE_SIZE * 3);
    public static final int TITLE_TEXT_POSITION = BOARD_HEIGHT / 4;
    public static final int SUBTITLE_TEXT_POSITION = (BOARD_HEIGHT / 4) + (TILE_SIZE * 2);

    public static final int MENU_CURSOR_OFFSET = -(TILE_SIZE / 2);
    public static final int LINE_1_CURSOR_POSITION = LINE_1_TEXT_POSITION + MENU_CURSOR_OFFSET;
    public static final int LINE_2_CURSOR_POSITION = LINE_2_TEXT_POSITION + MENU_CURSOR_OFFSET;
    public static final int LINE_3_CURSOR_POSITION = LINE_3_TEXT_POSITION + MENU_CURSOR_OFFSET;
    public static final int LINE_4_CURSOR_POSITION = LINE_4_TEXT_POSITION + MENU_CURSOR_OFFSET;

    public static final int CURSOR_1_X_POSITION = (BOARD_WIDTH / 4) - TILE_SIZE / 2;
    public static final int CURSOR_2_X_POSITION = ((BOARD_WIDTH / 4) * 3) - TILE_SIZE / 2;

}
