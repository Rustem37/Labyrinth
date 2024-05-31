package com.example.labyrinth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBGames {

    // Данные базы данных и таблиц
    public static final String DATABASE_NAME = "games.db";
    public static final int DATABASE_VERSION = 4;
    public static final String TABLE_NAME = "games";
    public static final String MAP_TABLE_NAME = "map";
    // Название столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_SEED = "seed";
    public static final String COLUMN_FORGET_WAY = "forgetWay";
    public static final String COLUMN_WAY_DISPLAY = "wayDisplay";
    public static final String COLUMN_MAP_UNLOCKED = "mapUnlocked";
    public static final String COLUMN_SUPPLIES_MAP = "suppliesMap";
    public static final String COLUMN_MOVE_LIMIT = "moveLimit";
    public static final String COLUMN_SMALL_SUPPLIES = "smallSupplies";
    public static final String COLUMN_MEDIUM_SUPPLIES = "mediumSupplies";
    public static final String COLUMN_LARGE_SUPPLIES = "largeSupplies";
    public static final String COLUMN_SCORE = "score";
    public static final String MAP_COLUMN_ID = "_id";
    public static final String MAP_COLUMN_GAME_ID = "game_id";
    public static final String MAP_COLUMN_NUMBER = "number";
    public static final String MAP_COLUMN_X = "x";
    public static final String MAP_COLUMN_Y = "y";
    // Номера столбцов
    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_TIME = 1;
    public static final int NUM_COLUMN_NAME = 2;
    public static final int NUM_COLUMN_SIZE = 3;
    public static final int NUM_COLUMN_SEED = 4;
    public static final int NUM_COLUMN_FORGET_WAY = 5;
    public static final int NUM_COLUMN_WAY_DISPLAY = 6;
    public static final int NUM_COLUMN_MAP_UNLOCKED = 7;
    public static final int NUM_COLUMN_SUPPLIES_MAP = 8;
    public static final int NUM_COLUMN_MOVE_LIMIT = 9;
    public static final int NUM_COLUMN_SMALL_SUPPLIES = 10;
    public static final int NUM_COLUMN_MEDIUM_SUPPLIES = 11;
    public static final int NUM_COLUMN_LARGE_SUPPLIES = 12;
    public static final int NUM_COLUMN_SCORE = 13;
    public static final int MAP_NUM_COLUMN_ID = 0;
    public static final int MAP_NUM_COLUMN_GAME_ID = 1;
    public static final int MAP_NUM_COLUMN_NUMBER = 2;
    public static final int MAP_NUM_COLUMN_X = 3;
    public static final int MAP_NUM_COLUMN_Y = 4;
    private SQLiteDatabase mDataBase;

    public DBGames(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public void insert(DBGamesObject obj) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TIME, obj.time);
        cv.put(COLUMN_NAME, obj.name);
        cv.put(COLUMN_SIZE, obj.size);
        cv.put(COLUMN_SEED, obj.seed);
        cv.put(COLUMN_FORGET_WAY, obj.forgetWay);
        cv.put(COLUMN_WAY_DISPLAY, obj.wayDisplay);
        cv.put(COLUMN_MAP_UNLOCKED, obj.mapUnlocked);
        cv.put(COLUMN_SUPPLIES_MAP, obj.suppliesMap);
        cv.put(COLUMN_MOVE_LIMIT, obj.moveLimit);
        cv.put(COLUMN_SMALL_SUPPLIES, obj.smallSupplies);
        cv.put(COLUMN_MEDIUM_SUPPLIES, obj.mediumSupplies);
        cv.put(COLUMN_LARGE_SUPPLIES, obj.largeSupplies);
        cv.put(COLUMN_SCORE, obj.score);
        long gameID = mDataBase.insert(TABLE_NAME, null, cv);
        for (int[] room : obj.rooms) {
            cv = new ContentValues();
            cv.put(MAP_COLUMN_GAME_ID, gameID);
            cv.put(MAP_COLUMN_NUMBER, room[0]);
            cv.put(MAP_COLUMN_X, room[1]);
            cv.put(MAP_COLUMN_Y, room[2]);
            mDataBase.insert(MAP_TABLE_NAME, null, cv);
        }

    }

    public void delete(long id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<DBGamesObject> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<DBGamesObject> arr = new ArrayList<>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String time = mCursor.getString(NUM_COLUMN_TIME);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                int size = mCursor.getInt(NUM_COLUMN_SIZE);
                long seed = mCursor.getLong(NUM_COLUMN_SEED);
                boolean forgetWay = mCursor.getInt(NUM_COLUMN_FORGET_WAY) == 1;
                boolean wayDisplay = mCursor.getInt(NUM_COLUMN_WAY_DISPLAY) == 1;
                boolean mapUnlocked = mCursor.getInt(NUM_COLUMN_MAP_UNLOCKED) == 1;
                boolean suppliesMap = mCursor.getInt(NUM_COLUMN_SUPPLIES_MAP) == 1;
                boolean moveLimit = mCursor.getInt(NUM_COLUMN_MOVE_LIMIT) == 1;
                int smallSupplies = mCursor.getInt(NUM_COLUMN_SMALL_SUPPLIES);
                int mediumSupplies = mCursor.getInt(NUM_COLUMN_MEDIUM_SUPPLIES);
                int largeSupplies = mCursor.getInt(NUM_COLUMN_LARGE_SUPPLIES);
                int score = mCursor.getInt(NUM_COLUMN_SCORE);
                ArrayList<int[]> rooms = new ArrayList<>();
                Cursor mapCursor = mDataBase.query(MAP_TABLE_NAME, null, MAP_COLUMN_GAME_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
                mapCursor.moveToFirst();
                if (!mapCursor.isAfterLast()) {
                    do {
                        int number = mapCursor.getInt(MAP_NUM_COLUMN_NUMBER);
                        int x = mapCursor.getInt(MAP_NUM_COLUMN_X);
                        int y = mapCursor.getInt(MAP_NUM_COLUMN_Y);
                        rooms.add(new int[]{number, x, y});
                    } while (mapCursor.moveToNext());
                }
                arr.add(new DBGamesObject(id, time, name, size, seed,
                        forgetWay, wayDisplay, mapUnlocked, suppliesMap, moveLimit,
                        smallSupplies, mediumSupplies, largeSupplies, score, rooms));
            } while (mCursor.moveToNext());
        }
        return arr;
    }


    public class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_SIZE + " INTEGER, " +
                    COLUMN_SEED + " INTEGER, " +
                    COLUMN_FORGET_WAY + " INTEGER, " +
                    COLUMN_WAY_DISPLAY + " INTEGER, " +
                    COLUMN_MAP_UNLOCKED + " INTEGER, " +
                    COLUMN_SUPPLIES_MAP + " INTEGER, " +
                    COLUMN_MOVE_LIMIT + " INTEGER, " +
                    COLUMN_SMALL_SUPPLIES + " INTEGER, " +
                    COLUMN_MEDIUM_SUPPLIES + " INTEGER, " +
                    COLUMN_LARGE_SUPPLIES + " INTEGER, " +
                    COLUMN_SCORE + " INTEGER);";
            String mapQuery = "CREATE TABLE " + MAP_TABLE_NAME + " (" +
                    MAP_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MAP_COLUMN_GAME_ID + " INTEGER, " +
                    MAP_COLUMN_NUMBER + " INTEGER, " +
                    MAP_COLUMN_X + " INTEGER, " +
                    MAP_COLUMN_Y + " INTEGER, " +
                    "FOREIGN KEY(" + MAP_COLUMN_GAME_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + ")" + "ON DELETE CASCADE);";
            db.execSQL(query);
            db.execSQL(mapQuery);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MAP_TABLE_NAME);
            onCreate(db);
        }
    }
}
