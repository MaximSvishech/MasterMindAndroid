package com.example.bullsandcows;

public enum eNumOfChoices {
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Nine(9),
    Ten(10);

    private final int id;
    eNumOfChoices(int id) {this.id = id; }
    public int getValue() {return id; }
}
