package com.ile.icircle;

import android.net.Uri;

public class CircleContract {
	public static final String AUTHORITY = "com.ile.icircle";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

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
		public static final String CLASSIFY_TITLE = "classifyTitle";
		public static final String CLASSIFY_INTRODUCE = "classifyIntroduce";
		public static final String START_TIME = "startTime";
		public static final String END_TIME = "endTime";
		public static final String LOCATION = "location";
		public static final String STATE = "state";
		public static final String HOTTAG = "hotTag";
		public static final String INTEREST_PEOPLE = "interestPeople";
		public static final String ATTEND_PEOPLE = "attendPeople";
		public static final String POSTER = "poster";

		public static final String PUBLISH_TIME = "publish_time";
		public static final String TAG_ID = "tagId";
		public static final String ACT_INTRODUCE = "actIntroduce";
		
		public static final String ACT_INVITER_PERSONAL = "actInviterPersonal";
	}

	public static class ActPeople {

		private ActPeople() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath("actpeople").build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/actpeople";

		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/actpeople";

		public static final String TABLE_NAME = "actpeople";
		// columns

		public static final String _ID = "_id";
		public static final String PEOPLE_ID= "peopleId";
		public static final String INTREST_ACT_TAGID = "interestActTagId";
		public static final String INTREST_ACT_TIME = "interestActTime";
		public static final String ATTEND_ACT_TAGID = "attendActTagId";
		public static final String ATTEND_ACT_TIME = "attendActTime";
	}

	public static final class ActLive{

		private ActLive() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath("actlive").build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/actlive";

		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/actlive";

		public static final String TABLE_NAME = "actlive";
		
		public static final String _ID = "_id";
		public static final String PEOPLE_ID= "peopleId";
		public static final String ACTLIVE_ACT_ID= "actLiveActId";
		public static final String ACTLIVE_COMMENT_CONTENT= "actLiveCommentContent";
		public static final String ACTLIVE_COMMENT_TIME = "actLiveCommentTime";
		public static final String ACTLIVE_COMMENT_IMG = "actLiveCommentImg";
	}

	public static class People {

		private People() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath("people").build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/people";

		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/people";

		public static final String TABLE_NAME = "people";
		// columns

		public static final String _ID = "_id";
		public static final String PEOPLE_ID= "peopleId";
		public static final String PROTRAIT= "portrait";
		public static final String NAME = "name";
	}

	public static final class Friendship {

		private Friendship() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath("friendship").build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/friendship";

		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/friendship";

		public static final String TABLE_NAME = "friendship";
		// columns

		public static final String _ID = "_id";
		public static final String PEOPLE_ID= "peopleId";
		public static final String FRIEND_ID= "friendId";
		public static final String TIME_MAKE_FRIEND= "timeMakeFriend";
	}

	public static final class Personal extends People{

		private Personal() {}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath("personal").build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/personal";

		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/personal";

		public static final String TABLE_NAME = "personal";
	}
}
