package com.hty.locusmaptianditu;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_menu);

        Button button_nomap = (Button) findViewById(R.id.button_nomap);
        Button button_locus_new = (Button) findViewById(R.id.button_locus_new);
        Button button_locus_history = (Button) findViewById(R.id.button_locus_history);
        Button button_server = (Button) findViewById(R.id.button_server);
        Button button_set = (Button) findViewById(R.id.button_set);
        Button button_about = (Button) findViewById(R.id.button_about);
        Button button_quit = (Button) findViewById(R.id.button_quit);

        button_nomap.setOnClickListener(new ClickListener());
        button_locus_new.setOnClickListener(new ClickListener());
        button_locus_history.setOnClickListener(new ClickListener());
        button_server.setOnClickListener(new ClickListener());
        button_set.setOnClickListener(new ClickListener());
        button_about.setOnClickListener(new ClickListener());
        button_quit.setOnClickListener(new ClickListener());

    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.button_nomap:
                    startActivity(new Intent(MenuActivity.this, NoMapActivity.class));
                    break;
                case R.id.button_locus_new:
                    startActivity(new Intent(MenuActivity.this, CurrentActivity.class));
                    break;
                case R.id.button_locus_history:
                    startActivity(new Intent(MenuActivity.this, GPXListActivity.class));
                    break;
                case R.id.button_server:
                    Intent intent = new Intent();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
                    String uploadServer = sharedPreferences.getString("uploadServer","");
                    intent.setData(Uri.parse(uploadServer.substring(0,uploadServer.lastIndexOf("/"))));
                    intent.setAction(Intent.ACTION_VIEW);
                    MenuActivity.this.startActivity(intent);
                    break;
                case R.id.button_set:
                    startActivity(new Intent(MenuActivity.this, SettingActivity.class));
                    break;
                case R.id.button_about:
                    new AlertDialog.Builder(MenuActivity.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("轨迹地图天地图版  V1.0")
                            .setMessage("利用天地图API提供的地图、定位、绘图和手机的GPS功能绘制、记录位移轨迹，查看记录的轨迹，上传GPS数据到服务器。\n作者：黄颖\nE-mail：sonichy@163.com\nQQ：84429027\n\n更新历史\n1.0 (2018-07)\n百度地图API向天地图API迁移。\n增加上传服务器设置。")
                            .setPositiveButton("确定", null).show();
                    break;
                case R.id.button_quit:
                    MainApplication.getInstance().exit();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}