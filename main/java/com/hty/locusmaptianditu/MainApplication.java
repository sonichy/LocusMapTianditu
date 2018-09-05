package com.hty.locusmaptianditu;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
	private static Context mContext;
	private static String rfn="";
	private static String fn="";
	private static String msg="";
	private final List<Activity> activityList = new LinkedList<Activity>();
	private static MainApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	public static void setmsg(String s) {
		msg = s;
	}

	public static String getmsg() {
		return msg;
	}

	public static void setrfn(String s) {
		rfn = s;
	}

	public static String getrfn() {
		return rfn;
	}

	public static void setfn(String s) {
		fn = s;
	}

	public static String getfn() {
		return fn;
	}

	public static Context getContext() {
		return mContext;
	}

	static MainApplication getInstance() {
		if (null == instance) {
			instance = new MainApplication();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish(); // 将activity推向后台
		}
		System.exit(0); // 杀进程释放内存
	}
}
