package com.zy.utils.model;

public class Point implements Comparable<Point> {
	public float x;
	public float y;
	public int shopID;
	public int cityID;

	public Point(float x, float y, int shopID,int cityID) {
		this(x, y);
		this.shopID = shopID;
		this.cityID = cityID;
	}

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Point() {

	}

	public int compareTo(Point o) {
		return 0;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}

	public int getShopID() {
		return shopID;
	}

	public void setShopID(int shopID) {
		this.shopID = shopID;
	}

	public int getCityID() {
		return cityID;
	}

	public void setCityID(int cityID) {
		this.cityID = cityID;
	}

}
