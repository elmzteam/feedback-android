package design.jsby.feedback.parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;

import design.jsby.feedback.model.Restaurant;
import design.jsby.feedback.util.Utils;

public class RestaurantHandler {
	private static final String TAG = Utils.makeLogTag(RestaurantHandler.class);

	public static Restaurant parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	public static Restaurant parse(JSONObject obj) throws JSONException, ParseException {
		return new Restaurant.Builder()
				.name(obj.getString("name"))
				.distance((float) obj.getDouble("distance"))
//				.restricted(obj.getBoolean("restricted"))
//				.presign(obj.getBoolean("presign"))
				.build();
	}
}
