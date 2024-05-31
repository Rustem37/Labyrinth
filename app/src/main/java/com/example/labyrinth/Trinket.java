package com.example.labyrinth;

import java.io.Serializable;
import java.util.Random;

public class Trinket implements Serializable {

    private static final Trinket[] trinkets = {
            new Trinket(5, "маленький мешок с припасами"),
            new Trinket(10, "обычный мешок с припасами"),
            new Trinket(20, "большой мешок с припасами"),
    };

    private final int cost;
    private final String name;
    int count;

    public Trinket(Random random) {
        Trinket trinket = getRandomTrinket(random);
        this.cost = trinket.getCost();
        this.name = trinket.getName();
    }

    public Trinket(int cost, String name) {
        this.cost = cost;
        this.name = name;
        this.count = 0;
    }

    public Trinket(int cost, String name, int count) {
        this.cost = cost;
        this.name = name;
        this.count = count;
    }

    private static Trinket getRandomTrinket(Random random) {
        int n = random.nextInt(7);
        if (n < 4) return trinkets[0];
        else if (n < 6) return trinkets[1];
        else return trinkets[2];
    }

    public int getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }
}
