package com.example.bullsandcows;

public class GameUtils {
    public static void addChoices(CircularLinkedList list){
        eNumOfChoices choices[] = eNumOfChoices.values();
        for (eNumOfChoices choice: choices){
            list.addNode(choice.getValue());
        }
    }
}
