package com.example.bullsandcows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum eColor {
    MAGENTA("#FF00FF"),
    RED("#FF0000"),
    GREEN("#00FF00"),
    CYAN("#00FFFF"),
    BLUE("#0000FF"),
    YELLOW("#FFFF00"),
    BROWN("#964B00"),
    WHITE("#FFFFFF");

    private final String hex;
    eColor(String hex) {this.hex = hex; }
    public String getValue() {return hex; }

    private static final List<eColor> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    private static final int SIZE = VALUES.size();

    private static final Random RANDOM = new Random();

    public static String fromString(String text) {
        for (eColor b : eColor.values()) {
            if (b.hex.equalsIgnoreCase(text)) {
                return b.toString();
            }
        }
        return null;
    }

    public static eColor randomColor()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}


