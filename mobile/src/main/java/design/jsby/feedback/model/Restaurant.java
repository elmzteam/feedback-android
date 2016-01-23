package design.jsby.feedback.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable {
	private String name;
	private String address;
	private float distance;
	private int rating;
	private int price;
	private MenuEntry[] entries;

	public Restaurant(String name, float distance, int rating, int price) {
		this.name = name;
		this.distance = distance;
		this.rating = rating;
		this.price = price;
	}

	public static class Builder {
		private String name;
		private float distance;
		private int rating;
		private int price;

		public Builder() {
		}

		public Builder name(String s) {
			name = s;
			return this;
		}

		public Builder distance(float f) {
			distance = f;
			return this;
		}

		public Builder rating(int i) {
			rating = i;
			return this;
		}

		public Builder price(int i) {
			price = i;
			return this;
		}

		public Restaurant build() {
			return new Restaurant(name, distance, rating, price);
		}
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public float getDistance() {
		return distance;
	}

	public void setEntries(MenuEntry[] entries) {
		this.entries = entries;
	}

	protected Restaurant(Parcel in) {
		name = in.readString();
		distance = in.readFloat();
		rating = in.readInt();
		price = in.readInt();
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
		dest.writeInt(rating);
		dest.writeInt(price);
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