package com.nct.cache;

import android.os.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Các hàm hỗ trợ làm việc với cache
 * 
 * @author Nghi Do
 * 
 */
public class CacheUtils {
	/*
	 * Các loại cache hỗ trợ
	 */
	public static final int CACHE_TYPE_IMAGE = 1;
	public static final int CACHE_TYPE_VIDEO = 2;

	/*
	 * Dung lượng giới hạn cho thư mục cache
	 */
	private static final long CACHE_FOLDER_SIZE_LIMIT_IMAGES = 1024 * 1024 * 300; // 300MB
	private static final long CACHE_FOLDER_SIZE_LIMIT_VIDEOS = 1024 * 1024 * 150; // 150MB
	private static final long CACHE_FOLDER_SIZE_LIMIT_OTHERS = 1024 * 1024 * 100; // 100MB

	/*
	 * Dung lượng định mức khi cần xóa bớt cache
	 */
	private static final long CACHE_FOLDER_SIZE_STANDARD_IMAGES = 1024 * 1024 * 150; // 150MB
	private static final long CACHE_FOLDER_SIZE_STANDARD_VIDEOS = 1024 * 1024 * 100; // 150MB
	private static final long CACHE_FOLDER_SIZE_STANDARD_OTHERS = 1024 * 1024 * 30; // 100MB

	/*
	 * Thời gian dự kiến để ktra cache
	 */
	private static long mNextCheckTimeImage = 0;
	private static long mNextCheckTimeVideo = 0;
	private static long mNextCheckTimeOthers = 0;

	private static final String mCachePath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/.camerahd/cache";
	
	/*
	 * sub folder cho từng loại cache
	 */
	private static final String SUB_IMAGES = "/images";
	private static final String SUB_VIDEOS = "/videos";
	private static final String SUB_OTHERS = "/others";

	private static final long MIN_NEXT_CHECK_INTERVAL = 30000;

	/**
	 * Chuyển từ url của file sang tên file trên hệ thống cache hiện tại
	 */
	public static String getFilenameForUrl(String url) {
		return String.valueOf(url.hashCode());
	}

	/**
	 * Lấy file cache dựa trên loại file và tên file
	 */
	public static File getCacheFile(int cacheType, String name) {
		StringBuilder path = new StringBuilder(mCachePath);
		
		switch (cacheType) {
		case CACHE_TYPE_IMAGE:
			path.append(SUB_IMAGES);
			break;

		case CACHE_TYPE_VIDEO:
			path.append(SUB_VIDEOS);
			break;
			
		default:
			path.append(SUB_OTHERS);
		}

		File folder = new File(path.toString());
		if (!folder.exists()) {
			folder.mkdirs();
		}
		path.append("/").append(name);

		return new File(path.toString());
	}

	/**
	 * Lấy file cache dựa trên loại file và url của file cần tải về
	 */
	public static File getCacheFileByUrl(int cacheType, String url) {
		return getCacheFile(cacheType, Integer.toString(url.hashCode()));
	}

	/**
	 * Kiểm tra dung lượng cache và xóa bớt nếu vượt quá giới hạn
	 */
	public static void validateCache(int cacheType) {
		// Ktra xem đã cần phải ktra cache hay chưa
		File folder;
		long size;
		long next;

		switch (cacheType) {
		case CACHE_TYPE_IMAGE:
			if (System.currentTimeMillis() > mNextCheckTimeImage) {
				folder = new File(mCachePath + SUB_IMAGES);
				size = calculateFolderSize(folder);
				if (size > CACHE_FOLDER_SIZE_LIMIT_IMAGES) {
					size = resizeFolder(folder, size,
							CACHE_FOLDER_SIZE_STANDARD_IMAGES);
				}
				next = Math.max((CACHE_FOLDER_SIZE_LIMIT_IMAGES - size) / 60,
                        MIN_NEXT_CHECK_INTERVAL);
				mNextCheckTimeImage = System.currentTimeMillis() + next;
			}
			break;

		case CACHE_TYPE_VIDEO:
			if (System.currentTimeMillis() > mNextCheckTimeVideo) {
				folder = new File(mCachePath + SUB_VIDEOS);
				size = calculateFolderSize(folder);
				if (size > CACHE_FOLDER_SIZE_LIMIT_VIDEOS) {
					size = resizeFolder(folder, size,
							CACHE_FOLDER_SIZE_STANDARD_VIDEOS);
				}
				next = Math.max((CACHE_FOLDER_SIZE_LIMIT_VIDEOS - size) / 60,
                        MIN_NEXT_CHECK_INTERVAL);
				mNextCheckTimeVideo = System.currentTimeMillis() + next;
			}
			break;

		default:
			if (System.currentTimeMillis() > mNextCheckTimeOthers) {
				folder = new File(mCachePath + SUB_OTHERS);
				size = calculateFolderSize(folder);
				if (size > CACHE_FOLDER_SIZE_LIMIT_OTHERS) {
					size = resizeFolder(folder, size,
							CACHE_FOLDER_SIZE_STANDARD_OTHERS);
				}
				next = Math.max((CACHE_FOLDER_SIZE_LIMIT_OTHERS - size) / 60,
                        MIN_NEXT_CHECK_INTERVAL);
				mNextCheckTimeOthers = System.currentTimeMillis() + next;
			}
		}

	}

	/**
	 * Xóa các file trong thư mục này đến khi còn mức dung lượng giới hạn hoặc
	 * ko còn file nào có thể xóa
	 * 
	 */
	private static long resizeFolder(File folder, long currentSize,
			long targetSize) {
		if (currentSize > targetSize) {
			File[] files = folder.listFiles();
			Arrays.sort(files, new FileDateModifiedComparator());
			for (File file : files) {
				long size = file.length();
				if (file.delete()) {
					currentSize -= size;
					if (currentSize <= targetSize) {
						break;
					}
				}
			}
		}

		return currentSize;
	}

	/**
	 * So sánh 2 file dựa trên ngày cuối cùng sửa file
	 */
	static class FileDateModifiedComparator implements Comparator<File> {

		@Override
		public int compare(File lhs, File rhs) {
			if ((lhs.lastModified() - rhs.lastModified()) > 0) {
				return 1;
			} else if ((lhs.lastModified() - rhs.lastModified()) < 0) {
				return -1;
			} else {
				return 0;
			}
		}

	}

	/**
	 * Tính toán dung lượng của một folder
	 */
	private static long calculateFolderSize(File folder) {
		long size = 0;
		if (folder.exists()) {
			// Ktra lại dung lượng thư mục cache
			long start = System.currentTimeMillis();
			File[] files = folder.listFiles();
			for (File child : files) {
				size += child.length();
			}
		}

		return size;
	}
}
