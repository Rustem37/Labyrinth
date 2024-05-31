package com.example.labyrinth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText etSeed, etSize;
    Button btnStart;
    CheckBox cbForget, cbWay, cbMapUnlocked, cbImportantPoints, cbMoveLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSeed = findViewById(R.id.etSeed);
        etSize = findViewById(R.id.etSize);
        btnStart = findViewById(R.id.btnStart);
        cbForget = findViewById(R.id.cbForget);
        cbWay = findViewById(R.id.cbWay);
        cbMapUnlocked = findViewById(R.id.cbMapUnlocked);
        cbImportantPoints = findViewById(R.id.cbImportantPoints);
        cbMoveLimit = findViewById(R.id.cbMoveLimit);
        Intent intent = getIntent();
        etSeed.setText(intent.getStringExtra("seed"));
        etSize.setText(intent.getStringExtra("size"));
        cbForget.setChecked(intent.getBooleanExtra("forgetWay", false));
        cbWay.setChecked(intent.getBooleanExtra("wayDisplay", false));
        cbMapUnlocked.setChecked(intent.getBooleanExtra("mapUnlocked", false));
        cbImportantPoints.setChecked(intent.getBooleanExtra("suppliesMap", false));
        cbMoveLimit.setChecked(intent.getBooleanExtra("moveLimit", false));
    }

    public void start(View view) {
        Intent intent = new Intent(MainActivity.this, LabyrinthActivity.class);

        String seed = etSeed.getText().toString();
        long seedLong = !seed.equals("") ? Long.parseLong(seed) : new Random().nextInt();

        String size = etSize.getText().toString();
        int sizeInt = !size.equals("") ? Integer.parseInt(size) : 9;

        intent.putExtra("seed", seedLong);
        intent.putExtra("size", sizeInt);
        intent.putExtra("forgetWay", cbForget.isChecked());
        intent.putExtra("wayDisplay", cbWay.isChecked());
        intent.putExtra("mapUnlocked", cbMapUnlocked.isChecked());
        intent.putExtra("importantPointsDisplay", cbImportantPoints.isChecked());
        intent.putExtra("moveLimited", cbMoveLimit.isChecked());
        startActivity(intent);
    }

    public void openHistory(View view) {
        Intent intent = new Intent(MainActivity.this, OldGamesActivity.class);
        startActivity(intent);
        finish();
    }
}