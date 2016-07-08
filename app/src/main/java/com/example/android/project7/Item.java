package com.example.android.project7;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Item {

	private String name;
	private String category;
	private int photo;
	private static Context context;

	private static final Random RANDOM = new Random();

	public Item(Context context, String name, int photo, String category) {
		this.context = context;
		this.name = name;
		this.photo = photo;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setPhoto(int photo) {
		this.photo = photo;
	}

	public int getPhoto() {
		return photo;
	}

	public String getCategory() {
		return category;
	}
}