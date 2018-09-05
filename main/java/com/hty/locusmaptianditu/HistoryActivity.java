package com.hty.locusmaptianditu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapController;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.overlay.MarkerOverlay;
import com.tianditu.android.maps.overlay.PolylineOverlay;
import com.tianditu.android.maps.renderoption.LineOption;


public class HistoryActivity extends Activity {
    MapView mMapView;
    MapController mMapController;
    MarkerOverlay mMarker;
    TextView tv1, tv2;
    //GeoPoint p1, p2;
    double lc = 0, lc0 = 0, speed, speedmax = 0, pi = 3.14, ltt, lgt, ltt1, lgt1, distance, lc1 = 0, distance1, speed1, speedmax1 = 0, atan;
    Date date, starttime;
    long duration;
    SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
    ImageButton imageButton_history_location;
    Button button_animate, button_stop;
    int num,c;
    boolean flag = true;
    RadioGroup mRadioGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_history);
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        imageButton_history_location = (ImageButton) findViewById(R.id.imageButton_history_location);
        imageButton_history_location.setOnClickListener(new clickListener());
        button_animate = (Button) findViewById(R.id.button_animate);
        button_animate.setOnClickListener(new clickListener());
        button_stop = (Button) findViewById(R.id.button_stop);
        button_stop.setOnClickListener(new clickListener());

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setLogoPos(MapView.LOGO_LEFT_BOTTOM);
        mMapController = mMapView.getController();
        //mMapView.setMapType(MapView.TMapType.MAP_TYPE_VEC);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_history);
        int mapType = mMapView.getMapType();
        if (mapType == MapView.TMapType.MAP_TYPE_VEC)
            mRadioGroup.check(R.id.HRB_VEC);
        else if (mapType == MapView.TMapType.MAP_TYPE_IMG)
            mRadioGroup.check(R.id.HRB_IMG);
        else if (mapType == MapView.TMapType.MAP_TYPE_TERRAIN)
            mRadioGroup.check(R.id.HRB_TERRAIN);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.HRB_VEC){
                    mMapView.setMapType(MapView.TMapType.MAP_TYPE_VEC);
                } else if (checkedId == R.id.HRB_IMG){
                    mMapView.setMapType(MapView.TMapType.MAP_TYPE_IMG);
                } else if (checkedId == R.id.HRB_TERRAIN) {
                    mMapView.setMapType(MapView.TMapType.MAP_TYPE_TERRAIN);
                }
            }
        });

        points = RWXML.read(MainApplication.getfn());
        Drawgpx();
    }

    class clickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageButton_history_location:
                    mMapController.setCenter(points.get(num/2));
                    break;
                case R.id.button_animate:
                    flag = false;
                    c = 0;
                    new Thread(new MyThread()).start();
                    break;
                case R.id.button_stop:
                    flag = true;
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Resume History", MainApplication.getfn());
        points = null;
        if (!MainApplication.getfn().equals("")) {
            points = RWXML.read(MainApplication.getfn());
            Drawgpx();
        } else {
            //sendBroadcast(new Intent("finishGPXList"));
            startActivity(new Intent(HistoryActivity.this, GPXListActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "删除");
        menu.add(0, 1, 1, "退出");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        switch (item_id) {
            case 0:
                new AlertDialog.Builder(HistoryActivity.this).setIcon(android.R.drawable.stat_sys_warning).setTitle("删除操作")
                        .setMessage("此步骤不可还原，确定删除\n" + MainApplication.getfn() + " ？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String ttext = null;
                                Log.e("MApplication.getfn()", MainApplication.getfn());
                                Log.e("MApplication.getrfn()", MainApplication.getrfn());
                                if (!MainApplication.getfn().equals(MainApplication.getrfn())) {
                                    ttext = MainApplication.getfn() + "已删除！";
                                    RWXML.del(MainApplication.getfn());
                                    startActivity(new Intent(HistoryActivity.this, GPXListActivity.class));
                                } else {
                                    ttext = "此文件正在记录中，请先退出新行程！";
                                }
                                Toast.makeText(getApplicationContext(), ttext, Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                break;
            case 1:
                finish();
                startActivity(new Intent(HistoryActivity.this, MenuActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(HistoryActivity.this, GPXListActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void Drawgpx() {
        mMapView.removeAllOverlay();
        num = points.size();
        tv1.setText(MainApplication.getmsg());
        tv2.setText(num + "个点");
        mMapController.setCenter(points.get(num/2));
        mMapController.setZoom(12);

        if (num == 1) {
            points.add(points.get(0));
        }

        mMarker = new MarkerOverlay();
        mMarker.setPosition(points.get(0));
        mMarker.setIcon(getResources().getDrawable(R.drawable.start));
        mMarker.setTitle("起点");
        mMapView.addOverlay(mMarker);

        mMarker = new MarkerOverlay();
        mMarker.setPosition(points.get(num-1));
        mMarker.setIcon(getResources().getDrawable(R.drawable.end));
        mMarker.setTitle("终点");
        mMapView.addOverlay(mMarker);

        mMarker = new MarkerOverlay();
        mMarker.setPosition(points.get(0));
        mMarker.setIcon(getResources().getDrawable(R.drawable.blue));
        mMarker.setTitle("当前");
        mMapView.addOverlay(mMarker);

        LineOption LOption = new LineOption();
        LOption.setStrokeWidth(3);
        LOption.setDottedLine(false);
        LOption.setStrokeColor(0xFF0000FF); // 0xAARRGGBB
        PolylineOverlay polyline = new PolylineOverlay();
        polyline.setOption(LOption);
        polyline.setPoints(points);
        mMapView.addOverlay(polyline);
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            while (!flag) {
                if (c < num) {
                    mMarker.setPosition(points.get(c));
                    mMapView.addOverlay(mMarker);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c++;
                }
            }
        }
    }
}
