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

import com.nct.model.CardObject;


import java.util.ArrayList;

import thh.com.mycouper.R;

public class FragMemberCardAdapter extends BaseAdapterApp<CardObject> {

	public FragMemberCardAdapter(Context context, ArrayList<CardObject> list) {
		super(context, list);
		mContext = context;
		//setDisplayImageOptions(R.drawable.store_default_topic);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.item_member_card, parent,
					false);
			holder = new ViewHolder();

			holder.imgThumb = (ImageView) view
					.findViewById(R.id.item_membercard_img);
			holder.tvTitle = (TextView) view
					.findViewById(R.id.item_membercard_tv_title);
			holder.tvDes = (TextView) view
					.findViewById(R.id.item_membercard_tv_des);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}


		return view;
	}

	private static class ViewHolder {
		ImageView imgThumb;
		TextView tvTitle;
		TextView tvDes;
	}

}