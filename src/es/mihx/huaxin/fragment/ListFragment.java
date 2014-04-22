package es.mihx.huaxin.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import es.mihx.huaxin.R;
import es.mihx.huaxin.adapter.ListModeAdapter;
import es.mihx.huaxin.utils.Constants;

public class ListFragment extends Fragment {

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

		layout = inflater.inflate(R.layout.fragment_list, null);

		initScreen();

		prepareControls();

		return layout;
	}

	private void initScreen() {
		lv_list = (ListView) layout.findViewById(R.id.lv_list);
	}

	private void prepareControls() {

		// Ponemos los anuncios
		ListModeAdapter adapter = new ListModeAdapter(getActivity(), Constants.getApp().getItems(), lv_list);
		lv_list.setAdapter(adapter);
	}
}
