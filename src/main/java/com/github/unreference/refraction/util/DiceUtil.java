package com.github.unreference.refraction.util;

import java.util.Random;

public class DiceUtil {
    public static final Random random = new Random();

    private DiceUtil() {
    }

    public static int roll(int sides) {
        return random.nextInt(sides) + 1;
    }
}
