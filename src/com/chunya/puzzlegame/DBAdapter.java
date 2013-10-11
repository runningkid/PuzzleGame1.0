package com.chunya.puzzlegame;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_PATH = "path";

	private static final String DB_NAME = "db_img_path";
	private static final String DB_TABLE = "img_path";
	private static final int DB_VER = 1;

	private static final String DB_CREATE = "CREATE TABLE img_path (_id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT NOT NULL);";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context c) {
			super(c, DB_NAME, null, DB_VER);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
			Field[] list = R.drawable.class.getFields();
			int count = 0, index = 0;
			for (int i = 0; i < list.length; i++)
				if (list[i].getName().startsWith("img_"))
					count++;
			String inner_img_path[] = new String[count];
			for (int i = 0; i < list.length; i++)
				if (list[i].getName().startsWith("img_")) {
					try {
						inner_img_path[index++] = "img_" + list[i].getInt(null);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			for (int i = 0; i < count; i++) {
				try {
					String str = "INSERT INTO img_path (_id, path) VALUES(" + i
							+ ",'" + inner_img_path[i] + "');";
					db.execSQL(str);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
		}
	}

	private final Context context;
	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public DBAdapter(Context c) {
		this.context = c;
	}

	public DBAdapter open() throws SQLException {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
		return this;
	}

	public void close() {
		helper.close();
	}

	public boolean queryPath(String path) {
		Cursor cursor;
		try {
			cursor = db.query(DB_TABLE, new String[] { KEY_PATH }, KEY_PATH
					+ "=?", new String[] { path }, null, null, null);
			if (cursor != null && cursor.getCount() == 1)
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public long insertPath(String path) {
		ContentValues vals = new ContentValues();
		vals.put(KEY_PATH, path);
		return db.insert(DB_TABLE, null, vals);
	}

	public void deletePath(String path) {
		try {
			db.delete(DB_TABLE, KEY_PATH + "=?", new String[] { path });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getPaths() {
		ArrayList<String> dataArray = new ArrayList<String>();

		Cursor cursor;

		try {
			cursor = db.query(DB_TABLE, new String[] { KEY_PATH }, null, null,
					null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					dataArray.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dataArray;
	}
}
