package com.ile.icircle;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ile.icircle.CircleContract.*;

public class CircleDBHeaper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "iCircle.db";

	public static final int DATABASE_VERSION = 1;

	private static CircleDBHeaper sSingleton = null;

	public CircleDBHeaper(Context context) {
		this(context, DATABASE_NAME);
	}

	private CircleDBHeaper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
	}

	public static synchronized CircleDBHeaper getInstance(Context context) {
		if (sSingleton == null) {
			sSingleton = new CircleDBHeaper(context);
		}
		return sSingleton;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + School.TABLE_NAME + " (" +
				School._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				School.NAME +" TEXT NOT NULL" +
				");");
		
		db.execSQL("CREATE TABLE " + Activity.TABLE_NAME + " (" +
				Activity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				Activity.CLASSIFY_TITLE +" TEXT NOT NULL," +
				Activity.CLASSIFY_INTRODUCE +" TEXT NOT NULL," +
				Activity.START_TIME +" TEXT NOT NULL," +
				Activity.END_TIME +" TEXT NOT NULL," +
				Activity.STATE +" TEXT NOT NULL," +
				Activity.INTEREST_PEOPLE +" TEXT NOT NULL," +
				Activity.ATTEND_PEOPLE +" TEXT NOT NULL," +
				Activity.LOCATION +" TEXT NOT NULL," +
				Activity.ACT_INTRODUCE +" TEXT NOT NULL," +
				Activity.HOTTAG +" INTEGER NOT NULL DEFAULT 0," +
				Activity.PUBLISH_TIME +" DATATIME," +
				Activity.TAG_ID + " INTEGER NOT NULL," +
				Activity.POSTER +" INTEGER NOT NULL DEFAULT 0" +
				");");
		
		db.execSQL("CREATE TABLE " + ActPeople.TABLE_NAME + " (" +
				ActPeople._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				ActPeople.PEOPLE_ID + " INTEGER," +
				ActPeople.INTREST_ACT_TAGID +" INTEGER," +
				ActPeople.ATTEND_ACT_TAGID +" INTEGER" +
				");");
		
		db.execSQL("CREATE TABLE " + People.TABLE_NAME + " (" +
				People._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				People.PEOPLE_ID + " INTEGER NOT NULL," +
				People.NAME +" TEXT NOT NULL," +
				People.PROTRAIT +" INTEGER NOT NULL DEFAULT 0" +
				");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + School.TABLE_NAME + ";");
		db.execSQL("DROP TABLE IF EXISTS " + Activity.TABLE_NAME + ";");
		db.execSQL("DROP TABLE IF EXISTS " + ActPeople.TABLE_NAME + ";");
		db.execSQL("DROP TABLE IF EXISTS " + People.TABLE_NAME + ";");
		onCreate(db);
	}

}
