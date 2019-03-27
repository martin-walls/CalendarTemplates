package com.martinwalls.calendartemplates;

public enum Colour {
    LAVENDER(1),
    SAGE(2),
    GRAPE(3),
    FLAMINGO(4),
    BANANA(5),
    TANGERINE(6),
    PEACOCK(7),
    GRAPHITE(8),
    BLUEBERRY(9),
    BASIL(10),
    TOMATO(11);

    private int colourId;

    public int getColourId() {
        return colourId;
    }

    Colour(int colourId) {
        this.colourId = colourId;
    }
}
