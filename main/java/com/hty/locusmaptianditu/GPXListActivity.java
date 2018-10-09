package com.hty.locusmaptianditu;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class GPXListActivity extends ListActivity {
    ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().addActivity(this);
        genList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
        menu.setHeaderTitle("文件操作");
        menu.add(0, 1, 0, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                final String file = lv.getItemAtPosition(info.position).toString();
                if (file.toLowerCase().endsWith(".gpx")) {
                    new AlertDialog.Builder(GPXListActivity.this)
                            .setIcon(android.R.drawable.stat_sys_warning)
                            .setTitle("删除操作")
                            .setMessage("此步骤不可还原，确定删除\n" + file + " ？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,	int which) {
                                    String ttext = null;
                                    if (!file.equals(MainApplication.getrfn())) {
                                        RWXML.del(file);
                                        Intent intent = getIntent();
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(intent);
                                        ttext = file + "已删除";
                                    } else {
                                        ttext = file + "正在写入数据，禁止删除！";
                                    }
                                    Toast.makeText(getApplicationContext(),	ttext, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,	int which) {
                                }
                            })
                            .show();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(GPXListActivity.this, MenuActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    //		@Override
    //		public void onReceive(Context context, Intent intent) {
    //			context.unregisterReceiver(this);
    //			finish();
    //		}
    //	};

    @Override
    protected void onResume() {
        super.onResume();
        genList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void genList() {
        String[] s = RWXML.gpxlist();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, s);
        setListAdapter(adapter);
        lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String file = lv.getItemAtPosition(arg2).toString();
                Log.e("file", file);
                Log.e("MApplication.getrfn()", MainApplication.getrfn());
                if (file.toLowerCase().endsWith(".gpx")) {
                    if (RWXML.read(file) != null) {
                        MainApplication.setfn(file);
                        Intent intent = new Intent(GPXListActivity.this, HistoryActivity.class);
                        startActivity(intent);
                    } else {
                        String ttext = null;
                        if (!file.equals(MainApplication.getrfn())) {
                            RWXML.del(file);
                            ttext = file + "是空文档，自动删除！";
                            Intent intent = getIntent();
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                        } else {
                            ttext = file + "刚创建，即将写入数据！";
                        }
                        Toast.makeText(getApplicationContext(), ttext, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        registerForContextMenu(getListView());
    }

}
