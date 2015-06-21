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
import com.nct.utils.Pref;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class AtLanguageAdapter extends BaseAdapterApp<String> {

	public AtLanguageAdapter(Context context, ArrayList<String> list) {
		super(context, list);
		mContext = context;
		indexSelect = Pref.getIntObject(Constants.SAVE_ID_LANGUAGE,mContext);
	}

	private int indexSelect = 0;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.item_language, parent,
					false);
			holder = new ViewHolder();

			holder.tvTitle = (TextView) view.findViewById(R.id.item_language_tv_title);
			holder.radio = (ImageView) view.findViewById(R.id.item_language_img_radio);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		String item = getItem(position);
		holder.tvTitle.setText(item);

		if(indexSelect == position)
			holder.radio.setImageResource(R.drawable.icon_radio_check);
		else
			holder.radio.setImageResource(R.drawable.icon_radio);


		return view;
	}

	public void setIndexSelect(int pos)
	{
		indexSelect  = pos;
		notifyDataSetChanged();
	}

	private static class ViewHolder {
		TextView tvTitle;
		ImageView radio;
	}

	public void setIndex(int pos)
	{
		indexSelect = pos;
		notifyDataSetChanged();
	}

}
