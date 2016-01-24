package design.jsby.feedback.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;

import design.jsby.feedback.model.Restaurant;
import design.jsby.feedback.util.Utils;

public class RestaurantHandler {
	private static final String TAG = Utils.makeLogTag(RestaurantHandler.class);

	public static Restaurant[] parseAll(InputStream in) throws JSONException, ParseException {
		return parseAll(Utils.inputStreamToJSONArray(in));
	}

	public static Restaurant[] parseAll(JSONArray array) throws JSONException, ParseException {
		final Restaurant[] restaurants = new Restaurant[array.length()];
		for (int i = 0; i < array.length(); i++) {
			restaurants[i] = parse(array.getJSONObject(i));
		}
		return restaurants;
	}

	public static Restaurant parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	public static Restaurant parse(JSONObject obj) throws JSONException, ParseException {
		return new Restaurant.Builder()
				.name(obj.getString("name"))
				.distance((float) obj.getDouble("distance"))
				.categories(obj.getString("categories"))
				.address(obj.getString("address"))
				.images(Utils.JSONArrayToStringArray(obj.getJSONArray("images")))
				.id(obj.getString("_id"))
				.build();
	}
}
