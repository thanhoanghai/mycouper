/**
 * 
 */
package com.nct.adapter;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.constants.Constants;
import com.nct.model.CountryObject;
import com.nct.model.PosObject;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class DialogNationAdapter extends BaseAdapterApp<CountryObject> {

	public DialogNationAdapter(Context context, ArrayList<CountryObject> list) {
		super(context, list);
		mContext = context;
	}

	private int indexSelect = 0;
	private Boolean isFirst = true;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.item_country, parent,
					false);
			holder = new ViewHolder();

			holder.tvTitle = (TextView) view.findViewById(R.id.item_country_title);
			holder.img = (ImageView) view.findViewById(R.id.item_country_img);
			holder.radio = (ImageView) view.findViewById(R.id.item_country_img_radio);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		CountryObject item = getItem(position);
		holder.tvTitle.setText(item.country_name);

		if(item.country_code.equals("0"))
		{
			holder.img.setImageResource(R.drawable.icon_border_grey1);
		}else if(!TextUtils.isEmpty(item.country_flag))
		{
			displayImage(holder.img,item.country_flag);
		}

		if(isFirst)
		{
			if(item.country_id.equals(Constants.ID_COUNTRY_DEFAUL_VIETNAME))
			{
				holder.radio.setBackgroundResource(R.drawable.icon_radio_check);
				isFirst = false;
				indexSelect = position;
			}else
				holder.radio.setBackgroundResource(R.drawable.icon_radio);
		}else
		{
			if(indexSelect == position)
				holder.radio.setBackgroundResource(R.drawable.icon_radio_check);
			else
				holder.radio.setBackgroundResource(R.drawable.icon_radio);
		}
		return view;
	}

	public void setIndexSelect(int pos)
	{
		indexSelect  = pos;
		notifyDataSetChanged();
	}

	public String getIDCountry(int pos)
	{
		CountryObject item = getItem(pos);
		return item.country_id;
	}

	public String getFlagCountry(int pos)
	{
		CountryObject item = getItem(pos);
		return item.country_flag;
	}


	private static class ViewHolder {
		TextView tvTitle;
		ImageView img;
		ImageView radio;
	}

}
