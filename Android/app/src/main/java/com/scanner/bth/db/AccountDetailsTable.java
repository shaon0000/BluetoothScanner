package com.scanner.bth.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by shaon on 6/10/2015.
 */
public class AccountDetailsTable extends BaseTable<AccountDetails> {
    public static final Column IS_ADMIN = new Column("is_admin", Column.BOOLEAN);

    private static final ArrayList<Column> mColumns = new ArrayList<>();
    static {
        mColumns.add(IS_ADMIN);
    }

    private static AccountDetailsTable mSingleton;

    @Override
    public String getName() {
        return "AccountDetailsTable";
    }

    @Override
    protected ArrayList<Column> initializeColumns() {
        return mColumns;
    }

    @Override
    public ContentValues serialize(AccountDetails obj) {
        ContentValues values = new ContentValues();
        values.put(IS_ADMIN.getKey(), obj.getIsAdmin());
        return values;
    }

    @Override
    public AccountDetails deserialize(Cursor cursor) {
        boolean isAdmin = (boolean) extract(IS_ADMIN, cursor);
        return new AccountDetails(isAdmin);
    }

    public static AccountDetailsTable getSingleton() {
        if (mSingleton == null) {
            mSingleton = new AccountDetailsTable();
        }

        return mSingleton;
    }
}
