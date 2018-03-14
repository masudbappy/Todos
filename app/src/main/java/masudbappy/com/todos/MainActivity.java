package masudbappy.com.todos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import org.xml.sax.ext.DeclHandler;

import masudbappy.com.todos.data.DatabaseHelper;
import masudbappy.com.todos.data.TodosContract;

public class MainActivity extends AppCompatActivity {

    String[] itemname = {
      "Wake up 5:30",
            "Order pizza for tonight",
            "Buy Something",
            "Hang out at 5pm",
            "Watch movies"
    };
    private void readData(){
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] projection = {
                TodosContract.TodosEntry.COLUMN_TEXT,
                TodosContract.TodosEntry.COLUMN_CREATED,
                TodosContract.TodosEntry.COLUMN_EXPIRED,
                TodosContract.TodosEntry.COLUMN_DONE,
                TodosContract.TodosEntry.COLUMN_CATEGORY,
        };
        String selection = TodosContract.TodosEntry.COLUMN_CATEGORY + " = ? ";
        String[] selectionArgs = {"1"};
        Cursor c = db.query(TodosContract.TodosEntry.TABLE_NAME,
                projection,selection,selectionArgs,null,null,null);
        int i = c.getCount();
        Log.d("Record Count", String.valueOf(i));
        String roContent = "";
        while (c.moveToNext()){
            for (i=0; i<=4; i++){
                roContent += c.getString(i)+ "-";
            }
            Log.i("Row " + String.valueOf(c.getPosition()),roContent);
            roContent = "";
        }
        c.close();
    }
    private void updateTodo(){
        int id =1;
        DatabaseHelper handler = new DatabaseHelper(this);
        SQLiteDatabase db = handler.getReadableDatabase();
        String[] args = {String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(TodosContract.TodosEntry.COLUMN_TEXT,"Call Mr Bond");
        int numRows = db.update(TodosContract.TodosEntry.TABLE_NAME,values, TodosContract.TodosEntry._ID + " =?",args);
        Log.d("Update Rows",String.valueOf(numRows));
        db.close();
    }
    private void deleteTodo(){
        int id =1;
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] args = {String.valueOf(id)};
        int numRows = db.delete(TodosContract.TodosEntry.TABLE_NAME, TodosContract.TodosEntry._ID + " =?", args);
        Log.d("Delete Rows", String.valueOf(numRows));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        DatabaseHelper helper = new DatabaseHelper(this);
//        SQLiteDatabase db = helper.getReadableDatabase();
//        createTodo();
//        readData();
        updateTodo();
        deleteTodo();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ListView lv = (ListView) findViewById(R.id.lvTodos);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.todos_list_item,
                    R.id.tvNote, itemname));
        //adds the click event to the listView, reading the content
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                String content = (String) lv.getItemAtPosition(pos);
                intent.putExtra("Content",content);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createTodo(){
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String query = "INSERT INTO todos ("
                + TodosContract.TodosEntry.COLUMN_TEXT + ","
                + TodosContract.TodosEntry.COLUMN_CATEGORY + ","
                + TodosContract.TodosEntry.COLUMN_CREATED + ","
                + TodosContract.TodosEntry.COLUMN_EXPIRED + ","
                + TodosContract.TodosEntry.COLUMN_DONE + ")"
                + "VALUES (\"go to the gym\", 1, \"2018-01-03\", \"\", 0)";
        db.execSQL(query);

        ContentValues values = new ContentValues();
        values.put(TodosContract.TodosEntry.COLUMN_TEXT,"Call Mr Bean");
        values.put(TodosContract.TodosEntry.COLUMN_CATEGORY,1);
        values.put(TodosContract.TodosEntry.COLUMN_CREATED,"2018-03-04");
        values.put(TodosContract.TodosEntry.COLUMN_DONE, 0);
        long todo_id = db.insert(TodosContract.TodosEntry.TABLE_NAME,null,values);
    }
}
