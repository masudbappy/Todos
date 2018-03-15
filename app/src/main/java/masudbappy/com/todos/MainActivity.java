package masudbappy.com.todos;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import masudbappy.com.todos.data.DatabaseHelper;
import masudbappy.com.todos.data.TodosContract;
import masudbappy.com.todos.data.TodosContract.TodosEntry;
import masudbappy.com.todos.data.TodosQueryHandler;
import masudbappy.com.todos.model.Category;
import masudbappy.com.todos.model.CategoryList;
import masudbappy.com.todos.model.Todo;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int ALL_RECORDS = -1;
    static final int ALL_CATEGORIES = -1;

    private static final int URL_LOADER = 0;
    Cursor cursor;
    TodosCursorAdapter adapter;
    Spinner spinner;
    CategoryList list = new CategoryList();
    CategoryListAdapter categoryAdapter;
    private void updateTodo() {
        int id = 2;
        String[] args = {String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(TodosEntry.COLUMN_TEXT, "Call Mr Clark Kent");
        int numRows = getContentResolver().update(TodosEntry.CONTENT_URI, values,
                TodosEntry._ID + "=?", args);
        Log.d("Update Rows ", String.valueOf(numRows));
    }

    private void deleteTodo(int id) {
        String[] args = {String.valueOf(id)};
        if (id == ALL_RECORDS) {
            args = null;
        }
        TodosQueryHandler handler = new TodosQueryHandler(
                this.getContentResolver());
        handler.startDelete(1, null,
                TodosEntry.CONTENT_URI, TodosEntry._ID + " =?", args);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner=(Spinner) findViewById(R.id.spinCategories);
        getLoaderManager().initLoader(URL_LOADER, null, this);
        setCategories();
        final ListView lv = (ListView) findViewById(R.id.lvTodos);
        adapter = new TodosCursorAdapter(this, cursor, false);
        lv.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//adds the click event to the listView, reading the content
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //move the cursor to the selected row
                cursor = (Cursor) adapterView.getItemAtPosition(pos);
                //get the object data from the cursor
                int todoId = cursor.getInt(
                        cursor.getColumnIndex(TodosEntry._ID));
                String todoText = cursor.getString(
                        cursor.getColumnIndex(TodosEntry.COLUMN_TEXT));
                String todoExpireDate = cursor.getString(
                        cursor.getColumnIndex(TodosEntry.COLUMN_EXPIRED));
                int todoDone = cursor.getInt(
                        cursor.getColumnIndex(TodosEntry.COLUMN_DONE));
                String todoCreated = cursor.getString(
                        cursor.getColumnIndex(TodosEntry.COLUMN_CREATED));
                String todoCategory = cursor.getString(cursor.getColumnIndex(TodosEntry.COLUMN_CATEGORY));
                //create the object that will be passed to the todoActivity
                boolean boolDone = (todoDone == 1);
                Todo todo = new Todo (todoId,todoText, todoCreated, todoExpireDate, boolDone,
                        todoCategory);
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                //pass the ID to the todoActivity
                intent.putExtra("todo", todo);
                intent.putExtra("categories", list);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Todo todo = new Todo (0,"", "", "", false, "0");
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                //pass the ID to the todoActivity
                intent.putExtra("todo", todo);
                intent.putExtra("categories", list);
                startActivity(intent);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position >= 0) {
                    getLoaderManager().restartLoader(URL_LOADER, null, MainActivity.this);
                    //getLoaderManager().initLoader(URL_LOADER, null, TodoListActivity.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void createTestTodos() {
        for (int i = 1; i<=20; i++) {
            ContentValues values = new ContentValues();
            values.put(TodosEntry.COLUMN_TEXT, "Todo Item #" + i);
            values.put(TodosEntry.COLUMN_CATEGORY, 1);
            values.put(TodosEntry.COLUMN_CREATED, "2016-01-02");
            values.put(TodosEntry.COLUMN_DONE, 0);
            TodosQueryHandler handler = new TodosQueryHandler(
                    this.getContentResolver());
            handler.startInsert(1, null, TodosEntry.CONTENT_URI,
                    values );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.action_settings:
                //get the categories cursor for the
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
                break;
            case R.id.action_delete_all_todos:
                deleteTodo(ALL_RECORDS);
                break;
            case R.id.action_create_test_data:
                createTestTodos();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setCategories() {
        //get cursor with all the activities
        final TodosQueryHandler categoriesHandler = new TodosQueryHandler(
                this.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie,
                                           Cursor cursor) {
                try {
                    if ((cursor != null)) {
                        int i = 0;
                        list.ItemList.add(i, new Category(ALL_CATEGORIES, "All Categories"));
                        i++;
                        while (cursor.moveToNext()) {
                            list.ItemList.add(i, new Category(
                                    cursor.getInt(0),
                                    cursor.getString(1)
                            ));
                            i++;
                        }
                    }
                } finally {
                    //cm = null;
                }
            }
        };
        categoriesHandler.startQuery(1, null, TodosContract.CategoryEntry.CONTENT_URI, null, null, null,
                TodosContract.CategoryEntry.COLUMN_DESCRIPTION);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {TodosEntry.COLUMN_TEXT,
                TodosEntry.TABLE_NAME + "." + TodosEntry._ID,
                TodosEntry.COLUMN_CREATED,
                TodosEntry.COLUMN_EXPIRED,
                TodosEntry.COLUMN_DONE,
                TodosEntry.COLUMN_CATEGORY,
                TodosContract.CategoryEntry.TABLE_NAME + "." +
                        TodosContract.CategoryEntry.COLUMN_DESCRIPTION};
        String selection;
        String[] arguments = new String[1];

        if (spinner.getSelectedItemId()< 0) {
            selection = null;
            arguments = null;
        }
        else {
            selection = TodosEntry.COLUMN_CATEGORY + "=?";
            arguments[0] = String.valueOf(spinner.getSelectedItemId());
        }
        return new CursorLoader(
                this,
                TodosEntry.CONTENT_URI,
                projection,
                selection, arguments, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
        if (categoryAdapter == null){
            categoryAdapter = new CategoryListAdapter(list.ItemList);
            spinner.setAdapter(categoryAdapter);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}