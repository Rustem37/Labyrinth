package com.example.labyrinth;

import java.io.Serializable;
import java.util.Random;

public class Room implements Serializable {


    String north, east, south, west;
    Trinket trinket;
    int[] coords;

    Room(int x, int y) {
        coords = new int[2];
        coords[0] = x;
        coords[1] = y;
        north = "wall";
        east = "wall";
        south = "wall";
        west = "wall";
    }

    void makeTrinket(Random random) {
        if ((north.equals("wall") && east.equals("wall") && south.equals("wall"))
                || (east.equals("wall") && south.equals("wall") && west.equals("wall"))
                || (south.equals("wall") && west.equals("wall") && north.equals("wall"))
                || (west.equals("wall") && north.equals("wall") && east.equals("wall"))) {
            trinket = new Trinket(random);
        }
    }
}
