package com.example.homeactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adapter.ShopCartListViewAdapter;
import com.example.adapter.ShopCartListViewAdapter.CheckInterface;
import com.example.adapter.ShopCartListViewAdapter.ModifyCountInterface;
import com.example.entity.BaseInfo;
import com.example.entity.GroupInfo;
import com.example.entity.ProductInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ShopCartActivity extends Activity implements ModifyCountInterface, CheckInterface, OnClickListener {
	private ListView listView;
	private ShopCartListViewAdapter sclva;
	private List<Map<String, Object>> list=new ArrayList<>();//源数据列表
	private List<GroupInfo> groups = new ArrayList<>();//组元素列表，对源数据列表进行二次选择封装，便于多选框选择变化时的操作
	private Map<String, List<ProductInfo>> children = new HashMap<>();//子元素列表，对源数据列表进行二次选择封装，便于多选框选择变化时的操作
	
	private CheckBox cb_check_all;
	private TextView tv_total_price;
	private TextView tv_delete;
	private TextView tv_go_to_pay;
	private Context context;

	private double totalPrice = 0.00;//总价
	private int totalCount = 0;//购买的商品种类

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shop_cart);
		initView();
		initEvents();
	}

	private void initView() {
		context = this;
		listView = (ListView) findViewById(R.id.listView);
		cb_check_all = (CheckBox) findViewById(R.id.cb_check_all);
		tv_total_price = (TextView) findViewById(R.id.tv_total_price);
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_go_to_pay = (TextView) findViewById(R.id.tv_go_to_pay);
	}

	private void initEvents() {
		virtualData();
		sclva = new ShopCartListViewAdapter(list, this);
		sclva.setCheckInterface(this);//设置多选框接口
		sclva.setModifyCountInterface(this);//设置改变数量接口
		listView.setAdapter(sclva);
		cb_check_all.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
		tv_go_to_pay.setOnClickListener(this);
	}

	/** 模拟数据*/
	private void virtualData() {
		String[] groupNames = new String[] { "Bulldog", "Labrador"};
		String[] groupIds = new String[] { "shop1", "shop2" };
		for (int i = 0; i < groupNames.length; i++) {
			Map<String, Object> group = new HashMap<>();
			GroupInfo ginfo = new GroupInfo();
			ginfo.setName(groupNames[i]);
			ginfo.setId(groupIds[i]);
			group.put(ShopCartListViewAdapter.SHOPCART_TYPE, ShopCartListViewAdapter.SHOPCART_FLAG_GROUP);
			group.put(ShopCartListViewAdapter.SHOPCART_DATA, ginfo);
			list.add(group);
			groups.add(ginfo);

			List<ProductInfo> childs = new ArrayList<>();
			for (int j = 0; j <= i; j++) {
				Map<String, Object> product = new HashMap<>();
				ProductInfo pinfo = new ProductInfo();
				pinfo.setName("Dog" + (j + 1));
				pinfo.setDesc(groupNames[i] );
				pinfo.setPrice(199 + 50 * j);
				pinfo.setCount(1);
				product.put(ShopCartListViewAdapter.SHOPCART_PARENT_ID, groupIds[i]);
				product.put(ShopCartListViewAdapter.SHOPCART_PARENT_POSITION, i);
				product.put(ShopCartListViewAdapter.SHOPCART_TYPE, ShopCartListViewAdapter.SHOPCART_FLAG_CHILDREN);
				product.put(ShopCartListViewAdapter.SHOPCART_DATA, pinfo);
				list.add(product);
				childs.add(pinfo);
			}
			children.put(groupIds[i], childs);
		}
	}

	@Override
	public void checkGroup(int position, boolean isChecked) {
		Map<String, Object> parent = list.get(position);
		String parentId = ((GroupInfo) (parent.get(ShopCartListViewAdapter.SHOPCART_DATA))).getId();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			String child_parentId = (String) map.get(ShopCartListViewAdapter.SHOPCART_PARENT_ID);
			if (parentId.equals(child_parentId)) {
				ProductInfo pinfo = (ProductInfo) map.get(ShopCartListViewAdapter.SHOPCART_DATA);
				pinfo.setChoosed(isChecked);
			}
		}

		boolean allGroupSameState = true;
		for (int i = 0; i < groups.size(); i++) {
			if (isChecked != groups.get(i).isChoosed()) {
				allGroupSameState = false;
				break;
			}
		}
		if (allGroupSameState) {
			cb_check_all.setChecked(isChecked);
		} else {
			cb_check_all.setChecked(false);
		}
		sclva.notifyDataSetChanged();//刷新界面
		calculateAll();//更新总价数量
	}

	@Override
	public void checkChild(int position, boolean isChecked) {
		Map<String, Object> child = list.get(position);
		int parentPosition = (int) child.get(ShopCartListViewAdapter.SHOPCART_PARENT_POSITION);
		GroupInfo parent = groups.get(parentPosition);
		List<ProductInfo> childs = children.get(parent.getId());
		boolean allChildSameState = true;
		for (int i = 0; i < childs.size(); i++) {
			if (childs.get(i).isChoosed() != isChecked) {
				allChildSameState = false;
				break;
			}
		}
		if (allChildSameState) {
			parent.setChoosed(isChecked);
		} else {
			parent.setChoosed(false);
		}

		boolean allGroupSameState = true;
		boolean firstState = groups.get(0).isChoosed();
		for (int i = 0; i < groups.size(); i++) {
			if (firstState != groups.get(i).isChoosed()) {
				allGroupSameState = false;
				break;
			}
		}
		if (allGroupSameState) {
			cb_check_all.setChecked(firstState);
		} else {
			cb_check_all.setChecked(false);
		}
		sclva.notifyDataSetChanged();//刷新界面
		calculateAll();//更新总价数量
	}

	@Override
	public void doIncrease(int position, View showCountView, boolean isChecked) {	
		if (isChecked) {
			Map<String, Object> map = list.get(position);
			ProductInfo product = (ProductInfo) map.get(ShopCartListViewAdapter.SHOPCART_DATA);
			int currentCount = product.getCount();
			currentCount++;
			product.setCount(currentCount);
			((TextView)showCountView).setText(currentCount + "");
			sclva.notifyDataSetChanged();//刷新界面
			calculateAll();//更新总价数量
		}
	}

	@Override
	public void doDecrease(int position, View showCountView, boolean isChecked) {		
		if(isChecked){		
			Map<String, Object> map = list.get(position);
			ProductInfo product = (ProductInfo) map.get(ShopCartListViewAdapter.SHOPCART_DATA);
			int currentCount = product.getCount();
			currentCount--;
			if (currentCount < 0) {
				currentCount = 0;
			}
			product.setCount(currentCount);
			((TextView)showCountView).setText(currentCount + "");
			sclva.notifyDataSetChanged();//刷新界面
			calculateAll();//更新总价数量
		}	
	}

	/** 统计操作 */
	private void calculateAll() {
		totalCount=0;
		totalPrice=0.00;
		for (int i = 0; i < groups.size(); i++) {
			GroupInfo group = groups.get(i);
			List<ProductInfo> childs = children.get(group.getId());
			for (int j = 0; j < childs.size(); j++) {
				ProductInfo product = childs.get(j);
				if (product.isChoosed()) {
					totalCount++;
					totalPrice += product.getCount() * product.getPrice();
				}
			}
		}

		tv_total_price.setText("€" + totalPrice);
		tv_go_to_pay.setText("Checkout(" + totalCount + ")");
	}

	/** 全选与反选 */
	private void doCheckAll() {
		for (int i = 0; i < groups.size(); i++) {
			groups.get(i).setChoosed(cb_check_all.isChecked());
			List<ProductInfo> childs = children.get(groups.get(i).getId());
			for (int j = 0; j < childs.size(); j++) {
				childs.get(j).setChoosed(cb_check_all.isChecked());
			}
		}
		sclva.notifyDataSetChanged();//刷新界面
		calculateAll();//更新总价数量
	}
	
	/**删除操作
	 * 注意：执行删除操作时，不可边遍历边删除，因为极有可能触发数组越界的错误<br>
	 * 这里是在遍历的时候将要删除的元素放进相应的列表容器中，遍历完后，执行removeAll的方法来进行删除*/
	private void doDelete(){
		List<Map<String, Object>> toBeDelete=new ArrayList<>();
		List<GroupInfo> toBeDeleteGroups=new ArrayList<>();
		
		for(int i=0;i<groups.size();i++){
			GroupInfo group=groups.get(i);
			List<ProductInfo> childs=children.get(group.getId());
			if(group.isChoosed()){
				toBeDeleteGroups.add(group);		
			}		
			List<ProductInfo> toBeDeleteProducts=new ArrayList<>();
			for(int j=0;j<childs.size();j++){
				if(childs.get(j).isChoosed()){
					toBeDeleteProducts.add(childs.get(j));
				}
			}
			childs.removeAll(toBeDeleteProducts);//删除子元素列表
		}
		groups.removeAll(toBeDeleteGroups);//删除组元素列表
		
		//再次遍历，删除源数据列表
		for(int i=0;i<list.size();i++){
			//由于两种Item都继承BaseInfo,所以这里造型为BaseInfo，能比较方便地查看选中状态
			BaseInfo info=(BaseInfo) list.get(i).get(ShopCartListViewAdapter.SHOPCART_DATA);
			if(info.isChoosed()){
				toBeDelete.add(list.get(i));
			}
		}
		list.removeAll(toBeDelete);
		
		sclva.notifyDataSetChanged();//刷新界面
		calculateAll();//更新总价数量
	}

	@Override
	public void onClick(View v) {
		AlertDialog alert;
		switch (v.getId()) {
		case R.id.cb_check_all:
			doCheckAll();
			break;
		case R.id.tv_delete:
			if(totalCount==0){
				Toast.makeText(context, "Choose what you want to remove", Toast.LENGTH_LONG).show();
				return;
			}
			alert=new AlertDialog.Builder(context).create();
			alert.setTitle("Note");
			alert.setMessage("Are you sure to remove these items?");
			alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "Submit",new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doDelete();
				}
			});
			alert.show();
			break;
		case R.id.tv_go_to_pay:
			if(totalCount==0){
				Toast.makeText(context, "Choose what you want to pay", Toast.LENGTH_LONG).show();
				return;
			}
			alert=new AlertDialog.Builder(context).create();
			alert.setTitle("Note");
			alert.setMessage("Total:\n"+totalCount+"items\n"+totalPrice+"euro");
			alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "Submit",new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alert.show();
			break;
		}

	}
}
