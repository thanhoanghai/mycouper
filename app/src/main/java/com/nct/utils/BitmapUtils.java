package com.nct.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.zxing.common.BitMatrix;
import com.nct.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

    public static File saveBitmapInSDCard(Context ctx, String fileName, Bitmap bmp) {
		createCouperFolder(ctx);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        File f = new File(getRootPath(ctx)
                + File.separator
                + FOLDER_NAME_MYCOUPER //parent folder
                + File.separator
                + FOLDER_NAME_INCLUDE_PHOTO //avatar folder
                + File.separator
                + fileName); //avatar name
        try {
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            // remember close de FileOutput
            fo.close();
            return f;
        } catch (IOException e) {
            return null;
        }
    }

	public static Uri addImageToGallery(Context context, String filepath, String title, String description) {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, title);
		values.put(MediaStore.Images.Media.DESCRIPTION, description);
		values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.MediaColumns.DATA, filepath);

		return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

    public static final String FOLDER_NAME_MYCOUPER = "mycouper";
    public static final String FOLDER_NAME_INCLUDE_PHOTO = "couper_photo";
    public static void createCouperFolder(Context ctx) {
        File folder = new File(getRootPath(ctx) + File.separator + FOLDER_NAME_MYCOUPER + File.separator + FOLDER_NAME_INCLUDE_PHOTO);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static String getRootPath(Context ctx) {

        String pathDir = null;
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            pathDir = Environment.getExternalStorageDirectory().getPath();
        } else {
            pathDir = ctx.getFilesDir().getPath();
        }
        return pathDir;
    }

    public static ByteArrayOutputStream getByteArrayOutputStream(Bitmap bitmap,
                                                                 int quality) {
        if (bitmap == null || quality < 0 || quality > 100)
            return null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)) {
                return bos;
            } else
                return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap scaleDownBitmap(byte[] img){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(img,0,img.length, options);
        options.inSampleSize = calculateInSampleSize(options, Constants.CB_IMAGE_WIDTH, Constants.CB_IMAGE_HEIGHT);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(img,0,img.length, options);

    }

    public static Bitmap scaleDownBitmap(String imgPath, int mWidth, int mHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        options.inSampleSize = calculateInSampleSize(options, mWidth, mHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        return calculateInSampleSize(width, height, reqWidth, reqHeight);
    }

    public static int calculateInSampleSize(int curWidth, int curHeight, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = curHeight;
        final int width =  curWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

	public static Bitmap toBitmap(BitMatrix matrix){
		int height = matrix.getHeight();
		int width = matrix.getWidth();
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
			}
		}
		return bmp;
	}
}
