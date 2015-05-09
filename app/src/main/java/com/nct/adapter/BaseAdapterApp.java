/**
 * 
 */
package com.nct.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public abstract class BaseAdapterApp<T> extends android.widget.BaseAdapter {

	protected Context mContext;
	protected LayoutInflater mInflater;

	protected ArrayList<T> mArrayList;
	private final Object mLock = new Object();

	private boolean mNotifyOnChange = true;
	private int idResPlaceHolder = -1;

	public BaseAdapterApp(@NonNull Context context, @NonNull ArrayList<T> list) {
		this.mContext = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Fixed List (return from server) is null
		this.mArrayList = list != null ? list : new ArrayList<T>();

	}

	/**
	 * set NotifyOnChange
	 * 
	 * @param notifyOnChange
	 */
	protected void setNotifyOnChange(boolean notifyOnChange) {
		this.mNotifyOnChange = notifyOnChange;
	}

	/**
	 * set DisplayImageOptions
	 * 
	 * @param resId
	 */
	protected void setDisplayImageOptions(int resId) {
		idResPlaceHolder = resId;
	}

	/**
	 * display Image
	 * 
	 * @param img
	 * @param uri
	 */
	protected void displayImage(ImageView img, String uri) {

		if(idResPlaceHolder > 0)
			Glide.with(mContext).load(uri).placeholder(idResPlaceHolder).centerCrop().crossFade().into(img);
		else
			Glide.with(mContext).load(uri).centerCrop().crossFade().into(img);

	}

	public void updateData(ArrayList<T> datas) {
		clearData();
		addData(datas);
	}

	/**
	 * add Data
	 * 
	 * @param datas
	 */
	public void addData(ArrayList<T> datas) {
		synchronized (mLock) {
			mArrayList.addAll(datas);
		}
		if (mNotifyOnChange)
			this.notifyDataSetChanged();
	}

	public void addDataAtIndex(int index, ArrayList<T> datas) {
		synchronized (mLock) {
			mArrayList.addAll(index, datas);
		}
		if (mNotifyOnChange)
			this.notifyDataSetChanged();
	}

	/**
	 * replace Data
	 * 
	 * @param index
	 * @param object
	 */
	public void replaceData(int index, T object) {
		synchronized (mLock) {
			mArrayList.set(index, object);
		}
		if (mNotifyOnChange)
			this.notifyDataSetChanged();
	}

	/**
	 * clear Data
	 */
	public void clearData() {
		synchronized (mLock) {
			mArrayList.clear();
		}
		if (mNotifyOnChange)
			this.notifyDataSetChanged();
	}

	public ArrayList<T> getData() {
		return mArrayList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mArrayList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public T getItem(int position) {
		return mArrayList.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

}
