package com.example.homeactivity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.homeactivity.ProductActivity.JSONTask;
import com.savagelook.android.UrlJsonAsyncTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {
	private Button button1,button2,button3,button4;
	private TextView responseText;
	private static final String TASKS_URL = "http://192.168.0.33:3000/login";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button1 = (Button)findViewById(R.id.button1);
		button2 = (Button)findViewById(R.id.button2);
		button3 = (Button)findViewById(R.id.button3);
		button4 = (Button)findViewById(R.id.button4);
		loadTasksFromAPI(TASKS_URL);
		
		button1.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent =new Intent(MainActivity.this,ProductActivity.class);
				startActivity(intent);
				
			}
		});
		button2.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent =new Intent(MainActivity.this,BreederActivity.class);
				startActivity(intent);
				
			}
		});
		button3.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent =new Intent(MainActivity.this,AddActivity.class);
				startActivity(intent);
				
			}
		});
		
		button4.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent =new Intent(MainActivity.this,ShopCartActivity.class);
				startActivity(intent);
				
			}
		});
		
	}
	
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void loadTasksFromAPI(String url) {
	    GetTasksTask getTasksTask = new GetTasksTask(MainActivity.this);
	    getTasksTask.setMessageLoading("Loading tasks...");
	    getTasksTask.execute(url);
	}

	private class GetTasksTask extends UrlJsonAsyncTask {
	    public GetTasksTask(Context context) {
	        super(context);
	    }

	    @Override
	        protected void onPostExecute(JSONObject json) {
	            try {
	               responseText.setText(json.toString());
	                
	            } catch (Exception e) {
	            Toast.makeText(context, e.getMessage(),
	                Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}
}
