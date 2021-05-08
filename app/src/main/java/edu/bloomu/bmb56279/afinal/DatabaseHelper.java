package edu.bloomu.bmb56279.afinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * A custom class that extends with SQLiteOpenHelper and will deal with creating,
 * writing, and reading from a database.
 *
 * @author Brett Bernardi
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "SCORE_TABLE";
    public static final String COL0 = "ID";
    private static final String COL1 = "NAME";
    private static final String COL2 = "SCORE";
    private static final int MAX_NUMBER_SCORES = 5;

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COL0 + " INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, " + COL1 + " TEXT, " + COL2 + " INTEGER)";

        System.out.println(createTable);
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Add data to the database. This would be a high score.
     */
    public boolean addData(String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        // first convert data to content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, name);
        contentValues.put(COL2, score);

        long result = db.insert(TABLE_NAME, null, contentValues);
        // insert() method returns -1 if data inserted correctly.
        if(result == -1) {
            return false;
        }
        return true;
    }

    /**
     * Returns a cursor object which points to data in the data to be accessed.
     */
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY SCORE DESC";
        Cursor data = db.rawQuery(query, null);
        return data;

    }

    /**
     * Checks if the Score is a valid score to add. Meaning, it is better than the all
     * the socres on the list (which is 5).
     */
    public boolean isNewScoreARecord(int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        // queury the table, sort in descending order by score and get the highest
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY SCORE DESC";
        Cursor data = db.rawQuery(query, null);

        // IF there is less than 5 records, always add score
        if(data.getCount() < MAX_NUMBER_SCORES) {
            return true;
        }

        // Checks of user score in parameter is greater than any of the five scores in
        // the database.
        int counter = 0;
        while(data.moveToNext() && counter < MAX_NUMBER_SCORES) {
            String highScoreString = data.getString(2);
            int tableScore = Integer.parseInt(highScoreString);
            if(score > tableScore) {
                return true;
            }
            counter++;

        }
        // the score in parameter is a highest score.
        return false;
    }


}
