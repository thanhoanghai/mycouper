package com.nct.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	public static final int CORNER_PERCENT = 8;

	/*
	 * flag use for round image conrner
	 */
	public static final int ROUND_NONE = 0;
	public static final int ROUND_ALL = 1;
	public static final int ROUND_TOP = 2;
	public static final int ROUND_BOTTOM = 3;

	// round
	// corner----------------------------------------------------------------------------//

	/**
	 * round bitmap by roundType
	 * 
	 * @param bitmap
	 * @param roundType
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundType,
			int roundPercent) {
		Bitmap output = bitmap;

		switch (roundType) {
		case ROUND_ALL:
			output = getRoundedCornerBitmap(bitmap, roundPercent);
			break;
		case ROUND_TOP:
			output = getRoundedTopCornerBitmap(bitmap);
			break;
		case ROUND_BOTTOM:
			output = getRoundedBottomCornerBitmap(bitmap);
			break;
		}

		return output;
	}

	/**
	 * round bitmap's 4 corner
	 * 
	 * @param bitmap
	 * @return
	 */
	private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPercent) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(0xffffffff);

		final Rect rectFullSize = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		final RectF rectFFullSize = new RectF(rectFullSize);
		final float roundPx = bitmap.getWidth() * roundPercent / 100;

		canvas.drawRoundRect(rectFFullSize, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rectFullSize, rectFullSize, paint);

		return output;
	}

	/**
	 * round bitmap's 4 corner
	 * 
	 * @param bitmap
	 *            using default values of corner round percent
	 * @return
	 */
	private static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(0xffffffff);

		final Rect rectFullSize = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		final RectF rectFFullSize = new RectF(rectFullSize);
		final float roundPx = bitmap.getWidth() * CORNER_PERCENT / 100;

		canvas.drawRoundRect(rectFFullSize, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rectFullSize, rectFullSize, paint);

		return output;
	}

	/**
	 * round bitmap's 2 top corner
	 * 
	 * @param bitmap
	 * @return
	 */
	private static Bitmap getRoundedTopCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(0xffffffff);

		final Rect rectFullSize = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		final float roundPx = bitmap.getWidth() * CORNER_PERCENT / 100;
		final Rect rectFullSizeTop = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight() + (int) roundPx);
		final RectF rectFFullSizeTop = new RectF(rectFullSizeTop);

		canvas.drawRoundRect(rectFFullSizeTop, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rectFullSize, rectFullSize, paint);

		return output;
	}

	/**
	 * round bitmap's 2 bottom corner
	 * 
	 * @param bitmap
	 * @return
	 */
	private static Bitmap getRoundedBottomCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(0xffffffff);

		final Rect rectFullSize = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		final float roundPx = bitmap.getWidth() * CORNER_PERCENT / 100;
		final Rect rectFullSizeTop = new Rect(0, (int) -roundPx,
				bitmap.getWidth(), bitmap.getHeight());
		final RectF rectFFullSizeTop = new RectF(rectFullSizeTop);

		canvas.drawRoundRect(rectFFullSizeTop, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rectFullSize, rectFullSize, paint);

		return output;
	}

	// shadow----------------------------------------------------------------------------//

	public static Bitmap addShadow(Bitmap bitmap, int shadowWidtbPercent,
			int shadowRestId, Context context) {
		return addShadow(bitmap, shadowWidtbPercent,
				BitmapFactory.decodeResource(context.getResources(),
                        shadowRestId));
	}

	/**
	 * add shadow to a bitmap
	 * 
	 * @param bitmap
	 *            bitmap to add shadow
	 * @param shadowWidtbPercent
	 *            percent of input bitmap width to use for shadow
	 * @param shadowBitmap
	 *            bitmap to use as shadow
	 * @return
	 */
	public static Bitmap addShadow(Bitmap bitmap, int shadowWidtbPercent,
			Bitmap shadowBitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(0xffffffff);

		Rect bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Rect shadowRect = new Rect(0, 0, shadowBitmap.getWidth(),
				shadowBitmap.getHeight());
		int shadowWidth = bitmap.getWidth() * shadowWidtbPercent / 100;
		Rect bitmapShadowRect = new Rect(shadowWidth, shadowWidth
				* bitmap.getHeight() / bitmap.getWidth(), bitmap.getWidth()
				- shadowWidth, bitmap.getHeight() - shadowWidth
				* bitmap.getHeight() / bitmap.getWidth());

		canvas.drawBitmap(shadowBitmap, shadowRect, bitmapRect, paint);
		canvas.drawBitmap(bitmap, bitmapRect, bitmapShadowRect, paint);

		return output;
	}

	// ----------------------------------------------------------------------------//

	/**
	 * crop bitmap by ratio ratio is percent of width in compare with height ex:
	 * 25 - mean crop witdh to de 25% of height
	 * 
	 * @param ratio
	 * @return
	 */
	public static Bitmap cropBitmapByRatio(Bitmap bitmap, int ratio) {
		Bitmap output = null;

		int srcW = bitmap.getWidth();
		int srcH = bitmap.getHeight();
		int desW = srcW, desH = srcH;
		int l = 0, t = 0, r = srcW, b = srcH;
		if (srcW * 100 < srcH * ratio) {
			// crop height
			desH = desW * 100 / ratio;

			l = 0;
			r = srcW;
			t = (srcH - desH) / 2;
			b = srcH - (srcH - desH) / 2;
		} else {
			// crop width
			desW = desH * ratio / 100;

			t = 0;
			b = srcH;
			l = (srcW - desW) / 2;
			r = srcW - (srcW - desW) / 2;
		}

		output = Bitmap.createBitmap(desW, desH, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(0xffffffff);

		Rect srcRect = new Rect(l, t, r, b);
		Rect desRect = new Rect(0, 0, desW, desH);

		canvas.drawBitmap(bitmap, srcRect, desRect, paint);

		return output;
	}
	
	public static Bitmap rotate(Bitmap bitmap, int degree) {
	    int w = bitmap.getWidth();
	    int h = bitmap.getHeight();

	    Matrix mtx = new Matrix();
	    mtx.postRotate(degree);

	    return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}	
	
	/**
	 * crop bitmap by rect zone
	 * @param bitmap
	 * @param startX
	 * @param endX
	 * @param startY
	 * @param endY
	 * @return
	 */
	public static Bitmap cropBitmap( Bitmap bitmap, int startX, int endX, int startY, int endY ) {
		Bitmap output = Bitmap.createBitmap(endX - startX, endY - startY, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(0xffffffff);
		
		Rect srcRect = new Rect(startX, startY, endX, endY);
		Rect desRect = new Rect(0, 0, endX - startX, endY - startY);
		
		canvas.drawBitmap(bitmap, srcRect, desRect, paint);
		
		return output;
	}
	
	public static byte[] decodeByteArrayEffectOverLay(Bitmap tmpBitmap) {
		int offset = 0;
		byte[] result = new byte[tmpBitmap.getWidth() * tmpBitmap.getHeight()
				* 3];
		for (int i = 0; i < tmpBitmap.getHeight(); i++) {
			for (int j = 0; j < tmpBitmap.getWidth(); j++) {
				int pixelColor = tmpBitmap.getPixel(j, i);
				result[offset++] = (byte) ((pixelColor >> 16) & 0xFF);
				result[offset++] = (byte) ((pixelColor >> 8) & 0xFF);
				result[offset++] = (byte) (pixelColor & 0xFF);
			}
		}
		return result;
	}
}
