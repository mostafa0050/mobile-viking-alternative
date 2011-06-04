package com.profete162.mvforandroid.view;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.profete162.mvforandroid.R;
import com.profete162.mvforandroid.data.Call;
import com.profete162.mvforandroid.util.AndroidUtils;
import com.profete162.mvforandroid.util.DateParser;

public class DataAdapter extends CallHistoryAdapter {

	public DataAdapter(Context context, ArrayList<Call> calls) {
		super(context, calls);
	}

	@Override
	public void fillView(View view, Call call) {
		((TextView) view.findViewById(R.id.callog_detail_voice_date))
				.setText(DateParser.getDisplayStringTime(call.getBegin()));
		((TextView) view.findViewById(R.id.callog_detail_voice_price))
				.setText("€ " + call.getPrice());
		DecimalFormat df = new DecimalFormat("#######.##");
		double data = (double) call.getData() / 1024;
		((TextView) view.findViewById(R.id.callog_detail_voice_duration))
				.setText(df.format(data) + " KB");
		((TextView) view.findViewById(R.id.callog_detail_voice_number))
				.setText(AndroidUtils.getContactNameFromNumber(context, call
						.getTo()));
		if (call.isIncoming()) {
			((ImageView) view.findViewById(R.id.callog_detail_voice_img))
					.setImageResource(R.drawable.sym_call_incoming);
		} else {
			((ImageView) view.findViewById(R.id.callog_detail_voice_img))
					.setImageResource(R.drawable.sym_call_outgoing);
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.callog_detail_voice;
	}
}
