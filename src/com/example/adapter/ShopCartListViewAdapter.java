package com.example.adapter;

import java.util.List;
import java.util.Map;

import com.example.entity.GroupInfo;
import com.example.entity.ProductInfo;
import com.example.homeactivity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * ListView的购物车适配器
 * @author chenjunsen
 * 2015年8月18日下午3:56:22
 */
public class ShopCartListViewAdapter extends BaseAdapter{
	private List<Map<String, Object>> list;//源数据列表
	private Context context;
	private ModifyCountInterface modifyCountInterface;//自定义的改变商品数量的接口
	private CheckInterface checkInterface;//自定义的复选框状态变化的接口
		
	public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
		this.modifyCountInterface = modifyCountInterface;
	}

	public void setCheckInterface(CheckInterface checkInterface) {
		this.checkInterface = checkInterface;
	}

	/**
	 * 构造函数
	 * @param list
	 * @param context
	 */
	public ShopCartListViewAdapter(List<Map<String, Object>> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("CutPasteId")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		/*整体思路与普通ListView的适配器思路大致相同，不过，在绘制View的时候，根据Item的类型进行选择性绘制*/
		int type=getItemViewType(position);//获取Item的类型代码，代码自己定义，具体参照本适配器的末尾描述
		switch (type) {
		case SHOPCART_FLAG_GROUP:
			final GroupHolder gholder;
			if(convertView==null){
				gholder=new GroupHolder();
				
				gholder.cb_check=(CheckBox) convertView.findViewById(R.id.cb_check);
				
				convertView.setTag(gholder);
			}else{
				gholder=(GroupHolder) convertView.getTag();
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Object> group=(Map<String, Object>) getItem(position);
			final GroupInfo groupinfo=(GroupInfo) group.get(SHOPCART_DATA);//根据绝对位置获取组元素
			if(groupinfo!=null){
				gholder.tv_group_name.setText(groupinfo.getName());
				/*设置组选框的点击事件
				 * 1.给对应的组元素设置选中与否的属性(内在的，实质的选中与否)
				 * 2.将相关的组元素信息通过自定义接口暴露出去，这里根据需求，暴露了绝对位置position和组选框的选中与否状态(视图上的选中与否)*/
				gholder.cb_check.setOnClickListener(new OnClickListener() {		
					@Override
					public void onClick(View v) {
						groupinfo.setChoosed(gholder.cb_check.isChecked());
						checkInterface.checkGroup(position, gholder.cb_check.isChecked());
					}
				});
				gholder.cb_check.setChecked(groupinfo.isChoosed());//将实质的选中与否与视图的相结合，当对适配器进行notifyDataSetChanged操作时会有明显的效果
			}
			break;

		case SHOPCART_FLAG_CHILDREN:
			final ChildrenHolder cholder;
			if(convertView==null){
				cholder=new ChildrenHolder();
				convertView=View.inflate(context, R.layout.item_shopcart_product, null);
				cholder.cb_check=(CheckBox) convertView.findViewById(R.id.cb_check);
				cholder.iv_pic=(ImageView) convertView.findViewById(R.id.iv_pic);
				cholder.tv_product_name=(TextView) convertView.findViewById(R.id.tv_product_name);
				cholder.tv_product_desc=(TextView) convertView.findViewById(R.id.tv_product_desc);
				cholder.tv_price=(TextView) convertView.findViewById(R.id.tv_price);
				
				
				convertView.setTag(cholder);
			}else{
				cholder=(ChildrenHolder) convertView.getTag();
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> children=(Map<String, Object>) getItem(position);
			final ProductInfo productInfo=(ProductInfo) children.get(SHOPCART_DATA);
			if(productInfo!=null){
				cholder.tv_product_name.setText(productInfo.getName());
				cholder.tv_product_desc.setText(productInfo.getDesc());
				cholder.tv_price.setText("￥"+productInfo.getPrice());
				cholder.tv_count.setText(productInfo.getCount()+"");//setText里不能放int等数值类型，否则运行时报错
				/*描述参照组选框*/
				cholder.cb_check.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View v) {
						productInfo.setChoosed(cholder.cb_check.isChecked());
						checkInterface.checkChild(position, cholder.cb_check.isChecked());
					}
				});
				/*增加数量按钮*/
				cholder.iv_increase.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View v) {
						boolean isChecked=cholder.cb_check.isChecked();
						modifyCountInterface.doIncrease(position, cholder.tv_count, isChecked);
					}
				});
				/*减少数量按钮*/
				cholder.iv_decrease.setOnClickListener(new OnClickListener() {		
					@Override
					public void onClick(View v) {
						boolean isChecked=cholder.cb_check.isChecked();
						modifyCountInterface.doDecrease(position, cholder.tv_count, isChecked);
					}
				});
				cholder.cb_check.setChecked(productInfo.isChoosed());
			}
			break;
		}
		return convertView;
	}

	/*获取ListView的Item类型代号*/
	@Override
	public int getItemViewType(int position) {
		Map<String, Object> item=list.get(position);
		return (Integer) item.get(SHOPCART_TYPE);
	}
	/*获取ListView的Item的种类数量*/
	@Override
	public int getViewTypeCount() {
		return 2;//本适配器只有组和子两种元素的Item，所以返回2
	}
	/**
	 * 组元素绑定器
	 * @author chenjunsen
	 * 2015年8月17日下午11:14:47
	 */
	private class GroupHolder{
		CheckBox cb_check;
		TextView tv_group_name;
	}
	/**
	 * 子元素绑定器
	 * @author chenjunsen
	 * 2015年8月17日下午11:14:30
	 */
	private class ChildrenHolder{
		CheckBox cb_check;
		ImageView iv_pic;
		TextView tv_product_name;
		TextView tv_product_desc;
		TextView tv_price;
		ImageView iv_increase;
		TextView tv_count;
		ImageView iv_decrease;
	}
	
	/**
	 * 改变商品数量的接口
	 * @author chenjunsen
	 * 2015年8月16日上午11:50:19
	 */
	public interface ModifyCountInterface{
		/**
		 * 增加操作
		 * @param position 商品的绝对位置(在整个源数据list中的位置)
		 * @param showCountView 用于展示操作后数量的view
		 * @param isChecked 是否被选中
		 */
		public void doIncrease(int position,View showCountView,boolean isChecked);
		/**
		 * 减少操作
		 * @param position 商品的绝对位置(在整个源数据list中的位置)
		 * @param showCountView 用于展示操作后数量的view
		 * @param isChecked 是否被选中
		 */
		public void doDecrease(int position,View showCountView,boolean isChecked);
	}
	
	/**
	 * 复选框操作接口
	 * @author chenjunsen
	 * 2015年8月16日上午11:50:01
	 */
	public interface CheckInterface{
		/**
		 * 组选框选中与否
		 * @param position 组元素的绝对位置(在整个源数据list中的位置)
		 * @param isChecked 组选框选中状态
		 */
		public void checkGroup(int position,boolean isChecked);
		/**
		 * 子选框选中与否
		 * @param position 子元素的绝对位置(在整个源数据list中的位置)
		 * @param isChecked 子选框选中状态
		 */
		public void checkChild(int position,boolean isChecked);
	}
	
	
	public static final int SHOPCART_FLAG_GROUP=0;
	public static final int SHOPCART_FLAG_CHILDREN=1;
	
	public static final String SHOPCART_TYPE="type";
	
	public static final String SHOPCART_DATA="data";
	
	public static final String SHOPCART_PARENT_ID="parent_id";
	
	public static final String SHOPCART_PARENT_POSITION="parent_position";

}
