package com.example.android.project7;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ItemDetailActivity extends AppCompatActivity {
	public static final String EXTRA_NAME = "item_name";
	private String name;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		Intent intent = getIntent();
		final String itemName = intent.getStringExtra(EXTRA_NAME);
		name = itemName;

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(itemName);

		loadBackdrop();
	}

	private void loadBackdrop() {
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this)
			.load(Item.getItemDrawable(name))
			.centerCrop()
			.into(imageView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.sample_actions, menu);
		return true;
	}
}
