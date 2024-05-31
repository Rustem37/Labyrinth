package com.example.labyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MapView extends View {

    private final Paint wall = new Paint();
    private final Paint hall = new Paint();
    private final Paint player = new Paint();
    private final Paint background = new Paint();
    private final Paint startPaint = new Paint();
    private final Paint exitPaint = new Paint();
    private final Paint trinketPaint = new Paint();
    private final Paint actualTrinketPaint = new Paint();
    private final Paint wayPaint = new Paint();

    private Room[][] rooms;
    private ArrayList<int[]> way;
    private ArrayList<int[]> finalWay;
    private int[] start;
    private int[] exit;
    private Room[] startRoomsWithTrinkets;
    private Room[] roomsWithTrinkets;

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, Room[][] rooms, ArrayList<int[]> way, ArrayList<int[]> finalWay, int[] start, int[] exit, Room[] startRoomsWithTrinkets, Room[] roomsWithTrinkets) {
        super(context);
        this.rooms = rooms;
        this.way = way;
        this.finalWay = finalWay;
        this.start = start;
        this.exit = exit;
        this.startRoomsWithTrinkets = startRoomsWithTrinkets;
        this.roomsWithTrinkets = roomsWithTrinkets;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float roomSize = (getWidth() - 100) / rooms.length;
        float firstX = (getWidth() - roomSize * rooms.length) / 2;
        float firstY = 40;
        float halfWall = roomSize / 20;

        wall.setColor(Color.rgb(84, 110, 122));
        wall.setStyle(Paint.Style.FILL);
        wall.setStrokeWidth(roomSize / 10);
        wayPaint.setColor(Color.rgb(102, 178, 255));
        wayPaint.setStyle(Paint.Style.FILL);
        wayPaint.setStrokeWidth(roomSize / 20);
        startPaint.setColor(Color.rgb(25, 150, 0));
        startPaint.setStyle(Paint.Style.STROKE);
        startPaint.setStrokeWidth(roomSize / 15);
        exitPaint.setColor(Color.rgb(255, 0, 0));
        exitPaint.setStyle(Paint.Style.FILL);
        exitPaint.setStrokeWidth(roomSize / 10);
        hall.setColor(Color.rgb(247, 247, 208));
        hall.setStyle(Paint.Style.FILL);
        trinketPaint.setColor(Color.rgb(0, 255, 0));
        trinketPaint.setStyle(Paint.Style.FILL);
        actualTrinketPaint.setColor(Color.rgb(100, 0, 255));
        actualTrinketPaint.setStyle(Paint.Style.FILL);
        player.setColor(Color.rgb(0, 0, 255));
        player.setStyle(Paint.Style.FILL);
        background.setColor(Color.rgb(255, 255, 255));
        background.setStyle(Paint.Style.FILL);

        canvas.drawRect(firstX, firstY, firstX + roomSize * rooms.length, firstY + roomSize * rooms[0].length, background);
        for (int i = 0; i < way.size(); i++) {
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

        if (finalWay != null) {
            final float halfWay = roomSize / 40;
            float px1, py1, px2 = 0, py2 = 0;
            for (int i = 0; i < finalWay.size() - 1; i++) {
                px1 = finalWay.get(i)[0] * roomSize + roomSize / 2 + firstX;
                py1 = finalWay.get(i)[1] * roomSize + roomSize / 2 + firstY;
                px2 = finalWay.get(i + 1)[0] * roomSize + roomSize / 2 + firstX;
                py2 = finalWay.get(i + 1)[1] * roomSize + roomSize / 2 + firstY;
                canvas.drawLine(px1, py1, px2, py2, wayPaint);
                canvas.drawLine(px1 - halfWay, py1, px1 + halfWay, py1, wayPaint);
            }
            int[] last = finalWay.get(finalWay.size() - 1);
            Room lastRoom = rooms[last[0]][last[1]];
            if (lastRoom.north.equals("hole") && last[1] == 0)
                canvas.drawLine(px2, py2 + halfWay, px2, py2 - roomSize / 2 - halfWall, wayPaint);
            if (lastRoom.east.equals("hole") && last[0] == rooms.length - 1)
                canvas.drawLine(px2 - halfWay, py2, px2 + roomSize / 2 + halfWall, py2, wayPaint);
            if (lastRoom.south.equals("hole") && last[1] == rooms[0].length - 1)
                canvas.drawLine(px2, py2 - halfWay, px2, py2 + roomSize / 2 + halfWall, wayPaint);
            if (lastRoom.west.equals("hole") && last[0] == 0)
                canvas.drawLine(px2 + halfWay, py2, px2 - roomSize / 2 - halfWall, py2, wayPaint);

            startPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(start[0] * roomSize + roomSize / 2 + firstX, start[1] * roomSize + roomSize / 2 + firstY, roomSize / 6, startPaint);
        } else
            canvas.drawCircle(start[0] * roomSize + roomSize / 2 + firstX, start[1] * roomSize + roomSize / 2 + firstY, roomSize / 4, startPaint);
        if (startRoomsWithTrinkets != null) {
            float px1 = exit[0] * roomSize + firstX;
            float py1 = exit[1] * roomSize + firstY;
            float px2 = px1 + roomSize;
            float py2 = py1 + roomSize;
            switch (exit[2]) {
                case 0:
                    canvas.drawLine(px1 + halfWall, py1, px2 - halfWall, py1, exitPaint);
                    break;
                case 1:
                    canvas.drawLine(px2, py1 + halfWall, px2, py2 - halfWall, exitPaint);
                    break;
                case 2:
                    canvas.drawLine(px1 + halfWall, py2, px2 - halfWall, py2, exitPaint);
                    break;
                case 3:
                    canvas.drawLine(px1, py1 + halfWall, px1, py2 - halfWall, exitPaint);
                    break;
            }
            for (Room room : startRoomsWithTrinkets) {
                canvas.drawCircle(room.coords[0] * roomSize + roomSize / 2 + firstX, room.coords[1] * roomSize + roomSize / 2 + firstY, roomSize / 5, trinketPaint);
            }
            for (Room room : roomsWithTrinkets) {
                canvas.drawCircle(room.coords[0] * roomSize + roomSize / 2 + firstX, room.coords[1] * roomSize + roomSize / 2 + firstY, roomSize / 5, actualTrinketPaint);
            }
        }
    }
}
