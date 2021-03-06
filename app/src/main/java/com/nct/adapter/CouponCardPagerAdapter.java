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
import com.nct.model.CouponCategory;
import com.nct.mv.AtStoreDetail;
import com.nct.utils.BitmapUtils;

import java.util.ArrayList;

import thh.com.mycouper.R;

/**
 * Created by nghidv on 6/29/2015.
 */
public class CouponCardPagerAdapter extends PagerAdapter {

    private ArrayList<CouponCategory> listData;
    private LayoutInflater mLayoutInflater;
    private Activity mContext;
    private int bitmapWidth = 0;

    public CouponCardPagerAdapter(Activity context, ArrayList<CouponCategory> listData){
        this.mContext = context;
        this.listData = listData;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        bitmapWidth = metrics.widthPixels * 6 / 10;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mLayoutInflater.inflate(R.layout.item_qrcode_pager, null);

        ImageView imgv = (ImageView) view.findViewById(R.id.stores_img_qrcode);
        CouponCategory item = listData.get(position);
        if(item != null){
            String idQrcode = item.qrcode_id;
            imgv.setTag(idQrcode);//tag of imageView == path to image
            new LoadImage().execute(imgv);
//            try{
//                String idQrcode = item.qrcode_id;
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
                ((AtStoreDetail) mContext).onPagerPromotionItemClick(position, true);
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
