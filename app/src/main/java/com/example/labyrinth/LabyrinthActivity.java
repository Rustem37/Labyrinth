package com.example.labyrinth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class LabyrinthActivity extends AppCompatActivity {

    private static final int SWIPE_MIN_DISTANCE = 50;
    private boolean forget;
    private boolean wayDisplay;
    private boolean mapUnlocked;
    private boolean importantPointsDisplay;
    private boolean moveLimited;
    private Player player;
    private TextView tvPopupText, tvMoveLimit;
    private MinimapView minimapView;
    private int seconds;
    private boolean running;
    private boolean wasRunning;
    private long seed;
    private int size;
    private int oldTime;
    private ArrayList<Room> roomsWithTrinkets;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labyrinth);
        Intent receivedIntent = getIntent();

        forget = receivedIntent.getBooleanExtra("forgetWay", true);
        wayDisplay = receivedIntent.getBooleanExtra("wayDisplay", true);
        mapUnlocked = receivedIntent.getBooleanExtra("mapUnlocked", true);
        importantPointsDisplay = receivedIntent.getBooleanExtra("importantPointsDisplay", true);
        moveLimited = receivedIntent.getBooleanExtra("moveLimited", true);

        tvPopupText = findViewById(R.id.tvPopupText);
        tvMoveLimit = findViewById(R.id.tvMoveLimit);
        RelativeLayout rlMain = findViewById(R.id.rlMain);
        ImageButton btnInventory = findViewById(R.id.btnInventory);

        if (moveLimited) tvMoveLimit.setVisibility(View.VISIBLE);

        seed = receivedIntent.getLongExtra("seed", new Random().nextLong());
        size = receivedIntent.getIntExtra("size", 9);
        Labyrinth labyrinth = new Labyrinth(new Random(seed), size);

        roomsWithTrinkets = new ArrayList<>();
        for (Room[] roomsRow : labyrinth.rooms)
            for (Room room : roomsRow)
                if (room.trinket != null) roomsWithTrinkets.add(room);

        player = new Player(labyrinth.rooms, labyrinth.startPoint, moveLimited, labyrinth.maxWaySize);
        tvMoveLimit.setText(String.valueOf(player.score));

        minimapView = new MinimapView(this, labyrinth.rooms, labyrinth.startPoint, wayDisplay, mapUnlocked, forget);
        LinearLayout minimapContainer = findViewById(R.id.minimapContainer);
        minimapContainer.addView(minimapView);

        GestureDetectorCompat swipeDetector = new GestureDetectorCompat(this, new MyGestureListener());
        rlMain.setOnTouchListener((view, motionEvent) -> swipeDetector.onTouchEvent(motionEvent));

        btnInventory.setOnClickListener((View) -> {
            Intent intent = new Intent(LabyrinthActivity.this, InventoryActivity.class);
            intent.putExtra("rooms", labyrinth.rooms);
            intent.putExtra("start", labyrinth.startPoint);
            intent.putExtra("exit", labyrinth.exit);
            intent.putExtra("way", minimapView.way);
            if (importantPointsDisplay) {
                Room[] startRoomsWithTrinketsArray = new Room[roomsWithTrinkets.size()];
                startRoomsWithTrinketsArray = roomsWithTrinkets.toArray(startRoomsWithTrinketsArray);
                intent.putExtra("startRoomsWithTrinkets", startRoomsWithTrinketsArray);
                Room[] roomsWithTrinketsArray = new Room[player.roomsWithTrinkets.size()];
                roomsWithTrinketsArray = player.roomsWithTrinkets.toArray(roomsWithTrinketsArray);
                intent.putExtra("roomsWithTrinkets", roomsWithTrinketsArray);
            }
            intent.putExtra("trinkets", player.trinkets);
            startActivity(intent);
        });

        // This callback is only called when MyFragment is at least started
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (seconds - oldTime > 10 || oldTime == 0) {
                    Toast.makeText(LabyrinthActivity.this, "Нажмите ещё раз, чтобы завершить игру", Toast.LENGTH_SHORT).show();
                    oldTime = seconds;
                } else finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        running = true;
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTimer();


    }

    public void retToLab() {
        running = true;
        player.retToLab();
    }

    public void exit() {
        Intent intentEnd = new Intent(LabyrinthActivity.this, MainActivity.class);
        startActivity(intentEnd);
    }

    void moveClick(int direction) {
        String moveResult = player.move(direction);
        if (moveResult.equals("exit")) {
            running = false;
            ExitFragment exitFragment = new ExitFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            exitFragment.show(transaction, "dialog");
        } else if (moveResult.equals("die")) {
            Toast.makeText(this, "Вы проиграли", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!moveResult.equals("wall")) {
            if (moveResult.equals("")) tvPopupText.setVisibility(View.INVISIBLE);
            else {
                tvPopupText.setVisibility(View.VISIBLE);
                tvPopupText.setText(moveResult);
            }
            minimapView.update(player.coords);
            tvMoveLimit.setText(String.valueOf(player.score));
        }
    }

    @Override
    public void onSaveInstanceState(
            @NonNull Bundle outState,
            @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) running = true;
    }

    private void runTimer() {
        final TextView timeView = findViewById(R.id.tvStopwatch);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                timeView.setText(String.format(Locale.getDefault(),
                        "%02d:%02d", minutes, secs));
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void save(String time, String name) {
        ArrayList<int[]> way = minimapView.way;
        ArrayList<int[]> finalWay = minimapView.finalWay;
        ArrayList<int[]> rooms = new ArrayList<>();
        for (int i = 0; i < finalWay.size(); i++)
            rooms.add(new int[]{i, finalWay.get(i)[0], finalWay.get(i)[1]});
        for (int[] i : way) {
            boolean contains = false;
            for (int[] room : rooms)
                if (room[1] == i[0] && room[2] == i[1]) {
                    contains = true;
                    break;
                }
            if (!contains) rooms.add(new int[]{-1, i[0], i[1]});
        }
        new DBGames(this).insert(new DBGamesObject(time, name, size, seed,
                forget, wayDisplay, mapUnlocked, importantPointsDisplay, moveLimited,
                player.trinkets[0].count, player.trinkets[1].count, player.trinkets[2].count,
                player.score, rooms));
    }

    public void back(View view) {
        if (seconds - oldTime > 10 || oldTime == 0) {
            Toast.makeText(this, "Нажмите ещё раз, чтобы завершить игру", Toast.LENGTH_SHORT).show();
            oldTime = seconds;
        } else finish();
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            assert e1 != null;
            float deltaX = e2.getX() - e1.getX();
            float deltaY = e2.getY() - e1.getY();

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (deltaX > SWIPE_MIN_DISTANCE) {
                    moveClick(1);
                } else if (deltaX < -SWIPE_MIN_DISTANCE) {
                    moveClick(3);
                }
            } else {
                if (deltaY > SWIPE_MIN_DISTANCE) {
                    moveClick(2);
                } else if (deltaY < -SWIPE_MIN_DISTANCE) {
                    moveClick(0);
                }
            }

            return false;
        }
    }
}