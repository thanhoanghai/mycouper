package com.nct.customview;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nct.utils.Debug;
import thh.com.mycouper.R;

/**
 * @author trieulv
 * 
 */
public class TfTextView extends TextView {
	
	private static Map<String, Typeface> typefaceCache = new HashMap<String, Typeface>();
	private static final String CONST_FONT_FOLDER_NAME = "fonts/";
	private static final String CONST_FONT_FILE_SUFFIX = ".ttf";

	public TfTextView(Context context) {
		super(context);
	}

	public TfTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (!isInEditMode()) {
			setTypeface(attrs, this);
		}
	}

	public TfTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if (!isInEditMode()) {
			setTypeface(attrs, this);
		}
	}

	public static void setTypeface(AttributeSet attrs, TextView textView) {
		Context context = textView.getContext();

		TypedArray values = context.obtainStyledAttributes(attrs,
				R.styleable.TypefaceTextView);

		String typefaceName = values
				.getString(R.styleable.TypefaceTextView_typeface_name)
				+ CONST_FONT_FILE_SUFFIX;

		int typefaceStyle = values.getInt(
				R.styleable.TypefaceTextView_typeface_style, 0);

		if (typefaceCache.containsKey(typefaceName)) {
			textView.setTypeface(typefaceCache.get(typefaceName), typefaceStyle);
		} else {
			Typeface typeface;
			try {
				typeface = Typeface.createFromAsset(textView.getContext()
						.getAssets(), CONST_FONT_FOLDER_NAME + typefaceName);
			} catch (Exception e) {
				Debug.logError(
						context.getString(R.string.app_name),
						String.format(
								"Typeface %s not found, or could not be loaded. Showing default typeface.",
								typefaceName));
				return;
			}

			typefaceCache.put(typefaceName, typeface);
			textView.setTypeface(typeface, typefaceStyle);
		}
		values.recycle();
	}
}
