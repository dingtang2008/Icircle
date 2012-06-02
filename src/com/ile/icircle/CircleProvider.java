package com.ile.icircle;

import java.util.HashMap;

import com.ile.icircle.CircleContract.ActPeople;
import com.ile.icircle.CircleContract.Activity;
import com.ile.icircle.CircleContract.People;
import com.ile.icircle.CircleContract.Personal;
import com.ile.icircle.CircleContract.School;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class CircleProvider extends AbstractCircleProvider {

	private CircleDBHeaper mOpenHelper;
	protected SQLiteDatabase mDb;

	private static final int ACTIVITY = 100;
	private static final int ACTIVITY_ID = 101;

	private static final int SCHOOL = 102;
	private static final int ACTPEOPLE = 103;
	private static final int PEOPLE = 104;
	private static final int PERSONAL = 105;

	protected static final String SQL_WHERE_ID = Activity._ID + "=?";

	private static final UriMatcher MATCHER;

	private static final HashMap<String, String> sActivityProjectionMap;
	private static final HashMap<String, String> sSchoolProjectionMap;
	private static final HashMap<String, String> sActPeopleProjectionMap;
	private static final HashMap<String, String> sPeopleProjectionMap;
	private static final HashMap<String, String> sPersonalProjectionMap;

	static {
		MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		String auth = CircleContract.AUTHORITY;

		MATCHER.addURI(auth, "activity", ACTIVITY);
		MATCHER.addURI(auth, "activity/#", ACTIVITY_ID);
		MATCHER.addURI(auth, "school", SCHOOL);
		MATCHER.addURI(auth, "actpeople", ACTPEOPLE);
		MATCHER.addURI(auth, "people", PEOPLE);

		sSchoolProjectionMap = new HashMap<String, String>();
		sSchoolProjectionMap.put(School._ID, School._ID);
		sSchoolProjectionMap.put(School.NAME, School.NAME);
		
		sActivityProjectionMap = new HashMap<String, String>();
		sActivityProjectionMap.put(Activity._ID, Activity._ID);
		sActivityProjectionMap.put(Activity.CLASSIFY_TITLE, Activity.CLASSIFY_TITLE);
		sActivityProjectionMap.put(Activity.CLASSIFY_INTRODUCE, Activity.CLASSIFY_INTRODUCE);
		sActivityProjectionMap.put(Activity.LOCATION, Activity.LOCATION);
		sActivityProjectionMap.put(Activity.START_TIME, Activity.START_TIME);
		sActivityProjectionMap.put(Activity.END_TIME, Activity.END_TIME);
		sActivityProjectionMap.put(Activity.STATE, Activity.STATE);
		sActivityProjectionMap.put(Activity.HOTTAG, Activity.HOTTAG);
		sActivityProjectionMap.put(Activity.INTEREST_PEOPLE, Activity.INTEREST_PEOPLE);
		sActivityProjectionMap.put(Activity.ATTEND_PEOPLE, Activity.ATTEND_PEOPLE);
		sActivityProjectionMap.put(Activity.POSTER, Activity.POSTER);
		
		sActivityProjectionMap.put(Activity.TAG_ID, Activity.TAG_ID);
		sActivityProjectionMap.put(Activity.ACT_INTRODUCE, Activity.ACT_INTRODUCE);
		sActivityProjectionMap.put(Activity.PUBLISH_TIME, Activity.PUBLISH_TIME);
	
		sActPeopleProjectionMap = new HashMap<String, String>();
		sActPeopleProjectionMap.put(ActPeople._ID, ActPeople._ID);
		sActPeopleProjectionMap.put(ActPeople.PEOPLE_ID, ActPeople.PEOPLE_ID);
		sActPeopleProjectionMap.put(ActPeople.INTREST_ACT_TAGID, ActPeople.INTREST_ACT_TAGID);
		sActPeopleProjectionMap.put(ActPeople.ATTEND_ACT_TAGID, ActPeople.ATTEND_ACT_TAGID);
		
		sPeopleProjectionMap = new HashMap<String, String>();
		sPeopleProjectionMap.put(People._ID, People._ID);
		sPeopleProjectionMap.put(People.PEOPLE_ID, People.PEOPLE_ID);
		sPeopleProjectionMap.put(People.NAME, People.NAME);
		sPeopleProjectionMap.put(People.PROTRAIT, People.PROTRAIT);
		
		sPersonalProjectionMap = sPeopleProjectionMap;
	}

	@Override
	public boolean onCreate() {
		return super.onCreate();
	}

	@Override
	protected SQLiteOpenHelper getDatabaseHelper(Context context) {
		return CircleDBHeaper.getInstance(context);
	}

	@Override
	public String getType(Uri uri) {
		int match = MATCHER.match(uri);
		switch (match) {

		case ACTIVITY_ID: {
			return Activity.CONTENT_ITEM_TYPE;
		}
		case ACTIVITY: {
			return Activity.CONTENT_TYPE;
		}

		case SCHOOL: {
			return School.CONTENT_TYPE;
		}

		case ACTPEOPLE: {
			return ActPeople.CONTENT_TYPE;
		}

		case PEOPLE: {
			return People.CONTENT_TYPE;
		}

		case PERSONAL: {
			return Personal.CONTENT_TYPE;
		}

		default: {
			throw new IllegalArgumentException("unknown uri " + uri);
		}
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = getDatabaseHelper().getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int match = MATCHER.match(uri);
		switch (match) {
		case ACTIVITY_ID: {
			selection = UtilString.concatenateWhere(selection, Activity.TABLE_NAME + "._id=?");
			selectionArgs = UtilString.appendSelectionArgs(selectionArgs, new String[] { Long.toString(ContentUris.parseId(uri)) });
			//qb.appendWhere(SQL_WHERE_ID);
		}
		case ACTIVITY: {
			qb.setTables(Activity.TABLE_NAME);
			qb.setProjectionMap(sActivityProjectionMap);
			break;
		}

		case SCHOOL: {
			qb.setTables(School.TABLE_NAME);
			qb.setProjectionMap(sSchoolProjectionMap);
			break;
		}

		case ACTPEOPLE: {
			qb.setTables(ActPeople.TABLE_NAME);
			qb.setProjectionMap(sActPeopleProjectionMap);
			break;
		}

		case PEOPLE: {
			qb.setTables(People.TABLE_NAME);
			qb.setProjectionMap(sPeopleProjectionMap);
			break;
		}

		case PERSONAL: {
			qb.setTables(Personal.TABLE_NAME);
			qb.setProjectionMap(sPersonalProjectionMap);
			break;
		}

		default: {
			throw new IllegalArgumentException("unkown uri " + uri);
		}
		}

		Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		if (cursor != null) {
			cursor.setNotificationUri(getContext().getContentResolver(), CircleContract.AUTHORITY_URI);
		}
		return cursor;
	}

	@Override
	protected Uri insertInTransaction(Uri uri, ContentValues values) {
		SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
		int match = MATCHER.match(uri);
		long id = -1;
		switch (match) {
		case ACTIVITY: {
			id = db.insert(Activity.TABLE_NAME, null, values);
			break;
		}
		
		case SCHOOL: {
			id = db.insert(School.TABLE_NAME, null, values);
			break;
		}

		case ACTPEOPLE: {
			id = db.insert(ActPeople.TABLE_NAME, null, values);
			break;
		}

		case PEOPLE: {
			id = db.insert(People.TABLE_NAME, null, values);
			break;
		}

		case PERSONAL: {
			id = db.insert(Personal.TABLE_NAME, null, values);
			break;
		}

		default: {
			throw new IllegalArgumentException("unkown uri " + uri);
		}
		}

		if (id >= 0) {
			return ContentUris.withAppendedId(uri, id);
		}
		return null;
	}

	@Override
	protected int updateInTransaction(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
		int match = MATCHER.match(uri);
		int count = 0;
		switch (match) {
		case ACTIVITY_ID: {
			selection = UtilString.concatenateWhere(selection, Activity.TABLE_NAME + "._id=?");
			selectionArgs = UtilString.appendSelectionArgs(selectionArgs,
					new String[] { Long.toString(ContentUris.parseId(uri)) });
		}
		case ACTIVITY: {
			count = db.update(Activity.TABLE_NAME, values, selection, selectionArgs);
			break;
		}
		case SCHOOL: {
			count = db.update(School.TABLE_NAME, values, selection, selectionArgs);
			break;
		}

		case ACTPEOPLE: {
			count = db.update(ActPeople.TABLE_NAME, values, selection, selectionArgs);
			break;
		}

		case PEOPLE: {
			count = db.update(People.TABLE_NAME, values, selection, selectionArgs);
			break;
		}

		case PERSONAL: {
			count = db.update(Personal.TABLE_NAME, values, selection, selectionArgs);
			break;
		}

		default: {
			throw new IllegalArgumentException("unkown uri " + uri);
		}
		}

		return count;
	}

	@Override
	protected int deleteInTransaction(Uri uri, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
		int match = MATCHER.match(uri);
		int count = 0;
		switch (match) {
		case ACTIVITY_ID: {
			selection = UtilString.concatenateWhere(selection, Activity.TABLE_NAME + "._id=?");
			selectionArgs = UtilString.appendSelectionArgs(selectionArgs,
					new String[] { Long.toString(ContentUris.parseId(uri)) });
			// fall through
		}
		case ACTIVITY: {
			count = db.delete(Activity.TABLE_NAME, selection, selectionArgs);
			break;
		}
		case SCHOOL: {
			count = db.delete(School.TABLE_NAME, selection, selectionArgs);
			break;
		}

		case ACTPEOPLE: {
			count = db.delete(ActPeople.TABLE_NAME, selection, selectionArgs);
			break;
		}

		case PEOPLE: {
			count = db.delete(People.TABLE_NAME, selection, selectionArgs);
			break;
		}

		case PERSONAL: {
			count = db.delete(Personal.TABLE_NAME, selection, selectionArgs);
			break;
		}

		default: {
			throw new IllegalArgumentException("unkown uri " + uri);
		}
		}

		return count;
	}

	@Override
	protected void notifyChange() {
		getContext().getContentResolver().notifyChange(CircleContract.AUTHORITY_URI, null, false);
	}

}
