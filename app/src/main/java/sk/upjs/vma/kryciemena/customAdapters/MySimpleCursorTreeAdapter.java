package sk.upjs.vma.kryciemena.customAdapters;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.content.Loader;
import android.util.Log;
import android.widget.SimpleCursorTreeAdapter;

import sk.upjs.vma.kryciemena.activities.WordDatabaseActivity;

public class MySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {

    private final String LOG_TAG = getClass().getSimpleName().toString();
    private WordDatabaseActivity wordDatabaseActivity;
    protected final HashMap<Integer, Integer> mGroupMap;
    protected final HashMap<Integer, Integer> posToIdGroupMap;

    // Please Note: Here cursor is not provided to avoid querying on main
    // thread.
    public MySimpleCursorTreeAdapter(Context context, int groupLayout,
                                     int childLayout, String[] groupFrom, int[] groupTo,
                                     String[] childrenFrom, int[] childrenTo) {

        super(context, null, groupLayout, groupFrom, groupTo, childLayout,
                childrenFrom, childrenTo);
        wordDatabaseActivity = (WordDatabaseActivity) context;
        mGroupMap = new HashMap<Integer, Integer>();
        posToIdGroupMap = new HashMap<>();
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        // Logic to get the child cursor on the basis of selected group.
        int groupPos = groupCursor.getPosition();
        int groupId = groupCursor.getInt(groupCursor
                .getColumnIndex(ContactsContract.Contacts._ID));

        Log.d(LOG_TAG, "getChildrenCursor() for groupPos " + groupPos);
        Log.d(LOG_TAG, "getChildrenCursor() for groupId " + groupId);

        mGroupMap.put(groupId, groupPos);
        posToIdGroupMap.put(groupPos, groupId);
        Loader<Cursor> loader = wordDatabaseActivity.getLoaderManager().getLoader(groupId);
        if (loader != null && !loader.isReset()) {
            wordDatabaseActivity.getLoaderManager()
                    .restartLoader(groupId, null, wordDatabaseActivity);
        } else {
            wordDatabaseActivity.getLoaderManager().initLoader(groupId, null, wordDatabaseActivity);
        }

        return null;
    }

    public HashMap<Integer, Integer> getGroupMap() {
        return mGroupMap;
    }

    public HashMap<Integer, Integer> getPosToIdGroupMap() {
        return posToIdGroupMap;
    }

}