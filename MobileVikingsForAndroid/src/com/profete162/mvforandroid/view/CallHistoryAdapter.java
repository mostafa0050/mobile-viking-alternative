package com.profete162.mvforandroid.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.profete162.mvforandroid.data.Call;

/**
 * Provides a template for the different call history detail screens.
 */
public abstract class CallHistoryAdapter extends BaseAdapter {

	protected Context context;
	protected ArrayList<Call> calls;

	public CallHistoryAdapter(Context context, ArrayList<Call> calls) {
		this.context = context;
		this.calls = calls;
	}


	public int getCount() {
		return calls.size();
	}

	
	public Call getItem(int index) {
		return calls.get(index);
	}

	
	public long getItemId(int index) {
		return index;
	}

	
	public View getView(int index, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(getLayoutId(), null);
		fillView(view, calls.get(index));
		return view;
	}

	/**
	 * Return the id of the layout to be used for this adapter. (R.layout.*)
	 */
	public abstract int getLayoutId();

	/**
	 * Fill the view for this adapter with the data from the given call.
	 */
	public abstract void fillView(View view, Call call);

}
