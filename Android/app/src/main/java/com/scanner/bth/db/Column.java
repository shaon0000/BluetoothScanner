/**
 * 
 */
package com.scanner.bth.db;

import android.database.Cursor;

import java.util.UUID;

/**
 * Class defines a database column, consisting of column name,
 * and column type.
 * 
 * @author Shaon
 *
 */
public class Column {

    public boolean isUnique() {
        return isUnique;
    }

    public boolean isNotNullable() {
        return isNotNullable;
    }

    // Describes the various types of columns we can have.
	// If we want to add a new type of column, we add it here.
    public static abstract class ColumnType<T> {
        String dbType;
        ColumnType(String dbType) {
            this.dbType = dbType;
        }
        public String toString() {
            return this.dbType;
        }
        public abstract T extract(Cursor cursor, int pos);
    }

    private static class _PrimaryIntKey extends ColumnType<Integer> {

        _PrimaryIntKey() {
            super("INTEGER PRIMARY KEY");
        }

        @Override
        public Integer extract(Cursor cursor, int pos) {
            return cursor.getInt(pos);
        }

    }


    private static class _IntKey extends ColumnType<Integer> {

        _IntKey() {
            super("INTEGER");
        }

        @Override
        public Integer extract(Cursor cursor, int pos) {
            return cursor.getInt(pos);
        }

    }

    private static class _LongKey extends ColumnType<Long> {

        _LongKey() {
            super("LONG");
        }

        @Override
        public Long extract(Cursor cursor, int pos) {
            return cursor.getLong(pos);
        }
    }

    private static class _StringKey extends ColumnType<String> {
        _StringKey() {
            super("TEXT");
        }

        @Override
        public String extract(Cursor cursor, int pos) {
            return cursor.getString(pos);
        }
    }

    private static class _DoubleKey extends ColumnType<Double> {

        _DoubleKey() {
            super("TEXT");
        }

        @Override
        public Double extract(Cursor cursor, int pos) {
            return cursor.getDouble(pos);
        }
    }

    private static class _BooleanKey extends ColumnType<Boolean> {
        _BooleanKey() {super("INTEGER"); }

        @Override
        public Boolean extract(Cursor cursor, int pos) {
            Integer val = cursor.getInt(pos);
            return val > 0 ? true : false;

        }
    }

    private static class _UuidKey extends ColumnType<UUID> {
        _UuidKey() {super("TEXT");}

        @Override
        public UUID extract (Cursor cursor, int pos) {
            String val = cursor.getString(pos);
            return java.util.UUID.fromString(val);
        }
    }

    public static final _PrimaryIntKey PRIMARY_INT_KEY = new _PrimaryIntKey();
    public static final _IntKey INTEGER = new _IntKey();
    public static final _StringKey STRING = new _StringKey();
    public static final _DoubleKey DOUBLE = new _DoubleKey();
    public static final _LongKey LONG = new _LongKey();
    public static final _BooleanKey BOOLEAN = new _BooleanKey();
    public static final _UuidKey UUID = new _UuidKey();

	private String key;
	private ColumnType type;
	private boolean isUnique;
    private boolean isNotNullable;

	public Column(String key, ColumnType type) {
		this.key = key;
		this.type = type;
	}

    public Column setUnique() {
        isUnique = true;
        return this;
    }

    public Column setNotNull() {
        isNotNullable = true;
        return this;
    }

	
	public String toString() {
		return key + " " + type;
	}
	
	public ColumnType getType() {
		return type;
	}
	
	public String getKey() {
		return key;
	}

}
