package com.nct.background;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.nct.cache.CacheUtils;
import com.nct.customview.RoundedDrawable;
import com.nct.utils.BitmapUtils;

import thh.com.mycouper.R;

/**
 * Lớp này sẽ xử lý việc load hình ảnh cho toàn ứng dụng, việc xử lý tập trung
 * lại đây để có thể tránh việc chạy quá nhiều thread cùng lúc cho việc load
 * ảnh, đồng thời có thể sắp xếp độ ưu tiên thứ tự hoặc ngưng việc load nhưng
 * ảnh ko cần thiết nữa.
 * 
 * Lưu ý AsyncTask tuy hỗ trợ việc thực thi các tác vụ ngầm khá tốt nhưng có
 * những giới hạn nhất định như số lượng task cho một ứng dụng, queue cho những
 * task cần thực thi... và khi số lượng task quá tải (thường là trên 10 task) sẽ
 * có những hiện tượng task bị treo, leaking.
 * 
 * Vì đã đưa về đây xử lý tập trung nên sẽ sử dụng thread và handler thay vì sử
 * dụng AsyncTask vì như thế việc kiểm soát queue và thread sẽ chủ động hơn
 * 
 * @author Nghi Do
 */
public class ImageLoader {

	private static final int READ_SIZE = 4096;

	/**
	 * Thread pool dùng cho việc tải ảnh, vì việc tải ảnh có thể sẽ thay đổi thứ
	 * tự các ảnh cần tải hoặc hủy bớt những ảnh ko cần thiết nữa nên dùng
	 * thread kết hợp với một list để xử lý, dùng handler sẽ không đáp ứng được
	 */
	private static final int CORE_DOWNLOAD_POOL_SIZE = 4;
	private static final int MAXIMUM_DOWNLOAD_POOL_SIZE = 4;
	private static final int KEEP_ALIVE_TIME = 3;
	private final BlockingQueue<Runnable> mDownloadTaskQueue;
	private final ThreadPoolExecutor mDownloadThreadPool;

	/**
	 * Thread pool dùng cho việc thực hiện decode ảnh
	 */
	private static final int CORE_DECODE_POOL_SIZE = Runtime.getRuntime()
			.availableProcessors();
	private final BlockingQueue<Runnable> mDecodeTaskQueue;
	private final ThreadPoolExecutor mDecodeThreadPool;

	/**
	 * Handler này dùng cho việc xử lý kết quả của việc tải và decode ảnh trên
	 * main thread, ví dụ như load ảnh vào ImageView
	 */
	private Handler mUIThreadHandler;

	/**
	 * Danh sách các url đang chờ download
	 */
	private ArrayList<String> mWaitingDownloadURLs = new ArrayList<String>();

	/**
	 * Danh sách các url đang chờ decode
	 */
	private ArrayList<String> mWaitingDecodeURLs = new ArrayList<String>();

	/**
	 * Danh sách các url đang được xử lý
	 */
	private ArrayList<String> mProcessingURLs = new ArrayList<String>();

	/**
	 * Bảng mapping giữa một url và những imageview đăng ký hiển thị ảnh load từ
	 * url này
	 */

	// Other threads may cause this to be a null pointer exception
	static final HashMap<String, ArrayList<ImageCallback>> mReferenceImageCallbacks = new HashMap<String, ArrayList<ImageCallback>>();

	/**
	 * Bảng lưu thông tin ImageView nào hiện đang chờ load url nào, vì mỗi
	 * ImageView chỉ có thể chọn một URL duy nhất là url đăng ký gần nhất nên
	 * dùng cái này để quản lý
	 */
	private WeakHashMap<ImageView, String> mImageViews = new WeakHashMap<ImageView, String>();

	private Context mAppContext;

	private static final Object mLock = new Object();
	private static ImageLoader mInstance;

	private Bitmap shadowBitmapDefault;
	
