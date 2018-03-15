package masudbappy.com.todos.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

/**
 * Created by bappy on 3/15/2018.
 */

public class TodosQueryHandler extends AsyncQueryHandler {
    public TodosQueryHandler(ContentResolver cr) {
        super(cr);
    }
}
