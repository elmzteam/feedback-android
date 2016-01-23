package design.jsby.feedback.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuEntry implements Parcelable {
	private String imageURL;
	private String name;
	private String tags;

	public MenuEntry(String imageURL, String name, String tags) {
		this.imageURL = imageURL;
		this.name = name;
		this.tags = tags;
	}

	public static class Builder {
		private String imageURL;
		private String name;
		private String tags;

		public Builder imageURL(String s) {
			imageURL = s;
			return this;
		}

		public Builder name(String s) {
			name = s;
			return this;
		}

		public Builder tags(String s) {
			tags = s;
			return this;
		}

		public MenuEntry build() {
			return new MenuEntry(imageURL, name, tags);
		}
	}

	public String getImageURL() {
		return imageURL;
	}

	public String getName() {
		return name;
	}

	public String getTags() {
		return tags;
	}

	protected MenuEntry(Parcel in) {
		name = in.readString();
		imageURL = in.readString();
		tags = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(imageURL);
		dest.writeString(tags);
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
