package design.jsby.feedback.util;

import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public final class API {
	private static String TAG = Utils.makeLogTag(API.class);
	private static String BASE_URL = "http://162.243.9.96";
	private static String NEARBY = BASE_URL + "/restaurants";
	private static String MENU = BASE_URL + "/items";
	private static String RATING = BASE_URL + "/rating";

	public static URL getNearby(Location location, int startIndex) {
		try {
			return new URL(NEARBY + "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude());
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL", e);
			return null;
		}
	}

	public static URL getMenu(String restaurantId) {
		try {
			return new URL(MENU + "/" + restaurantId);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL", e);
			return null;
		}
	}

	public static URL putRating() {
		try {
			return new URL(RATING);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL", e);
			return null;
		}
	}

	public static JSONObject putRating(float rating, String entryId, String restaurantId) throws JSONException {
		return new JSONObject()
				.put("item", entryId)
				.put("rating", rating)
				.put("restaurant", restaurantId);
	}
}
