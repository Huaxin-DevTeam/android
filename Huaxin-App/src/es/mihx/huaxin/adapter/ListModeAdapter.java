package es.mihx.huaxin.adapter;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import es.mihx.huaxin.R;
import es.mihx.huaxin.activity.ListActivity;
import es.mihx.huaxin.list.ItemList;
import es.mihx.huaxin.model.Item;
import es.mihx.huaxin.utils.Constants;
import es.mihx.huaxin.utils.Utils;

public class ListModeAdapter extends ModeAdapter {

	private ItemList items;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private Context context;
	
	private DetailListener listener;
	
	private final boolean[] mHighlightedPositions;

	public ListModeAdapter(Context context, ItemList items, ListView listView) {
		this.items = items;
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		listener = (ListActivity)context;
		
		this.mHighlightedPositions = new boolean[items.size()];
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
		
		
		if(Utils.isFavorite(item.getId())) {
	        holder.fav.setBackgroundResource(R.drawable.ic_star_on);
	        mHighlightedPositions[position] = true;
	    }else {
	        holder.fav.setBackgroundResource(R.drawable.ic_star_off);
	        mHighlightedPositions[position] = false;
	    }
		
		holder.fav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.switchFavorite(item.getId());

			    // Toggle background resource
			    RelativeLayout layout = (RelativeLayout)v.getParent();
			    Button button = (Button)layout.findViewById(R.id.btn_fav);
			    if(mHighlightedPositions[position]) {
			        button.setBackgroundResource(R.drawable.ic_star_off);
			        mHighlightedPositions[position] = false;
			    }else {
			        button.setBackgroundResource(R.drawable.ic_star_on);
			        mHighlightedPositions[position] = true;
			    }
				
			}
		});
		
		holder.share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.share(context, item.getId(), item.getTitle());
			}
		});
		
		convertView.setTag(holder);
		convertView.postInvalidate();
		convertView.setClickable(false);
		
		convertView.invalidate();
		
		OnClickListener clickEnter = new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.enterDetail(item.getId());
			}
		};

		holder.img.setOnClickListener(clickEnter);
		holder.title.setOnClickListener(clickEnter);
		holder.description.setOnClickListener(clickEnter);

		
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
