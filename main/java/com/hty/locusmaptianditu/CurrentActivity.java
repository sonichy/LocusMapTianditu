package com.hty.locusmaptianditu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapController;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MyLocationOverlay;
import com.tianditu.android.maps.overlay.PolylineOverlay;
import com.tianditu.android.maps.renderoption.LineOption;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CurrentActivity extends Activity {
    TextView textView_currentLocation, textView_upload;
    RadioGroup mRadioGroup;
    ImageButton imageButton_location;
    MapView mMapView;
    MyLocationOverlay mMyLocation;
    GeoPoint GP0;
    boolean isFirst = true;
    Date time_start, datel;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    SimpleDateFormat timeformatDuration = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    double lgt, ltt, lgt0, ltt0, lc = 0, distance, speed;
    int c = 0;
    SharedPreferences sharedPreferences;
    String uploadServer = "", RC = "", filename = "";
    DecimalFormat DF1 = new DecimalFormat("0.0");
    DecimalFormat DF2 = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        MainApplication.getInstance().addActivity(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        uploadServer = sharedPreferences.getString("uploadServer", MainApplication.uploadServer);
        if (uploadServer.equals(""))
            uploadServer = MainApplication.uploadServer;
        time_start = new Date();
        filename = dateformat1.format(time_start) + "TD.gpx";
        MainApplication.setrfn(filename);
        RWXML.create(dateformat1.format(time_start));
        timeformatDuration.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        textView_currentLocation = (TextView) findViewById(R.id.textView_currentLocation);
        textView_upload = (TextView) findViewById(R.id.textView_upload);
        imageButton_location = (ImageButton) findViewById(R.id.imageButton_location);
        imageButton_location.setOnClickListener(new ClickListener());

        mMapView = (MapView) findViewById(R.id.mapView);
        //设置启用内置的缩放控件
        mMapView.setBuiltInZoomControls(true);
        //得到mMapView的控制权,可以用它控制和驱动平移和缩放
        MapController mMapController = mMapView.getController();
        //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
        //设置地图中心点
        mMapController.setCenter(point);
        //设置地图zoom级别
        mMapController.setZoom(12);
        mMapView.setLogoPos(MapView.LOGO_LEFT_BOTTOM);
        //mMapView.setMapType(MapView.TMapType.MAP_TYPE_VEC);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int mapType = mMapView.getMapType();
        if (mapType == MapView.TMapType.MAP_TYPE_VEC)
            mRadioGroup.check(R.id.radioButton1);
        else if (mapType == MapView.TMapType.MAP_TYPE_IMG)
            mRadioGroup.check(R.id.radioButton2);
        else if (mapType == MapView.TMapType.MAP_TYPE_TERRAIN)
            mRadioGroup.check(R.id.radioButton3);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton1) {
                    mMapView.setMapType(MapView.TMapType.MAP_TYPE_VEC);
                } else if (checkedId == R.id.radioButton2) {
                    mMapView.setMapType(MapView.TMapType.MAP_TYPE_IMG);
                } else if (checkedId == R.id.radioButton3) {
                    mMapView.setMapType(MapView.TMapType.MAP_TYPE_TERRAIN);
                }
            }
        });

        mMyLocation = new MyOverlay(this, mMapView);
        mMapView.addOverlay(mMyLocation);
        mMyLocation.enableCompass();
        mMyLocation.enableMyLocation();
        mMyLocation.setGpsFollow(true);

    }

    /*
    MyLocationOverlay getOverlay() {
        return mMyLocation;
    }
    */

    class MyOverlay extends MyLocationOverlay {
        public MyOverlay(Context context, MapView mapView) {
            super(context, mapView);
        }

        // 处理在"我的位置"上的点击事件
        protected boolean dispatchTap() {
            return true;
        }

        @Override
        public void onLocationChanged(Location location) {
            super.onLocationChanged(location);
            if (location != null) {
                datel = new Date(location.getTime());
                lgt = location.getLongitude();
                ltt = location.getLatitude();
                speed = location.getSpeed();
                String slgt = String.valueOf(lgt);
                String sltt = String.valueOf(ltt);
                Date date = new Date();
                long duration = date.getTime() - time_start.getTime();
                String sduration = timeformatDuration.format(duration);
                textView_currentLocation.setText(filename);
                textView_currentLocation.append("\n" + dateformat.format(datel));
                textView_currentLocation.append("\n经度：" + slgt);
                textView_currentLocation.append("\n纬度：" + sltt);
                textView_currentLocation.append("\n海拔：" + location.getAltitude() + " 米");
                textView_currentLocation.append("\n速度：" + DF1.format(speed) + " 米/秒");
                textView_currentLocation.append("\n时长：" + sduration);
                ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
                GeoPoint GP = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
                if (isFirst) {
                    isFirst = false;
                    GP0 = GP;
                    lgt0 = lgt;
                    ltt0 = ltt;
                }
                points.add(GP0);
                points.add(GP);
                LineOption LOption = new LineOption();
                LOption.setStrokeWidth(3);
                LOption.setDottedLine(false);
                LOption.setStrokeColor(0xFF00FF00); // 0xAARRGGBB
                PolylineOverlay ployline = new PolylineOverlay();
                ployline.setOption(LOption);
                ployline.setPoints(points);
                mMapView.addOverlay(ployline);
                //distance = Utils.getDistance(lgt, ltt, lgt0, ltt0);
                //distance = com.tianditu.maps.Map.Project.getDistanceMeters(lgt, ltt, lgt0, ltt0);
                float[] results = new float[1];
                Location.distanceBetween(ltt, lgt, ltt0, lgt0, results);
                distance = results[0];
                mMapView.getProjection();
                lc += distance;
                textView_currentLocation.append("\n位移：" + DF2.format(distance) + "米");
                textView_currentLocation.append("\n路程：" + DF2.format(lc) + "米");
                RWXML.add(filename, dateformat.format(date), String.valueOf(ltt), String.valueOf(lgt), DF2.format(lc), sduration);
                if (c > 10) {
                    new Thread(t).start();
                    c = 0;
                }
                c++;
                GP0 = GP;
                lgt0 = lgt;
                ltt0 = ltt;
            } else {
                textView_currentLocation.append("无法定位");
            }
        }

    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.imageButton_location:
                    mMyLocation.enableCompass();
                    mMyLocation.enableMyLocation();
                    mMyLocation.setGpsFollow(true);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(CurrentActivity.this, MenuActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "退出");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        switch (item_id) {
            case 0:
                MainApplication.setrfn("");
                finish();
                break;
        }
        return true;
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            String dateu = "";
            String timeu = "";
            try {
                dateu = URLEncoder.encode(dateformat2.format(datel), "utf-8");
                timeu = URLEncoder.encode(timeformat.format(datel), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String SU = uploadServer + "/add.php?date=" + dateu + "&time=" + timeu + "&longitude=" + lgt + "&latitude=" + ltt + "&speed=" + DF1.format(speed) + "&distance=" + DF2.format(distance);
            RWXML.append("TDMap.log", SU);
            RC = Utils.sendURLResponse(SU);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView_upload.setText(RC);
                }
            });
        }
    });

}