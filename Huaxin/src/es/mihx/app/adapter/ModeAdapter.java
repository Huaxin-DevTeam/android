package es.mihx.app.adapter;

import android.widget.BaseAdapter;

public abstract class ModeAdapter extends BaseAdapter {
	
	public interface DetailListener {
		public void enterDetail(long id);
	}
	
}
