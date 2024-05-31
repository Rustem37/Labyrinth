package com.example.labyrinth;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {

    private final Room[][] rooms;
    ArrayList<Room> roomsWithTrinkets;
    int[] coords;
    int score;
    Trinket[] trinkets;
    boolean moveLimited;

    public Player(Room[][] rooms, int[] startPoint, boolean moveLimited, int maxWaySize) {
        this.rooms = rooms;
        this.coords = startPoint.clone();
        this.moveLimited = moveLimited;
        if (moveLimited) this.score = maxWaySize;
        roomsWithTrinkets = new ArrayList<>();
        for (Room[] roomsRow : rooms)
            for (Room room : roomsRow)
                if (room.trinket != null) roomsWithTrinkets.add(room);
        trinkets = new Trinket[]{
                new Trinket(5, "маленький мешок с припасами"),
                new Trinket(10, "обычный мешок с припасами"),
                new Trinket(20, "большой мешок с припасами"),
        };
        checkItems();
    }

    String move(int direction) {
        switch (direction) {
            case 0: // north
                if (rooms[coords[0]][coords[1]].north.equals("hole")) {
                    coords[1]--;
                    if (moveLimited) score--;
                    if (coords[1] < 0) return "exit";
                } else return "wall";
                break;
            case 1: // east
                if (rooms[coords[0]][coords[1]].east.equals("hole")) {
                    coords[0]++;
                    if (moveLimited) score--;
                    if (coords[0] >= rooms.length) return "exit";
                } else return "wall";
                break;
            case 2: // south
                if (rooms[coords[0]][coords[1]].south.equals("hole")) {
                    coords[1]++;
                    if (moveLimited) score--;
                    if (coords[1] >= rooms[0].length) return "exit";
                } else return "wall";
                break;
            case 3: // west
                if (rooms[coords[0]][coords[1]].west.equals("hole")) {
                    coords[0]--;
                    if (moveLimited) score--;
                    if (coords[0] < 0) return "exit";
                } else return "wall";
                break;
        }
        String s = checkItems();
        if (moveLimited && s.equals("") && score <= 0) return "die";
        return s;
    }

    private String checkItems() {
        String s = "";
        for (int i = 0; i < roomsWithTrinkets.size(); i++)
            if (Arrays.equals(coords, roomsWithTrinkets.get(i).coords)) {
                for (Trinket trinket : trinkets)
                    if (trinket.getName().equals(roomsWithTrinkets.get(i).trinket.getName())) {
                        trinket.count++;
                        break;
                    }
                score += roomsWithTrinkets.get(i).trinket.getCost();
                s = "Вы нашли " + roomsWithTrinkets.get(i).trinket.getName();
                roomsWithTrinkets.get(i).trinket = null;
                roomsWithTrinkets.remove(i);
                break;
            }
        return s;
    }

    void retToLab() {
        if (coords[1] < 0) coords[1]++;
        else if (coords[0] >= rooms.length) coords[0]--;
        else if (coords[1] >= rooms[0].length) coords[1]--;
        else if (coords[0] < 0) coords[0]++;
    }
}