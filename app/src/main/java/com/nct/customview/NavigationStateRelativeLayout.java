package com.nct.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author  Nghi Do
 */
public class NavigationStateRelativeLayout extends RelativeLayout implements Checkable{

	private boolean mChecked;
	
	private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

	public NavigationStateRelativeLayout(Context context) {
		this(context, null);
	}

	public NavigationStateRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NavigationStateRelativeLayout (Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	public void toggle() {
		setChecked(!mChecked);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	public boolean isChecked() {
		
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		
		if (mChecked != checked) {
            mChecked = checked;
        }
		refreshDrawableState();
		setCheckedRecursive(this, checked);
	}
	
	@Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }
	
	private void setCheckedRecursive(ViewGroup parent, boolean checked) {
        int count = parent.getChildCount();
        for(int i = 0; i < count; i++ ) { 
            View v = parent.getChildAt(i);
            if(v instanceof NavigationStateImageView) {
                ((NavigationStateImageView) v).setChecked(checked);
            }
            
            if(v instanceof TextView) {
				if (checked){
//					((TfTextView) v).setTypeface(null, Typeface.BOLD);
                    ((TfTextView) v).setTextColor(Color.WHITE);
				} else{
                    ((TfTextView) v).setTextColor(Color.parseColor("#FF331291"));
//                    ((TfTextView) v).setTypeface(null, Typeface.NORMAL);
                }

            }
        }
    }
 
    /**************************/
    /**   Drawable States    **/
    /**************************/
 
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
 
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
 
        Drawable drawable = getBackground();
        if (drawable != null) {
            int[] myDrawableState = getDrawableState();
            drawable.setState(myDrawableState);
            invalidate();
        }
    }
 
    /**************************/
    /**   State persistency  **/
    /**************************/
 
    static class SavedState extends BaseSavedState {
        boolean checked;
 
        SavedState(Parcelable superState) {
            super(superState);
        }
 
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean)in.readValue(null);
        }
 
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }
 
        @Override
        public String toString() {
            return "NavigationStateRelativeLayout.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }
 
        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            @Override
			public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
 
            @Override
			public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
 
    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
 
        ss.checked = isChecked();
        return ss;
    }
 
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
 
        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

}