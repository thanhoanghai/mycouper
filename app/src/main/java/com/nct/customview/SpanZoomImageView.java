package com.nct.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SpanZoomImageView extends ImageView {	
	//width/height percent for fit bound - default 100%
	private int mBoundWidthPercent, mBoundHeightPercent;
	//the minimum rect this image must fill when move/zoom
	private Rect mScaleBoundRect;
	
	//move distance
	private float mDeltaX, mDeltaY;
	//start pointer down position
	private float mStartX1, mStartY1, mStartX2, mStartY2;
	
	//scale factor in compare with real bitmap size
	private float mScaleFactor;
	//scale factor on start zoom
	private float mStartScaleFactor;
	
	//bitmap real size
	private int mBitmapW, mBitmapH;
	
	//enable span/zoom or not
	private boolean isEnableEdit;
	
	public SpanZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	public SpanZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public SpanZoomImageView(Context context) {
		super(context);
		initialize();
	}
	
	private void initialize() {
		//default fit bound percent to 100%
		mBoundHeightPercent = 100;
		mBoundWidthPercent = 100;
						
		//default scaletype to center
		setScaleType(ScaleType.CENTER);
						
		//default edit enable
		isEnableEdit = true;
		
		reset();
	}
	
	private void reset() {
		mDeltaX = 0;
		mDeltaY = 0;
		
		mStartScaleFactor = 1.0f;
		mScaleFactor = -1;
		
		mBitmapW = 0;
		mBitmapH = 0;
	}
	
	public void setScaleBound(int w, int h) {
		mBoundWidthPercent = w;
		mBoundHeightPercent = h;
	}
	
	public void setEnableEdit(boolean enable) {
		isEnableEdit = enable;
	}
				
	/*
	 * suppose to support all case so we call reset() in here if dedicated for
	 * kbuilder only then we can call reset() on setImageBitmap instead and
	 * remove this call 
	 * (non-Javadoc)
	 * @see android.view.View#onLayout(boolean, int, int, int, int)
	 */
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//	
//		reset();
//		invalidate();
//	}
	
	/*
	 * call reset() on onLayout(...) for supporting all case
	 * (non-Javadoc)
	 * @see android.widget.ImageView#setImageBitmap(android.graphics.Bitmap)
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		
		reset();
		invalidate();
	}
	
	@SuppressLint("FloatMath")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if( !isEnableEdit || mScaleBoundRect == null) {
			return super.onTouchEvent(event);
		}
		
		int action = event.getActionMasked();
		int pointerCount = event.getPointerCount();
		float x1, y1,
			x2 = -1, y2 = -1;
		
		x1 = event.getX(0);
		y1 = event.getY(0);
		
		if( pointerCount > 1 ) {
			x2 = event.getX(1);
			y2 = event.getY(1);		
		}
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mStartX1 = x1;
			mStartY1 = y1;			
			break;
		case MotionEvent.ACTION_MOVE:						
			if( pointerCount > 1 ) {
				//zoom
				float dx1 = mStartX1 - mStartX2;
				float dy1 = mStartY1 - mStartY2;
				float distance1 = FloatMath.sqrt(dx1 * dx1 + dy1 * dy1);
				float dx2 = x1 - x2;
				float dy2 = y1 - y2;
				float distance2 = FloatMath.sqrt(dx2 * dx2 + dy2 * dy2);
				
				float tmpScaleFactor = mStartScaleFactor * distance2 / distance1;
				Rect tmpRect = getSpanZoomRect(tmpScaleFactor, mDeltaX, mDeltaY);
				
				if( isRectFill(tmpRect, mScaleBoundRect) ) {
					mScaleFactor = tmpScaleFactor;
				}
			} else {
				//move
				float tmpDx = mDeltaX + x1 - mStartX1;
				float tmpDy = mDeltaY + y1 - mStartY1;
				Rect tmpRect = getSpanZoomRect(mScaleFactor, tmpDx, tmpDy);
				
				if( isRectFill(tmpRect, mScaleBoundRect) ) {
					//move x y
					mDeltaX = tmpDx;
					mDeltaY = tmpDy;										
				} else if (isRectFill(getSpanZoomRect(mScaleFactor, tmpDx, mDeltaY), mScaleBoundRect)) {
					//move only x
					mDeltaX = tmpDx;
				} else if (isRectFill(getSpanZoomRect(mScaleFactor, mDeltaX, tmpDy), mScaleBoundRect)) {
					//move only y
					mDeltaY = tmpDy;
				}
				
				mStartX1 = x1;
				mStartY1 = y1;
			}
			break;
		case MotionEvent.ACTION_UP:			
			break;
			
		case MotionEvent.ACTION_POINTER_DOWN:			
			if( event.getActionIndex() == 1 ) {
				//start zoom
				mStartX2 = x2;
				mStartY2 = y2;
				mStartScaleFactor = mScaleFactor;
			}			
			break;
		case MotionEvent.ACTION_POINTER_UP:			
			if( pointerCount == 2 ) {
				//end zoom - change to move
				if( event.getActionIndex() == 1 ) {
					mStartX1 = x1;
					mStartY1 = y1;
				} else {
					mStartX1 = x2;
					mStartY1 = y2;
				}
			} else if( pointerCount > 2 ) {
				/*
				 * more than 2 pointer and the pointer index 1 or 2 was up
				 * reset scale values
				 * TODO need to check more here
				 */
				if( event.getActionIndex() == 1 ) {
					mStartX1 = event.getX(0);
					mStartY1 = event.getY(0);					
					mStartX2 = event.getX(2);
					mStartY2 = event.getY(2);		
					mStartScaleFactor = mScaleFactor;
				} else if( event.getActionIndex() == 0 ) {
					mStartX1 = event.getX(1);
					mStartY1 = event.getY(1);					
					mStartX2 = event.getX(2);
					mStartY2 = event.getY(2);
					mStartScaleFactor = mScaleFactor;
				} 								
			}
			break;

		default:
			super.onTouchEvent(event);
			break;
		}
				
		invalidate();
		return true;
	}
		
	/**
	 * caculate the span zoom bound
	 * @param scaleFactor
	 * @param deltaX
	 * @param deltaY
	 * @return
	 */
	private Rect getSpanZoomRect(float scaleFactor, float deltaX, float deltaY) {
		Rect rect = new Rect();
		int viewW = getWidth();
		int viewH = getHeight();
		
		//getnerate bound at (0,0)
		rect.left = -(int) (mBitmapW*scaleFactor/2);
		rect.right = (int) (mBitmapW*scaleFactor/2);
		rect.top = -(int) (mBitmapH*scaleFactor/2);
		rect.bottom = (int) (mBitmapH*scaleFactor/2);
		
		//move to center of view
		rect.left += viewW/2;
		rect.right += viewW/2;
		rect.top += viewH/2;
		rect.bottom += viewH/2;
		
		//move deltaX/deltaY
		rect.left += deltaX;
		rect.right += deltaX;
		rect.top += deltaY;
		rect.bottom += deltaY;
		
		return rect;
	}
	
	/**
	 * check if src rect can fill desc rect or not
	 * @param src
	 * @param desc
	 * @return
	 */
	private boolean isRectFill(Rect src, Rect desc) {
		return src.top <= desc.top &&
				src.bottom >= desc.bottom &&
				src.left <= desc.left &&
				src.right >= desc.right;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {		
		Drawable drawable = getDrawable();
		if( drawable == null ) return;
		
		if( mScaleFactor == -1 ) {
			//init scale value
			int viewW = getWidth();
			int viewH = getHeight();
			Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
			mBitmapW = bitmap.getWidth();
			mBitmapH = bitmap.getHeight();
		
			//scale to fit view ~ centerdrop
			if( mBitmapH*viewW < mBitmapW*viewH ) {
				//scale height
				mScaleFactor = (float)viewH/mBitmapH;
			} else {
				//scale width
				mScaleFactor = (float)viewW/mBitmapW;
			}
			
			//caculate mininum scale bound rect
			mScaleBoundRect = new Rect();
			mScaleBoundRect.left = (viewW - (viewW*mBoundWidthPercent/100))/2;
			mScaleBoundRect.right = viewW - mScaleBoundRect.left;
			mScaleBoundRect.top = (viewH - (viewH*mBoundHeightPercent/100))/2;
			mScaleBoundRect.bottom = viewH - mScaleBoundRect.top;
		}
				
		drawable.setBounds(getSpanZoomRect(mScaleFactor, mDeltaX, mDeltaY));
		drawable.draw(canvas);
	}
	
	public float getScaleFactor() {
		return mScaleFactor;
	}
	
	public float getDeltaX() {
		return mDeltaX;
	}
	
	public float getDeltaY() {
		return mDeltaY;
	}
}