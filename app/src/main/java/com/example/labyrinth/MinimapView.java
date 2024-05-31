package com.example.labyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class MinimapView extends View {

    private final Paint wall = new Paint();
    private final Paint hall = new Paint();
    private final Paint player = new Paint();
    private final Paint fog = new Paint();
    private final Paint wayPaint = new Paint();
    private final int sizeX = 3;
    private final int sizeY = 5;
    ArrayList<int[]> way;
    ArrayList<int[]> finalWay;
    private Room[][] rooms;
    private int[] coords;
    private int[] miniCoords;
    private boolean wayDisplay;
    private boolean mapUnlocked;
    private boolean forget;
    private final int memory = 30;

    public MinimapView(Context context) {
        super(context);
    }

    public MinimapView(Context context, Room[][] rooms, int[] startPoint, boolean wayDisplay, boolean mapUnlocked, boolean forget) {
        super(context);
        this.rooms = rooms;
        coords = startPoint.clone();
        way = new ArrayList<>();
        this.wayDisplay = wayDisplay;
        this.mapUnlocked = mapUnlocked;
        if (mapUnlocked) this.forget = false;
        else this.forget = forget;
        finalWay = new ArrayList<>();
        if (mapUnlocked) for (Room[] roomRow : rooms)
            for (Room room : roomRow) way.add(room.coords);
        else way.add(startPoint.clone());
        finalWay.add(startPoint.clone());
        miniCoords = new int[]{sizeX / 2, sizeY / 2};
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float roomSize = (getWidth() - 100) / 5;
        float firstX = getWidth() / 2 - roomSize * sizeX / 2 - roomSize * (coords[0] - sizeX / 2);
        float firstY = 800 - roomSize * sizeY / 2 - roomSize * (coords[1] - sizeY / 2);

        wall.setColor(Color.rgb(84, 110, 122));
        wall.setStyle(Paint.Style.FILL);
        wall.setStrokeWidth(roomSize / 10);
        wayPaint.setColor(Color.rgb(102, 178, 255));
        wayPaint.setStyle(Paint.Style.FILL);
        wayPaint.setStrokeWidth(roomSize / 20);
        hall.setColor(Color.rgb(255, 255, 255));
        hall.setStyle(Paint.Style.FILL);
        player.setColor(Color.rgb(0, 0, 255));
        player.setStyle(Paint.Style.FILL);
        fog.setColor(Color.rgb(200, 200, 200));
        fog.setStyle(Paint.Style.FILL);

        canvas.drawRect(firstX, firstY,
                firstX + roomSize * rooms.length,
                firstY + roomSize * rooms[0].length,
                fog);

        final float halfWall = roomSize / 20;
        for (int i = way.size() - 1; i >= 0; i--) {
            if (forget && way.size() - i > memory) break;
            int x = way.get(i)[0];
            int y = way.get(i)[1];
            float px1 = x * roomSize + firstX;
            float py1 = y * roomSize + firstY;
            float px2 = px1 + roomSize;
            float py2 = py1 + roomSize;

            canvas.drawRect(px1, py1, px2, py2, hall);

            if (rooms[x][y].north.equals("wall"))
                canvas.drawLine(px1 - halfWall, py1, px2 + halfWall, py1, wall);
            if (rooms[x][y].east.equals("wall"))
                canvas.drawLine(px2, py1 - halfWall, px2, py2 + halfWall, wall);
            if (rooms[x][y].south.equals("wall"))
                canvas.drawLine(px1 - halfWall, py2, px2 + halfWall, py2, wall);
            if (rooms[x][y].west.equals("wall"))
                canvas.drawLine(px1, py1 - halfWall, px1, py2 + halfWall, wall);

            if (x == 0 || y == 0 || (rooms[x - 1][y].north.equals("wall") || rooms[x][y - 1].west.equals("wall")))
                canvas.drawLine(px1 - halfWall, py1, px1 + halfWall, py1, wall);
            if (x == rooms.length - 1 || y == 0 || (rooms[x + 1][y].north.equals("wall") || rooms[x][y - 1].east.equals("wall")))
                canvas.drawLine(px2 - halfWall, py1, px2 + halfWall, py1, wall);
            if (x == 0 || y == rooms[0].length - 1 || (rooms[x - 1][y].south.equals("wall") || rooms[x][y + 1].west.equals("wall")))
                canvas.drawLine(px1 - halfWall, py2, px1 + halfWall, py2, wall);
            if (x == rooms.length - 1 || y == rooms[0].length - 1 || (rooms[x + 1][y].south.equals("wall") || rooms[x][y + 1].east.equals("wall")))
                canvas.drawLine(px2 - halfWall, py2, px2 + halfWall, py2, wall);
        }

        if (wayDisplay) {
            final float halfWay = roomSize / 40;
            for (int i = 0; i < finalWay.size() - 1; i++) {
                boolean contains;
                if (forget) {
                    contains = false;
                    for (int j = way.size() - 1; j >= 0; j--) {
                        if (way.size() - j > memory) break;
                        if (Arrays.equals(finalWay.get(i), way.get(j))) {
                            contains = true;
                            break;
                        }
                    }
                } else contains = true;
                if (contains) {
                    float px1 = finalWay.get(i)[0] * roomSize + roomSize / 2 + firstX;
                    float py1 = finalWay.get(i)[1] * roomSize + roomSize / 2 + firstY;
                    float px2 = finalWay.get(i + 1)[0] * roomSize + roomSize / 2 + firstX;
                    float py2 = finalWay.get(i + 1)[1] * roomSize + roomSize / 2 + firstY;
                    canvas.drawLine(px1, py1, px2, py2, wayPaint);
                    canvas.drawLine(px1 - halfWay, py1, px1 + halfWay, py1, wayPaint);
                }
            }
        }

        canvas.drawCircle(miniCoords[0] * roomSize + roomSize / 2 + (getWidth() / 2 - roomSize * sizeX / 2),
                miniCoords[1] * roomSize + roomSize / 2 + (800 - roomSize * sizeY / 2),
                roomSize / 3, player);
    }

    void update(int[] coords) {
        miniCoords[0] += coords[0] - finalWay.get(finalWay.size() - 1)[0];
        miniCoords[1] += coords[1] - finalWay.get(finalWay.size() - 1)[1];
        if (!mapUnlocked) {
            if (!forget) {
                boolean contains = false;
                for (int[] a : way)
                    if (Arrays.equals(coords, a)) {
                        contains = true;
                        break;
                    }
                if (!contains) way.add(coords.clone());
            } else way.add(coords.clone());
        }
        finalWay.add(coords.clone());
        if (finalWay.size() > 2) {
            if (Arrays.equals(coords, finalWay.get(finalWay.size() - 3))) {
                finalWay.remove(finalWay.size() - 1);
                finalWay.remove(finalWay.size() - 1);
                Log.d(null, "Way removed");
            }
        }
        if (miniCoords[0] > sizeX - 1) {
            this.coords[0]++;
            miniCoords[0]--;
        }
        if (miniCoords[0] < 0) {
            this.coords[0]--;
            miniCoords[0]++;
        }
        if (miniCoords[1] > sizeY - 1) {
            this.coords[1]++;
            miniCoords[1]--;
        }
        if (miniCoords[1] < 0) {
            this.coords[1]--;
            miniCoords[1]++;
        }
        invalidate();
    }
}
