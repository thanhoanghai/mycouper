package com.nct.mv;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;

import com.nct.customview.TfTextView;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtStore extends AtBase {
	private static final String tag = "AtIntroduce";

	private Button bntLogin;
	private TfTextView txtRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_store);

		setLanguge();
		initTopbar(getString(R.string.stores));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.INVISIBLE);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bntLogin = (Button) findViewById(R.id.at_store_bnt_login);
		bntLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenStoreDetail(AtStore.this);
			}
		});

		txtRegister = (TfTextView) findViewById(R.id.at_register_tv);

		String strHtml = "No account? Please visit "
				+ "<font color='blue'><a href='GoRegister'><i>www.mycouper.com</i></a></font>"
				+ " to register. ";

		CharSequence sequence = Html.fromHtml(strHtml);
		SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
		URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
		for(URLSpan span : urls) {
			makeLinkClickable(strBuilder, span);
		}
		txtRegister.setLinksClickable(true);
		txtRegister.setLinkTextColor(Color.BLUE);
		txtRegister.setMovementMethod(LinkMovementMethod.getInstance());
		txtRegister.setText(strBuilder);
		txtRegister.setTextColor(Color.BLACK);

	}

	protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
	{
		int start = strBuilder.getSpanStart(span);
		int end = strBuilder.getSpanEnd(span);
		int flags = strBuilder.getSpanFlags(span);
		ClickableSpan clickable = new ClickableSpan() {
			public void onClick(View view) {
				// Do something with span.getURL() to handle the link click...
				Intent intent = null;
				String mKeyValue = span.getURL().toString();
				if(mKeyValue.equals("GoRegister")){
					String url = "http://mycouper.com/";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			}
		};
		strBuilder.setSpan(clickable, start, end, flags);
		strBuilder.removeSpan(span);
	}

}
