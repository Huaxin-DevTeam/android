package es.mihx.huaxin.adapter;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import es.mihx.huaxin.R;
import es.mihx.huaxin.list.ItemList;
import es.mihx.huaxin.model.Item;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.Utils;

public class ListModeAdapter extends BaseAdapter {

	private ItemList items;
	private ListView listView;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private Context context;

	public ListModeAdapter(Context context, ItemList items, ListView listView) {
		this.items = items;
		this.listView = listView;
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.listitem_listmode, parent, false);
			holder = new ViewHolder();
			holder.img = (ImageView)convertView.findViewById(R.id.img_item);
			holder.title = (TextView)convertView.findViewById(R.id.txt_item_title);
			holder.category = (TextView)convertView.findViewById(R.id.txt_item_category);
			holder.location = (TextView)convertView.findViewById(R.id.txt_item_location);
			holder.description = (TextView)convertView.findViewById(R.id.txt_item_description);
			holder.price = (TextView)convertView.findViewById(R.id.txt_item_price);
			holder.call = (Button)convertView.findViewById(R.id.btn_call);
			holder.fav = (Button)convertView.findViewById(R.id.btn_fav);
			holder.share = (Button)convertView.findViewById(R.id.btn_share);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		final Item item = items.get(position);
		
		UrlImageViewHelper.setUrlDrawable(holder.img, Constants.BASEURL + item.getImageUrl());
		holder.title.setText(item.getTitle());
		holder.category.setText(Constants.getApp().getCategories().getName(item.getCategoryId()).toUpperCase(Locale.US));
		holder.location.setText(item.getLocation());
		holder.description.setText(item.getDescription());
		holder.price.setText(Utils.numberFormat(item.getPrice()) + "€");
		
		holder.call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = "tel:" + item.getPhone();
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse(uri));
				context.startActivity(intent);
			}
		});
		
		convertView.setTag(holder);
		convertView.postInvalidate();

		convertView.setClickable(true);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listView.requestFocusFromTouch();
				listView.setSelection(position);
			}
		});

		
		return convertView;
	}

	private static class ViewHolder {
		ImageView img;
		TextView title;
		TextView category;
		TextView location;
		TextView description;
		TextView price;
		Button call;
		Button fav;
		Button share;
	}

}
