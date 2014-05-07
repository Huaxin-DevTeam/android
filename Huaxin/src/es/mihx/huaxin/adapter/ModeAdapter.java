package es.mihx.huaxin.adapter;

import android.widget.BaseAdapter;

public abstract class ModeAdapter extends BaseAdapter {
	
	public interface DetailListener {
		public void enterDetail(long id);
	}
	
}
