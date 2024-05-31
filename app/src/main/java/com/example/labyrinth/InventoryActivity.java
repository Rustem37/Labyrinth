package com.example.labyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    Room[] roomsWithTrinkets;
    Room[] startRoomsWithTrinkets;
    private TableLayout trinketsContainer;
    private MapView mapView;
    private Room[][] rooms;
    private int[] start;
    private int[] exit;
    private Trinket[] trinkets;
    private ArrayList<int[]> way;
    private ArrayList<int[]> finalWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Intent intent = getIntent();

        rooms = (Room[][]) intent.getSerializableExtra("rooms");
        start = (int[]) intent.getSerializableExtra("start");
        exit = (int[]) intent.getSerializableExtra("exit");
        way = (ArrayList<int[]>) intent.getSerializableExtra("way");
        finalWay = (ArrayList<int[]>) intent.getSerializableExtra("finalWay");
        trinkets = (Trinket[]) intent.getSerializableExtra("trinkets");
        roomsWithTrinkets = (Room[]) intent.getSerializableExtra("roomsWithTrinkets");
        startRoomsWithTrinkets = (Room[]) intent.getSerializableExtra("startRoomsWithTrinkets");

        trinketsContainer = findViewById(R.id.trinketsContainer);

        mapView = new MapView(this, rooms, way, finalWay, start, exit,
                startRoomsWithTrinkets, roomsWithTrinkets);
        LinearLayout mapContainer = findViewById(R.id.mapContainer);
        mapContainer.addView(mapView);

        for (Trinket trinket : trinkets) {
            TableRow row = new TableRow(this);

            TextView name = new TextView(this);
            name.setPadding(10, 10, 10, 10);
            name.setTextSize(20);
            switch (trinket.getName()) {
                case "маленький мешок с припасами":
                    name.setText("малые припасы");
                    break;
                case "обычный мешок с припасами":
                    name.setText("средние припасы");
                    break;
                case "большой мешок с припасами":
                    name.setText("большие припасы");
                    break;
            }
            row.addView(name);

            TextView count = new TextView(this);
            count.setPadding(10, 10, 10, 10);
            count.setTextSize(20);
            count.setText(String.valueOf(trinket.count));
            row.addView(count);

            TextView cost = new TextView(this);
            cost.setPadding(10, 10, 10, 10);
            cost.setTextSize(20);
            cost.setText(String.valueOf(trinket.getCost()));
            row.addView(cost);

            trinketsContainer.addView(row);
        }
    }
}