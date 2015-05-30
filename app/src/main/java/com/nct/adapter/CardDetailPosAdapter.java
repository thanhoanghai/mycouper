/**
 * 
 */
package com.nct.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.constants.Constants;
import com.nct.fragment.FragCreateCardInfo;
import com.nct.model.CompanyObject;
import com.nct.model.PosObject;
import com.nct.mv.AtCreateCard;

import org.w3c.dom.Text;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class CardDetailPosAdapter extends BaseAdapterApp<PosObject> {

	public CardDetailPosAdapter(Context context, ArrayList<PosObject> list) {
		super(context, list);
		mContext = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.item_pos, parent,
					false);
			holder = new ViewHolder();

			holder.tvTitle = (TextView) view.findViewById(R.id.item_pos_tv_title);
			holder.tvStreet = (TextView) view.findViewById(R.id.item_pos_tv_street);
			holder.tvPhone = (TextView) view.findViewById(R.id.item_pos_tv_phone);
			holder.tvOpen = (TextView) view.findViewById(R.id.item_pos_tv_open);
			holder.tvClose = (TextView) view.findViewById(R.id.item_pos_tv_close);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		PosObject item = getItem(position);
		holder.tvTitle.setText(item.pos_name);
		if(!TextUtils.isEmpty(item.address))
			holder.tvStreet.setText(item.address);
		else
			holder.tvStreet.setText("");
		if(!TextUtils.isEmpty(item.phone))
			holder.tvPhone.setText(item.phone);
		else
			holder.tvPhone.setText("");

		holder.tvOpen.setText(mContext.getString(R.string.open_) +  item.opening_hour);
		holder.tvClose.setText(mContext.getString(R.string.close_) +  item.closing_hour);

		return view;
	}

	private static class ViewHolder {
		TextView tvTitle;
		TextView tvStreet;
		TextView tvPhone;
		TextView tvOpen;
		TextView tvClose;
	}

}
