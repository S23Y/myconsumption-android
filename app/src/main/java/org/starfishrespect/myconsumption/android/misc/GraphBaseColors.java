package org.starfishrespect.myconsumption.android.misc;

import java.util.Random;

/**
 * Set of colors to use when displaying graphs for the first time
 */
public class GraphBaseColors {
    private static int[] colors = {0xffff0000, 0xff0000ff, 0xff000000,
            0xff000060, 0xff008000, 0xff600000, 0xff661144, 0xff606060, 0xffaa6611};

    public static int getRandomColor() {
        return colors[new Random().nextInt(colors.length)];
    }
}
