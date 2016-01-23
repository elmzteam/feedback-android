package design.jsby.feedback.util;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Utils {
	private static final String TAG = makeLogTag(Utils.class);
	public static final String PREFS_NAME = "AUTH";
	private static final String LOG_PREFIX = "feedback_";
	private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
	private static final int MAX_LOG_TAG_LENGTH = 23;


	public static String makeLogTag(String str) {
		if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
			return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
		}

		return LOG_PREFIX + str;
	}

	public static String makeLogTag(Class cls) {
		return makeLogTag(cls.getSimpleName());
	}

	public static String getAuthKey(SharedPreferences preferences) {
		final String username, password;
		if ((username = preferences.getString("username", null)) != null &&
				(password = preferences.getString("password", null)) != null) {
			return username + ":" + password;
		}
		return null;
	}

	public static JSONObject inputStreamToJSON(InputStream inputStream) {
		try {
			final String temp = inputStreamToBufferedString(inputStream);
			if (temp == null) {
				return null;
			}
			return new JSONObject(temp);
		} catch (JSONException e) {
			Log.e(TAG, "Exception", e);
		}
		return null;
	}

	public static JSONArray inputStreamToJSONArray(InputStream inputStream) {
		try {
			final String temp = inputStreamToBufferedString(inputStream);
			if (temp == null) {
				return null;
			}
			return new JSONArray(temp);
		} catch (JSONException e) {
			Log.e(TAG, "Exception", e);
		}
		return null;
	}

	private static String inputStreamToBufferedString(InputStream inputStream) {
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			final StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (IOException e) {
			Log.e(TAG, "Exception", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String inputStreamToString(InputStream inputStream) {
		final Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : null;
	}

	public static String[] JSONArrayToStringArray(JSONArray array) throws JSONException {
		final String[] generic = new String[array.length()];
		for (int i = 0; i < generic.length; i++) {
			generic[i] = array.getString(i);
		}
		return generic;
	}

	public static boolean[] JSONArrayToBooleanArray(JSONArray array) throws JSONException {
		final boolean[] generic = new boolean[array.length()];
		for (int i = 0; i < generic.length; i++) {
			generic[i] = array.getBoolean(i);
		}
		return generic;
	}

	public static <T> String join(T[] array) {
		return join(array, ", ");
	}

	public static <T> String join(T[] array, String delim) {
		final StringBuilder sb = new StringBuilder();
		for (T t : array) {
			if (sb.length() > 0) {
				sb.append(delim);
			}
			sb.append(t);
		}
		return sb.toString();
	}
}