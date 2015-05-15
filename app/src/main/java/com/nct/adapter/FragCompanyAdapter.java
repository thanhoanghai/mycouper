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
import com.nct.fragment.FragCreateCardInfo;
import com.nct.model.CompanyObject;
import com.nct.model.MemberCardObject;
import com.nct.mv.AtCreateCard;

import java.lang.reflect.Array;
import java.util.ArrayList;

import thh.com.mycouper.R;

public class FragCompanyAdapter extends BaseAdapterApp<CompanyObject> {

	private String filter = "";
	private ArrayList<CompanyObject> saveList;

	public FragCompanyAdapter(Context context, ArrayList<CompanyObject> list) {
		super(context, list);
		mContext = context;
		saveList = new ArrayList<CompanyObject>();
		saveList.addAll(list);
		//setDisplayImageOptions(R.drawable.store_default_topic);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.item_company, parent,
					false);
			holder = new ViewHolder();

			holder.imgThumb = (ImageView) view
					.findViewById(R.id.item_company_img);
			holder.tvTitle = (TextView) view
					.findViewById(R.id.item_company_tvtitle);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		CompanyObject item = getItem(position);
		displayImage(holder.imgThumb,item.company_logo);
		holder.tvTitle.setText(item.company_name);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard)mContext).changeFragment( Constants.TYPE_CREATE_CARD_INFO,new FragCreateCardInfo());
            }
        });

		return view;
	}

	private static class ViewHolder {
		ImageView imgThumb;
		TextView tvTitle;
	}

	public void setFilter(String temp)
	{
		filter = temp;
		if(!TextUtils.isEmpty(filter))
		{
			clearData();
			ArrayList<CompanyObject> tempList = new ArrayList<CompanyObject> ();
			for(CompanyObject item : saveList)
			{
				if(item.company_name.toLowerCase().contains(filter.toLowerCase()))
				{
					tempList.add(item);
				}
			}
			addData(tempList);
			notifyDataSetChanged();
		}else
			addData(saveList);
	}

}
