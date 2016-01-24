package design.jsby.feedback.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuEntry implements Parcelable {
	private String name;
	private float rating;
	private String description;
	private String id;

	public MenuEntry(String name, float rating, String description, String id) {
		this.name = name;
		this.rating = rating;
		this.description = description;
		this.id = id;
	}

	public static class Builder {
		private String name;
		private float rating;
		private String description;
		private String id;

		public Builder name(String s) {
			name = s;
			return this;
		}

		public Builder rating(float s) {
			rating = s;
			return this;
		}

		public Builder description(String s) {
			description = s;
			return this;
		}

		public Builder id(String s) {
			id = s;
			return this;
		}

		public MenuEntry build() {
			return new MenuEntry(name, rating, description, id);
		}
	}

	public String getName() {
		return name;
	}

	public float getRating() {
		return rating;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	protected MenuEntry(Parcel in) {
		name = in.readString();
		rating = in.readFloat();
		description = in.readString();
		id = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeFloat(rating);
		dest.writeString(description);
		dest.writeString(id);
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
