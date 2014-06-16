package es.mihx.app.adapter;

import java.text.DecimalFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import es.mihx.app.list.CreditsList;
import es.mihx.app.model.CreditOption;
import es.mihx.app.R;

public class CreditsAdapter extends BaseAdapter {
	
	private CreditsList items;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private Context context;
	
	public CreditsAdapter(Context context, CreditsList items, ListView list){
		this.items = items;
		this.context = context;
		inflater = (LayoutInflater) context
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.listitem_credits, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.txt_name);
			holder.num_credits = (TextView)convertView.findViewById(R.id.txt_num_credits);
			holder.price = (TextView)convertView.findViewById(R.id.txt_price);
			holder.text = (TextView)convertView.findViewById(R.id.txt_text);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		final CreditOption item = items.get(position);
		
		DecimalFormat dec = new DecimalFormat("#.00 EUR");
	    String price = dec.format(item.getPrice());
		
		holder.id = item.getId();
		holder.name.setText(item.getName());
		holder.num_credits.setText(item.getNum_credits() + " " + context.getResources().getString(R.string.credits));
		holder.price.setText(price);
		holder.text.setText(item.getText());
		holder.sku = item.getSku();
		
		convertView.setTag(holder);
		convertView.postInvalidate();
		convertView.setClickable(false);
		
		convertView.invalidate();

		return convertView;
	}
	
	private static class ViewHolder {
		TextView name;
		TextView price;
		TextView num_credits;
		TextView text;
		String sku;
		int id;
	}

}
