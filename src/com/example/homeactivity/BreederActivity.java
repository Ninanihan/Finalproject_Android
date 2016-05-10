package com.example.homeactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BreederActivity extends Activity {
	private ListView lvBreeders;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_breeder);
		ImageView iv_breeder = (ImageView)findViewById(R.id.iv_breeder);

		iv_breeder.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				new JSONTask().execute("http://3953615f.ngrok.io/breeders.json");
				
			}
		});
		
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
	    
	    .build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())

	    .defaultDisplayImageOptions(defaultOptions)
	    .build();
		ImageLoader.getInstance().init(config);
		
		
		lvBreeders = (ListView)findViewById(R.id.lvBreeders);
		//new JSONTask().execute("http://661857a3.ngrok.io/products.json");
    }						
			
	
	public class JSONTask extends AsyncTask<String, String, List<BreederModel> >{

		@Override
		protected List<BreederModel> doInBackground(String... params){
			HttpURLConnection connection = null;
			BufferedReader reader = null;
			
			try {
				URL url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();				
				InputStream stream = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(stream));				
				StringBuffer buffer = new StringBuffer();				
				String line = "";			
				while ((line = reader.readLine()) != null){
					buffer.append(line);
				}
				
				String finalJson = buffer.toString();
                JSONArray parentArray = new JSONArray(finalJson);
                List<BreederModel> breederModelList = new ArrayList<BreederModel>();
                
                for (int i=0; i<parentArray.length(); i++) {
                	JSONObject finalObject = parentArray.getJSONObject(i);
                	BreederModel breederModel = new BreederModel();
                	breederModel.setName(finalObject.getString("name")); 
                	breederModel.setBreeds(finalObject.getString("breeds")); 
                	breederModel.setCity(finalObject.getString("city"));
                	breederModel.setGender(finalObject.getInt("gender"));
                	breederModel.setAge(finalObject.getString("age"));
                	breederModel.setImageurl(finalObject.getString("imageurl"));
                	//adding the final object in the list
                    breederModelList.add(breederModel);
				   
			   }
				
				return breederModelList;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				if(connection != null){
				    connection.disconnect();
				}
				try {
					if(reader !=null){
						reader.close();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			return null;
		}
		protected void onPostExecute(List<BreederModel> result){
			super.onPostExecute(result);
			
			BreederAdapter adapter = new BreederAdapter(getApplicationContext(), R.layout.row2, result);
		    lvBreeders.setAdapter(adapter);
		}
	}
	
	public class BreederAdapter extends ArrayAdapter{
		
		private List<BreederModel> breederModelList;
		private int resource;
		private LayoutInflater inflater;
		public BreederAdapter(Context context, int resource, List<BreederModel> objects){
			super(context, resource, objects);
			breederModelList = objects;
			this.resource = resource;
			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			
			if (convertView == null){
				holder = new ViewHolder();
				convertView = inflater.inflate(resource, null);
				holder.ivBreeder = (ImageView)convertView.findViewById(R.id.ivBreeder);
				holder.tvName = (TextView)convertView.findViewById(R.id.tvName);
				holder.tvBreeds1 = (TextView)convertView.findViewById(R.id.tvBreeds1);
				
				holder.tvCity1 = (TextView)convertView.findViewById(R.id.tvCity1);
				holder.tvGender1 = (TextView)convertView.findViewById(R.id.tvGender1);
				holder.tvAge1 = (TextView)convertView.findViewById(R.id.tvAge1);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}

			
			final ProgressBar progressBar2 = (ProgressBar)convertView.findViewById(R.id.progressBar2);
			
			
			//Display image
			ImageLoader.getInstance().displayImage(breederModelList.get(position).getImageurl(), holder.ivBreeder, new ImageLoadingListener() {
			    public void onLoadingStarted(String imageUri, View view) {
			    	progressBar2.setVisibility(View.VISIBLE);
			    }
			    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			    	progressBar2.setVisibility(View.GONE); 
			    }
			    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			    	progressBar2.setVisibility(View.GONE);
			    }
			    public void onLoadingCancelled(String imageUri, View view) {
			    	progressBar2.setVisibility(View.GONE);
			    }
			 
			    
			});
			
			holder.tvName.setText(breederModelList.get(position).getName());
			holder.tvBreeds1.setText("Breeds:" + breederModelList.get(position).getBreeds());			
			holder.tvCity1.setText("City:" + breederModelList.get(position).getCity());
			holder.tvGender1.setText("Gender:" + breederModelList.get(position).getGender());
			holder.tvAge1.setText("Age:" + breederModelList.get(position).getAge());
			
			return convertView;
		}
		
		
		class ViewHolder{
			private ImageView ivBreeder;
			private TextView tvName;
			private TextView tvBreeds1;
			
			private TextView tvCity1;
			private TextView tvGender1;
			private TextView tvAge1;
			
		}
	}
	

}
