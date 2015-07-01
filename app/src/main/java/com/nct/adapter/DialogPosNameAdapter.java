/**
 *
 */
package com.nct.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class DialogPosNameAdapter extends BaseAdapterApp<String> {

    public DialogPosNameAdapter(Context context, ArrayList<String> list) {
        super(context, list);
        mContext = context;
    }

    private int indexSelect = 0;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_pos_name, parent,
                    false);
            holder = new ViewHolder();

            holder.tvTitle = (TextView) view.findViewById(R.id.item_country_title);
            holder.radio = (ImageView) view.findViewById(R.id.item_country_img_radio);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String item = getItem(position);
        holder.tvTitle.setText(item);

        if (indexSelect == position)
            holder.radio.setBackgroundResource(R.drawable.icon_radio_check);
        else
            holder.radio.setBackgroundResource(R.drawable.icon_radio);

        return view;
    }

    public void setIndexSelect(int pos) {
        indexSelect = pos;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView tvTitle;
        ImageView img;
        ImageView radio;
    }

}
