package com.example.bullsandcows.utils;

import com.example.bullsandcows.CircularLinkedList;
import com.example.bullsandcows.eColor;
import com.example.bullsandcows.eNumOfChoices;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class GameUtils {
    public static void addChoices(CircularLinkedList list){
        eNumOfChoices choices[] = eNumOfChoices.values();
        for (eNumOfChoices choice: choices){
            list.addNode(choice.getValue());
        }
    }

    public static ArrayList<String> getColors(){
        ArrayList<String> colorList = new ArrayList<String>(
                Arrays.asList(eColor.CYAN.getValue(),
                        eColor.MAGENTA.getValue(),
                        eColor.YELLOW.getValue(),
                        eColor.BROWN.getValue(),
                        eColor.RED.getValue(),
                        eColor.BLUE.getValue(),
                        eColor.WHITE.getValue(),
                        eColor.GREEN.getValue()));

        return colorList;
    }
}
