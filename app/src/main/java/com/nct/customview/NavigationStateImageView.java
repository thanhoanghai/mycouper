package com.nct.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * @author  Ly Tuan Cuong
 */
public class NavigationStateImageView extends ImageView implements Checkable{

	private boolean mChecked;
	
	private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

	public NavigationStateImageView(Context context) {
		this(context, null);
	}

	public NavigationStateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NavigationStateImageView (Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	@Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        return drawableState;
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
	}
	
	@Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

}