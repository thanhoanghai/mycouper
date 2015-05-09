package com.nct.gridview;

public interface OnMoreListener {
	/**
	 * @param numberOfItems
	 * @param numberBeforeMore
	 * @param currentItemPos
	 */
	public void onMoreAsked(int numberOfItems, int numberBeforeMore,
			int currentItemPos);
}
