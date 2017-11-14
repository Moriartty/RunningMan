package com.moriarty.user.weather;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.moriarty.user.weather.Utils;
import com.moriarty.user.weather.WeatherApplication;
import com.moriarty.user.weather.entity.DailyForecast;
import com.moriarty.user.weather.entity.Weather;


@SuppressLint("SimpleDateFormat") @SuppressWarnings("deprecation")
public class ApiManager {

//	private static AreaData AREA_DATA;

	//static final String URL = "https://api.heweather.com/x3/weather?";
	static final String URL = "https://free-api.heweather.com/v5/weather?";
	static final Gson GSON = new Gson();
	static final String TAG = ApiManager.class.getSimpleName();

	public interface GeocoderListener {
		public void onConvertCity(String cityName);

		public void onError();
	}

	public interface ApiListener {
		public void onReceiveWeather(Weather weather, boolean updated);

		public void onUpdateError();
	}

	public interface LoadAreaDataListener {
		public void onLoaded(AreaData areaData);

		public void onError();
	}

	private static final String KEY_SELECTED_AREA = "KEY_SELECTED_AREA";
	private static final String KEY_DEFAULT_SELECTED_AREA_ID = "KEY_DEFAULT_SELECTED_AREA_ID";

//	public static Weather loadDefaultSelectedAreaWeather(Context context) {
//		final String defalutAreaId = getDefaultSelectedAreaId(context);
//		if (!TextUtils.isEmpty(defalutAreaId)) {
//			return loadWeather(context, defalutAreaId);
//		}
//		return null;
//	}

