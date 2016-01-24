package design.jsby.feedback.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable {
	private String name;
	private String address;
	private float distance;
	private String id;
	private String categories;
	private String[] images;
	private MenuEntry[] entries;

	public Restaurant(String name, float distance, String categories, String address, String[] images, String id) {
		this.name = name;
		this.distance = distance;
		this.images = images;
		this.categories = categories;
		this.address = address;
		this.id = id;
//		this.preference = preference;
//		this.price = price;
	}

	public static class Builder {
		private String address;
		private String categories;
		private String name;
		private float distance;
		private String[] images;
		private String id;

		public Builder name(String s) {
			name = s;
			return this;
		}

		public Builder distance(float f) {
			distance = f;
			return this;
		}

		public Builder id(String i) {
			id = i;
			return this;
		}

		public Builder categories(String s) {
			categories = s;
			return this;
		}

		public Builder address(String s) {
			address = s;
			return this;
		}

		public Builder images(String[] s) {
			images = s;
			return this;
		}

		public Restaurant build() {
			return new Restaurant(name, distance, categories, address, images, id);
		}
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getDistance() {
		return (int) distance;
	}

	public String getCategories() {
		return categories;
	}

	public String getImageURL(int index) {
		return images[index];
	}

	public int getImageNum() {
		return images.length;
	}

	public boolean hasImages() {
		return images.length != 0;
	}

	public String getId() {
		return id;
	}

	public void setEntries(MenuEntry[] entries) {
		this.entries = entries;
	}

	protected Restaurant(Parcel in) {
		name = in.readString();
		distance = in.readFloat();
		address = in.readString();
		categories = in.readString();
		images = in.createStringArray();
		id = in.readString();
		entries = (MenuEntry[]) in.readParcelableArray(MenuEntry.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeFloat(distance);
		dest.writeString(address);
		dest.writeString(categories);
		dest.writeStringArray(images);
		dest.writeString(id);
		dest.writeParcelableArray(entries, 0);
	}

	public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
		@Override
		public Restaurant createFromParcel(Parcel in) {
			return new Restaurant(in);
		}

		@Override
		public Restaurant[] newArray(int size) {
			return new Restaurant[size];
		}
	};
}