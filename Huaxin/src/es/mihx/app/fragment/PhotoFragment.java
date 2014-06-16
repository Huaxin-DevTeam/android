package es.mihx.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import es.mihx.app.adapter.PhotoModeAdapter;
import es.mihx.app.utils.Constants;
import es.mihx.app.R;

public class PhotoFragment extends Fragment {

	private View layout;
	private ListView lv_list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_photo, null);

		initScreen();

		prepareControls();

		return layout;
	}

	private void initScreen() {
		lv_list = (ListView) layout.findViewById(R.id.lv_list);
	}

	private void prepareControls() {

		// Ponemos los anuncios, el adapter se encarga de notificar a la activity
		PhotoModeAdapter adapter = new PhotoModeAdapter(getActivity(), Constants.getApp().getItems(), lv_list);
		lv_list.setAdapter(adapter);
	}
}
