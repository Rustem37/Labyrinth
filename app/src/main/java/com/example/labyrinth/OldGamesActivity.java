package com.example.labyrinth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OldGamesActivity extends AppCompatActivity {

    DBGames database;
    ArrayList<DBGamesObject> games;
    GamesArrayAdapter adapter;
    DBGamesObject current;

    TextView tvSelected;
    ImageButton btnInventory, btnCopy, btnDelete;
    ListView lvGames;
    boolean doDelete;
    private DBGamesObject copied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_games);

        tvSelected = findViewById(R.id.tvSelected);
        btnDelete = findViewById(R.id.btnDelete);
        btnInventory = findViewById(R.id.btnInventory);
        btnCopy = findViewById(R.id.btnCopy);
        lvGames = findViewById(R.id.lvGames);
        database = new DBGames(this);

        btnDelete.setVisibility(View.GONE);
        btnInventory.setVisibility(View.GONE);
        btnCopy.setVisibility(View.GONE);

        games = new ArrayList<>();
        games.addAll(database.selectAll());

        adapter = new GamesArrayAdapter(this, R.layout.game_list_item, games);

        lvGames.setOnItemClickListener((parent, view, position, id) -> {
            current = games.get(position);
            tvSelected.setText(current.name);
            btnDelete.setVisibility(View.VISIBLE);
            btnInventory.setVisibility(View.VISIBLE);
            btnCopy.setVisibility(View.VISIBLE);
            doDelete = false;
        });

        lvGames.setAdapter(adapter);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(OldGamesActivity.this, MainActivity.class);
                if (copied != null) {
                    intent.putExtra("size", copied.size + "");
                    intent.putExtra("seed", copied.seed + "");
                    intent.putExtra("forgetWay", copied.forgetWay);
                    intent.putExtra("wayDisplay", copied.wayDisplay);
                    intent.putExtra("mapUnlocked", copied.mapUnlocked);
                    intent.putExtra("suppliesMap", copied.suppliesMap);
                    intent.putExtra("moveLimit", copied.moveLimit);
                }
                startActivity(intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void back(View view) {
        Intent intent = new Intent(OldGamesActivity.this, MainActivity.class);
        if (copied != null) {
            intent.putExtra("seed", copied.seed + "");
            intent.putExtra("size", copied.size + "");
            intent.putExtra("forgetWay", copied.forgetWay);
            intent.putExtra("wayDisplay", copied.wayDisplay);
            intent.putExtra("mapUnlocked", copied.mapUnlocked);
            intent.putExtra("suppliesMap", copied.suppliesMap);
            intent.putExtra("moveLimit", copied.moveLimit);
        }
        startActivity(intent);
        finish();
    }

    public void delete(View view) {
        if (!doDelete) {
            Toast.makeText(this, "Нажмите ещё раз для удаления", Toast.LENGTH_SHORT).show();
            doDelete = true;
        } else {
            database.delete(current.id);
            games.remove(current);
            tvSelected.setText("Не выбрано");
            adapter.notifyDataSetChanged();
            btnDelete.setVisibility(View.GONE);
            btnInventory.setVisibility(View.GONE);
            btnCopy.setVisibility(View.GONE);
        }
    }

    public void openInventory(View view) {
        Labyrinth labyrinth = new Labyrinth(new Random(current.seed), current.size);
        Intent intent = new Intent(OldGamesActivity.this, InventoryActivity.class);
        intent.putExtra("rooms", labyrinth.rooms);
        intent.putExtra("start", labyrinth.startPoint);
        intent.putExtra("exit", labyrinth.exit);
        ArrayList<int[]> way = new ArrayList<>();
        for (int[] room : current.rooms) way.add(new int[]{room[1], room[2]});
        ArrayList<int[]> finalWay = new ArrayList<>();
        for (int[] room : current.rooms) {
            if (room[0] == -1) break;
            finalWay.add(new int[]{room[1], room[2]});
        }
        intent.putExtra("way", way);
        intent.putExtra("finalWay", finalWay);
        Trinket[] trinkets = new Trinket[]{
                new Trinket(5, "маленький мешок с припасами", current.smallSupplies),
                new Trinket(10, "обычный мешок с припасами", current.mediumSupplies),
                new Trinket(20, "большой мешок с припасами", current.largeSupplies),
        };
        intent.putExtra("trinkets", trinkets);
        startActivity(intent);
    }

    public void copySettings(View view) {
        copied = current;
        Toast.makeText(this, "Настройки скопированы\n(Главное меню)", Toast.LENGTH_SHORT).show();
    }

    static class GamesArrayAdapter extends ArrayAdapter<DBGamesObject> {
        public GamesArrayAdapter(@NonNull Context context, int resource, @NonNull List<DBGamesObject> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_list_item, null);
            }

            DBGamesObject game = getItem(position);

            TextView tvGameName = convertView.findViewById(R.id.tvGameName);
            TextView tvTime = convertView.findViewById(R.id.tvTime);
            TextView tvScore = convertView.findViewById(R.id.tvScore);

            assert game != null;
            tvGameName.setText(game.name);
            tvTime.setText(game.time);
            tvScore.setText("Счёт: " + game.score);

            return convertView;
        }
    }
}