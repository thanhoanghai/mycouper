/**
 *
 */
package com.nct.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.constants.Constants;
import com.nct.model.CountryObject;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class AtAskAdapter extends BaseAdapterApp<String> {

	public AtAskAdapter(Context context, ArrayList<String> list) {
		super(context, list);
		mContext = context;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.item_ask, parent,
					false);
			holder = new ViewHolder();

			holder.tvTitle = (TextView) view.findViewById(R.id.item_ask_tv_title);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

        holder.tvTitle.setText("Question " + position);

		return view;
	}

	private static class ViewHolder {
		TextView tvTitle;
	}

}
