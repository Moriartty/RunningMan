package com.moriarty.user.mapdemotest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

public class MainActivity extends Activity {
	private MapView mMapView;
	private Marker mMarker;
	private AMap mAMap;
	private UiSettings mUiSettings;
	public static final String TAG="Moriarty";
	private Marker mBgMarker;

	private List<LatLongData> mList;
	private String[] lng = {"114.363547,23.038575","114.363225,23.038141","114.362538,23.037884",
			"114.361036,23.03743","114.359534,23.036877","114.358633,23.0366","114.358633,23.0366",
			"114.356509,23.03581","114.354942,23.035277","114.353977,23.034961","114.354148,23.034231",
			"114.354299,23.03354","114.354384,23.033125","114.354513,23.032513","114.354642,23.031683",
			"114.354813,23.030834","114.354524,23.030439","114.35432,23.030034"};
	/*private double[] speedList = { 5.88, 6.9, 9.5, 10.5, 8.9, 5.88, 6.9, 9.5,
			10.5, 8.9, 5.88, 6.9, 9.5, 10.5, 8.9, 5.88, 6.9, 9.5, 10.5, 8.9,
			5.88, 6.9, 9.5, 10.5, 8.9, };// 每点速度*/
	private double[] speedList = {4.98,4.98,4.98,4.98,4.98,4.98,4.98,4.98,4.98,4.98,
			4.98,4.98,4.98,4.98,4.98,4.98,4.98,4.98};

	private int[] colorList = { 0xFFFBE01C, 0xFFE1E618, 0xFF7DFF00, 0xffDE2C00 };// 颜色值
	public HashMap<Integer, Integer> agrSpeerColorHashMap;
	FloatingActionButton fb;
	MainActivityPresenter presenter;
	int flag=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_main);
		setMap(savedInstanceState);
		presenter=new MainActivityPresenter(this,this);
		presenter.inspectLocationPermission();
		fb=(FloatingActionButton)findViewById(R.id.listen_state_fb);
		fb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag=1-flag;
				switch (flag){
					case 0:fb.setImageDrawable(getResources().getDrawable(R.drawable.start));break;
					case 1:fb.setImageDrawable(getResources().getDrawable(R.drawable.stop));break;
				}
				presenter.changeListenState();
			}
		});
		getAgrSpeerColorHashMap();
		initData();
		drawPath();
		setMarker();
//		setMoveCamera();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

	private void drawPath() {
		// TODO Auto-generated method stub
		
		List<LatLongData> mListItem = getSpeerDateList(mList);
		List<Integer> colorList = new ArrayList<Integer>();
		
		PolylineOptions po = new PolylineOptions();
		for (int i = 0; i < mListItem.size(); i++) {
			LatLng point = new LatLng(mListItem.get(i).getLattitude(), mListItem.get(i).getLongitude());
			po.add(point);
			colorList.add(colorList.size(), agrSpeerColorHashMap.get(Integer.parseInt(mListItem.get(i).getSpeer())));
		}
		
		po.width(12);
		po.useGradient(true);
		po.colorValues(colorList);
		po.zIndex(10);
		mAMap.addPolyline(po);
	}

	private void setMarker() {
		// TODO Auto-generated method stub
		LatLng startPoint = new LatLng(mList.get(0).getLattitude(),
				mList.get(0).getLongitude());
		LatLng endPoint = new LatLng(mList.get(mList.size() - 1).getLattitude(),
				mList.get(mList.size() - 1).getLongitude());
		addMarker(startPoint, R.drawable.ic_marker_start);
		addMarker(endPoint, R.drawable.ic_marker_end);
		addBgMarker(endPoint,R.drawable.ic_marker_bg);
		
		mAMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(new LatLng(mList.get(mList.size() - 1).getLattitude(),
						mList.get(mList.size() - 1).getLongitude()), 15,
						0, 0)));
	}

	/**
	 * 设置地图参数
	 * 
	 * @param savedInstanceState
	 */
	private void setMap(Bundle savedInstanceState) {

		mMapView = (MapView) findViewById(R.id.map1);

		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mUiSettings = mAMap.getUiSettings();
		}
		mMapView.onCreate(savedInstanceState);
		mUiSettings.setMyLocationButtonEnabled(true);
	}

	/**
	 * 设置初始数据
	 */
	private void initData() {
		mList = new ArrayList<LatLongData>();
		for(int i = 0; i < lng.length; i++){
			mList.add(mList.size(), addLatLongData(lng[i], speedList[i]));
		}

	}

	private LatLongData addLatLongData(String lng, double d) {
		LatLongData mLatLongData = new LatLongData();
		String[] lat = lng.split("\\,");
		mLatLongData.setSpeer("" + d);
		mLatLongData.setLongitude(Double.parseDouble(lat[0]));
		mLatLongData.setLattitude(Double.parseDouble(lat[1]));
		
		return mLatLongData;
	}
	
	/**
	 * 添加起点终点Marker
	 * @param ll
	 * @param icon
	 */
	public final void addMarker(LatLng ll, int icon) {
		final MarkerOptions mo = new MarkerOptions();
		mo.position(ll);
		mo.perspective(true);
		mo.draggable(true);
		mo.zIndex(5);
		mo.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
				.decodeResource(this.getResources(), icon)));
		mAMap.addMarker(mo);
	}
	
	/**
	 * 
	 * Description: 添加障碍物
	 * 
	 */
	public final void addBgMarker(LatLng endPoint,int icon) {
		mAMap.addGroundOverlay(new GroundOverlayOptions()
				.position(endPoint, 2*1000*1000, 2*1000*1000)
				.image(BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(this.getResources(), icon))));
	}

	@SuppressLint("UseSparseArrays") 
	public void getAgrSpeerColorHashMap() {
		agrSpeerColorHashMap = new HashMap<Integer, Integer>();
		agrSpeerColorHashMap.put(5, colorList[0]);
		agrSpeerColorHashMap.put(6, colorList[1]);
		agrSpeerColorHashMap.put(8, colorList[2]);
		agrSpeerColorHashMap.put(9, colorList[3]);
	}
	
	
	/**
	 * 根据速度区间，把轨迹点归类到不同均值列表中
	 * @param list
	 * @return
	 */
	public static List<LatLongData> getSpeerDateList(List<LatLongData> list){
		if (list == null || list.size() == 0) {
			return null;
		}
	
		for (LatLongData  item : list) {
			if (Float.parseFloat(item.getSpeer()) < 6) {
				item.setSpeer(String.valueOf(5));
			} else if (Float.parseFloat(item.getSpeer()) < 7
					&& Float.parseFloat(item.getSpeer()) >= 6) {
				item.setSpeer(String.valueOf(6));
			} else if (Float.parseFloat(item.getSpeer()) < 9
					&& Float.parseFloat(item.getSpeer()) >= 7) {
				item.setSpeer(String.valueOf(8));
			} else {
				item.setSpeer(String.valueOf(9));
			}
		}
		
		return list;
		
	}
	
	/**
	 * 设置轨迹可视范围
	 *
	 */
	private void setMoveCamera() {
		LatLngBounds.Builder b = new LatLngBounds.Builder();
		for (int i = 0; i < mList.size(); i++) {
			LatLng point = new LatLng(mList.get(i).getLattitude(), mList.get(i).getLongitude());

			b.include(point);
		}
		if (mList.size() > 2) {
			mAMap.moveCamera(
					CameraUpdateFactory.newLatLngBounds(b.build(), 11));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
