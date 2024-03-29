package design.jsby.feedback.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;

import design.jsby.feedback.model.MenuEntry;
import design.jsby.feedback.util.Utils;

public class MenuHandler {
	public static MenuEntry[] parseAll(InputStream in) throws JSONException, ParseException {
		return parseAll(Utils.inputStreamToJSONArray(in));
	}

	private static MenuEntry[] parseAll(JSONArray array) throws JSONException, ParseException {
		final MenuEntry[] menuEntries = new MenuEntry[array.length()];
		for (int i = 0; i < array.length(); i++) {
			menuEntries[i] = parse(array.getJSONObject(i));
		}
		return menuEntries;
	}

	public static MenuEntry parse(InputStream in) throws JSONException, ParseException {
		return parse(Utils.inputStreamToJSON(in));
	}

	private static MenuEntry parse(JSONObject obj) throws JSONException, ParseException {
		int rating = 0;
		if (obj.has("rating")) {
			rating = 1 + (int) (obj.getDouble("rating") * 2);
		}
		return new MenuEntry.Builder()
				.name(obj.getString("name"))
				.description(Utils.JSONArrayToStringArray(obj.getJSONArray("description")))
				.preference((float) obj.getDouble("preference"))
				.id(obj.getString("_id"))
				.rating(rating)
				.build();
	}
}
