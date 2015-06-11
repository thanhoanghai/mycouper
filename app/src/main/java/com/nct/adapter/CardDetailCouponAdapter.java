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
import com.nct.model.CouponObject;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class CardDetailCouponAdapter extends BaseAdapterApp<CouponObject> {

    private String linkImage = "";

    public CardDetailCouponAdapter(Context context, ArrayList<CouponObject> list , String link) {
        super(context, list);
        mContext = context;
        linkImage = link;
    }

    private int indexSelect = 0;
    private Boolean isFirst = true;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_coupon, parent,
                    false);
            holder = new ViewHolder();

            holder.tvTitle = (TextView) view.findViewById(R.id.item_coupon_tv_title);
            holder.img = (ImageView) view.findViewById(R.id.item_coupon_img);
            holder.tvExpire = (TextView) view.findViewById(R.id.item_coupon_tv_expire);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CouponObject item = getItem(position);
        if(!TextUtils.isEmpty(linkImage))
            displayImage(holder.img,linkImage);

        holder.tvTitle.setText(item.card_name);
        holder.tvExpire.setText(mContext.getString(R.string.expire_at) + item.valid_to);

        return view;
    }


    private static class ViewHolder {
        TextView tvTitle;
        TextView tvExpire;
        ImageView img;

    }

}
