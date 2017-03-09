package com.emc.adgoal.smartlock;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.emc.adgoal.smartlock.R;

import java.util.List;

public class PhoneListAdapter extends ArrayAdapter<PhoneNumber> {

	private List<PhoneNumber> items;
	private int layoutResourceId;
	private Context context;

	public PhoneListAdapter(Context context, int layoutResourceId, List<PhoneNumber> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PhoneListHolder holder = null;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new PhoneListHolder();
		holder.PhoneNumber = items.get(position);
		holder.removeUserButton = (ImageButton)row.findViewById(R.id.removeNumber);
		holder.removeUserButton.setTag(holder.PhoneNumber);

		holder.number = (TextView)row.findViewById(R.id.mobileNumber);

		row.setTag(holder);

		setupItem(holder);
		return row;
	}

	private void setupItem(PhoneListHolder holder) {
		holder.number.setText(holder.PhoneNumber.getPhoneNumber());
	}

	public static class PhoneListHolder {
		PhoneNumber PhoneNumber;
		TextView number;
		ImageButton removeUserButton;
	}
}