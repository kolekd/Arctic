package cz.danik.arctic.logic;


import javax.swing.*;

import static cz.danik.arctic.values.Constants.*;

public class GameFlow {

    public static Timer timer;

    static int SPEED_INCREASE_VALUE;
    static int SPEED_INCREASE_FREQUENCY;
    static int GENERATED_WALLS_COUNT;

    static int TOTAL_TICK_COUNT;
    static int TICK_COUNT;

    public static void manageSpeeds() {

        /*  Shortens the time between each tick, resets TICK_COUNT and
            increases the value the TICK_COUNT has to reach to run these methods.   */
        if (TICK_COUNT > SPEED_INCREASE_FREQUENCY && GameFlow.timer.getDelay() > MIN_DELAY) {
            GameFlow.timer.setDelay(GameFlow.timer.getDelay() - SPEED_INCREASE_VALUE);
            SPEED_INCREASE_FREQUENCY += INITIAL_SPEED_INCREASE_FREQUENCY;
            TICK_COUNT = 0;
        }

        /*  At a certain point the value by which the time between each tick is shortened
        gets gradually decremented to prevent extreme game speed increase.  */
        if (TOTAL_TICK_COUNT > POINT_OF_DECREMENTING_SI_VALUE &&
                TOTAL_TICK_COUNT % SI_VALUE_DECREASE_FREQUENCY == 0 && SPEED_INCREASE_VALUE > 1) {
            SPEED_INCREASE_VALUE--;
        }
    }

}
