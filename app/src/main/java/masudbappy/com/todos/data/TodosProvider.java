package masudbappy.com.todos.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static masudbappy.com.todos.data.TodosContract.CONTENT_AUTHORITY;
import static masudbappy.com.todos.data.TodosContract.PATH_CATEGORIES;
import static masudbappy.com.todos.data.TodosContract.PATH_TODOS;

/**
 * Created by bappy on 3/14/2018.
 */

public class TodosProvider extends ContentProvider {
    //constants for the operation
    private static final int TODOS = 1;
    private static final int TODOS_ID = 2;
    private static final int CATEGORIES = 3;
    private static final int CATEGORIES_ID = 4;

    //urimather
    private static final UriMatcher uriMatcher = new
            UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY,PATH_TODOS, TODOS);
        uriMatcher.addURI(CONTENT_AUTHORITY,PATH_TODOS
                + "/#", TODOS_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_CATEGORIES
        , CATEGORIES);
        uriMatcher.addURI(CONTENT_AUTHORITY,PATH_CATEGORIES
         + "/#", CATEGORIES_ID);
    }

    private DatabaseHelper helper;
    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] prjection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        String inTables = TodosContract.TodosEntry.TABLE_NAME
                + " inner join "
                + TodosContract.CategoryEntry.TABLE_NAME
                + " on " + TodosContract.TodosEntry.COLUMN_CATEGORY + " = "
                + TodosContract.CategoryEntry.TABLE_NAME + "."
                + TodosContract.CategoryEntry._ID;
        SQLiteQueryBuilder builder;
        switch (match){
            case TODOS:
                builder = new SQLiteQueryBuilder();
                builder.setTables(inTables);
                cursor = builder.query(db,prjection,selection,selectionArgs,
                        null, null, orderBy);
                break;
            case TODOS_ID:
                builder = new SQLiteQueryBuilder();
                builder.setTables(inTables);
                selection = TodosContract.TodosEntry._ID + "=?";
                selectionArgs = new String[]  {String.valueOf(ContentUris.parseId(uri))};
                cursor = builder.query(db,prjection,selection,selectionArgs,
                        null, null, orderBy);
                break;
            case CATEGORIES:
                cursor = db.query(TodosContract.CategoryEntry.TABLE_NAME
                ,prjection,selection,selectionArgs,null, null, orderBy);
                break;
            case CATEGORIES_ID:
                selection = TodosContract.CategoryEntry._ID + "=?";
                selectionArgs = new String[]  {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TodosContract.CategoryEntry.TABLE_NAME,
                        prjection,null,null,null,null,orderBy);
                break;
                default:
                    throw new IllegalArgumentException("Unknown URI");
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
