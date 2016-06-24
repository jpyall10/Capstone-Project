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

	public Item(Context context, String name, int photo, String category){
		this.context = context;
		this.name = name;
		this.photo = photo;
		this.category = category;
	}

	public String getName(){
		return name;
	}
	public void setPhoto(int photo){
		this.photo = photo;
	}

	public int getPhoto(){
		return photo;
	}

	public String getCategory(){
		return category;
	}

	public static int getRandomItemDrawable() {
		switch (RANDOM.nextInt(5)){
			default:
			case 0:
				return R.drawable.v_face;
			case 1:
				return R.drawable.cat_1;
			case 2:
				return R.drawable.dog_1;
			case 3:
				return R.drawable.owl_1;
			case 4:
				return R.drawable.cow_1;
		}
	}

	public static int getItemDrawable(String name){
		switch(name.toLowerCase()){
			case "dog":
				return R.drawable.dog_1;
			case "cat":
				return R.drawable.cat_1;
			case "owl":
				return R.drawable.owl_1;
			case "cow":
				return R.drawable.cow_1;
			case "apple":
				return R.drawable.apple_1;
			case "banana":
				return R.drawable.banana_1;
			default:
				return R.drawable.v_face;
		}
	}

	public static final String[] sAnimals = {"cat","cow","dog","owl"};
	public static final String[] sPeople = {"mom","dad","sister","brother"};
	public static final String[] sFoods = {"apple", "banana"};

	public static final String[] sItemStrings = {
		"dog", "cat", "owl", "cow", "vera", "apple", "banana", "mom", "dad", "brother", "sister"
	};

//	public static List<Item> getItems(){
//
//		List<Item> sItems = new ArrayList<Item>();
//		sItems.add(new Item(context,R.string.animal_label_cat,R.drawable.cat_1));
//		sItems.add(new Item(context,R.string.animal_label_cow,R.drawable.cow_1));
//		sItems.add(new Item(context,R.string.animal_label_dog,R.drawable.dog_1));
//		sItems.add(new Item(context,R.string.animal_label_owl,R.drawable.owl_1));
//		sItems.add(new Item(context,R.string.person_name_label,R.drawable.v_face));
//
//		return sItems;
//
//	}

//	sItems.add(new Item(context,R.string.animal_label_cat,R.drawable.cat_1));
//	,
//					 				new Item(context,R.string.animal_label_cow,R.drawable.cow_1),
//									new Item(context,R.string.animal_label_dog,R.drawable.dog_1),
//									new Item(context,R.string.person_name_label,R.drawable.v_face)};

}