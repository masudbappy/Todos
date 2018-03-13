package masudbappy.com.todos.data;

import android.provider.BaseColumns;

/**
 * Created by bappy on 3/14/2018.
 */

public class TodosContract {
    public static final class TodosEntry implements BaseColumns{
        public static final String TABLE_NAME = "todos";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COUMN_TEXT = "text";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_EXPIRED = "expired";
        public static final String COLUMN_DONE = "done";
        public static final String COLUMN_CATEGORY = "category";

    }

    public static final class CategoryEntry implements BaseColumns{
        public static final String TABLE_NAME = "categories";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DESCRIPTION = "descriptions";
    }
}
