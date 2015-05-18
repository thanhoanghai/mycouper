package com.nct.background;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hỗ trợ thực thi các task ở thread khác với UI thread
 * 
 * @author Tran Vu Tat Binh
 * 
 */
public class TaskExecuter {
	public static final String TAG = "TaskExecuter";
	
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_BLOCKING = 1;

	private static final Object mLock = new Object();
	private static TaskExecuter mInstance;

	// Chưa dùng tới nên tạm thời comment lại
	// private Context mAppContext;

	/* Thread pool dành cho normal task */
	private static final int CORE_NORMAL_POOL_SIZE = 4;
	private static final int MAXIMUM_NORMAL_POOL_SIZE = 4;
	private static final int KEEP_ALIVE_TIME = 2;
	private final BlockingQueue<Runnable> mNormalTaskQueue;
	private final ThreadPoolExecutor mTaskThreadPool;

	/*
	 * handler that do works on main thread
	 */
	static Handler mUIThreadHandler;
	
	/*
	 * Thread pool dành cho urgent task, thông thường chỉ cần dùng normal task,
	 * urgent task chỉ dùng đến khi thực sự cần ưu tiên cao vì số thread này
	 * giới hạn và dành riêng cho những task đặc thù như khi mở một màn hình
	 * đang hiện loading dialog thì nên dùng cái này và khi loading dialog đóng
	 * thì cái này đã xong, những việc ko block màn hình để chờ thì dùng normal
	 * thread pool là được
	 */
	private static final int CORE_URGENT_POOL_SIZE = 2;
	private static final int MAXIMUM_URGENT_POOL_SIZE = 2;
	private final BlockingQueue<Runnable> mUrgentTaskQueue;
	private final ThreadPoolExecutor mUrgentTaskThreadPool;

	public static TaskExecuter getInstance(Context context) {
		synchronized (mLock) {
			if (mInstance == null) {
				mInstance = new TaskExecuter(context.getApplicationContext());
			}
			return mInstance;
		}
	}

	private TaskExecuter(Context context) {
		// mAppContext = context;
		mNormalTaskQueue = new LinkedBlockingQueue<Runnable>();
		mTaskThreadPool = new ThreadPoolExecutor(CORE_NORMAL_POOL_SIZE,
				MAXIMUM_NORMAL_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				mNormalTaskQueue);
		mTaskThreadPool.allowCoreThreadTimeOut(true);

		mUrgentTaskQueue = new LinkedBlockingQueue<Runnable>();
		mUrgentTaskThreadPool = new ThreadPoolExecutor(CORE_URGENT_POOL_SIZE,
				MAXIMUM_URGENT_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				mUrgentTaskQueue);
		mUrgentTaskThreadPool.allowCoreThreadTimeOut(true);
		
		mUIThreadHandler = new Handler(context.getMainLooper());
	}

	public void execute(Runnable runnable) {
		execute(runnable, PRIORITY_NORMAL);
	}

	/**
	 * Đăng ký thực thi một tác vụ load dữ liệu nào đó, truyền vào đối tượng
	 * DataLoaderTask mô tả tác vụ và độ ưu tiên để sắp xếp lại thứ tự thực thi
	 * nếu cần thiết, trong trường hợp rất gấp có thể ko dùng đến phương thức
	 * này của DataLoader vì không chắc chắn 100% là thực thi được ngay lập tức <br>
	 * <br>
	 * 
	 * <b>Lưu ý:</b> hiện tại độ ưu tiên chưa được implement, model này vẫn đang
	 * trong quá trình xây dựng
	 */
	public void execute(Runnable runnable, int priority) {
		if (priority == PRIORITY_BLOCKING) {
			mUrgentTaskThreadPool.execute(runnable);
		} else {
			mTaskThreadPool.execute(runnable);
		}
	}
	
	public void execute(CancelableTask runnable) {
		execute(runnable, PRIORITY_NORMAL);
	}

	public void cancel(Runnable runnable) {
		mTaskThreadPool.remove(runnable);
		mUrgentTaskThreadPool.remove(runnable);
	}
	
	//Remove all runnables are specified comeFrom value
	public void cancel(String comeFrom) {
		if(mNormalTaskQueue != null){
			Log.d(TAG, "size normal before cancel = " + mNormalTaskQueue.size());
			Iterator<Runnable> itNormal = mNormalTaskQueue.iterator();
			while (itNormal.hasNext()) {
				Runnable r = itNormal.next();
				Log.d(TAG, "normal runable instance = " + r.getClass().getName());
				if(r instanceof CancelableTask){
					CancelableTask runable = (CancelableTask) r;
					if(runable.getComeFrom().equals(comeFrom)){
						mNormalTaskQueue.remove(runable);
						Log.d(TAG, "normal FROM  == " + runable.getComeFrom() + " == removed");
					}
				}
			}
			Log.d(TAG, "size normal after remove = " + mNormalTaskQueue.size());

		}
		
		if(mUrgentTaskQueue != null){
			Log.d(TAG, "size urgent before cancel = " + mUrgentTaskQueue.size());
			Iterator<Runnable> itUrgent = mUrgentTaskQueue.iterator();
			while (itUrgent.hasNext()) {
				Runnable r = itUrgent.next();
				Log.d(TAG, "urgent runable instance = " + r.getClass().getName());
				if(r instanceof CancelableTask){
					CancelableTask runable = (CancelableTask) r;
					if(runable.getComeFrom().equals(comeFrom)){
						mUrgentTaskQueue.remove(runable);
						Log.d(TAG, "urgent FROM  == " + runable.getComeFrom() + " == removed");
					}
				}
			}
			Log.d(TAG, "size urgent after remove = " + mUrgentTaskQueue.size());

		}
	}

	/**
	 * class that support to do similar things as AsyncTask, but managed by this
	 * threadpool
	 * 
	 * @author nvhau
	 *
	 * @param <I> input params
	 * @param <O> output of doInBackground
	 */
	public static abstract class TaskTemplate<I,O> implements Runnable {
		private I[] params;		
		O result;
		
		/**
		 * this methods will be run on parent threadpool
		 */
		public abstract O doInBackground(I... params);
		
		/**
		 * this methods will be run on UI thread
		 */
		public void onPostExecute(O result) {}
				
		/**
		 * set input params - same as AsyncTask.execute
		 */
		public void setParams(I... params) {
			this.params = params;
		}
		
		@Override
		public void run() {			
			result = doInBackground(params);
			mUIThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					onPostExecute(result);
				}
			});
		}					
	}
	
	public static abstract class CancelableTask implements Runnable {

		protected String comeFrom = "";

		public CancelableTask() {
			// TODO Auto-generated constructor stub
		}

		public CancelableTask(String comeFrom) {
			this.comeFrom = comeFrom;
		}

		public String getComeFrom() {
			return comeFrom;
		}

	}
}
