package com.moriarty.user.weather;

import android.app.Application;

public class WeatherApplication extends Application {
	
	
	public static final boolean DEBUG = true;
	public static boolean USE_SAMPLE_DATA = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
}
