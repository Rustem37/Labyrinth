package com.example.labyrinth;

import java.util.ArrayList;

public class DBGamesObject {
    long id;
    String time;
    String name;
    int size;
    long seed;
    boolean forgetWay;
    boolean wayDisplay;
    boolean mapUnlocked;
    boolean suppliesMap;
    boolean moveLimit;
    int smallSupplies;
    int mediumSupplies;
    int largeSupplies;
    int score;
    ArrayList<int[]> rooms;

    public DBGamesObject(
            long id, String time, String name, int size, long seed,
            boolean forgetWay, boolean wayDisplay, boolean mapUnlocked,
            boolean suppliesMap, boolean moveLimit,
            int smallSupplies, int mediumSupplies, int largeSupplies,
            int score, ArrayList<int[]> rooms) {
        this.id = id;
        this.time = time;
        this.name = name;
        this.size = size;
        this.seed = seed;
        this.forgetWay = forgetWay;
        this.wayDisplay = wayDisplay;
        this.mapUnlocked = mapUnlocked;
        this.suppliesMap = suppliesMap;
        this.moveLimit = moveLimit;
        this.smallSupplies = smallSupplies;
        this.mediumSupplies = mediumSupplies;
        this.largeSupplies = largeSupplies;
        this.score = score;
        this.rooms = rooms;
    }

    public DBGamesObject(
            String time, String name, int size, long seed,
            boolean forgetWay, boolean wayDisplay, boolean mapUnlocked,
            boolean suppliesMap, boolean moveLimit,
            int smallSupplies, int mediumSupplies, int largeSupplies,
            int score, ArrayList<int[]> rooms) {
        this.time = time;
        this.name = name;
        this.size = size;
        this.seed = seed;
        this.forgetWay = forgetWay;
        this.wayDisplay = wayDisplay;
        this.mapUnlocked = mapUnlocked;
        this.suppliesMap = suppliesMap;
        this.moveLimit = moveLimit;
        this.smallSupplies = smallSupplies;
        this.mediumSupplies = mediumSupplies;
        this.largeSupplies = largeSupplies;
        this.score = score;
        this.rooms = rooms;
    }

}
