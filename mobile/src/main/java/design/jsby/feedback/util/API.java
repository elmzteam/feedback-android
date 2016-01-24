package design.jsby.feedback.util;

import android.location.Location;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public final class API {
	private static String TAG = Utils.makeLogTag(API.class);
	private static String BASE_URL = "http://162.243.9.96";
	private static String NEARBY = "/restaurants";
	private static String MENU = "/items";

	public static URL getNearby(Location location, int startIndex) {
		try {
			return new URL(BASE_URL + NEARBY + "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude());
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL", e);
			return null;
		}
	}

	public static URL getMenu(String restaurantId) {
		try {
			return new URL(BASE_URL + MENU + "/" + restaurantId);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL", e);
			return null;
		}
	}
}
