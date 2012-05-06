package com.ile.icircle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class mSqlLiteHelper {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String SCHOOLNEARBYTABLE_CREATE = "create table if not exists nearbyschool (_id integer primary key autoincrement, "
			+ "name text not null);";

	private static final String DATABASE_NAME = "icircle";
	private static final int DATABASE_VERSION = 1;
	
	private static final String SCHOOLNEARBYTABLE_TABLE = "nearbyschool";
	
	public static final int SCHOOLNEARBYTABLE = 0;
	public static final String KEY_SCHOOL_NAME = "name";
	

	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SCHOOLNEARBYTABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS nearbyschool");
			onCreate(db);
		}
	}
	
	public mSqlLiteHelper(Context ctx) {
		this.mCtx = ctx;
		this.mDbHelper = new DatabaseHelper(mCtx);
	}
	
	public boolean create(String sql) throws SQLException{
		//mDbHelper = new DatabaseHelper(mCtx,sql);
		return true;
	}
	
	public mSqlLiteHelper open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public Cursor getAllInfos(int tableIndex){
		switch(tableIndex)
		{
		case SCHOOLNEARBYTABLE:
			return mDb.query(SCHOOLNEARBYTABLE_TABLE, new String[] {"_id", KEY_SCHOOL_NAME }, 
					null, null, null, null, null);
		}
		
		return null;
	}
	
	public long insert(int tableIndex, String[] values){
		
		switch(tableIndex)
		{
		case SCHOOLNEARBYTABLE:
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SCHOOL_NAME, values[0]);
			return mDb.insert(SCHOOLNEARBYTABLE_TABLE, null, initialValues);
		}
		
		return 1;
	}
	
	public boolean delete(int tableIndex){
		return true;
	}
	
	public void deleteAll(int tableIndex){
		switch(tableIndex)
		{
		case SCHOOLNEARBYTABLE:
			try{
			mDb.execSQL("delete from "+SCHOOLNEARBYTABLE_TABLE);
			}catch (SQLException e)
			{
				Toast.makeText(mCtx, e.getMessage(), 10000).show();
			}
		}
	}
	
	public void close() {
		mDbHelper.close();
	}

}
