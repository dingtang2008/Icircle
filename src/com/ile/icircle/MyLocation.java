package com.ile.icircle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.ile.icircle.CircleContract.School;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyLocation extends MapActivity {

	private static final String TAG = "ICircle_MyLocation";

	private static final int LOCATION_PERIOD_MESC = 2 * 60 * 1000;
	private static final int LOCATION_UPDATE_PERIOD_MSEC = 3 * 60 * 1000;//3 mins
	private static final float INVALID_ACCURACY = 999999.0f;

	private static final int LOCATION_STOP = -1;
	private static final int LOCATION_START = 0;
	private static final int LOCATION_SUCCESS = 1;
	private static final int LOCATION_FAILUE = 2;
	private static final int LOCATION_CLOSE = 3;

	private static final int SCHOOL_SEARCH_START = 0;
	private static final int SCHOOL_SEARCH_SUCCESS = 1;
	private static final int SCHOOL_SEARCH_FAIL = 2;

	private LocationManager mLocationManager;
	// 定义地图引擎管理类   
	private BMapManager mapManager;  
	// 定义搜索服务类   
	private MKSearch mMKSearch;  

	private long mTimeBetweenLocationEvents = LOCATION_UPDATE_PERIOD_MSEC;
	private long mTimeOfLastLocationEvent = 0;
	private boolean mAccuracyOverride = true;
	private float mLastAccuracy = INVALID_ACCURACY;
	private boolean mOverrideLocation = false;
	private int mLocationStatus = -1;
	private int mSearchingStatus = -1;

	private EditText searchBoxField;
	private RelativeLayout locationStatus;
	private ProgressBar pb;
	private TextView locationStatusText;
	private Timer mTimer;
	private TimerTask locationtask;

	//private mSqlLiteHelper mDbHelper = null;

	private ArrayAdapter<String> mFavoriteSchoolsArrayAdapter;

	private static int[] mFavoriteSchoolList = {R.string.default_school_1,R.string.default_school_2,R.string.default_school_3,
		R.string.default_school_4,R.string.default_school_5,R.string.default_school_6,R.string.default_school_7,
		R.string.default_school_1,R.string.default_school_2,R.string.default_school_3,
		R.string.default_school_4,R.string.default_school_5,R.string.default_school_6,R.string.default_school_7};

	Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
			switch (msg.what) {   
			case UtilString.MSG_LOCATION_FAIL:
				if(mLocationStatus == LOCATION_START)
				{
					stopLocation();
				}
				mLocationStatus = LOCATION_FAILUE;
				locationStatusText.setText(R.string.locating_fail);
				pb.setVisibility(View.GONE);

				break;

			default:
				break;
			}   
			super.handleMessage(msg);   
		}   
	};

	class mTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mLastAccuracy == INVALID_ACCURACY)
			{
				Message message = new Message();   
				message.what = UtilString.MSG_LOCATION_FAIL;   
				handler.sendMessage(message);
			}
		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_location);

		// initialize MapActivity   
		mapManager = new BMapManager(getApplication());  
		// init with API Key   
		mapManager.init("CF98304CADEB9EB894A7A7804462B76F568F0133", null);  
		super.initMapActivity(mapManager);

		searchBoxField = (EditText)findViewById(R.id.searchBoxEditText);
		CharSequence searchBoxhint = getString(R.string.choose_school_hint).toString() ;
		searchBoxField.setHint(searchBoxhint);

		locationStatus = (RelativeLayout)findViewById(R.id.location_status);

		locationStatusText = (TextView)findViewById(R.id.location_status_text);

		pb = (ProgressBar)findViewById(R.id.location_progressbar);

		mFavoriteSchoolsArrayAdapter = new ArrayAdapter<String>(this, R.layout.school_name);

		ListView favoriteSchoolListView = (ListView) findViewById(R.id.favorite_school_list);
		favoriteSchoolListView.setAdapter(mFavoriteSchoolsArrayAdapter);
		favoriteSchoolListView.setOnItemClickListener(mSchoolListClickHandler);

		for (int school_name_id : mFavoriteSchoolList) {
			mFavoriteSchoolsArrayAdapter.add(getString(school_name_id).toString());
		}

		locationStatusText.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v){
				if(mLocationStatus == LOCATION_FAILUE)
				{
					startLocation();
					locationStatusText.setText(R.string.locating);
					pb.setVisibility(View.VISIBLE);
				}

				if(mLocationStatus == LOCATION_CLOSE)
				{
					Intent in = new Intent("android.settings.SECURITY_SETTINGS");
					//            		in.setClassName("com.android.settings", "SecuritySettings");
					//            		in.setAction("android.settings.SECURITY_SETTINGS");
					startActivity(in);
				}
			}
		}
				);

		startLocation();
	}

	private AdapterView.OnItemClickListener mSchoolListClickHandler = 
			new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView parent, View v,
				int position, long id) {
			//locationStatusText.setText(position);
			Intent intent = new Intent(MyLocation.this,CirclesTabActivity.class);
			intent.putExtra(UtilString.SCHOOLNAME, ((TextView)v).getText());
			startActivity(intent);
			finish();
		}

	};

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {

			Log.d(TAG, "onLocationChanged() triggered. Accuracy = "+Float.toString(location.getAccuracy()));
			mOverrideLocation = false;

			pb.setVisibility(View.GONE);

			if (location != null)
			{
				//if a more accurate coordinate is available within a set of events, then use it (if enabled by programmer)
				if (mAccuracyOverride == true)
				{
					//whenever the expected time period is reached invalidate the last known accuracy
					// so that we don't just receive better and better accuracy and eventually risk receiving
					// only minimal locations
					if (location.getTime() - mTimeOfLastLocationEvent >= mTimeBetweenLocationEvents)
					{
						mLastAccuracy = INVALID_ACCURACY;
					}


					if (location.hasAccuracy())
					{
						final float fCurrentAccuracy = location.getAccuracy();

						//the '<' is important here to filter out equal accuracies !
						if ((fCurrentAccuracy != 0.0f) && (fCurrentAccuracy < mLastAccuracy))
						{
							mOverrideLocation = true;
							mLastAccuracy = fCurrentAccuracy;

							mLocationStatus = LOCATION_SUCCESS;
						}
					}
				}

				//ensure that we don't get a lot of events
				// or if enabled, only get more accurate events within mTimeBetweenLocationEvents
				if (  (location.getTime() - mTimeOfLastLocationEvent >= mTimeBetweenLocationEvents)
						||(mOverrideLocation == true) )
				{
					//be sure to store the time of receiving this event !
					mTimeOfLastLocationEvent = location.getTime();

					String place = GetAddrFromGeocoder(location.getLatitude(),location.getLongitude());

					Toast.makeText(getApplicationContext(),place, 300000).show();

					stopLocation();

					if(!isEmptyNearBySchool())
					{
						double[] lastLocation = new double[2];
						if(getLastLocation(lastLocation))
						{
							float[] distance=new float[1];
							Location.distanceBetween(lastLocation[0], lastLocation[1], location.getLatitude(), location.getLongitude(), distance);

							if(distance[0] < 300)
							{
								displayNearBySchool();
								return;
							}
							else
							{
								deleteAllFromDB();
							}
						}
					}

					setLastLocation(location.getLatitude(), location.getLongitude());

					getSchoolListByLocation(location.getLatitude(), location.getLongitude());
				}
			}
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status,
				Bundle extras) {
		}
	};

	private void startLocation(){

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		if(mLocationManager == null)
		{
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}

		boolean GPSProvideEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if(GPSProvideEnable == true)
		{
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					LOCATION_UPDATE_PERIOD_MSEC, 0, locationListener);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					LOCATION_UPDATE_PERIOD_MSEC, 0, locationListener);

			mLocationStatus = LOCATION_START;

			if(mTimer != null && locationtask != null){
				mTimer.cancel();
				locationtask.cancel();
			}

			mTimer = new Timer();
			locationtask = new mTimerTask();
			mTimer.schedule(locationtask, LOCATION_PERIOD_MESC);
		}
		else
		{
			locationStatusText.setText(getString(R.string.gps_provide_request).toString());
			pb.setVisibility(View.GONE);
			mLocationStatus = LOCATION_CLOSE;
		}
	}

	private void startCellIDLocation(){

	}

	private void stopLocation(){
		mLocationManager.removeUpdates(locationListener);
		mLocationManager.removeUpdates(locationListener);

		mLocationStatus = LOCATION_STOP;
	}

	//    private String GetAddr(double latitude, double longitude) {
	//    	String placename = "";
	//    	placename = GetAddrFromGeocoder(latitude,longitude);
	//    	if(placename == null){
	//    		placename = GetAddrFromNetwork(latitude,longitude);
	//    	}
	//    	
	//    	return placename;
	//    }

	private String GetAddrFromGeocoder(double latitude, double longitude){
		String placename = "";
		List places = null;
		Geocoder geocoder=new Geocoder(this);
		try {
			places = geocoder.getFromLocation(latitude, longitude, 5);
		} catch (Exception e) {  
			e.printStackTrace();
		}

		if (places != null && places.size() > 0) {
			// placename=((Address)places.get(0)).getLocality();
			//一下的信息将会具体到某条街
			//其中getAddressLine(0)表示国家，getAddressLine(1)表示精确到某个区，getAddressLine(2)表示精确到具体的街
			placename = ((Address) places.get(0)).getAddressLine(0) + ", " + System.getProperty("line.separator")
					+ ((Address) places.get(0)).getAddressLine(1) + ", "
					+ ((Address) places.get(0)).getAddressLine(2);
		}
		else{
			return null;
		}

		return placename;
	}
	//    
	//    private String GetAddrFromNetwork(double latitude, double longitude){
	//    	String addr = "";
	//
	//	  	  // 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址
	//	  	  // 密钥可以随便写一个key=abc
	//	  	  // output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析
	//	  	String url = String.format(
	//	  	    "http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",
	//	  	    latitude, longitude);
	//	  	URL myURL = null;
	//	  	URLConnection httpsConn = null;
	//	  	try {
	//	  		myURL = new URL(url);
	//	  		} catch (MalformedURLException e) {
	//	  			e.printStackTrace();
	//	  			return null;
	//	  		}
	//	  	
	//	  	try {
	//	  		httpsConn = (URLConnection) myURL.openConnection();
	//	  		if (httpsConn != null) {
	//	  			InputStreamReader insr = new InputStreamReader(
	//	  					httpsConn.getInputStream(), "UTF-8");
	//	  			BufferedReader br = new BufferedReader(insr);
	//	  			String data = null;
	//	  			if ((data = br.readLine()) != null) {
	//	  				System.out.println(data);
	//	  				String[] retList = data.split(",");
	//	  				if (retList.length > 2 && ("200".equals(retList[0]))) {
	//	  					addr = retList[2];
	//	  					addr = addr.replace("\"", "");
	//	  					} else {
	//	  						addr = "";
	//	  						}
	//	  				}
	//	  			insr.close();
	//	  			}
	//	  		} catch (IOException e) {
	//	  			e.printStackTrace();
	//	  			return null;
	//	  		}
	//	  	return addr;
	//    }
	//    

	private void getSchoolListByLocation(double latitude, double longitude){
		// 初始化MKSearch
		if(mMKSearch == null)
		{
			mMKSearch = new MKSearch();  
			mMKSearch.init(mapManager, new MySearchListener());  
		}

		try {  
			// 将用户输入的经纬度值转换成int类型   
			int mlongitude = (int) (1000000 * latitude);  
			int mlatitude = (int) (1000000 * longitude);
			GeoPoint geoPoint = new GeoPoint(mlongitude,mlatitude);
			//String[] keys = {"学校"};

			// 查询该经纬度值所对应的地址位置信息   
			mMKSearch.poiSearchNearBy("大学学校", geoPoint,500);
			//mMKSearch.poiMultiSearchNearBy(keys, geoPoint,500);
			locationStatusText.setText(getString(R.string.search_nearby_school).toString());
			mSearchingStatus = SCHOOL_SEARCH_START;
		} catch (Exception e) {
			mSearchingStatus = SCHOOL_SEARCH_FAIL;
			Toast.makeText(getApplicationContext(),"something wrong in getSchoolListByLocation",3000).show();
		}   
	}

	public class MySearchListener implements MKSearchListener {

		public void onGetAddrResult(MKAddrInfo addrInfo, int iError) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), addrInfo.strAddr, 1000).show();
		}

		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
			if (result == null) {
				return;
			}
			Toast.makeText(getApplicationContext(),"onGetPoiResult called"+result.getNumPois(),3000).show();
			StringBuffer sb = new StringBuffer();  

			ArrayList<MKPoiInfo> schoolListInfo =  result.getAllPoi();

			if (null != schoolListInfo && !schoolListInfo.isEmpty()) {  
				// 遍历所有的兴趣点信息   
				for (MKPoiInfo poiInfo : schoolListInfo) {  
					sb.append("----------------------------------------").append("/n");  
					sb.append("名称：").append(poiInfo.name).append("/n");  
					sb.append("地址：").append(poiInfo.address).append("/n");  
					sb.append("经度：").append(poiInfo.pt.getLongitudeE6() / 1000000.0f).append("/n");  
					sb.append("纬度：").append(poiInfo.pt.getLatitudeE6() / 1000000.0f).append("/n");  
					sb.append("电话：").append(poiInfo.phoneNum).append("/n");  
					sb.append("邮编：").append(poiInfo.postCode).append("/n");  
					// poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路   
					sb.append("类型：").append(poiInfo.ePoiType).append("/n");  

					storeSchoolInfo(poiInfo.name);
				}
				displayNearBySchool();
				//locationStatusText.setText(sb.toString());
			}
			else
			{
				locationStatusText.setText(getString(R.string.empty_nearby_school).toString());
			}

			mSearchingStatus = SCHOOL_SEARCH_SUCCESS;
			pb.setVisibility(View.GONE);
		}

		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}  

	}

	@Override  
	protected void onPause() {  
		if (mapManager != null) {  
			// 终止百度地图API   
			mapManager.stop();  
		}  
		super.onPause();  
	} 

	public void onResume(){
		if (mapManager != null) {  
			// 开启百度地图API   
			mapManager.start();  
		}
		super.onResume();

		if(mLocationStatus == LOCATION_CLOSE)
		{
			if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			{
				locationStatusText.setText(getString(R.string.location_start).toString());
				mLocationStatus = LOCATION_FAILUE;
			}
		}
	}

	public void onDestroy() {
		if (mapManager != null) {  
			// 程序退出前需调用此方法   
			mapManager.destroy();  
			mapManager = null;  
		}
		super.onDestroy();
		stopLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean getLastLocation(double[] result){
		if(result == null || result.length<2)
		{
			return false;
		}

		SharedPreferences sharedata = getSharedPreferences("ICircle", 0);

		result[0] = (sharedata.getInt("lastlatitude", 0))/1000000.0f;
		result[1] = (sharedata.getInt("lastlongitude", 0))/1000000.0f;

		return true;
	}

	private void setLastLocation(double latitude,double longitude){
		Editor sharedata = getSharedPreferences("ICircle", 0).edit();
		sharedata.putInt("lastlatitude", (int) (1000000 * latitude));
		sharedata.putInt("lastlongitude", (int) (1000000 * longitude));
		sharedata.commit();
		//Toast.makeText(getApplicationContext(), "success", 11111).show();
	}

	private void storeSchoolInfo(String schoolname){
		String[] values = new String[1];
		values[0] = schoolname;

		//		if(mDbHelper == null)
		//		{
		//			mDbHelper = new mSqlLiteHelper(this);
		//			mDbHelper.open();
		//		}
		//
		//		mDbHelper.insert(mSqlLiteHelper.SCHOOLNEARBYTABLE,values);
		ContentValues initialValues = new ContentValues();
		initialValues.put(School.NAME, values[0]);
		getContentResolver().insert(School.CONTENT_URI, initialValues);
	}

	private boolean isEmptyNearBySchool(){
		//		if(mDbHelper == null)
		//		{
		//			mDbHelper = new mSqlLiteHelper(this);
		//			mDbHelper.open();
		//		}
		//
		//		Cursor mNearBySchoolCursor;
		//		mNearBySchoolCursor = mDbHelper.getAllInfos(mSqlLiteHelper.SCHOOLNEARBYTABLE);
		Cursor mNearBySchoolCursor = getContentResolver().query(School.CONTENT_URI, new String[] {School._ID, School.NAME },
				null, null, null);

		if(mNearBySchoolCursor!=null && mNearBySchoolCursor.getCount() > 0)
		{
			return false;
		}

		return true;
	}

	private void deleteAllFromDB(){
		//		if(mDbHelper == null)
		//		{
		//			mDbHelper = new mSqlLiteHelper(this);
		//			mDbHelper.open();
		//		}
		//		mDbHelper.deleteAll(mSqlLiteHelper.SCHOOLNEARBYTABLE);

		getContentResolver().delete(School.CONTENT_URI, null, null);
	}

	private void displayNearBySchool(){
		//		Cursor mNearBySchoolCursor;
		//		mNearBySchoolCursor = mDbHelper.getAllInfos(mSqlLiteHelper.SCHOOLNEARBYTABLE);

		Cursor mNearBySchoolCursor = getContentResolver().query(School.CONTENT_URI, new String[] {School._ID, School.NAME }, 
				null, null, null);

		if(mNearBySchoolCursor!=null && mNearBySchoolCursor.getCount() > 0)
		{
			startManagingCursor(mNearBySchoolCursor);

			String[] from = new String[] { mSqlLiteHelper.KEY_SCHOOL_NAME};
			int[] to = new int[] { R.id.school_name };

			try
			{

				SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
						R.layout.school_name, mNearBySchoolCursor, from, to);

				ListView nearBySchoolListView = (ListView) findViewById(R.id.school_around_list);
				nearBySchoolListView.setAdapter(notes);

				locationStatus.setVisibility(View.GONE);
				nearBySchoolListView.setVisibility(View.VISIBLE);
			}
			catch (Exception e)
			{
				Toast.makeText(getApplicationContext(), e.getMessage(), 1000).show();
			}
		}
		else
		{
			locationStatusText.setText(getString(R.string.empty_nearby_school).toString());
		}

	}
}
