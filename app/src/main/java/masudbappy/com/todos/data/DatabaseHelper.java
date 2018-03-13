package masudbappy.com.todos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import masudbappy.com.todos.data.TodosContract.TodosEntry;
import masudbappy.com.todos.data.TodosContract.CategoryEntry;

/**
 * Created by bappy on 3/14/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todosapp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CATEGORIES_CREATE =
            "CREATE TABLE" + CategoryEntry.TABLE_NAME + " (" +
                    CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                    CategoryEntry.COLUMN_DESCRIPTION + " TEXT " + ")";
    private static final String TABLE_TODOS_CREATE = "CREATE TABLE" +
            TodosEntry.TABLE_NAME + " (" +
            TodosEntry._ID + " INTEGER PRIMARY KEY, " +
            TodosEntry.COLUMN_TEXT + " TEXT, " +
            TodosEntry.COLUMN_CREATED + " TEXT default CURRENT_TIMESTAMP, "+
            TodosEntry.COLUMN_EXPIRED + " INTEGER, " +
            TodosEntry.COLUMN_DONE + " INTEGER,  "+
            TodosEntry.COLUMN_CATEGORY + " INTEGER, " +
            " FOREIGN KEY(" + TodosEntry.COLUMN_CATEGORY + ")REFERENCES"+
            CategoryEntry.TABLE_NAME + "(" + CategoryEntry._ID + ") " + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_TODOS_CREATE);
        db.execSQL(TABLE_CATEGORIES_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" +TodosEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS"+CategoryEntry.TABLE_NAME);
    }
}
