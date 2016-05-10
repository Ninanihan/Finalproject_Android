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
	
	public class ProductActivity extends Activity {
		
		private ListView lvProducts;
       
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_sale);
			ImageView iv_product = (ImageView)findViewById(R.id.iv_product);
			
			iv_product.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					new JSONTask().execute("http://3953615f.ngrok.io/products.json");
					
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
			
			
			lvProducts = (ListView)findViewById(R.id.lvProducts);
			//new JSONTask().execute("http://661857a3.ngrok.io/products.json");
	    }						
				

		
		public class JSONTask extends AsyncTask<String, String, List<ProductModel> >{

			@Override
			protected List<ProductModel> doInBackground(String... params){
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
	                List<ProductModel> productModelList = new ArrayList<ProductModel>();
	                
	                for (int i=0; i<parentArray.length(); i++) {
	                	JSONObject finalObject = parentArray.getJSONObject(i);
	                	ProductModel productModel = new ProductModel();
	                	productModel.setBreeds(finalObject.getString("breeds")); 
	                	productModel.setPrice(finalObject.getInt("price"));
	                	productModel.setCity(finalObject.getString("city"));
	                	productModel.setGender(finalObject.getInt("gender"));
	                	productModel.setAge(finalObject.getString("age"));
	                	productModel.setImageurl(finalObject.getString("imageurl"));
	                	//adding the final object in the list
	                    productModelList.add(productModel);
					   
				   }
					
					return productModelList;
					
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
			protected void onPostExecute(List<ProductModel> result){
				super.onPostExecute(result);
				
				ProductAdapter adapter = new ProductAdapter(getApplicationContext(), R.layout.row1, result);
			    lvProducts.setAdapter(adapter);
			}
		}
		
		public class ProductAdapter extends ArrayAdapter{
			
			private List<ProductModel> productModelList;
			private int resource;
			private LayoutInflater inflater;
			public ProductAdapter(Context context, int resource, List<ProductModel> objects){
				super(context, resource, objects);
				productModelList = objects;
				this.resource = resource;
				inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				
			}
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder holder = null;
				
				if (convertView == null){
					holder = new ViewHolder();
					convertView = inflater.inflate(resource, null);
					holder.ivProduct = (ImageView)convertView.findViewById(R.id.ivProduct);
					holder.tvBreeds = (TextView)convertView.findViewById(R.id.tvBreeds);
					holder.tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
					holder.tvCity = (TextView)convertView.findViewById(R.id.tvCity);
					holder.tvGender = (TextView)convertView.findViewById(R.id.tvGender);
					holder.tvAge = (TextView)convertView.findViewById(R.id.tvAge);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}

				
				final ProgressBar progressBar1 = (ProgressBar)convertView.findViewById(R.id.progressBar1);
				
				
				//Display image
				ImageLoader.getInstance().displayImage(productModelList.get(position).getImageurl(), holder.ivProduct, new ImageLoadingListener() {
				    public void onLoadingStarted(String imageUri, View view) {
				    	progressBar1.setVisibility(View.VISIBLE);
				    }
				    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				    	progressBar1.setVisibility(View.GONE); 
				    }
				    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				    	progressBar1.setVisibility(View.GONE);
				    }
				    public void onLoadingCancelled(String imageUri, View view) {
				    	progressBar1.setVisibility(View.GONE);
				    }
				 
				    
				});
				
				holder.tvBreeds.setText(productModelList.get(position).getBreeds());
				holder.tvPrice.setText("Price:"+ productModelList.get(position).getPrice());
				holder.tvCity.setText("City:" + productModelList.get(position).getCity());
				holder.tvGender.setText("Gender:" + productModelList.get(position).getGender());
				holder.tvAge.setText("Age:" + productModelList.get(position).getAge());
				
				return convertView;
			}
			
			
			class ViewHolder{
				private ImageView ivProduct;
				private TextView tvBreeds;
				private TextView tvPrice;
				private TextView tvCity;
				private TextView tvGender;
				private TextView tvAge;
				
			}
		}
		
		
		
		
		 

		
	}


