package com.example.labyrinth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Labyrinth {

    Room[][] rooms;
    int[] startPoint;
    int[] exit;
    int maxWaySize;

    Labyrinth(Random random, int size) {
        rooms = new Room[size][size];
        for (int i = 0; i < rooms.length; i++)
            for (int j = 0; j < rooms[0].length; j++)
                rooms[i][j] = new Room(i, j);

        startPoint = new int[2];
        int[] coords = new int[2];
        exit = new int[3];
        int exitNum = random.nextInt(size * 4);

        // number on border to coords
        if (exitNum < size) {
            coords[0] = exitNum % size;
            coords[1] = 0;
            rooms[coords[0]][coords[1]].north = "hole";
            exit[2] = 0;
        } else if (exitNum < size * 2) {
            coords[0] = exitNum % size;
            coords[1] = size - 1;
            rooms[coords[0]][coords[1]].south = "hole";
            exit[2] = 2;
        } else if (exitNum < size * 3) {
            coords[0] = size - 1;
            coords[1] = exitNum % size;
            rooms[coords[0]][coords[1]].east = "hole";
            exit[2] = 1;
        } else if (exitNum < size * 4) {
            coords[0] = 0;
            coords[1] = exitNum % size;
            rooms[coords[0]][coords[1]].west = "hole";
            exit[2] = 3;
        }
        exit[0] = coords[0];
        exit[1] = coords[1];

        int visitedCount = 1;
        boolean[][] visited = new boolean[size][size];
        visited[coords[0]][coords[1]] = true;

        LinkedList<int[]> way = new LinkedList<>();
        way.add(coords.clone());
        maxWaySize = 1;
        final int wallChance = 100;

        while (visitedCount < size * size) {
            switch (chooseDirection(random, visited, coords)) {
                case 0: // back on the way
                    way.removeLast();
                    coords = way.getLast().clone();
                    break;
                case 1: // north
                    rooms[coords[0]][coords[1]].north = "hole";
                    rooms[coords[0]][coords[1] - 1].south = "hole";
                    if (random.nextInt(100) < wallChance) {
                        coords[1]--;
                        way.add(coords.clone());
                        visited[coords[0]][coords[1]] = true;
                        visitedCount++;
                    }
                    break;
                case 2: // east
                    rooms[coords[0]][coords[1]].east = "hole";
                    rooms[coords[0] + 1][coords[1]].west = "hole";
                    if (random.nextInt(100) < wallChance) {
                        coords[0]++;
                        way.add(coords.clone());
                        visited[coords[0]][coords[1]] = true;
                        visitedCount++;
                    }
                    break;
                case 3: // south
                    rooms[coords[0]][coords[1]].south = "hole";
                    rooms[coords[0]][coords[1] + 1].north = "hole";
                    if (random.nextInt(100) < wallChance) {
                        coords[1]++;
                        way.add(coords.clone());
                        visited[coords[0]][coords[1]] = true;
                        visitedCount++;
                    }
                    break;
                case 4: // west
                    rooms[coords[0]][coords[1]].west = "hole";
                    rooms[coords[0] - 1][coords[1]].east = "hole";
                    if (random.nextInt(100) < wallChance) {
                        coords[0]--;
                        way.add(coords.clone());
                        visited[coords[0]][coords[1]] = true;
                        visitedCount++;
                    }
                    break;
            }
            if (way.size() > maxWaySize) {
                maxWaySize = way.size();
                startPoint = coords.clone();
            }
        }
        for (Room[] room : rooms)
            for (int j = 0; j < rooms[0].length; j++)
                room[j].makeTrinket(random);
        rooms[startPoint[0]][startPoint[1]].trinket = null;
    }

    private int chooseDirection(Random random, boolean[][] visited, int[] coords) {
        boolean[] directions = new boolean[]{coords[1] >= 1 && !visited[coords[0]][coords[1] - 1],
                coords[0] <= rooms.length - 2 && !visited[coords[0] + 1][coords[1]],
                coords[1] <= rooms[0].length - 2 && !visited[coords[0]][coords[1] + 1],
                coords[0] >= 1 && !visited[coords[0] - 1][coords[1]]
        };

        ArrayList<Integer> trueIndices = new ArrayList<>();
        for (int i = 0; i < directions.length; i++)
            if (directions[i]) trueIndices.add(i);

        int direction = 0;
        if (!trueIndices.isEmpty())
            direction = trueIndices.get(random.nextInt(trueIndices.size())) + 1;
        return direction;
    }
}