	public static final String GITHUB_SAMPLE_SELECTED_AREA = "[{\"city\":\"北京\",\"id\":\"CN101010100\",\"name_cn\":\"北京\",\"name_en\":\"beijing\",\"province\":\"北京\"},{\"city\":\"石家庄\",\"id\":\"CN101090101\",\"name_cn\":\"石家庄\",\"name_en\":\"shijiazhuang\",\"province\":\"河北\"},{\"city\":\"上海\",\"id\":\"CN101020100\",\"name_cn\":\"上海\",\"name_en\":\"shanghai\",\"province\":\"上海\"}]";
	public static ArrayList<Area> loadSelectedArea(Context context) {
		ArrayList<Area> areas = new ArrayList<ApiManager.Area>();
		String json = WeatherApplication.USE_SAMPLE_DATA ? GITHUB_SAMPLE_SELECTED_AREA : Utils.getPrefString(context, KEY_SELECTED_AREA, "");
		if (TextUtils.isEmpty(json)) {
			return areas;
		}
		try {
			Area[] aa = GSON.fromJson(json, Area[].class);
			if (aa != null) {
				Collections.addAll(areas, aa);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return areas;
	}




	/**
	 * 转换为城市 返回城市名 如”北京“（不包括”市“）
	 * 
	 * @param context
	 * @param location
	 * @param geocoderListener
	 */
//	public static void convertLocationToCity(@NonNull final Location location, final GeocoderListener geocoderListener) {
//		final String url = "http://api.map.baidu.com/geocoder/v2/?ak=yourkey&output=json&pois=0coordtype=wgs84ll&location="
//				+ location.getLatitude() + "," + location.getLongitude();
//		AsyncTask<Void, Void, String> loadTask = new AsyncTask<Void, Void, String>() {
//
//			@Override
//			protected String doInBackground(Void... params) {
//				try {
//					String json = doHttpGet(url);
//					JSONObject jsonObject = new JSONObject(json);
//					String cityName = jsonObject.getJSONObject("result").getJSONObject("addressComponent")
//							.getString("city");
//					if (!TextUtils.isEmpty(cityName)) {
//						if (cityName.endsWith("市")) {
//							cityName = cityName.substring(0, cityName.length() - 1);
//						}
//						return cityName;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(String result) {
//				super.onPostExecute(result);
//				if (geocoderListener != null) {
//					if (!TextUtils.isEmpty(result)) {
//						geocoderListener.onConvertCity(result);
//					} else {
//						geocoderListener.onError();
//					}
//				}
//
//			}
//		};
//		AsyncTaskCompat.executeParallel(loadTask);
//	}


//	public static void loadAreaData(final Context context, final LoadAreaDataListener loadAreaDataListener) {
//		if (loadAreaDataListener != null) {
//			if (AREA_DATA != null && AREA_DATA.list != null && AREA_DATA.list.size() > 0) {
//				loadAreaDataListener.onLoaded(AREA_DATA);
//				return;
//			}
//		}
//		final AssetManager assetManager = context.getAssets();
//		AsyncTask<Void, Void, AreaData> loadTask = new AsyncTask<Void, Void, AreaData>() {
//
//			@Override
//			protected AreaData doInBackground(Void... params) {
//				try {
//					InputStream is = assetManager.open("AreaData");
//					InputStreamReader isr = new InputStreamReader(is);
//					JsonReader reader = new JsonReader(isr);
//					AreaData areaData = GSON.fromJson(reader, AreaData.class);
//					reader.close();
//					return areaData;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(AreaData result) {
//				super.onPostExecute(result);
//				if (loadAreaDataListener != null) {
//					if (result != null && result.list != null && result.list.size() > 0) {
//						AREA_DATA = result;
//						loadAreaDataListener.onLoaded(result);
//					} else {
//						loadAreaDataListener.onError();
//					}
//				}
//
//			}
//		};
//		AsyncTaskCompat.executeParallel(loadTask);
//	}

	public static void updateWeather(@NonNull Context context, @NonNull String areaId,String key, @NonNull ApiListener apiListener) {
		if (TextUtils.isEmpty(areaId)) {
			return;
		}
		//UiUtil.logDebug(TAG, "updateWeather->" + areaId);
		final String url = getApiUrlFromAreaId(areaId,key);
		Log.d(WeatherActivity.TAG,url);
		ApiTask apiTask = new ApiTask(context, url, apiListener);
		AsyncTaskCompat.executeParallel(apiTask);
	}

	// public static void updateWeatherByLocation(@NonNull Context context,
	// @NonNull Location location, @NonNull ApiListener apiListener) {
	// location.get
	// ApiTask apiTask = new ApiTask(context,URL+ cityId, apiListener);
	// AsyncTaskCompat.executeParallel(apiTask);
	// }

	// private static SparseArray<Weather> CACHE_WEAHTER = new
	// SparseArray<Weather>();


//	private static void saveWeater(@NonNull Context context, @NonNull Weather weather, @NonNull String weatherJson) {
//		if (context == null || weather == null || !weather.isOK()) {
//			return;
//		}
//		try {
//			final String key = weather.get().basic.id;
//			UiUtil.logDebug(TAG, "saveWeater->" + key);
//			MxxPreferenceUtil.setPrefString(context, key, weatherJson);
//			if (TextUtils.equals(key, getDefaultSelectedAreaId(context))) {
//				// WeatherWidgetProvider.sendUpdateAppWidget(context, weather);
//				UpdateManager.sendUpdateWeather(context, weather);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static Weather loadWeather(@NonNull Context context, @NonNull String areaId) {
		if (context == null || TextUtils.isEmpty(areaId)) {
			return null;
		}
		try {
			String json = Utils.getPrefString(context, areaId, null);
			if (TextUtils.isEmpty(json)) {
				return null;
			}
			Weather weather = GSON.fromJson(json, Weather.class);
			return weather;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 是否需要更新Weather数据 1小时15分钟之内的return false; 传入null或者有问题的weather也会返回true
	 * 
	 * @param weather
	 * @return
	 */
	public static boolean isNeedUpdate(Weather weather) {
		if (!acceptWeather(weather)) {
			return true;
		}
		try {
			final String updateTime = weather.get().basic.update.loc;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date updateDate = format.parse(updateTime);
			Date curDate = new Date();
			long interval = curDate.getTime() - updateDate.getTime();// 时间间隔 ms
			if ((interval >= 0) && (interval < 75 * 60 * 1000)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 是否是今天2015-11-05 04:00 合法data格式： 2015-11-05 04:00 或者2015-11-05
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isToday(String date) {
		if (TextUtils.isEmpty(date) || date.length() < 10) {// 2015-11-05
															// length=10
			return false;
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String today = format.format(new Date());
			if (TextUtils.equals(today, date.substring(0, 10))) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		// final String[] strs = date.substring(0, 10).split("-");
		// final int year = Integer.valueOf(strs[0]);
		// final int month = Integer.valueOf(strs[1]);
		// final int day = Integer.valueOf(strs[2]);
		// Calendar c = Calendar.getInstance();
		// int curYear = c.get(Calendar.YEAR);
		// int curMonth = c.get(Calendar.MONTH) + 1;//Java月份从0月开始
		// int curDay = c.get(Calendar.DAY_OF_MONTH);
		// if(curYear == year && curMonth == month && curDay == day){
		// return true;
		// }
		// return false;
	}

	/**
	 * 是否是合法的Weather数据
	 * 
	 * @param weather
	 * @return
	 */
	public static boolean acceptWeather(Weather weather) {
		if (weather == null || !weather.isOK()) {
			return false;
		}
		return true;
	}

	/**
	 * 把Weather转换为对应的BaseDrawer.Type
	 * 
	 * @param weather
	 * @return
	 */
	/*public static BaseDrawer.Type convertWeatherType(Weather weather) {
		if (weather == null || !weather.isOK()) {
			return Type.DEFAULT;
		}
		final boolean isNight = isNight(weather);
		try {
			final int w = Integer.valueOf(weather.get().now.cond.code);
			switch (w) {
			case 100:
				return isNight ? Type.CLEAR_N : Type.CLEAR_D;
			case 101:// 多云
			case 102:// 少云
			case 103:// 晴间多云
				return isNight ? Type.CLOUDY_N : Type.CLOUDY_D;
			case 104:// 阴
				return isNight ? Type.OVERCAST_N : Type.OVERCAST_D;
				// 200 - 213是风
			case 200:
			case 201:
			case 202:
			case 203:
			case 204:
			case 205:
			case 206:
			case 207:
			case 208:
			case 209:
			case 210:
			case 211:
			case 212:
			case 213:
				return isNight ? Type.WIND_N : Type.WIND_D;
			case 300:// 阵雨Shower Rain
			case 301:// 强阵雨 Heavy Shower Rain
			case 302:// 雷阵雨 Thundershower
			case 303:// 强雷阵雨 Heavy Thunderstorm
			case 304:// 雷阵雨伴有冰雹 Hail
			case 305:// 小雨 Light Rain
			case 306:// 中雨 Moderate Rain
			case 307:// 大雨 Heavy Rain
			case 308:// 极端降雨 Extreme Rain
			case 309:// 毛毛雨/细雨 Drizzle Rain
			case 310:// 暴雨 Storm
			case 311:// 大暴雨 Heavy Storm
			case 312:// 特大暴雨 Severe Storm
			case 313:// 冻雨 Freezing Rain
				return isNight ? Type.RAIN_N : Type.RAIN_D;
			case 400:// 小雪 Light Snow
			case 401:// 中雪 Moderate Snow
			case 402:// 大雪 Heavy Snow
			case 403:// 暴雪 Snowstorm
			case 407:// 阵雪 Snow Flurry
				return isNight ? Type.SNOW_N : Type.SNOW_D;
			case 404:// 雨夹雪 Sleet
			case 405:// 雨雪天气 Rain And Snow
			case 406:// 阵雨夹雪 Shower Snow
				return isNight ? Type.RAIN_SNOW_N : Type.RAIN_SNOW_D;
			case 500:// 薄雾
			case 501:// 雾
				return isNight ? Type.FOG_N : Type.FOG_D;
			case 502:// 霾
			case 504:// 浮尘
				return isNight ? Type.HAZE_N : Type.HAZE_D;
			case 503:// 扬沙
			case 506:// 火山灰
			case 507:// 沙尘暴
			case 508:// 强沙尘暴
				return isNight ? Type.SAND_N : Type.SAND_D;
			default:
				return isNight ? Type.UNKNOWN_N : Type.UNKNOWN_D;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isNight ? Type.UNKNOWN_N : Type.UNKNOWN_D;
	}*/

	public static boolean isNight(Weather weather) {
		if (weather == null || !weather.isOK()) {
			return false;
		}
		// SimpleDateFormat time=new SimpleDateFormat("yyyy MM dd HH mm ss");
		try {
			final Date date = new Date();
			String todaydate = (new SimpleDateFormat("yyyy-MM-dd")).format(date);
			String todaydate1 = (new SimpleDateFormat("yyyy-M-d")).format(date);
			DailyForecast todayForecast = null;
			for (DailyForecast forecast : weather.get().dailyForecast) {
				if (TextUtils.equals(todaydate, forecast.date) || TextUtils.equals(todaydate1, forecast.date)) {
					todayForecast = forecast;
					break;
				}
			}
			if (todayForecast != null) {
				final int curTime = Integer.valueOf((new SimpleDateFormat("HHmm").format(date)));
				final int srTime = Integer.valueOf(todayForecast.astro.sr.replaceAll(":", ""));// 日出
				final int ssTime = Integer.valueOf(todayForecast.astro.ss.replaceAll(":", ""));// 日落
				if (curTime > srTime && curTime <= ssTime) {// 是白天
					return false;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	static class ApiTask extends AsyncTask<Void, Void, Weather> {

		private final Context context;
		private final String url;
		private final ApiListener listener;

		public ApiTask(Context context, String url, ApiListener listener) {
			super();
			this.context = context;
			this.url = url;
			this.listener = listener;
		}

		@Override
		protected Weather doInBackground(Void... params) {
			return updateWeatherFromInternet(context, url);
		}

		@Override
		protected void onPostExecute(Weather result) {
			super.onPostExecute(result);
			if (listener != null) {
				if (result != null) {
					listener.onReceiveWeather(result, true);
				} else {
					listener.onUpdateError();
					//UiUtil.toastDebug(context, "更新失败");
				}
			}

		}
	}

	static String getApiUrlFromAreaId(String areaId,String key) {
		return URL + "city="+areaId+"&key="+key;
	}

	/**
	 * 网络获取weather 如果成功ok自动保存
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	static Weather updateWeatherFromInternet(Context context, final String url) {
		try {
			String json = "";
			if(WeatherApplication.USE_SAMPLE_DATA){
				/*String areaName = url.substring(url.length() - 11);
				//UiUtil.logDebug("USE_SAMPLE_DATA", "areaName->" + areaName);
				InputStream is = context.getAssets().open(areaName);
				InputStreamReader isr = new InputStreamReader(is);
				JsonReader reader = new JsonReader(isr);
				//reader.setLenient(true);
				Weather weather = GSON.fromJson(reader, Weather.class);
				reader.close();
				is.close();
				Calendar calendar = Calendar.getInstance();
				//把sample数据的日期都修正成今天的
				weather.get().basic.update.loc = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date());//"2016-08-16 12:51";
				final ArrayList<DailyForecast> dailyForecasts = weather.get().dailyForecast;
				final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//"2016-08-16"
				for(DailyForecast dailyForecast : dailyForecasts){ 
					dailyForecast.date = dateFormat.format(calendar.getTime());
					calendar.add(Calendar.DAY_OF_YEAR, +1);
				}
				final ArrayList<HourlyForecast> hourlyForecasts = weather.get().hourlyForecast;//"2016-08-16 13:00"
				final String todayDate = dateFormat.format(System.currentTimeMillis());
				for(HourlyForecast hourlyForecast : hourlyForecasts){ 
//					UiUtil.logDebug("USE_SAMPLE_DATA", "hourlyForecast.date.fixbefore->" + hourlyForecast.date);
					hourlyForecast.date = hourlyForecast.date.substring(todayDate.length());
					hourlyForecast.date = todayDate + hourlyForecast.date;
//					UiUtil.logDebug("USE_SAMPLE_DATA", "hourlyForecast.date.fixafter->" + hourlyForecast.date);
				}
				Thread.sleep(800);//这个速度有点快，慢一些*/
				json=Utils.readFromFile(Utils.getFilePath(WeatherActivity.areaId));
				Log.d(WeatherActivity.TAG,json);
				Weather weather = GSON.fromJson(json,Weather.class);
				return weather;
			}else{
				json=doHttpGet(url);
				//Log.d(MainActivity.TAG,json);
				reserveWeatherData(json);
				if (!TextUtils.isEmpty(json)) {
					Weather weather = GSON.fromJson(json, Weather.class);
					if (acceptWeather(weather)) {
						//ApiManager.saveWeater(context, weather, json);
						return weather;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// UiUtil.logDebug("FUCK", e.toString());
		}
		return null;
	}
	private static void reserveWeatherData(String result){
		if(result!=null&&!result.equals("")){
			Utils.writeInFile(result,Utils.getFilePath(WeatherActivity.areaId));
		}
	}

	private static String doHttpGet(String url) {
		try {
			final int TIMEOUT = 10000;// 10秒
			String result = null;
			HttpGet request = new HttpGet(url);
			HttpConnectionParams.setConnectionTimeout(request.getParams(), TIMEOUT);
			HttpConnectionParams.setSoTimeout(request.getParams(), TIMEOUT);
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				if (result != null) {
					if (result.startsWith("\ufeff")) {
						result = result.substring(1);
					}
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class AreaData implements Serializable {
		private static final long serialVersionUID = 9143853030679080418L;
		@SerializedName("list")
		@Expose
		public ArrayList<Area> list = new ArrayList<ApiManager.Area>();
		@SerializedName("create")
		@Expose
		public String create;

		public AreaData() {
			super();
		}

	}

	public static class Area implements Serializable {
		private static final long serialVersionUID = 7646903512215148839L;

		public Area() {
			super();
		}

		@SerializedName("id")
		@Expose
		public String id;
		@SerializedName("name_en")
		@Expose
		public String name_en;
		@SerializedName("name_cn")
		@Expose
		public String name_cn;
		@SerializedName("city")
		@Expose
		public String city;
		@SerializedName("province")
		@Expose
		public String province;

		@Override
		public String toString() {
			return name_cn + " [" + city + "," + province + "]";// + "," + id
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((city == null) ? 0 : city.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((name_cn == null) ? 0 : name_cn.hashCode());
			result = prime * result + ((name_en == null) ? 0 : name_en.hashCode());
			result = prime * result + ((province == null) ? 0 : province.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Area other = (Area) obj;
			if (city == null) {
				if (other.city != null)
					return false;
			} else if (!city.equals(other.city))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (name_cn == null) {
				if (other.name_cn != null)
					return false;
			} else if (!name_cn.equals(other.name_cn))
				return false;
			if (name_en == null) {
				if (other.name_en != null)
					return false;
			} else if (!name_en.equals(other.name_en))
				return false;
			if (province == null) {
				if (other.province != null)
					return false;
			} else if (!province.equals(other.province))
				return false;
			return true;
		}

	}

}
