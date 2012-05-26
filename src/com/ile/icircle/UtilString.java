package com.ile.icircle;

import com.ile.icircle.CircleContract.Activity;

import android.text.TextUtils;

public class UtilString {
	public static final String PANEL_CONTENT_IMAGE_KEY = "ItemImage";
	public static final String PANEL_CONTENT_TEXT_KEY = "ItemText";
	public static final String PANEL_CONTENT_COUNT_KEY = "ItemCount";
	public static final String ACTTITLE = "ActTitle";
	public static final String ACTID = "ActId";
	public static final String ACVALUES = "ActValues";
	public static final String DETAILEXTEND = "DetailExtend";
	public static final String DETAILEXTENDTITLE = "DetailExtendTitle";
	public static final String ATTEND = "ATTEND";
	public static final String INTEREST = "INTEREST";
	public static final String LOCATION_KEY = "location_key";
	public static final String LOCATION_SHARE_ID = "mLocation";
	public static final String CLASSIFYID = "ClassifyId";
	public static final String CLASSIFYNAME = "ClassifyName";
	public static final String LOCATIONID = "LocationId";
	public static final String LOCATIONNAME = "LocationName";
	public static final String USERID = "UserId";
	public static final int MSG_MY_LOCATION = 1;
	public static final int MSG_LOCATION_FAIL = 2;
	public static final String SCHOOLNAME = "SchoolName";
	public static final String MSG_SHAREDPREFERENCES = "msg_SharedPreferences";
	public static final String[] hotActProjection = {
		Activity._ID,
		Activity.CLASSIFY_TITLE,
		Activity.CLASSIFY_INTRODUCE,
		Activity.LOCATION,
		Activity.START_TIME,
		Activity.END_TIME,
		Activity.STATE,
		Activity.HOTTAG,
		Activity.INTEREST_PEOPLE,
		Activity.ATTEND_PEOPLE,
		Activity.POSTERURL
	};
	public static final int hotActIdIndex = 0;
	public static final int hotActClassidyTitleIndex = 1;
	public static final int hotActClassifyIntroduceIndex = 2;
	public static final int hotActLocationIndex = 3;
	public static final int hotActStartTimeIndex = 4;
	public static final int hotActEndTimeIndex = 5;
	public static final int hotActStateIndex = 6;
	public static final int hotActHotTagIndex = 7;
	public static final int hotInterestPeopleIndex = 8;
	public static final int hotAttendPeopleIndex = 9;
	public static final int hotActPosterURLIndex = 10;

    /**
     * Appends one set of selection args to another. This is useful when adding a selection
     * argument to a user provided set.
     */
    public static String[] appendSelectionArgs(String[] originalValues, String[] newValues) {
        if (originalValues == null || originalValues.length == 0) {
            return newValues;
        }
        String[] result = new String[originalValues.length + newValues.length ];
        System.arraycopy(originalValues, 0, result, 0, originalValues.length);
        System.arraycopy(newValues, 0, result, originalValues.length, newValues.length);
        return result;
    }

    /**
     * Concatenates two SQL WHERE clauses, handling empty or null values.
     */
    public static String concatenateWhere(String a, String b) {
        if (TextUtils.isEmpty(a)) {
            return b;
        }
        if (TextUtils.isEmpty(b)) {
            return a;
        }

        return "(" + a + ") AND (" + b + ")";
    }
}
