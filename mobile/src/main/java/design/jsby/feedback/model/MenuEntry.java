package design.jsby.feedback.model;

import android.os.Parcel;
import android.os.Parcelable;

import design.jsby.feedback.util.Utils;

public class MenuEntry implements Parcelable {
	private String name;
	private float preference;
	private String[] description;
	private String id;
	private Rating rating;
	public enum Rating {
		NONE, DOWN, OK, UP;
		public static final Rating[] ratings = values();
	}

	public MenuEntry(String name, float preference, String[] description, String id, Rating rating) {
		this.name = name;
		this.preference = preference;
		this.description = description;
		this.id = id;
		this.rating = rating;
	}

	public static class Builder {
		private String name;
		private float preference;
		private String[] description;
		private String id;
		private Rating rating;

		public Builder name(String s) {
			name = s;
			return this;
		}

		public Builder preference(float f) {
			preference = f;
			return this;
		}

		public Builder description(String[] s) {
			description = s;
			return this;
		}

		public Builder id(String s) {
			id = s;
			return this;
		}

		public Builder rating(int i) {
			rating = Rating.ratings[i];
			return this;
		}

		public MenuEntry build() {
			return new MenuEntry(name, preference, description, id, rating);
		}
	}

	public String getName() {
		return name;
	}

	public float getPreference() {
		return preference;
	}

	public String getDescription() {
		return Utils.join(description);
	}

	public String[] getDescriptionArray() {
		return description;
	}

	public String getId() {
		return id;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating r) {
		rating = r;
	}

	protected MenuEntry(Parcel in) {
		name = in.readString();
		preference = in.readFloat();
		description = in.createStringArray();
		id = in.readString();
		rating = Rating.ratings[in.readInt()];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeFloat(preference);
		dest.writeStringArray(description);
		dest.writeString(id);
		dest.writeInt(rating.ordinal());
	}

	public static final Parcelable.Creator<MenuEntry> CREATOR = new Parcelable.Creator<MenuEntry>() {
		@Override
		public MenuEntry createFromParcel(Parcel in) {
			return new MenuEntry(in);
		}

		@Override
		public MenuEntry[] newArray(int size) {
			return new MenuEntry[size];
		}
	};
}
