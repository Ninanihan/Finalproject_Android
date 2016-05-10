package com.example.homeactivity;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class WelcomeActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

//through the function Timer, realize the activity skip
        new Timer().schedule(new TimerTask() {
        	public void run() {
        		startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
        		finish();
        		}
        	}, 2000);//waiting time 1000=1s
        }
}