package com.ile.icircle;

import android.net.Uri;

public class CircleContract {
	public static final String AUTHORITY = "com.ile.icircle";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public static final class Activity {

		private Activity() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath("activity").build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/activity";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/activity";

		public static final String TABLE_NAME = "activity";
		// columns
		public static final String _ID = "_id";
		public static final String CLASSIFY_TITLE = "classifytitle";
		public static final String CLASSIFY_INTRODUCE = "classifyintroduce";
		public static final String START_TIME = "starttime";
		public static final String END_TIME = "endtime";
		public static final String LOCATION = "location";
		public static final String STATE = "state";
		public static final String HOTTAG = "hotTag";
		public static final String INTEREST_PEOPLE = "interestpeople";
		public static final String ATTEND_PEOPLE = "attendpeople";
		public static final String POSTERURL = "posterurl";
	}

	public static final class School {

		private School() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath("school").build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/school";

		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/school";

		public static final String TABLE_NAME = "nearbyschool";
		// columns

		public static final int SCHOOLNEARBYTABLE = 0; 

		public static final String _ID = "_id";
		public static final String CITY = "city";
		public static final String NAME = "name";
	}

}