	/**
	 * memory cache bitmap, using a key to indexed bitmap insert to LRUCACHE
	 *
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	public static ImageLoader getInstance(Context context) {
		synchronized (mLock) {
			if (mInstance == null) {
				mInstance = new ImageLoader(context.getApplicationContext());
			}
			return mInstance;
		}
	}
	
	
	/**
	 * get default instant by application context
	 * maybe return null
	 * @return
	 */
	public static ImageLoader getInstance() {		
		return mInstance;		
	}

	private ImageLoader(Context context) {
		mAppContext = context;
		mUIThreadHandler = new Handler(context.getMainLooper());

		/* Init download thread pool */
		mDownloadTaskQueue = new LinkedBlockingQueue<Runnable>();
		mDownloadThreadPool = new ThreadPoolExecutor(CORE_DOWNLOAD_POOL_SIZE,
				MAXIMUM_DOWNLOAD_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				mDownloadTaskQueue);
		mDownloadThreadPool.allowCoreThreadTimeOut(true);

		/* Init decode thread pool */
		mDecodeTaskQueue = new LinkedBlockingQueue<Runnable>();
		mDecodeThreadPool = new ThreadPoolExecutor(CORE_DECODE_POOL_SIZE,
				CORE_DECODE_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				mDecodeTaskQueue);
		mDecodeThreadPool.allowCoreThreadTimeOut(true);
		
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	private class DownloadRunnable implements Runnable {
		private final String mUrl;

		public DownloadRunnable(String url) {
			mUrl = url;
		}

		@Override
		public void run() {
			// kiểm tra lại cache một lần nữa
			// TODO tạm thời chưa implement mem-cache vì OOM vẫn
			// chưa được xử lý <Tran Vu Tat Binh>
			File file = CacheUtils.getCacheFileByUrl(
                    CacheUtils.CACHE_TYPE_IMAGE, mUrl);
			if (!file.exists()) {
				// Chưa có trong file cache --> tải về
				downloadImage(mUrl, file);
			}

			synchronized (mProcessingURLs) {
				// Giờ thì file đã được tải về
				if (file.exists()) {
					// tải xong rồi giờ chạy bên decode thread pool
					if (mWaitingDecodeURLs.isEmpty()
							&& mDecodeTaskQueue.isEmpty()) {
						DecodeRunnable decodeRunnable = new DecodeRunnable(mUrl);
						mDecodeThreadPool.execute(decodeRunnable);
					} else {
						mWaitingDecodeURLs.add(mUrl);
					}
				}

				if (!mWaitingDownloadURLs.isEmpty()) {
					String url = mWaitingDownloadURLs.remove(0);
					mProcessingURLs.add(url);
					DownloadRunnable downloadRunnable = new DownloadRunnable(
							url);
					mDownloadThreadPool.execute(downloadRunnable);
				}
			}

			CacheUtils.validateCache(CacheUtils.CACHE_TYPE_IMAGE);
		}
	};

	private class DecodeRunnable implements Runnable {
		private final String mUrl;

		public DecodeRunnable(String url) {
			mUrl = url;
		}

		@Override
		public void run() {
			ArrayList<ImageCallback> callbacks = null;

			synchronized (mReferenceImageCallbacks) {
				if (mReferenceImageCallbacks.containsKey(mUrl)) {
					callbacks = new ArrayList<ImageCallback>(
							mReferenceImageCallbacks.get(mUrl));
				}

			}

			if (callbacks != null && callbacks.size() > 0) {
				for (ImageCallback icb : callbacks) {
					if (icb.imageView.get() != null) {
						File file = CacheUtils.getCacheFileByUrl(
								CacheUtils.CACHE_TYPE_IMAGE, mUrl);
						decodeImageAndSet(file, icb.shadowPercent, icb.shadowResourceId, icb.roundType, icb.roundPercent, icb.outputWidth, icb.ratio, icb.imageView, mUrl);
					} else if (icb.callback.get() != null) {
						File file = CacheUtils.getCacheFileByUrl(
								CacheUtils.CACHE_TYPE_IMAGE, mUrl);
						decodeImageAndCallback(file, icb.shadowPercent, icb.shadowResourceId, icb.roundType, icb.roundPercent, icb.outputWidth, icb.ratio, icb.callback, mUrl);
					}
				}
			}

			synchronized (mProcessingURLs) {
				mProcessingURLs.remove(mUrl);

				if (!mWaitingDecodeURLs.isEmpty()) {
					String url = mWaitingDecodeURLs.remove(0);
					mProcessingURLs.add(url);
					DecodeRunnable downloadRunnable = new DecodeRunnable(url);
					mDecodeThreadPool.execute(downloadRunnable);
				}
			}
		}
	}

	
	/**
	 * decode image and apply options
	 * @param file
	 * @param shadowPercent
	 * @param roundType
	 * @param outputWidth
	 * @param ratio
	 * @return
	 */
	private Bitmap decodeImage(File file, int shadowPercent, int shadowResId, int roundType, int roundPercent, int outputWidth, int ratio) {
		Bitmap bitmap = null;
		try {
			InputStream stream = new FileInputStream(file);

			if (outputWidth > 0) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;

				// Decode tạm để lấy thông tin tỉ lệ kích thước ảnh
				BitmapFactory.decodeStream(stream, null, options);
				stream.close();
				int originalWidth = options.outWidth;
				options = new BitmapFactory.Options();
				// tính tỉ lệ kích thước cần decode
				options.inSampleSize = Math.round((float) originalWidth
						/ (float) outputWidth);
				stream = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(stream, null, options);
			} else {
				bitmap = BitmapFactory.decodeStream(stream);
			}
									
			//crop image by ratio
			if( ratio > 0 ) {
				bitmap = BitmapUtils.cropBitmapByRatio(bitmap, ratio);
			}
			
			/*
			 * scale down bitmap to expected width
			 * only scale down - not scale up
			 */			
			if (outputWidth > 0 && bitmap.getWidth() > outputWidth) {
				int outHeight = (int) (outputWidth * (bitmap.getHeight() / (float) bitmap.getWidth()));
				bitmap = Bitmap.createScaledBitmap(bitmap, outputWidth, outHeight, true);
			}
			
			//round corner
			if( roundType != BitmapUtils.ROUND_NONE && shadowPercent > 0) {
				bitmap = BitmapUtils.getRoundedCornerBitmap(bitmap, roundType, roundPercent);
			}
			
			/*
			 * load default vertical bitmap to be used as shadow
			 * TODO add support custom bitmap			
			 */
			if (shadowPercent > 0 && shadowBitmapDefault == null) {				
				shadowBitmapDefault = BitmapFactory.decodeResource(mAppContext.getResources(), R.drawable.bg_icon_effect);
			}

			//add shadow to result bitmap			
			if( shadowPercent > 0 ) {
				if( shadowResId > 0 && shadowResId != R.drawable.bg_icon_effect ) {
					bitmap = BitmapUtils.addShadow(bitmap, shadowPercent, 
							BitmapFactory.decodeResource(mAppContext.getResources(), shadowResId));
				} else {
					//use default vertical shadow bitmap
					bitmap = BitmapUtils.addShadow(bitmap, shadowPercent, shadowBitmapDefault);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bitmap;
	}
	
	
	/**
	 * Decode ảnh, kèm hiệu ứng và đưa vào imageView để hiển thị
	 * @param file
	 * @param shadowPercent
	 * @param round
	 * @param roundTopOnly
	 * @param border
	 * @param darker
	 * @param outputWidth
	 * @param ratio percent of width in compare with height, ex: 25 - mean crop width to be 25% of height 
	 * @param imageViewRef
	 * @param url
	 */
	private void decodeImageAndSet(File file, final int shadowPercent, int shadowResId, final int roundType, final int roundPercent, int outputWidth, int ratio, final WeakReference<ImageView> imageViewRef,
			final String url) {

		try {			
			final Bitmap resultBitmap = decodeImage(file, shadowPercent, shadowResId, roundType, roundPercent, outputWidth, ratio);			
			mUIThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (mReferenceImageCallbacks) {
						ArrayList<ImageCallback> callbacks = null;
						if (mReferenceImageCallbacks.containsKey(url)) {
							callbacks = mReferenceImageCallbacks.get(url);
						}
						if (callbacks != null && callbacks.size() > 0) {
							for (ImageCallback icb : callbacks) {
								ImageView imageView = icb.imageView.get();
								if (imageView != null
										&& imageView == imageViewRef.get()) {
									if( roundType != BitmapUtils.ROUND_NONE && shadowPercent == 0 && resultBitmap != null) {
										RoundedDrawable resultRounded = new RoundedDrawable(resultBitmap);
										resultRounded = resultRounded.setCornerRadius(roundPercent);
										imageView.setImageDrawable(resultRounded);
									}
									else{
										if(resultBitmap != null)
											imageView.setImageBitmap(resultBitmap);
									}
									mImageViews.remove(imageView);
									callbacks.remove(icb);
									break;
								}
							}
						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decode ảnh, kèm hiệu ứng và đưa vào imageView để hiển thị
	 */
	private void decodeImageAndCallback(File file, int shadowPercent, int shadowResId, int roundType, int roundPercent, int outputWidth, int ratio, 
			final WeakReference<OnImageLoadingFinishListener> callbackRef,
			final String url) {

		try {
			final Bitmap resultBitmap = decodeImage(file, shadowPercent, shadowResId, roundType, roundPercent, outputWidth, ratio);
			mUIThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (mReferenceImageCallbacks) {
						ArrayList<ImageCallback> callbacks = null;
						if (mReferenceImageCallbacks.containsKey(url)) {
							callbacks = mReferenceImageCallbacks.get(url);
						}
						if (callbacks != null && callbacks.size() > 0) {
							for (ImageCallback icb : callbacks) {
								OnImageLoadingFinishListener cb = icb.callback
										.get();
								if (cb != null && cb == callbackRef.get()) {
									cb.onLoadingFinished(url, resultBitmap);
									callbacks.remove(icb);
									break;
								}
							}
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO chưa optimized kích thước ảnh trước khi lưu xuống file, để tìm hiểu
	 * thêm những màn hình khác dùng ảnh này ở những kích thước nào rồi sẽ tìm
	 * cách tính best-optimized size khi lưu xuống file - <Tran Vu Tat Binh>
	 */
	private void downloadImage(String urlStr, File targetFile) {
		InputStream byteStream = null;

		try {
			HttpURLConnection httpConn = (HttpURLConnection) new URL(urlStr)
					.openConnection();
			byteStream = httpConn.getInputStream();
			int contentSize = httpConn.getContentLength();

			if (-1 == contentSize) {
				byte[] tempBuffer = new byte[READ_SIZE];
				int bufferLeft = tempBuffer.length;
				int bufferOffset = 0;
				int readResult = 0;

				outer: do {
					while (bufferLeft > 0) {
						readResult = byteStream.read(tempBuffer, bufferOffset,
								bufferLeft);
						if (readResult < 0) {
							break outer;
						}
						bufferOffset += readResult;
						bufferLeft -= readResult;
					}

					bufferLeft = READ_SIZE;
					int newSize = tempBuffer.length + READ_SIZE;

					byte[] expandedBuffer = new byte[newSize];
					System.arraycopy(tempBuffer, 0, expandedBuffer, 0,
							tempBuffer.length);
					tempBuffer = expandedBuffer;
				} while (true);

				// Ghi file
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(tempBuffer, 0, bufferOffset);
				fos.flush();
				fos.close();
			} else {
				byte[] byteBuffer = new byte[contentSize];
				int remainingLength = contentSize;
				int bufferOffset = 0;

				while (remainingLength > 0) {
					int readResult = byteStream.read(byteBuffer, bufferOffset,
							remainingLength);
					if (readResult < 0) {
						throw new EOFException();
					}
					bufferOffset += readResult;
					remainingLength -= readResult;
				}

				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(byteBuffer, 0, contentSize);
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			Log.e("ImageLoader",
					"error loading file from url " + e.getMessage());
		} finally {
			if (null != byteStream) {
				try {
					byteStream.close();
				} catch (Exception e) {
					Log.e("ImageLoader",
							"error closing byte stream " + e.getMessage());
				}
			}
		}
	}
		
	/**
	 * Load ảnh xong thì gọi callback chứ ko gắn vào ImageView nào cả
	 * @param imageView
	 * @param url
	 * @param outputWidth expected image output width, using for scaling
	 * @param roundType see values on BitmapUtils ex: ROUND_NONE, ROUND_ALL, ROUND_TOP
	 * @param shadowPercent percent of width will use for drawing shadow
	 * @param ratio width percent in compare to height
	 * ex: 25 mean width will be crop to be 25% of height
	 * @param callback
	 */
	public void loadImageCallback(String url, int outputWidth, int roundType, int shadowPercent, int ratio,
			OnImageLoadingFinishListener callback) {
		//call loadImageCallback with default shadow vertical resource
		loadImageCallback(url, outputWidth, roundType, BitmapUtils.CORNER_PERCENT, shadowPercent, 0, ratio, callback);
	}	
	
	/**
	 * Load ảnh xong thì gọi callback chứ ko gắn vào ImageView nào cả
	 * @param imageView
	 * @param url
	 * @param outputWidth expected image output width, using for scaling
	 * @param roundType see values on BitmapUtils ex: ROUND_NONE, ROUND_ALL, ROUND_TOP
	 * @param shadowPercent percent of width will use for drawing shadow
	 * @param shadowResId resource id use as shadow bitmap - default is R.drawable.kard_shadow_default_vertical
	 * @param ratio width percent in compare to height
	 * ex: 25 mean width will be crop to be 25% of height
	 * @param callback
	 */
	public void loadImageCallback(String url, int outputWidth, int roundType, int roundPercent, int shadowPercent, int shadowResId, int ratio,
			OnImageLoadingFinishListener callback) {

		/* Trước hết kiểm tra url có hợp lệ hay không */
		if (!checkUrl(url)) {
			return;
		}

		synchronized (mReferenceImageCallbacks) {
			// Đưa callback vào danh sách liên quan với url này
			ArrayList<ImageCallback> imageViews = mReferenceImageCallbacks
					.get(url);
			if (imageViews == null) {
				imageViews = new ArrayList<ImageCallback>();
			}
			imageViews.add(new ImageCallback(null, outputWidth, roundType, roundPercent, shadowPercent, shadowResId, ratio, callback));
			mReferenceImageCallbacks.put(url, imageViews);

			// TODO
			synchronized (mProcessingURLs) {
				if (!mProcessingURLs.contains(url)) {
					if (mWaitingDownloadURLs.contains(url)) {
						mWaitingDownloadURLs.remove(url);
						mWaitingDownloadURLs.add(0, url);
					} else if (mWaitingDecodeURLs.contains(url)) {
						mWaitingDecodeURLs.remove(url);
						mWaitingDecodeURLs.add(0, url);
					} else {
						// Nên ktra file cache trước
						File file = CacheUtils.getCacheFileByUrl(
								CacheUtils.CACHE_TYPE_IMAGE, url);

						if (!file.exists()) {
							if (mWaitingDownloadURLs.isEmpty()
									&& mDownloadTaskQueue.isEmpty()) {
								mProcessingURLs.add(url);
								DownloadRunnable downloadRunnable = new DownloadRunnable(
										url);
								mDownloadThreadPool.execute(downloadRunnable);
							} else {
								mWaitingDownloadURLs.add(0, url);
							}
						} else {
							if (mWaitingDecodeURLs.isEmpty()
									&& mDecodeTaskQueue.isEmpty()) {
								mProcessingURLs.add(url);
								DecodeRunnable decodeRunnable = new DecodeRunnable(
										url);
								mDecodeThreadPool.execute(decodeRunnable);
							} else {
								mWaitingDecodeURLs.add(0, url);
							}
						}
					}
				}
			} // end synchronized (mProcessingURLs)
		} // end synchronized (mReferenceImageCallbacks)
	}
	
	/**
	 * Đăng ký load một ảnh với url tương ứng vào imageView
	 * @param imageView
	 * @param url
	 * @param outputWidth expected image output width, using for scaling
	 * @param roundType see values on BitmapUtils ex: ROUND_NONE, ROUND_ALL, ROUND_TOP
	 * @param shadowPercent percent of width will use for drawing shadow
	 * @param ratio width percent in compare to height
	 * ex: 25 mean width will be crop to be 25% of height
	 * @param defaultImageId default resource to use while waiting for loading
	 */
	public void loadImage(final ImageView imageView, String url, int outputWidth, int roundType, 
			int shadowPercent, int ratio, final int defaultImageId) {
		//call loadImage iwth default shadow vertical resource
		loadImage(imageView, url, outputWidth, roundType, BitmapUtils.CORNER_PERCENT, shadowPercent, 0, ratio, defaultImageId);
	}
	/*
	* load image with dynamic of corner percent
	* using for gift kard, etc ...
	*
	*/
	public void loadImageCorner(final ImageView imageView, String url, int outputWidth, int roundType, int roundPercent,
			int shadowPercent, int shadowResId, int ratio, final int defaultImageId) {
		//call loadImage iwth default shadow vertical resource
		loadImage(imageView, url, outputWidth, roundType, roundPercent, shadowPercent, shadowResId, ratio, defaultImageId);
	}
	
	/**
	 * Đăng ký load một ảnh với url tương ứng vào imageView
	 * @param imageView
	 * @param url
	 * @param outputWidth expected image output width, using for scaling
	 * @param roundType see values on BitmapUtils ex: ROUND_NONE, ROUND_ALL, ROUND_TOP
	 * @param shadowPercent percent of width will use for drawing shadow
	 * @param shadowResId resource id use as shadow bitmap - default is R.drawable.kard_shadow_default_vertical
	 * @param ratio width percent in compare to height
	 * ex: 25 mean width will be crop to be 25% of height
	 * @param defaultImageId default resource to use while waiting for loading
	 */
	synchronized
	public void loadImage(final ImageView imageView, String url, int outputWidth, int roundType, int roundPercent,
			int shadowPercent, int shadowResId, int ratio, final int defaultImageId) {

		if (!checkUrl(url)) {
			return;
		}

		synchronized (mReferenceImageCallbacks) {
			/*
			 * Thêm imageView này vào danh sách mapping với url này
			 */
			String prevUrl = mImageViews.put(imageView, url);

			// Xử lý các mapping giữa url và imageview
			if (!url.equals(prevUrl)) {
				if (prevUrl != null) {
					// Remove cái liên kết giữa url cũ và imageview này
					ArrayList<ImageCallback> imageViews = mReferenceImageCallbacks
							.get(prevUrl);
					if (imageViews != null && imageViews.size() > 0) {
						for (int i = imageViews.size() - 1; i >= 0; i--) {
							ImageCallback icb = imageViews.get(i);
							if (icb.imageView.get() == null) {
								imageViews.remove(i);
							} else if (icb.imageView.get() == imageView) {
								imageViews.remove(i);
								break;
							}
						}
					}
				}

				// Đưa imageview vào danh sách liên quan với url này
				ArrayList<ImageCallback> imageViews = mReferenceImageCallbacks
						.get(url);
				if (imageViews == null) {
					imageViews = new ArrayList<ImageCallback>();
				}
				imageViews.add(new ImageCallback(imageView, outputWidth,
						roundType, roundPercent, shadowPercent, shadowResId, ratio, null));
				mReferenceImageCallbacks.put(url, imageViews);
			}

			synchronized (mProcessingURLs) {
				if (!mProcessingURLs.contains(url)) {
					if (mWaitingDownloadURLs.contains(url)) {
						mWaitingDownloadURLs.remove(url);
						mWaitingDownloadURLs.add(0, url);
					} else if (mWaitingDecodeURLs.contains(url)) {
						mWaitingDecodeURLs.remove(url);
						mWaitingDecodeURLs.add(0, url);
					} else {
						// Nên ktra file cache trước
						File file = CacheUtils.getCacheFileByUrl(
								CacheUtils.CACHE_TYPE_IMAGE, url);

						if (!file.exists()) {
							if (mWaitingDownloadURLs.isEmpty()
									&& mDownloadTaskQueue.isEmpty()) {
								mProcessingURLs.add(url);
								DownloadRunnable downloadRunnable = new DownloadRunnable(
										url);
								mDownloadThreadPool.execute(downloadRunnable);
							} else {
								mWaitingDownloadURLs.add(0, url);
							}
						} else {
							if (mWaitingDecodeURLs.isEmpty()
									&& mDecodeTaskQueue.isEmpty()) {
								mProcessingURLs.add(url);
								DecodeRunnable decodeRunnable = new DecodeRunnable(
										url);
								mDecodeThreadPool.execute(decodeRunnable);
							} else {
								mWaitingDecodeURLs.add(0, url);
							}
						}
					}
				}
			} // end synchronized (mProcessingURLs)
		} // end synchronized (mReferenceImageCallbacks)

		// TODO nếu có trong mem cache thì ko set hình này
		if (defaultImageId > 0) {
			imageView.setImageResource(defaultImageId);
		}
	}

	/**
	 * Hoãn việc load ảnh với url này
	 */
	public void cancel(String url) {
		synchronized (mWaitingDownloadURLs) {
			mWaitingDownloadURLs.remove(url);
		}

		synchronized (mReferenceImageCallbacks) {
			mReferenceImageCallbacks.remove(url);
		}
	}

	/**
	 * Hoãn việc load ảnh vào imageView này
	 */
	public void cancel(ImageView imageView) {
		String url = null;
		synchronized (mReferenceImageCallbacks) {
			url = mImageViews.remove(imageView);
			if (url != null) {
				ArrayList<ImageCallback> imageViews = mReferenceImageCallbacks
						.get(url);
				if (imageViews != null && imageViews.size() > 0) {
					for (int i = imageViews.size() - 1; i >= 0; i--) {
						ImageCallback icb = imageViews.get(i);
						if (icb.imageView.get() == imageView) {
							imageViews.remove(i);
						}
					}
				}
			}
		}
	}

	private boolean checkUrl(String url) {
		if (url == null || url.isEmpty()) {
			for (int i = 0; i < 5; i++)
				Log.e("ImageLoader",
						"Why do you ask me to load image from a null url???");
			return false;
		}
		return true;
	}

	/**
	 * Với mỗi ảnh hiện thị trong ứng dụng này còn kèm theo những effect khác
	 * nhau nên ở đây tạo một lớp wrapper để kèm thêm những thông tin về các
	 * effect tương ứng với một ảnh cần hiển thị lên imageview
	 * 
	 * @author <Tran Vu Tat Binh>
	 * 
	 */
	static class ImageCallback {
		final WeakReference<ImageView> imageView;
		final int shadowPercent;
		final int shadowResourceId;//resource to use as shadow
		final int roundType;		
		final int roundPercent;		
		final int outputWidth;
		final int ratio;
		final WeakReference<OnImageLoadingFinishListener> callback;

		public ImageCallback(ImageView imageView, int outputWidth,
				int roundType, int roundPercent, int shadowPercent, int shadowResId, int ratio,
				OnImageLoadingFinishListener callback) {
			this.imageView = new WeakReference<ImageView>(imageView);
			this.shadowPercent = shadowPercent;
			this.shadowResourceId = shadowResId;
			this.roundType = roundType;			
			this.roundPercent = roundPercent;			
			this.outputWidth = outputWidth;
			this.ratio = ratio;
			this.callback = new WeakReference<OnImageLoadingFinishListener>(callback);
		}
	}

	public static interface OnImageLoadingFinishListener {
		void onLoadingFinished(String url, Bitmap resultBitmap);
	}
	
	/* function to get and set bitmap from mem cache*/
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
}
