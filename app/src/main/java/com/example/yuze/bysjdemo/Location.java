package com.example.yuze.bysjdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class Location extends AppCompatActivity implements SensorEventListener {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListener mListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode;

    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    MapView mMapView;
    BaiduMap mBaiduMap;

    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    private static final int MENU_LOGIN = 1;
    private static final int MENU_FOOTPRINT = 2;
    private static final int MENU_SEND = 3;
    private static final int MENU_CLOUD_SYNCING = 4;

    static final String DATABASE_NAME = "andtriplog.db";
    static final int DATABASE_VERSION = 7;
    public DBHelper mAndTripLogDB;

    private ExportThread mExportThread;
    private ProgressDialog mProgressDialog;
    private AlertDialog mDleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(mListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mAndTripLogDB = new DBHelper(this);
        mAndTripLogDB.startDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_LOGIN, 0, "Login").setIcon(android.R.drawable.ic_media_play).setEnabled(true);
        menu.add(Menu.NONE, MENU_FOOTPRINT, 0, "Footprint").setIcon(android.R.drawable.ic_menu_info_details).setEnabled(true);
        menu.add(Menu.NONE, MENU_SEND, 0, "Send").setIcon(android.R.drawable.ic_menu_send).setEnabled(true);
        menu.add(Menu.NONE, MENU_CLOUD_SYNCING, 0, "Cloud ayncing").setIcon(android.R.drawable.ic_popup_sync).setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case 1:
                intent.setClass(Location.this, Login.class);
                Location.this.startActivity(intent);
                startActivity(intent);
            case 2:
                finish();
                break;
            case 3:
                exportTrip(-1, true);
                return true;
            case 4:
                finish();
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 分享记录
     *
     * @param id
     * @param email
     */
    private void exportTrip(long id, boolean email) {
//        showDialog();
        mExportThread = new ExportThread(this, id, mProgressDialog, email);
        mExportThread.start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        //位系统的方向传感器注册监听器

    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
        //取消注册传感器监听
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocClient.stop();//退出时销毁定位
        mBaiduMap.setMyLocationEnabled(false);//关闭定位图层
        mMapView.onDestroy();
        mMapView = null;
    }
}
