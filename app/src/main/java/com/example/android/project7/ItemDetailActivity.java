package com.example.android.project7;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ItemDetailActivity extends AppCompatActivity {
	public static final String EXTRA_NAME = "item_name";
	private String mName;
	private FloatingActionButton fab;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		Intent intent = getIntent();
		final String itemName = intent.getStringExtra(EXTRA_NAME);
		mName = itemName;

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(itemName);

		//Button one = (Button) this.findViewById(R.id.button1);
		final MediaPlayer mp = MediaPlayer.create(this, R.raw.dogs_barking);

		fab = (FloatingActionButton)findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mp.start();
			}
		});

		loadBackdrop();
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//							 Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.activity_detail, container, false);
//
//		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//		CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//		collapsingToolbar.setTitle(mName);
//
//		loadBackdrop();
//		//mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
//		//mPhotoView = (DraweeView) mRootView.findViewById(R.id.image_backdrop);
//		//mPhotoView.setTransitionName(getString(R.string.transition_image) + mPosition);
//
//		//mStatusBarColorDrawable = new ColorDrawable(0);
//
//		view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				//startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
//				//		.setType("text/plain")
//				//		.setText("Some sample text")
//				//		.getIntent(), getString(R.string.action_share)));
//			}
//		});
//
//		//bindViews();
//		//updateStatusBar();
//		return view;
//	}


	private void loadBackdrop() {
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this)
			.load(Item.getItemDrawable(mName))
			.centerCrop()
			.into(imageView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.sample_actions, menu);
		return true;
	}
}
