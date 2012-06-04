package com.ile.icircle;

import com.ile.icircle.CircleContract.ActLive;
import com.ile.icircle.CircleContract.ActPeople;
import com.ile.icircle.CircleContract.Activity;
import com.ile.icircle.CircleContract.Friendship;
import com.ile.icircle.CircleContract.People;

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
		Activity.POSTER,
		Activity.TAG_ID
	};

	public static final String[] detailActProjection = {
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
		Activity.POSTER,
		Activity.TAG_ID,
		Activity.ACT_INTRODUCE,
		Activity.PUBLISH_TIME,
		Activity.ACT_INVITER_PERSONAL
	};
	
	public static final int actIdIndex = 0;
	public static final int actClassidyTitleIndex = 1;
	public static final int actClassifyIntroduceIndex = 2;
	public static final int actLocationIndex = 3;
	public static final int actStartTimeIndex = 4;
	public static final int actEndTimeIndex = 5;
	public static final int actStateIndex = 6;
	public static final int actHotTagIndex = 7;
	public static final int actInterestPeopleIndex = 8;
	public static final int actAttendPeopleIndex = 9;
	public static final int actPosterIndex = 10;
	public static final int actTagIdIndex = 11;
	public static final int actIntroduceIndex = 12;
	public static final int actPublishTimeIndex = 13;
	public static final int actInviterPersonalIndex = 14;


	public static final String[] peopleActProjection = {
		ActPeople._ID,
		ActPeople.PEOPLE_ID,
		ActPeople.INTREST_ACT_TAGID,
		ActPeople.ATTEND_ACT_TAGID,
		ActPeople.INTREST_ACT_TIME,
		ActPeople.ATTEND_ACT_TIME
	};
	public static final int actPeopleIdIndex = 0;
	public static final int actPeoplePeopleIdIndex = 1;
	public static final int actPeopleIntersetIdIndex = 2;
	public static final int actPeopleAttendIdIndex = 3;
	public static final int actcommentIntersetTimeIndex = 4;
	public static final int actcommentAttendTimeIndex = 5;
	
	public static final String[] peopleProjection = {
		People._ID,
		People.PEOPLE_ID,
		People.NAME,
		People.PROTRAIT
	};
	
	public static final int peopleIdIndex = 0;
	public static final int peoplePeopleIdIndex = 1;
	public static final int peopleNameIndex = 2;
	public static final int peopleProtraitIndex = 3;
	
	public static final String[] commentProjection = {
		ActLive._ID,
		ActLive.PEOPLE_ID,
		ActLive.ACTLIVE_ACT_ID,
		ActLive.ACTLIVE_COMMENT_CONTENT,
		ActLive.ACTLIVE_COMMENT_TIME,
		ActLive.ACTLIVE_COMMENT_IMG
	};
	public static final int commentIdIndex = 0;
	public static final int commentPeopleIdIndex = 1;
	public static final int commentActIdIndex = 2;
	public static final int commentContentIndex = 3;
	public static final int commentTimeIndex = 4;
	public static final int commentImgIndex = 5;
	
	public static final String[] friendshipProjection = {
		Friendship._ID,
		Friendship.PEOPLE_ID,
		Friendship.FRIEND_ID,
		Friendship.TIME_MAKE_FRIEND
	};
	public static final int friendshipIdIndex = 0;
	public static final int friendshipPeopleIdIndex = 1;
	public static final int friendshipFriendIdIndex = 2;
	public static final int friendshiptimeIndex = 3;
	
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
