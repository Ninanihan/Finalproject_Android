package com.example.homeactivity;

import com.example.picture.PostPictureActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class AddActivity extends Activity {
	private Button bt_picture,bt_add;
	protected void onCreate(Bundle savedInstanceState) {
    	LinearLayout open_layout;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		

		bt_picture = (Button) findViewById(R.id.bt_picture);
		bt_add = (Button) findViewById(R.id.bt_add);
		
		
		bt_picture.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent =new Intent(AddActivity.this,PostPictureActivity.class);
				startActivity(intent);
				
			}
		});
		
		
		bt_add.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(AddActivity.this, "Shared success", Toast.LENGTH_LONG).show();
				
			}
		});
    }	
	

}
