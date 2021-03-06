package com.nct.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nct.model.StampQrcode;
import com.nct.mv.AtStoreDetail;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Debug;

import java.util.ArrayList;

import thh.com.mycouper.R;

/**
 * Created by nghidv on 6/29/2015.
 */
public class StampCardPagerAdapter extends PagerAdapter {

    private ArrayList<StampQrcode> listData;
    private LayoutInflater mLayoutInflater;
    private Activity mContext;
    private int bitmapWidth = 0;

    public StampCardPagerAdapter(Activity context, ArrayList<StampQrcode> listData){
        this.mContext = context;
        this.listData = listData;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        bitmapWidth = metrics.widthPixels * 6 / 10;
    }

    public void setListData(ArrayList<StampQrcode> data){
        listData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(listData!=null)
            return listData.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mLayoutInflater.inflate(R.layout.item_qrcode_pager, null);

        Debug.logError("cardpager" ,"position=" + position);

        ImageView imgv = (ImageView) view.findViewById(R.id.stores_img_qrcode);
        StampQrcode item = listData.get(position);
        if(item != null){
            String idQrcode = item.qrcode;
            imgv.setTag(idQrcode);//tag of imageView == path to image
            new LoadImage().execute(imgv);
//            try{
//
//                DisplayMetrics metrics = new DisplayMetrics();
//                mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                int bitmapWidth = metrics.widthPixels * 6 / 10;
//
//                QRCodeWriter writeCode = new QRCodeWriter();
//                BitMatrix bitMatrix = writeCode.encode(idQrcode, BarcodeFormat.QR_CODE, bitmapWidth, bitmapWidth);
//                Bitmap bmp = BitmapUtils.toBitmap(bitMatrix);
//                imgv.setImageBitmap(bmp);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((AtStoreDetail) mContext).onPagerPromotionItemClick(position, false);
            }
        });

        ((ViewPager) container).addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String idQrcode;


        @Override
        protected Bitmap doInBackground(Object... params) {
            imv = (ImageView)   params[0];
            idQrcode = imv.getTag().toString();

            Bitmap bitmap = null;
            try{
                QRCodeWriter writeCode = new QRCodeWriter();
                BitMatrix bitMatrix = writeCode.encode(idQrcode, BarcodeFormat.QR_CODE, bitmapWidth, bitmapWidth);
                bitmap = BitmapUtils.toBitmap(bitMatrix);
            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null && imv != null){
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(result);
            }else{
                imv.setVisibility(View.GONE);
            }
        }
    }
}
